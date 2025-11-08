package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public interface IMecanumDrive {
    public void run(Gamepad driveGamepad, Telemetry telemetry);

    public void stop(Telemetry telemetry);
    public void hold();
}
