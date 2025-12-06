package org.firstinspires.ftc.teamcode.utils;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Class that can mirror telemetry data between panels and FTC.
 * Why not use JoinedTelemetry? To avoid hard dependencies on panels throughout our code.
 * This allows us to disable panels entirely if we want. Or in the future, change to a different
 * dashboard entirely.
 */
public class TelemetryMirror {
    private TelemetryManager panelsTelemetry = null;
    private Telemetry ftcTelemetry =  null;

    private boolean usePanels() {
        return panelsTelemetry != null;
    }

    public TelemetryMirror(Telemetry telemetry, boolean usePanels) {
        if (usePanels) {
            panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        }
        setTelemetry(telemetry);
    }

    public void setTelemetry(Telemetry telemetry) {
        ftcTelemetry = telemetry;
    }

    public void addData(String key, String value) {
        ftcTelemetry.addData(key, value);

        if (usePanels()) {
            panelsTelemetry.addData(key, value);
        }
    }

    public void addData(String key, Object value) {
        ftcTelemetry.addData(key, value);

        if (usePanels()) {
            panelsTelemetry.addData(key, value);
        }
    }

    public void addLine(String line) {
        ftcTelemetry.addLine(line);

        if (usePanels()) {
            panelsTelemetry.addLine(line);
        }
    }

    public void update() {
        ftcTelemetry.update();

        if (usePanels()) {
            panelsTelemetry.update();
        }
    }
}
