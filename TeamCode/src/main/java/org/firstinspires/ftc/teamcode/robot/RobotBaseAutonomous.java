package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

public class RobotBaseAutonomous extends RobotBase {
    private static RobotBaseAutonomous INSTANCE;

    protected RobotBaseAutonomous(HardwareMap hardwareMap) {

        super(hardwareMap, false);
    }

    public static RobotBaseAutonomous getInstance(HardwareMap hardwareMap, TelemetryMirror telemetryMirror) {
        if (INSTANCE == null) {
            INSTANCE = new RobotBaseAutonomous(hardwareMap);
        }
        return INSTANCE;
    }

    /**
     * Discards the singleton so the next OpMode run rebuilds all hardware from a fresh
     * HardwareMap; see {@link RobotBase#reset()} for why device objects must not be
     * reused across OpMode runs.
     */
    public static void reset() {
        INSTANCE = null;
    }
}
