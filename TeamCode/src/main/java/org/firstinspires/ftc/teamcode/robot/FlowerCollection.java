package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.utils.DButton;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;
public class FlowerCollection {
    private final Servo collectServo;

    double collectUpPos = 0.8;
    double collectDownPos = 0.2;

    public FlowerCollection(Servo collectServo) {
        this.collectServo = collectServo;
    }

    boolean servoDown = false;

    boolean upServoCalib = true; // Shows whether the servo calibration is on the up or down position

    boolean initialized = false;

    DButton collectButton = new DButton();

    DButton dpadLeft = new DButton();
    DButton dpadRight = new DButton();

    DButton dpadB = new DButton();

    private void init(TelemetryMirror telemetryMirror) {
        collectServo.setPosition(collectUpPos);
    }

    public void run(Gamepad gamepad, TelemetryMirror telemetryMirror) {
        if (!initialized) {
            init(telemetryMirror);
        }
        collectButton.update(gamepad.a);
        telemetryMirror.addData("collectServo", collectServo.getPosition());
        if (collectButton.pressed() && !servoDown) {
            collectServo.setPosition(collectDownPos);
            servoDown = true;
            telemetryMirror.addData("servoDown", "true");
        } else if (collectButton.pressed() && servoDown){
            collectServo.setPosition(collectUpPos);
            servoDown = false;
            telemetryMirror.addData("servoDown", "false");
        }

        dpadB.update(gamepad.b);
        if (dpadB.pressed() && upServoCalib) {
            upServoCalib = false;
            telemetryMirror.addData("upServoCalib", "false");
        } else if (dpadB.pressed() && !upServoCalib) {
            upServoCalib = true;
            telemetryMirror.addData("upServoCalib", "true");
        }

        dpadLeft.update(gamepad.dpad_left);
        dpadRight.update(gamepad.dpad_right);
        if (dpadLeft.pressed() && upServoCalib) {
            collectUpPos = collectUpPos - 0.05;
            telemetryMirror.addData("collectServo",collectServo.getPosition());
        } else if (dpadRight.pressed() && upServoCalib) {
            collectUpPos = collectUpPos + 0.05;
            telemetryMirror.addData("collectServo",collectServo.getPosition());
        } else if (dpadLeft.pressed() && !upServoCalib) {
            collectDownPos = collectDownPos - 0.05;
            telemetryMirror.addData("collectServo",collectServo.getPosition());
        } else if (dpadRight.pressed() && !upServoCalib) {
            collectDownPos = collectDownPos +0.05;
            telemetryMirror.addData("collectServo",collectServo.getPosition());
        }
    }

    public void stop(TelemetryMirror telemetry) {
        collectServo.setPosition(collectUpPos);
    }
}
