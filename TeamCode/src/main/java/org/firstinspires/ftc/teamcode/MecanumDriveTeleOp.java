package org.firstinspires.ftc.teamcode;

import com.bylazar.gamepad.PanelsGamepad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.robot.RobotBase;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

@TeleOp(name= MecanumDriveTeleOp.TELE_OP, group="Iterative Opmode")
public class MecanumDriveTeleOp extends LinearOpMode {

    public static final String TELE_OP = "TeleOp";

    private RobotBase robotBase;
    private boolean usePanels = false;
    private TelemetryMirror telemetryMirror;

    @Override
    public void waitForStart() {
        super.waitForStart();
        robotBase = RobotBase.getInstance(hardwareMap);
    }

    @Override
    public void runOpMode() {
        telemetryMirror = new TelemetryMirror(telemetry, usePanels);

        telemetryMirror.addData("Code Version", BuildConfig.VERSION_NAME);
        telemetryMirror.addData("Code Build Time", BuildConfig.APP_BUILD_TIME);
        telemetryMirror.addData(TELE_OP, "initialized");
        telemetryMirror.update();

        waitForStart();

        Gamepad driverGamepad = gamepad1;
        if (usePanels) {
            driverGamepad = PanelsGamepad.INSTANCE.getFirstManager().asCombinedFTCGamepad(gamepad1);
        }

        // main loop
        while(opModeIsActive()) {
            telemetryMirror.setTelemetry(telemetry);
            robotBase.run(driverGamepad, gamepad2, telemetryMirror);
            telemetryMirror.update();
        }
    }
}