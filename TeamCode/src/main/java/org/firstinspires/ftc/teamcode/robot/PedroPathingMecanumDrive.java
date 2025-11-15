package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.utils.DButton;

/**
 * This class implements a mecanum drive but uses Pedro Pathing's drive API
 * This allows the code to have hybrid teleop and autonomous functionality without conflicting
 * instructions that would happen if we used a different mecanum implementation
 * plus Pedro pathing for hybrid teleop and autonomous
 */
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

    private boolean initialized;

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

    public void init(Telemetry telemetry, Pose startingPose) {
        follower.startTeleOpDrive();
        if (startingPose != null) {
        follower.setStartingPose(startingPose);
        }
        this.initialized = true;
        telemetry.addData(SUBSYSTEM_NAME, "Initialized");
    }

    /**
     *
     * @param driveGamepad The gamepad used by the driver on the drive team
     * @param telemetry Telemetry instance for logging useful info
     * @param startingPose The robot starting pose. This is used to ensure the robot knows where
     *                     it is on the field so that it can follow any autonomous paths properly
     */
    public void run(Gamepad driveGamepad, Telemetry telemetry, Pose startingPose) {
        if (!initialized) {
            init(telemetry, startingPose);
        }
        telemetry.addData(SUBSYSTEM_NAME, "Running");

        follower.update();

        double driveSpeed = getDriveSpeed(driveGamepad);

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

        telemetry.addData(SUBSYSTEM_NAME + " FWD speed", forwardSpeed);
        telemetry.addData(SUBSYSTEM_NAME + " STRAFE speed", strafeSpeed);
        telemetry.addData(SUBSYSTEM_NAME + " TURN speed", turnSpeed);
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
     * Use {@link #run(Gamepad, Telemetry, Pose)} instead.
     * @param driveGamepad The gamepad used by the driver on the drive team
     * @param telemetry Telemetry instance for logging useful info
     */
    @Override
    public void run(Gamepad driveGamepad, Telemetry telemetry) {
        run(driveGamepad, telemetry, null);
    }

    @Override
    public void stop(Telemetry telemetry) {
        follower.startTeleopDrive(true);
        follower.setTeleOpDrive(0,0,0,true);
        telemetry.addData(SUBSYSTEM_NAME, STOPPED);
    }

    public void hold() {
        follower.holdPoint(follower.getPose());
    }
}
