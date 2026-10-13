package org.firstinspires.ftc.teamcode.utils;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Class that can mirror telemetry data between panels and FTC.
 * Why not use JoinedTelemetry? To avoid hard dependencies on panels throughout our code.
 * This allows us to disable panels entirely if we want. Or in the future, change to a different
 * dashboard entirely.
 */
public class TelemetryMirror {
    private Telemetry ftcTelemetry =  null;


    public TelemetryMirror(Telemetry telemetry, boolean usePanels) {
        setTelemetry(telemetry);
    }

    public void setTelemetry(Telemetry telemetry) {
        ftcTelemetry = telemetry;
    }

    public void addData(String key, String value) {
        ftcTelemetry.addData(key, value);
    }

    public void addData(String key, Object value) {
        ftcTelemetry.addData(key, value);
    }

    public void addLine(String line) {
        ftcTelemetry.addLine(line);
    }

    public void update() {
        ftcTelemetry.update();
    }
}
