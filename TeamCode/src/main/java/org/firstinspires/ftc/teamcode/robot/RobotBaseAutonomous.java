package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class RobotBaseAutonomous extends RobotBase {
    private static RobotBaseAutonomous INSTANCE;

    protected RobotBaseAutonomous(HardwareMap hardwareMap) {

        super(hardwareMap, false);
    }

    public static RobotBaseAutonomous getInstance(HardwareMap hardwareMap, Telemetry telemetry) {
        if (INSTANCE == null) {
            INSTANCE = new RobotBaseAutonomous(hardwareMap);
        }
        return INSTANCE;
    }
}
