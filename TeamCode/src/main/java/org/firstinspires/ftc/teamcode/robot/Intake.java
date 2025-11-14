package org.firstinspires.ftc.teamcode.robot;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake {
    private final DcMotor motor;
    private final DcMotor intakeAssistant;
    private boolean intakeRunning = false;
    final double intakeFwdPower = -0.8;
    final double intakeRevPower = 0.2;
    final double triggerDZ = 0.25;

    public Intake(DcMotor intakeBase, DcMotor intakeAssistant) {
        this.motor = intakeBase;
        this.intakeAssistant = intakeAssistant;
    }


    public void run(Gamepad gamepad, Telemetry telemetry) {
        telemetry.addData("Intake", "started");

        intakeRunning = (gamepad.right_trigger > triggerDZ);
        telemetry.addData("Intake running", intakeRunning);

        if (intakeRunning) {
            loadBallToShooter(telemetry);
        } else if (gamepad.right_bumper) {
            momentaryReverse(telemetry);
        } else {
            stop(telemetry);
        }
    }

    public void stop(Telemetry telemetry) {
        motor.setPower(0);
        intakeAssistant.setPower(0);
        telemetry.addData("Intake", "stopped");
    }

    public void loadBallToShooter(Telemetry telemetry) {
        intakeAssistant.setPower(-intakeFwdPower);
        motor.setPower(intakeFwdPower);
    }

    public void momentaryReverse(Telemetry telemetry) {
        intakeAssistant.setPower(-intakeRevPower);
        motor.setPower(intakeRevPower);
    }
}
