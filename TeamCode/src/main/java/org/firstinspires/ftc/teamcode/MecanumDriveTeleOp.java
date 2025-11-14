package org.firstinspires.ftc.teamcode;

import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.panels.Panels;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.robot.RobotBase;

@TeleOp(name= MecanumDriveTeleOp.TELE_OP, group="Iterative Opmode")
public class MecanumDriveTeleOp extends LinearOpMode {

    public static final String TELE_OP = "TeleOp";

    @Override
    public void runOpMode() {
        RobotBase robotBase = RobotBase.getInstance(hardwareMap);

        telemetry.addData("Code Version", BuildConfig.VERSION_NAME);
        telemetry.addData("Code Build Time", BuildConfig.APP_BUILD_TIME);
        telemetry.addData(TELE_OP, "initialized");
        telemetry.update();

        waitForStart();

        // main loop
        while(opModeIsActive()) {
            Gamepad panelGamepad = PanelsGamepad.INSTANCE.getFirstManager().asCombinedFTCGamepad(gamepad1);
            robotBase.run(panelGamepad, gamepad2, telemetry);
            telemetry.update();
        }
    }
}