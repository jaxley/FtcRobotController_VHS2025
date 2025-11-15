package org.firstinspires.ftc.teamcode;

import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.panels.Panels;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.robot.RobotBase;

@TeleOp(name= MecanumDriveTeleOp.TELE_OP, group="Iterative Opmode")
public class MecanumDriveTeleOp extends LinearOpMode {

    public static final String TELE_OP = "TeleOp";

    private JoinedTelemetry joinedTelemetry;
    private RobotBase robotBase;
    private boolean usePanels = false;

    @Override
    public void waitForStart() {
        super.waitForStart();
        robotBase = RobotBase.getInstance(hardwareMap);
    }

    @Override
    public void runOpMode() {
        if (usePanels) {
            joinedTelemetry = new JoinedTelemetry(telemetry, PanelsTelemetry.INSTANCE.getFtcTelemetry());
        }

        telemetry.addData("Code Version", BuildConfig.VERSION_NAME);
        telemetry.addData("Code Build Time", BuildConfig.APP_BUILD_TIME);
        telemetry.addData(TELE_OP, "initialized");
        telemetry.update();

        waitForStart();

        Gamepad driverGamepad = gamepad1;
        if (usePanels) {
            driverGamepad = PanelsGamepad.INSTANCE.getFirstManager().asCombinedFTCGamepad(gamepad1);
        }

        // main loop
        while(opModeIsActive()) {
            robotBase.run(driverGamepad, gamepad2, joinedTelemetry);
            telemetry.update();
        }
    }
}