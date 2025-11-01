package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class RobotBaseAutonomous extends RobotBase {
    private static RobotBaseAutonomous INSTANCE;

    protected RobotBaseAutonomous(HardwareMap hardwareMap) {
        super(hardwareMap);
    }

    public static RobotBaseAutonomous getInstance(HardwareMap hardwareMap, Telemetry telemetry) {
        if (INSTANCE == null) {
            INSTANCE = new RobotBaseAutonomous(hardwareMap)
                    .withAutonomousMode(telemetry);
        }
        return INSTANCE;
    }

    private boolean TELEOP_MODE = true;
    public RobotBaseAutonomous withAutonomousMode(Telemetry telemetry) {
        enableAutonomousMode(telemetry);
        return this;
    }

    private void enableAutonomousMode(Telemetry telemetry) {
        TELEOP_MODE = false;
        shooter = shooter.withAutonomousMode(telemetry);
        intake = intake.withAutonomousMode(telemetry);
    }
}
