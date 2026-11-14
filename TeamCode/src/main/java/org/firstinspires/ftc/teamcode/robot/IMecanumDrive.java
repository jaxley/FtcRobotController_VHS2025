package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

public interface IMecanumDrive {
    public void run(Gamepad driveGamepad, TelemetryMirror telemetry);

    public void stop(TelemetryMirror telemetry);
    public void hold();

    public DcMotor getFrontLeft();
    public DcMotor getFrontRight();
    public DcMotor getBackLeft();
    public DcMotor getBackRight();

    /**
     * Zeros the given drive motors. Null motors are skipped; each motor gets its
     * own try/catch so one failure cannot skip the rest.
     */
    static void stopMotors(DcMotor... wheels) {
        for (DcMotor wheel : wheels) {
            if (wheel == null) {
                continue;
            }
            try {
                wheel.setPower(0);
            } catch (Exception e) {
                RobotLog.ww("IMecanumDrive", "failed to zero drive motor: " + e);
            }
        }
    }

    /**
     * Zeros the drive via the wheel getters. This is the drive failsafe for STOP /
     * driver-station crash.
     */
    default void stopMotors() {
        stopMotors(getFrontLeft(), getFrontRight(), getBackLeft(), getBackRight());
    }
}
