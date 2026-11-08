package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

public class Intake {
    private final DcMotor motor;
    private final DcMotor intakeAssistant;
    private boolean intakeRunning = false;
    final double intakeFwdPower = 0.8;
    final double intakeRevPower = -0.2;
    final double triggerDZ = 0.25;

    public Intake(DcMotor intakeBase, DcMotor intakeAssistant) {
        this.motor = intakeBase;
        this.intakeAssistant = intakeAssistant;
    }


    public void run(Gamepad gamepad, TelemetryMirror telemetryMirror) {
        telemetryMirror.addData("Intake", "started");

        intakeRunning = (gamepad.right_trigger > triggerDZ);
        telemetryMirror.addData("Intake running", intakeRunning);

        if (intakeRunning) {
            loadBallToShooter(telemetryMirror);
        } else if (gamepad.right_bumper) {
            momentaryReverse(telemetryMirror);
        } else {
            stop(telemetryMirror);
        }
    }

    public void stop(TelemetryMirror telemetryMirror) {
        motor.setPower(0);
        intakeAssistant.setPower(0);
        telemetryMirror.addData("Intake", "stopped");
    }

    public void loadBallToShooter(TelemetryMirror telemetryMirror) {
        intakeAssistant.setPower(intakeFwdPower);
        motor.setPower(intakeFwdPower);
    }

    public void momentaryReverse(TelemetryMirror telemetryMirror) {
        intakeAssistant.setPower(intakeRevPower);
        motor.setPower(intakeRevPower);
    }
}
