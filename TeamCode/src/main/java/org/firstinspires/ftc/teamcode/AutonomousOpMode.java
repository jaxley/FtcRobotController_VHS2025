package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Alliance;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.Drawing;
import org.firstinspires.ftc.teamcode.robot.Poses;
import org.firstinspires.ftc.teamcode.robot.RobotBaseAutonomous;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

/**
 * Based on https://pedropathing.com/docs/pathing/examples/auto
 */

public abstract class AutonomousOpMode extends OpMode {

    public static final String AUTONOMOUS_OP_MODE = "AutonomousOpMode";
    public static final String ALLIANCE = "Alliance";
    private PathChain getRow3ThenReturnToStartTop1;

    private AutonomousOpMode() {}
    protected AutonomousOpMode(Alliance alliance) {
        this.alliance = alliance;
    }

    public static final boolean USE_PANELS = true;
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private RobotBaseAutonomous robotBase;

    private Alliance alliance;

    private PathState pathState;

    // Pedro paths
    private PathChain hitAndRun;
    private PathChain lowStartThenLeave;
    private TelemetryMirror telemetryMirror;
    private static ElapsedTime stopWatch = new ElapsedTime();
    boolean firstFired = false;
    boolean secondFired = false;
    boolean thirdFired = false;

    private void buildPaths() {

        Poses.AlliancePoses poses = Poses.forAlliance(alliance);
        // TODO: build our pedro paths here
        // hit and run is designed to move from starting to shooting position, then to leave position
        // TODO: this probably needs to be separate path segments to allow for shooting inbetween movement
        hitAndRun = follower.pathBuilder()
                .addPath(new BezierLine(poses.get(Poses.NamedPose.SHOOTING_GOAL_TOP_2),
                        poses.get(Poses.NamedPose.SHOOTING_GOAL_TOP_2)))
                .setConstantHeadingInterpolation(poses.get(Poses.NamedPose.SHOOTING_GOAL_TOP_2).getHeading())
                .addPath(new BezierLine(poses.get(Poses.NamedPose.SHOOTING_GOAL_TOP_2),
                        poses.get(Poses.NamedPose.LEAVE_TOP)))
                .build();

//        // TODO - rename these - these are minimal paths to exit the lower launch zone to get leave points ONLY
        lowStartThenLeave = follower.pathBuilder()
                .addPath(new BezierLine(poses.get(Poses.NamedPose.STARTING_LOW),
                        poses.get(Poses.NamedPose.LEAVE_LOW)))
                .setConstantHeadingInterpolation(poses.get(Poses.NamedPose.STARTING_LOW).getHeading())
                .build();

        getRow3ThenReturnToStartTop1 = follower.pathBuilder()
                .addPath(new BezierLine(poses.get(Poses.NamedPose.STARTING_TOP_1),
                        poses.get(Poses.NamedPose.INTAKE_ROW_3_START)))
                .setConstantHeadingInterpolation(poses.get(Poses.NamedPose.INTAKE_ROW_3_START).getHeading())
                .addPoseCallback(poses.get(Poses.NamedPose.INTAKE_ROW_3_START), new Runnable() {
                    @Override
                    public void run() {
                        robotBase.getIntake().loadBallToShooter(telemetryMirror);
                    }
                }, 0.5)
                .addPath(new BezierLine(poses.get(Poses.NamedPose.INTAKE_ROW_3_START),
                        poses.get(Poses.NamedPose.INTAKE_ROW_3_END)))
                .addPoseCallback(poses.get(Poses.NamedPose.INTAKE_ROW_3_END), new Runnable() {
                    @Override
                    public void run() {
                        robotBase.getIntake().stop(telemetryMirror);
                    }
                }, 0.5)
                .setConstantHeadingInterpolation(poses.get(Poses.NamedPose.STARTING_TOP_1).getHeading())
                .addPath(new BezierLine(poses.get(Poses.NamedPose.INTAKE_ROW_3_END),
                        poses.get(Poses.NamedPose.STARTING_TOP_1)))
                .build();

        // TODO - we are missing paths to collect balls from each of the rows...
    }

    /**
     * This is the main loop of the OpMode, it will run repeatedly after clicking "Play".
     **/
    @Override
    public void loop() {

        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();
        autonomousPathUpdate(telemetry);

        // Feedback to Driver Hub for debugging
        telemetryMirror.addData("path state", pathState);
        telemetryMirror.addData("x", follower.getPose().getX());
        telemetryMirror.addData("y", follower.getPose().getY());
        telemetryMirror.addData("heading", follower.getPose().getHeading());
        telemetryMirror.update();
        draw();
    }

    /**
     * This method is called once at the init of the OpMode.
     **/
    @Override
    public void init() {

        telemetryMirror = new TelemetryMirror(telemetry, USE_PANELS);
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        Poses.AlliancePoses poses = Poses.forAlliance(alliance);
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(poses.get(Poses.NamedPose.STARTING_TOP_2));
        buildPaths();
        drawOnlyCurrent();
    }

