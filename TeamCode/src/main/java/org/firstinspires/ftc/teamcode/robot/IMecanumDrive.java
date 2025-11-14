package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public interface IMecanumDrive {
    public void run(Gamepad driveGamepad, Telemetry telemetry);

    public void stop(Telemetry telemetry);
    public void hold();

    public DcMotor getFrontLeft();
    public DcMotor getFrontRight();
    public DcMotor getBackLeft();
    public DcMotor getBackRight();
}
