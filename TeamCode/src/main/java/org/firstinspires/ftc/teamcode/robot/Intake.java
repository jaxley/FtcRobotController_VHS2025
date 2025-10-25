package org.firstinspires.ftc.teamcode.robot;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

public class Intake {
    private final Gamepad gamepad;
    private final DcMotor motor;
    private boolean intakeRunning = false;
    final double intakeFwdPower = -0.8;
    final double intakeRevPower = 0.2;
    final double triggerDZ = 0.25;

    public Intake(Gamepad gamepad, DcMotor motor) {
        this.gamepad = gamepad;
        this.motor = motor;
    }

    public void run() {
        telemetry.addData("Intake", "started");

        intakeRunning = (gamepad.right_trigger > triggerDZ);
        telemetry.addData("Intake running", intakeRunning);

        if (intakeRunning) {
            motor.setPower(intakeFwdPower);
        } else if (gamepad.right_bumper) {
            motor.setPower(intakeRevPower);
        } else {
            stop();
        }
    }

    public void stop() {
        motor.setPower(0);
        telemetry.addData("Intake", "stopped");
    }
}
