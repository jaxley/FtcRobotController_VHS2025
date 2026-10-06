package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.utils.DButton;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

public class Limelight {
    public static final String SUBSYSTEM_NAME = "Limelight";
    private final Limelight3A limelight;

    private boolean initialized = false;
    private final DButton targetingButton = new DButton();

    // An isolated gamepad container to safely pass movement instructions to the drive loop
    private final Gamepad trackingGamepad = new Gamepad();

    // Guidance adjustments
    private final double KP_TURN = 0.035;
    private final double MIN_TURN_POWER = 0.15;
    private final double TARGET_TOLERANCE_DEG = 1.2;
    private final double SEARCH_ROTATION_SPEED = 0.32;

    public Limelight(HardwareMap hardwareMap) {
        this.limelight = hardwareMap.get(Limelight3A.class, "limelight");
    }

    private void init(TelemetryMirror telemetryMirror) {
        if (limelight == null) return;
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();
        initialized = true;
        telemetryMirror.addData(SUBSYSTEM_NAME, "initialized");
    }

    public void run(Gamepad gamepad, TelemetryMirror telemetryMirror) {
        if (!initialized) {
            init(telemetryMirror);
            return;
        }
        telemetryMirror.addData(SUBSYSTEM_NAME, "started");

        // Dynamically update state utilizing your team's custom DButton class
        targetingButton.update(gamepad.y);
        telemetryMirror.addData("Limelight targeting active", targetingButton.isPressed());

        if (targetingButton.isPressed()) {
            calculateTargetingVectors(telemetryMirror);
        } else {
            stop(telemetryMirror);
        }
    }

    public void stop(TelemetryMirror telemetryMirror) {
        // Safe reset: clear all stick vectors completely when button is released
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

    private void calculateTargetingVectors(TelemetryMirror telemetryMirror) {
        // Enforce 0 translation movement to guarantee the robot doesn't slide while tracking
        trackingGamepad.left_stick_x = 0.0f;
        trackingGamepad.left_stick_y = 0.0f;

        // CRITICAL PEDRO PATHING BYPASS: Set dpad_up to true inside this isolated container.
        // This ensures Pedro's internal getDriveSpeed() calculates max power instead of falling to 0.
        trackingGamepad.dpad_up = true;

        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            double tx = result.getTx();
            telemetryMirror.addData(SUBSYSTEM_NAME + " Tracking", "Target Found! TX: " + tx);

            if (Math.abs(tx) < TARGET_TOLERANCE_DEG) {
                telemetryMirror.addData(SUBSYSTEM_NAME + " Lock Status", "Target Centered");
                trackingGamepad.right_stick_x = 0.0f;
                return;
            }

            double turnPower = tx * KP_TURN;

            if (turnPower > 0) {
                turnPower = Math.max(turnPower, MIN_TURN_POWER);
            } else {
                turnPower = Math.min(turnPower, -MIN_TURN_POWER);
            }

            // Cap tracking acceleration speed parameters
            turnPower = Math.max(-0.45, Math.min(0.45, turnPower));

            // Reverse-scales the TURN_MAX_SPEED factor inside Pedro Pathing so the full power reaches the wheels
            trackingGamepad.right_stick_x = (float) (-turnPower / PedroPathingMecanumDrive.TURN_MAX_SPEED);
        } else {
            // SPEC COMPLIANCE FALLBACK: If no targets are visible, continue rotating to search the field
            telemetryMirror.addData(SUBSYSTEM_NAME + " Tracking", "Lost Sight - Hunting...");
            trackingGamepad.right_stick_x = (float) (-SEARCH_ROTATION_SPEED / PedroPathingMecanumDrive.TURN_MAX_SPEED);
        }
    }

    public boolean isTargetingActive() {
        return targetingButton.isPressed();
    }

    public Gamepad getTrackingGamepad() {
        return trackingGamepad;
    }
}
