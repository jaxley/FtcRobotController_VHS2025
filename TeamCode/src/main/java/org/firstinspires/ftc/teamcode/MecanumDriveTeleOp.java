package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.robot.Constants.Motor;
import static org.firstinspires.ftc.teamcode.robot.Constants.Wheel;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robot.Intake;
import org.firstinspires.ftc.teamcode.robot.RobotBase;
import org.firstinspires.ftc.teamcode.robot.Shooter;
import org.firstinspires.ftc.teamcode.robot.SimpleMecanumDrive;

@TeleOp(name="TeleOp", group="Iterative Opmode")
public class MecanumDriveTeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        RobotBase robotBase = RobotBase.getInstance(hardwareMap);

        telemetry.addData("Status", "initialized");
        telemetry.update();

        waitForStart();

        // main loop
        while(opModeIsActive()) {
            robotBase.run(gamepad1, gamepad2, telemetry);
            telemetry.update();
        }
    }
}