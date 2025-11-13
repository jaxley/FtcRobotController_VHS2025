package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.robot.Poses.blueLowLeavePose;
import static org.firstinspires.ftc.teamcode.robot.Poses.blueShootingPose;
import static org.firstinspires.ftc.teamcode.robot.Poses.blueStartingPose1;
import static org.firstinspires.ftc.teamcode.robot.Poses.blueStartingPose2;
import static org.firstinspires.ftc.teamcode.robot.Poses.redLowLeavePose;
import static org.firstinspires.ftc.teamcode.robot.Poses.redShootingPose;
import static org.firstinspires.ftc.teamcode.robot.Poses.redStartingPose1;
import static org.firstinspires.ftc.teamcode.robot.Poses.redStartingPose2;
import static org.firstinspires.ftc.teamcode.robot.Poses.redTopLeavePose;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.Path;
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
    private int pathState;

    // poses here

    //Pose redShootingPose = new Pose(72, 84, Math.toRadians(45));


    // Pedro paths
    private Path path1;
    private Path path2;
    private Path path3;
    private Path path4;
    private Path path5;
    private RobotBaseAutonomous robotBase;
    private PathChain redHitAndRun;
    private PathChain blueHitAndRun;
    private PathChain blueLeaveStart1;
    private PathChain redLeaveStart1;

    private void buildPaths() {
        // TODO: build our pedro paths here
        redHitAndRun = follower.pathBuilder()
                .addPath(new BezierLine(redStartingPose2, redShootingPose))
                .setConstantHeadingInterpolation(redStartingPose2.getHeading())
                .addPath(new BezierLine(redShootingPose, redTopLeavePose))
                .build();

        blueHitAndRun = follower.pathBuilder()
                .addPath(new BezierLine(blueStartingPose2, blueShootingPose))
                .setConstantHeadingInterpolation(blueStartingPose2.getHeading())
                .addPath(new BezierLine(blueShootingPose, blueLowLeavePose))
                .build();

        redLeaveStart1 = follower.pathBuilder()
                .addPath(new BezierLine(redStartingPose1, redLowLeavePose))
                .setConstantHeadingInterpolation(redStartingPose1.getHeading())
                .build();

        blueLeaveStart1 = follower.pathBuilder()
                .addPath(new BezierLine(blueStartingPose1, blueLowLeavePose))
                .setConstantHeadingInterpolation(blueStartingPose1.getHeading())
                .build();
    }

    /**
     * This is the main loop of the OpMode, it will run repeatedly after clicking "Play".
     **/

    private boolean done;

    @Override
    public void loop() {

        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();
        // TODO: we may not use this state machine
        //autonomousPathUpdate();

        if (!done && !follower.isBusy()) {
            follower.followPath(redLeaveStart1, true);
            done = true;
        }

        // Feedback to Driver Hub for debugging
        //telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    /**
     * This method is called once at the init of the OpMode.
     **/

    @Override
    public void init() {

        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(Constants.startingPose);
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

        telemetry.addData("Status", "initialized");
        telemetry.update();
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
            case 0:
                follower.followPath(path1);
                setPathState(1);
                break;
            case 1:

            /* You could check for
            - Follower State: "if(!follower.isBusy()) {}"
            - Time: "if(pathTimer.getElapsedTimeSeconds() > 1) {}"
            - Robot Position: "if(follower.getPose().getX() > 36) {}"
            */
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    /* Score Preload */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                    follower.followPath(path2, true);
                    setPathState(2);
                }
                break;
            case 2:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup1Pose's position */
                if (!follower.isBusy()) {
                    /* Grab Sample */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
                    follower.followPath(path3, true);
                    setPathState(3);
                }
                break;
            case 3:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    /* Score Sample */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                    follower.followPath(path4, true);
                    setPathState(4);
                }
                break;
            case 4:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup2Pose's position */
                if (!follower.isBusy()) {
                    /* Grab Sample */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
                    follower.followPath(path5, true);
                    setPathState(5);
                }
                break;
            case 5:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    /* Score Sample */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                    // follower.followPath(grabPickup3, true);
                    setPathState(6);
                }
                break;
            case 6:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup3Pose's position */
                if (!follower.isBusy()) {
                    /* Grab Sample */
                    /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
                    //   follower.followPath(scorePickup3, true);
                    setPathState(7);
                }
                break;

            case 7:

                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    /* Set the state to a Case we won't use or define, so it just stops running an new paths */

                    setPathState(-1);
                }
        }
    }

    /**
     * These change the states of the paths and actions. It will also reset the timers of the individual switches
     **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }
}