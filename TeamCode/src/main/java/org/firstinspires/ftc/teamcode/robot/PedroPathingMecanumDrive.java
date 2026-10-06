package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose; // FIX: Correct Pedro 3 package path
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.utils.DButton;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

/**
 * Implements a mecanum drive using Pedro Pathing's drive API.
 * This supports hybrid teleop and autonomous control safely.
 */
@SuppressWarnings("unused")
public class PedroPathingMecanumDrive implements IMecanumDrive {
    public static final String SUBSYSTEM_NAME = "PedroMecanumDrive";
    public static final double TURN_MAX_SPEED = 0.5;
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

    private double driveSpeedModifier = 1.0;
    private boolean initialized = false;
    private final boolean ROBOT_CENTRIC_DRIVE = true;

    public PedroPathingMecanumDrive(DcMotor frontLeft, DcMotor frontRight,
                                    DcMotor backLeft, DcMotor backRight, Follower follower) {
        this.follower = follower;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }

    public DcMotor getFrontLeft() { return frontLeft; }
    public DcMotor getFrontRight() { return frontRight; }
    public DcMotor getBackLeft() { return backLeft; }
    public DcMotor getBackRight() { return backRight; }

    public void init(TelemetryMirror telemetryMirror, Pose startingPose) {
        if (startingPose != null) {
            // FIX: Uses standard Pedro 3 positioning method
            follower.setPose(startingPose);
        }
        this.initialized = true;
        telemetryMirror.addData(SUBSYSTEM_NAME, "Initialized");
    }

    public void run(Gamepad driveGamepad, TelemetryMirror telemetryMirror, Pose startingPose) {
        if (!initialized) {
            init(telemetryMirror, startingPose);
        }
        telemetryMirror.addData(SUBSYSTEM_NAME, "Running");

        follower.update();

        double driveSpeed = getDriveSpeed(driveGamepad);
        double forwardSpeed = -driveGamepad.left_stick_y * driveSpeed;
        double strafeSpeed = -driveGamepad.left_stick_x * driveSpeed;
        double turnSpeed = TURN_MAX_SPEED * -driveGamepad.right_stick_x * driveSpeed;

        holdButton.update(driveGamepad.left_trigger != 0);

        if (holdButton.isPressed()) {
            this.hold();
        } else {
            // FIX: Standard manual driving input vector for TeleOp
            follower.manual(forwardSpeed, strafeSpeed, turnSpeed);
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

        return driveSpeedModifier;
    }

    @Override
    public void run(Gamepad driveGamepad, TelemetryMirror telemetryMirror) {
        run(driveGamepad, telemetryMirror, null);
    }

    @Override
    public void stop(TelemetryMirror telemetryMirror) {
        follower.manual(0, 0, 0);
        telemetryMirror.addData(SUBSYSTEM_NAME, STOPPED);
    }

    public void hold() {
        // FIX: Modernized to use native .hold() and .pose() mapping
        follower.hold(follower.pose());
    }
}