    /**
     * This method is called continuously after Init while waiting for "play".
     **/
    @Override
    public void init_loop() {
        telemetryMirror.addData("Code Version", BuildConfig.VERSION_NAME);
        telemetryMirror.addData("Code Build Time", BuildConfig.APP_BUILD_TIME);
        telemetryMirror.addData(ALLIANCE, alliance.name());
        telemetryMirror.addData(AUTONOMOUS_OP_MODE, "initialized");
        telemetryMirror.update();

        follower.update();
        drawOnlyCurrent();
    }

    /**
     * This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system
     **/
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setNextPathState(PathState.SCORE_PRELOADED);

        robotBase = RobotBaseAutonomous.getInstance(hardwareMap, telemetry);

        telemetryMirror.addData(ALLIANCE, alliance.name());
        telemetryMirror.addData(AUTONOMOUS_OP_MODE, "started");
        telemetryMirror.update();
        follower.update();
    }

    /**
     * We do not use this because everything should automatically disable
     **/

    @Override
    public void stop() {
    }

    /**
     * The switch is called continuously and runs the pathing, at certain points, it triggers the action state. The pathState variable in the switch statement will track the robot's movement throughout the autonomous. Every time the switch changes case, it will reset the timer. The followPath() function sets the follower to run the specific path, but does NOT wait for it to finish before moving on. Rather, the robot will transition between path states based on a specified condition in the if statement (known as a Finite State Machine, or FSM).
     * <p>
     * Below is an example state manager with explanations on what each case does, and how to modify it to fit your own routine.
     */
    public void autonomousPathUpdate(Telemetry telemetry) {
        switch (pathState) {
            case SCORE_PRELOADED:
                case SCORE:
                {
                    if (!follower.isBusy()) {
                        //follower.followPath(path1);
                        robotBase.getShooter().startFlywheel(telemetryMirror, -0.825);


                        if (pathTimer.getElapsedTime() == 500 && !firstFired) {
                            firstFired = true;
                            robotBase.getShooter().fire(telemetryMirror);
                        }
                        if (pathTimer.getElapsedTime() == 1000 && !secondFired) {
                            secondFired = true;
                            robotBase.getShooter().fire(telemetryMirror);
                        }
                        if (pathTimer.getElapsedTime() == 1500 && !thirdFired) {
                            thirdFired = true;
                            robotBase.getShooter().fire(telemetryMirror);
                            robotBase.getShooter().stop(telemetryMirror);
                        }


                        setNextPathState(PathState.INTAKE_ROW3);
                    }
                    break;
                }
            case INTAKE_ROW3:

            /* You could check for
            - Follower State: "if(!follower.isBusy()) {}"
            - Time: "if(pathTimer.getElapsedTimeSeconds() > 1) {}"
            - Robot Position: "if(follower.getPose().getX() > 36) {}"
            */
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    /* Score Preload */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */

                    follower.followPath(getRow3ThenReturnToStartTop1, 0.5,true);
                    setNextPathState(PathState.SCORE);
                }
                break;
            case INTAKE_ROW2:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup1Pose's position */

                if (!follower.isBusy()) {
                    /* Grab Sample */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */

                    //follower.followPath(path3, true);
                    setNextPathState(PathState.SCORE);
                }
                break;
            case INTAKE_ROW1:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */

                if (!follower.isBusy()) {
                    /* Score Sample */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */

                    //follower.followPath(path4, true);
                    setNextPathState(PathState.SCORE);
                }
                break;
            case SCORE_LEAVE_POINTS:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    /* Score Sample */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                    // follower.followPath(grabPickup3, true);
                    // TODO - do we want to consider a timer-based trigger to enter this state so we do it before auto ends?
                    setNextPathState(PathState.AUTO_DONE);
                }
                break;
            case AUTO_DONE:
                // terminal state
                // Stop robot
                robotBase.stop(telemetryMirror);
                break;
        }
    }

    /**
     * These change the states of the paths and actions. It will also reset the timers of the individual switches
     **/
    public void setNextPathState(PathState pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    // Use an enum to use *named states* for the path follower. integers vs. names aren't good
    // for human comprehension. These are suggestions
    public enum PathState {
        SCORE_PRELOADED,
        INTAKE_ROW1,
        INTAKE_ROW2,
        INTAKE_ROW3,
        SCORE,
        SCORE_LEAVE_POINTS,
        AUTO_DONE
    }

    public void drawOnlyCurrent() {
        try {
            Drawing.drawRobot(follower.getPose());
            Drawing.sendPacket();
        } catch (Exception e) {
            throw new RuntimeException("Drawing failed " + e);
        }
    }

    public void draw() {
        Drawing.drawDebug(follower);
    }
}