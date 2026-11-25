package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.utils.DButton;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

public class Limelight {
    public static final String SUBSYSTEM_NAME = "Limelight";

    final double KP_TURN = 0.035;
    final double MIN_TURN_POWER = 0.15;
    final double MAX_TURN_POWER = 0.45;
    final double TARGET_TOLERANCE_DEG = 1.2;
    final double SEARCH_ROTATION_SPEED = 0.32;

    private final Limelight3A limelight;
    private boolean initialized = false;
    private final DButton targetingButton = new DButton();
    private final Gamepad trackingGamepad = new Gamepad();

    public Limelight(HardwareMap hardwareMap) {
        this.limelight = hardwareMap.get(Limelight3A.class, RobotConstants.Camera.LIMELIGHT);
    }

    public void run(Gamepad gamepad, TelemetryMirror telemetryMirror) {
        if (!initialized) {
            init(telemetryMirror);
            return;
        }

        telemetryMirror.addData(SUBSYSTEM_NAME, "started");

        targetingButton.update(gamepad.y);
        telemetryMirror.addData("Limelight targeting active", targetingButton.isHeld());

        if (targetingButton.isHeld()) {
            calculateTargetingVectors(telemetryMirror);
        } else {
            stop(telemetryMirror);
        }
    }

    public void stop(TelemetryMirror telemetryMirror) {
        trackingGamepad.left_stick_x = 0.0f;
        trackingGamepad.left_stick_y = 0.0f;
        trackingGamepad.right_stick_x = 0.0f;
        trackingGamepad.dpad_up = false;

        telemetryMirror.addData(SUBSYSTEM_NAME, "stopped");
    }

    public void shutdownCamera() {
        if (limelight != null && initialized) {
            limelight.stop();
        }
    }

    private void init(TelemetryMirror telemetryMirror) {
        if (limelight == null) return;
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();
        initialized = true;
        telemetryMirror.addData(SUBSYSTEM_NAME, "initialized");
    }

    private void calculateTargetingVectors(TelemetryMirror telemetryMirror) {
        trackingGamepad.left_stick_x = 0.0f;
        trackingGamepad.left_stick_y = 0.0f;
        trackingGamepad.dpad_up = true;

        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            aimAt(result.getTx(), telemetryMirror);
        } else {
            searchForTarget(telemetryMirror);
        }
    }

    private void aimAt(double tx, TelemetryMirror telemetryMirror) {
        telemetryMirror.addData(SUBSYSTEM_NAME + " Tracking", "Target Found! TX: " + tx);

        if (Math.abs(tx) < TARGET_TOLERANCE_DEG) {
            telemetryMirror.addData(SUBSYSTEM_NAME + " Lock Status", "Target Centered");
            trackingGamepad.right_stick_x = 0.0f;
            return;
        }

        double turnPower = clampTurnPower(tx * KP_TURN);
        setTurnPower(turnPower);
    }

    private void searchForTarget(TelemetryMirror telemetryMirror) {
        telemetryMirror.addData(SUBSYSTEM_NAME + " Tracking", "Lost Sight - Hunting...");
        setTurnPower(SEARCH_ROTATION_SPEED);
    }

    private double clampTurnPower(double turnPower) {
        if (turnPower > 0) {
            turnPower = Math.max(turnPower, MIN_TURN_POWER);
        } else {
            turnPower = Math.min(turnPower, -MIN_TURN_POWER);
        }
        return Math.max(-MAX_TURN_POWER, Math.min(MAX_TURN_POWER, turnPower));
    }

    private void setTurnPower(double turnPower) {
        trackingGamepad.right_stick_x = (float) (-turnPower / PedroPathingMecanumDrive.TURN_MAX_SPEED);
    }

    public boolean isTargetingActive() {
        return targetingButton.isHeld();
    }

    public Gamepad getTrackingGamepad() {
        return trackingGamepad;
    }
}
