package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.RobotBase;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

@TeleOp(name= MecanumDriveTeleOp.TELE_OP, group="Iterative Opmode")
public class MecanumDriveTeleOp extends PanelsOpMode {

    public static final String TELE_OP = "TeleOp";

    private static final boolean USE_PANELS = true;

    private RobotBase robotBase;

    @Override
    public void init() {
        telemetryMirror = new TelemetryMirror(telemetry, USE_PANELS);

        telemetryMirror.addData("Code Version", BuildConfig.VERSION_NAME);
        telemetryMirror.addData("Code Build Time", BuildConfig.APP_BUILD_TIME);
        telemetryMirror.addData(TELE_OP, "initialized");
        telemetryMirror.update();

        robotBase = RobotBase.getInstance(hardwareMap);
        follower = robotBase.getFollower();
        super.init();
    }

    @Override
    public void start() {
        telemetryMirror.setTelemetry(telemetry);
    }

    @Override
    public void loop() {
        follower.update();
        robotBase.run(gamepad1, gamepad2, telemetryMirror);
        telemetryMirror.addData("x", follower.pose().x());
        telemetryMirror.addData("y", follower.pose().y());
        telemetryMirror.addData("heading", Math.toDegrees(follower.pose().heading()));
        telemetryMirror.update();
        super.loop();
    }

    @Override
    public void stop() {
        if (robotBase != null) {
            robotBase.stop(telemetryMirror);
        }
        // Drop the singleton so the next OpMode run rebuilds hardware from a fresh
        // HardwareMap instead of reusing devices from this (now closing) one.
        RobotBase.reset();
    }
}
