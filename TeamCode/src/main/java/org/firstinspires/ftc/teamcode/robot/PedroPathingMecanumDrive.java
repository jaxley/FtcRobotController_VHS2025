package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.utils.DButton;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

/**
 * This class implements a mecanum drive but uses Pedro Pathing's drive API
 * This allows the code to have hybrid teleop and autonomous functionality without conflicting
 * instructions that would happen if we used a different mecanum implementation
 * plus Pedro pathing for hybrid teleop and autonomous
 */
public class PedroPathingMecanumDrive implements IMecanumDrive {
    public static final String SUBSYSTEM_NAME = "PedroMecanumDrive";
    public static final double TURN_MAX_SPEED = 1.0;
    private static final String STOPPED = "Stopped";
    private final Follower follower;
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;

    private final DcMotor backRight;
    private final DButton holdButton = new DButton();

    private final DButton dpadUp = new DButton();
    private final DButton dpadRight = new DButton();
    private final DButton dpadDown = new DButton();
    private final DButton dpadLeft = new DButton();
    private final DButton yButton = new DButton();

    private double driveSpeedModifier = 1.0;

    private boolean initialized = false;

    private boolean ROBOT_CENTRIC_DRIVE = true;
    public PedroPathingMecanumDrive(DcMotor frontLeft, DcMotor frontRight,
                DcMotor backLeft, DcMotor backRight, Follower follower) {
        this.follower = follower;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }

    public DcMotor getFrontLeft() {
        return frontLeft;
    }

    public DcMotor getFrontRight() {
        return frontRight;
    }

    public DcMotor getBackLeft() {
        return backLeft;
    }

    public DcMotor getBackRight() {
        return backRight;
    }

    public void init(TelemetryMirror telemetryMirror, Pose startingPose) {
        follower.startTeleOpDrive();
        if (startingPose != null) {
        follower.setStartingPose(startingPose);
        }
        this.initialized = true;
        telemetryMirror.addData(SUBSYSTEM_NAME, "Initialized");
    }

    /**
     *
     * @param driveGamepad The gamepad used by the driver on the drive team
     * @param telemetryMirror Telemetry instance for logging useful info
     * @param startingPose The robot starting pose. This is used to ensure the robot knows where
     *                     it is on the field so that it can follow any autonomous paths properly
     */
    public void run(Gamepad driveGamepad, TelemetryMirror telemetryMirror, Pose startingPose) {
        if (!initialized) {
            init(telemetryMirror, startingPose);
        }
        telemetryMirror.addData(SUBSYSTEM_NAME, "Running");

        follower.update();

        double driveSpeed = getDriveSpeed(driveGamepad);

        // Robot Centric Drive Toggle Button
        yButton.update(driveGamepad.y);
        if (yButton.released()) {
            ROBOT_CENTRIC_DRIVE = !ROBOT_CENTRIC_DRIVE;
        }

        telemetryMirror.addData("Drive Mode", ROBOT_CENTRIC_DRIVE ? "Robot" : "Field");

        double forwardSpeed = -driveGamepad.left_stick_y * driveSpeed;
        double strafeSpeed = -driveGamepad.left_stick_x * driveSpeed;
        double turnSpeed = TURN_MAX_SPEED * -driveGamepad.right_stick_x * driveSpeed;
        follower.setTeleOpDrive(forwardSpeed, strafeSpeed, turnSpeed, ROBOT_CENTRIC_DRIVE); // TODO - allow selecting mode at runtime

        holdButton.update(driveGamepad.left_trigger != 0);
        if (holdButton.pressed()) {
            hold();
        } else if (holdButton.released()) {
            follower.startTeleOpDrive(); // go back to manual mode
            follower.setTeleOpDrive(forwardSpeed, strafeSpeed, turnSpeed, ROBOT_CENTRIC_DRIVE);
        }

        telemetryMirror.addData(SUBSYSTEM_NAME + " FWD speed", forwardSpeed);
        telemetryMirror.addData(SUBSYSTEM_NAME + " STRAFE speed", strafeSpeed);
        telemetryMirror.addData(SUBSYSTEM_NAME + " TURN speed", turnSpeed);
    }

    private double getDriveSpeed(Gamepad driveGamepad) {
        dpadUp.update(driveGamepad.dpad_up);
        dpadRight.update(driveGamepad.dpad_right);
        dpadDown.update(driveGamepad.dpad_down);
        dpadLeft.update(driveGamepad.dpad_left);

        if (dpadUp.pressed()) {
            driveSpeedModifier = 1.0;
        } else if (dpadRight.pressed()) {
            driveSpeedModifier = 0.75;
        } else if (dpadDown.pressed()) {
            driveSpeedModifier = 0.50;
        } else if (dpadLeft.pressed()) {
            driveSpeedModifier = 0.25;
        }

        return driveSpeedModifier * Constants.TELEOP_MOTOR_MAX_POWER;
    }

    /**
     * This run() method doesn't set a starting pose so the drive won't be able to use
     * autonomous path following (since it won't know where it is on the field)
     * Use {@link #run(Gamepad, TelemetryMirror, Pose)} instead.
     * @param driveGamepad The gamepad used by the driver on the drive team
     * @param telemetryMirror Telemetry instance for logging useful info
     */
    @Override
    public void run(Gamepad driveGamepad, TelemetryMirror telemetryMirror) {
        run(driveGamepad, telemetryMirror, null);
    }

    @Override
    public void stop(TelemetryMirror telemetryMirror) {
        follower.startTeleopDrive(true);
        follower.setTeleOpDrive(0,0,0,true);
        telemetryMirror.addData(SUBSYSTEM_NAME, STOPPED);
    }

    public void hold() {
        follower.holdPoint(follower.getPose());
    }
}
