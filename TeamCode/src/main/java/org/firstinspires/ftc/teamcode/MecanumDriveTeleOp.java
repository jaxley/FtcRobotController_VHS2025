package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.robot.Constants.Motor;
import static org.firstinspires.ftc.teamcode.robot.Constants.Wheel;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robot.Intake;
import org.firstinspires.ftc.teamcode.robot.Shooter;
import org.firstinspires.ftc.teamcode.robot.SimpleMecanumDrive;

@TeleOp(name="TeleOp", group="Iterative Opmode")
public class MecanumDriveTeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor frontLeft = hardwareMap.get(DcMotor.class, Wheel.FRONT_LEFT);
        DcMotor frontRight = hardwareMap.get(DcMotor.class, Wheel.FRONT_RIGHT);
        DcMotor backLeft = hardwareMap.get(DcMotor.class, Wheel.BACK_LEFT);
        DcMotor backRight = hardwareMap.get(DcMotor.class, Wheel.BACK_RIGHT);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        SimpleMecanumDrive mecanumDrive =
                new SimpleMecanumDrive(frontLeft, frontRight, backLeft, backRight);
        mecanumDrive.setTelemetry(telemetry);

        Intake intake = new Intake(hardwareMap.get(DcMotor.class, Motor.INTAKE));
        intake.setTelemetry(telemetry);

        Shooter shooter = new Shooter(hardwareMap.get(DcMotorEx.class, Motor.FLYWHEEL),
                hardwareMap.get(Servo.class, Motor.LAUNCH_SERVO));
        shooter.setTelemetry(telemetry);

        telemetry.addData("Status", "initialized");
        telemetry.update();

        waitForStart();

        // main loop
        while(opModeIsActive()) {

            intake.run(gamepad2);
            shooter.run(gamepad2);
            mecanumDrive.run(gamepad1);

            telemetry.update();
        }
    }
}