package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * This is an example minimal implementation of the mecanum drivetrain
 * for demonstration purposes.  Not tested and not guaranteed to be bug free.
 *
 * @author Brandon Gong
 */
@TeleOp(name="TeleOp", group="Iterative Opmode")
public class MecanumDriveTeleOp extends LinearOpMode {

    /*
     * The mecanum drivetrain involves four separate motors that spin in
     * different directions and different speeds to produce the desired
     * movement at the desired speed.
     */

    // declare and initialize four DcMotors.
    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;
    private DcMotor intake;
    private DcMotorEx flywheel;
    private Servo fireServo;

    final double intakeFwdPower = -0.8;
    final double intakeRevPower = 0.2;
    final double triggerDZ = 0.25;

    final double flywheelSpeed = -6000; // RPM
    final double FWRPM2CPS = 28/60;


    final double fireDownPos = 0;
    final double fireUpPos= 0.5;
    final double firePeriod = 800; // ms
    final double maxDriveSpeed = 0.8;
    double driveSpeed = maxDriveSpeed;
    double fireTime = 0;

    //control vars
    boolean intakeRunning = false;
    boolean flywheelRunning = false;

    DButton fireButton = new DButton();


    @Override
    public void runOpMode() {
        // Name strings must match up with the config on the Robot Controller
        // app.
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake = hardwareMap.get(DcMotor.class, "intake");
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");

        fireServo = hardwareMap.get(Servo.class, "launch servo");
        telemetry.addData("Status", "initialized");
        telemetry.update();


        waitForStart();

        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        double lastMillis = 0;

        // main loop
        while(opModeIsActive()) {
            lastMillis = getRuntime();

            intakeRunning = (gamepad2.right_trigger > triggerDZ);
            if (intakeRunning) {
                intake.setPower(intakeFwdPower);
            } else if (gamepad2.right_bumper) {
                intake.setPower(intakeRevPower);
            } else {
                intake.setPower(0);
            }

            flywheelRunning = (gamepad2.left_trigger > triggerDZ);
            if (flywheelRunning) {
                flywheel.setVelocity(gamepad2.left_trigger * flywheelSpeed * FWRPM2CPS);
            } else {
                flywheel.setPower(0);
            }

            fireButton.update(gamepad2.x);
            if (fireButton.pressed()) {
                fireTime = firePeriod;
                fireServo.setPosition(fireUpPos);
            }
            fireTime -= getRuntime() - lastMillis;
            // Mecanum drive is controlled with three axes: drive (front-and-back),
            // strafe (left-and-right), and twist (rotating the whole chassis).
            double drive = gamepad1.left_stick_y;
            double strafe = -gamepad1.left_stick_x;
            double twist = -gamepad1.right_stick_x;

            double[] speeds = {
                    (drive + strafe + twist),
                    (drive - strafe - twist),
                    (drive - strafe + twist),
                    (drive + strafe - twist)
            };

            driveSpeed = maxDriveSpeed * (1 - gamepad1.right_trigger);

            // Because we are adding vectors and motors only take values between
            // [-1,1] we may need to normalize them.

            // Loop through all values in the speeds[] array and find the greatest
            // *magnitude*.  Not the greatest velocity.
            double max = Math.abs(speeds[0]);
            for (int i = 0; i < speeds.length; i++) {
                if (max < Math.abs(speeds[i])) max = Math.abs(speeds[i]);
            }

            // If and only if the maximum is outside of the range we want it to be,
            // normalize all the other speeds based on the given speed value.
            if (max > 1) {
                for (int i = 0; i < speeds.length; i++) speeds[i] *= (driveSpeed / max);
            }

            // apply the calculated values to the motors.
            frontLeft.setPower(speeds[0]);
            frontRight.setPower(speeds[1]);
            backLeft.setPower(speeds[2]);
            backRight.setPower(speeds[3]);

            telemetry.addData("speeds", speeds[0]);
            telemetry.update();
        }
    }
}