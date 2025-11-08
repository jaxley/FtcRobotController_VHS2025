package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utils.DButton;

/*
 * The mecanum drivetrain involves four separate motors that spin in
 * different directions and different speeds to produce the desired
 * movement at the desired speed.
 */
/**
 * This is an example minimal implementation of the mecanum drivetrain
 * for demonstration purposes.  Not tested and not guaranteed to be bug free.
 *
 * @author Brandon Gong
 */
public class SimpleMecanumDrive implements IMecanumDrive {
    public static final int FL_SPEED_IDX = 0;
    public static final int FR_SPEED_IDX = 1;
    public static final int BL_SPEED_IDX = 2;
    public static final int BR_SPEED_IDX = 3;
    public static final String SUBSYSTEM_NAME = "SimpleMecanumDrive";
    public static final String STOPPED = "stopped";
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;
    private double drive;
    private double strafe;
    private double twist;

    public int driveDir = 1;
    DButton dirSwitch = new DButton();

    final double maxDriveSpeed = 0.8;
    double driveSpeed = maxDriveSpeed;

    public SimpleMecanumDrive(DcMotor frontLeft, DcMotor frontRight,
                              DcMotor backLeft, DcMotor backRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void init(Gamepad driveGamepad) {
        // Mecanum drive is controlled with three axes: drive (front-and-back),
        // strafe (left-and-right), and twist (rotating the whole chassis).
        drive = driveGamepad.left_stick_y * driveDir * driveSpeed;
        strafe = -driveGamepad.left_stick_x * driveDir * driveSpeed;
        twist = 0.5 * -driveGamepad.right_stick_x * driveSpeed;
    }

    public void hold() {
        return; // not supported
    }

    public void run(Gamepad driveGamepad, Telemetry telemetry) {
        init(driveGamepad);
        dirSwitch.update(driveGamepad.left_bumper);

        if(dirSwitch.pressed())
            driveDir = -driveDir;

        double[] speeds = {
                (drive + strafe + twist),
                (drive - strafe - twist),
                (drive - strafe + twist),
                (drive + strafe - twist)
        };

        //driveSpeed = maxDriveSpeed * (1 - driveGamepad.right_trigger);
        driveSpeed = driveGamepad.right_bumper ? 0.5 : maxDriveSpeed;

        // Because we are adding vectors and motors only take values between
        // [-1,1] we may need to normalize them.

        // Loop through all values in the speeds[] array and find the greatest
        // *magnitude*.  Not the greatest velocity.
        double max = Math.abs(speeds[FL_SPEED_IDX]);
        for (double speed : speeds) {
            if (max < Math.abs(speed)) {
                max = Math.abs(speed);
            }
        }

        // If and only if the maximum is outside of the range we want it to be,
        // normalize all the other speeds based on the given speed value.
        if (max > 1) {
            for (int i = 0; i < speeds.length; i++) {
                speeds[i] *= (1 / max);
            }
        }

        // apply the calculated values to the motors.
        frontLeft.setPower(speeds[FL_SPEED_IDX]);
        frontRight.setPower(speeds[FR_SPEED_IDX]);
        backLeft.setPower(speeds[BL_SPEED_IDX]);
        backRight.setPower(speeds[BR_SPEED_IDX]);

        telemetry.addData(SUBSYSTEM_NAME + " FL speed", speeds[FL_SPEED_IDX]);
        telemetry.addData(SUBSYSTEM_NAME + " FR speed", speeds[FR_SPEED_IDX]);
        telemetry.addData(SUBSYSTEM_NAME + " BL speed", speeds[BL_SPEED_IDX]);
        telemetry.addData(SUBSYSTEM_NAME + " BR speed", speeds[BR_SPEED_IDX]);
    }

    public void stop(Telemetry telemetry) {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        telemetry.addData(SUBSYSTEM_NAME, STOPPED);
    }
}
