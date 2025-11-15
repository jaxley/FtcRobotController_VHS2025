package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.robot.Poses.blueLowLeavePose;
import static org.firstinspires.ftc.teamcode.robot.Poses.blueShootingPose;
import static org.firstinspires.ftc.teamcode.robot.Poses.redLowLeavePose;
import static org.firstinspires.ftc.teamcode.robot.Poses.redShootingPose;
import static org.firstinspires.ftc.teamcode.robot.Poses.redTopLeavePose;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.robot.RobotBaseAutonomous;

/**
 * Based on https://pedropathing.com/docs/pathing/examples/auto
 */

@Autonomous
public class AutonomousOpMode extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private RobotBaseAutonomous robotBase;

    private PathState pathState;

    // Pedro paths
    private PathChain redHitAndRun;
    private PathChain blueHitAndRun;
    private PathChain blueLeaveStart1;
    private PathChain redLeaveStart1;
    private JoinedTelemetry joinedTelemetry;

    private void buildPaths() {
        // TODO: build our pedro paths here
        // TODO - these hit and run paths are using OLD shooting positions in front the goals. Need to change based on updated shooter mechanism
//        redHitAndRun = follower.pathBuilder()
//                .addPath(new BezierLine(redStartingPose2, redShootingPose))
//                .setConstantHeadingInterpolation(redStartingPose2.getHeading())
//                .addPath(new BezierLine(redShootingPose, redTopLeavePose))
//                .build();
//
//        blueHitAndRun = follower.pathBuilder()
//                .addPath(new BezierLine(blueStartingPose2, blueShootingPose))
//                .setConstantHeadingInterpolation(blueStartingPose2.getHeading())
//                .addPath(new BezierLine(blueShootingPose, blueLowLeavePose))
//                .build();
//
//        // TODO - rename these - these are minimal paths to exit the lower launch zone to get leave points ONLY
//        redLeaveStart1 = follower.pathBuilder()
//                .addPath(new BezierLine(redStartingPose1, redLowLeavePose))
//                .setConstantHeadingInterpolation(redStartingPose1.getHeading())
//                .build();
//
//        blueLeaveStart1 = follower.pathBuilder()
//                .addPath(new BezierLine(blueStartingPose1, blueLowLeavePose))
//                .setConstantHeadingInterpolation(blueStartingPose1.getHeading())
//                .build();

        // TODO - we are missing paths to collect balls from each of the rows...
    }

    /**
     * This is the main loop of the OpMode, it will run repeatedly after clicking "Play".
     **/

    private boolean done;

    @Override
    public void loop() {

        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();
        // TODO: we need to configure the state machine
        //autonomousPathUpdate();

        if (!done && !follower.isBusy()) {
            follower.followPath(redLeaveStart1, true);
            done = true;
        }

        // Feedback to Driver Hub for debugging
        //telemetry.addData("path state", pathState);
        joinedTelemetry.addData("x", follower.getPose().getX());
        joinedTelemetry.addData("y", follower.getPose().getY());
        joinedTelemetry.addData("heading", follower.getPose().getHeading());
        joinedTelemetry.update();
    }

    /**
     * This method is called once at the init of the OpMode.
     **/

    @Override
    public void init() {

        joinedTelemetry = new JoinedTelemetry(telemetry, PanelsTelemetry.INSTANCE.getFtcTelemetry());
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
       // follower.setStartingPose(Constants.startingPose);
        //follower.setStartingPose(redStartingPose1);
    }

    /**
     * This method is called continuously after Init while waiting for "play".
     **/

    @Override
    public void init_loop() {
    }

    /**
     * This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system
     **/

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        //setPathState(0);

        robotBase = RobotBaseAutonomous.getInstance(hardwareMap, telemetry);

        joinedTelemetry.addData("Status", "initialized");
        joinedTelemetry.update();
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
    public void autonomousPathUpdate() {
        switch (pathState) {
            case SCORE_PRELOADED:
                if (!follower.isBusy()) {
                    //follower.followPath(path1);
                    setNextPathState(PathState.INTAKE_ROW3);
                }
                break;
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

                    //follower.followPath(path2, true);
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
            case SCORE:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup2Pose's position */

                if (!follower.isBusy()) {
                    /* Grab Sample */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
                    //follower.followPath(path5, true);
                    // TODO: SCORE state can lead to more than one next state, including SCORE_LEAVE_POINTS
                    // How should this handle those cases?
                    setNextPathState(PathState.INTAKE_ROW2);
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
                robotBase.stop(telemetry);
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
}