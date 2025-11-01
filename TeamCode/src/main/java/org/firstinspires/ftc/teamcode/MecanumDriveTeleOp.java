package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.RobotBase;

@TeleOp(name= MecanumDriveTeleOp.TELE_OP, group="Iterative Opmode")
public class MecanumDriveTeleOp extends LinearOpMode {

    public static final String TELE_OP = "TeleOp";

    @Override
    public void runOpMode() {
        RobotBase robotBase = RobotBase.getInstance(hardwareMap);

        telemetry.addData(TELE_OP, "initialized");
        telemetry.update();

        waitForStart();

        // main loop
        while(opModeIsActive()) {
            robotBase.run(gamepad1, gamepad2, telemetry);
            telemetry.update();
        }
    }
}