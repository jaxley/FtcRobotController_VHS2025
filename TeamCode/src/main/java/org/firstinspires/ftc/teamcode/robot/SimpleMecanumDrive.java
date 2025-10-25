package org.firstinspires.ftc.teamcode.robot;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

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
public class SimpleMecanumDrive {
    private final Gamepad driveGamepad;
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;
    private final double drive;
    private final double strafe;
    private final double twist;

    final double maxDriveSpeed = 0.8;
    double driveSpeed = maxDriveSpeed;
    private Telemetry telemetry;

    public SimpleMecanumDrive(Gamepad driveGamepad, DcMotor frontLeft, DcMotor frontRight,
                              DcMotor backLeft, DcMotor backRight) {
        this.driveGamepad = driveGamepad;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;

        // Mecanum drive is controlled with three axes: drive (front-and-back),
        // strafe (left-and-right), and twist (rotating the whole chassis).
        this.drive = this.driveGamepad.left_stick_y;
        this.strafe = -this.driveGamepad.left_stick_x;
        this.twist = -this.driveGamepad.right_stick_x;
    }

    public void setTelemetry(Telemetry telemetry){
        this.telemetry = telemetry;
    }

    public void run() {

        double[] speeds = {
                (drive + strafe + twist),
                (drive - strafe - twist),
                (drive - strafe + twist),
                (drive + strafe - twist)
        };

        driveSpeed = maxDriveSpeed * (1 - driveGamepad.right_trigger);

        // Because we are adding vectors and motors only take values between
        // [-1,1] we may need to normalize them.

        // Loop through all values in the speeds[] array and find the greatest
        // *magnitude*.  Not the greatest velocity.
        double max = Math.abs(speeds[0]);
        for (double speed : speeds) {
            if (max < Math.abs(speed)) max = Math.abs(speed);
        }

        // If and only if the maximum is outside of the range we want it to be,
        // normalize all the other speeds based on the given speed value.
        if (max > 1) {
            for (int i = 0; i < speeds.length; i++) {
                speeds[i] *= (driveSpeed / max);
            }
        }

        // apply the calculated values to the motors.
        frontLeft.setPower(speeds[0]);
        frontRight.setPower(speeds[1]);
        backLeft.setPower(speeds[2]);
        backRight.setPower(speeds[3]);

        telemetry.addData("MecanumDrive speeds", speeds[0]);
    }

    public void stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        telemetry.addData("MecanumDrive", "stopped");
    }
}
