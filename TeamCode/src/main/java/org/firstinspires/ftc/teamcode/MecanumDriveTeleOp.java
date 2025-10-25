package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.robot.Constants.Motors;
import static org.firstinspires.ftc.teamcode.robot.Constants.Wheel;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robot.SimpleMecanumDrive;


@TeleOp(name="TeleOp", group="Iterative Opmode")
public class MecanumDriveTeleOp extends LinearOpMode {

    private DcMotor intake;
    private DcMotorEx flywheel;
    private Servo fireServo;

    final double intakeFwdPower = -0.8;
    final double intakeRevPower = 0.2;
    final double triggerDZ = 0.25;

    final double flywheelSpeed = -6000; // RPM
    final double FWRPM2CPS = (double) 28 /60;


    final double fireDownPos = 0;
    final double fireUpPos= 0.5;
    final double firePeriod = 800; // ms

    double fireTime = 0;

    //control vars
    boolean intakeRunning = false;
    boolean flywheelRunning = false;

    DButton fireButton = new DButton();

    @Override
    public void runOpMode() {
        DcMotor frontLeft = hardwareMap.get(DcMotor.class, Wheel.FRONT_LEFT);
        DcMotor frontRight = hardwareMap.get(DcMotor.class, Wheel.FRONT_RIGHT);
        DcMotor backLeft = hardwareMap.get(DcMotor.class, Wheel.BACK_LEFT);
        DcMotor backRight = hardwareMap.get(DcMotor.class, Wheel.BACK_RIGHT);
        SimpleMecanumDrive mecanumDrive =
                new SimpleMecanumDrive(gamepad1, frontLeft, frontRight, backLeft, backRight);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake = hardwareMap.get(DcMotor.class, Motors.INTAKE);
        flywheel = hardwareMap.get(DcMotorEx.class, Motors.FLYWHEEL);

        fireServo = hardwareMap.get(Servo.class, Motors.LAUNCH_SERVO);
        telemetry.addData("Status", "initialized");
        telemetry.update();

        waitForStart();

        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double lastMillis;

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

            mecanumDrive.run();
            telemetry.update();
        }
    }
}