package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.RobotBase;

@TeleOp(name="TeleOp", group="Iterative Opmode")
public class MecanumDriveTeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        RobotBase robotBase = RobotBase.getInstance(hardwareMap);

        telemetry.addData("Code Version", BuildConfig.VERSION_NAME);
        telemetry.addData("Code Build Time", BuildConfig.APP_BUILD_TIME);
        telemetry.addData("TeleOp", "initialized");
        telemetry.update();

        waitForStart();

        // main loop
        while(opModeIsActive()) {
            robotBase.run(gamepad1, gamepad2, telemetry);
            telemetry.update();
        }
    }
}