package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.utils.DButton;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;
public class FlowerCollection {
    private final Servo collectServo;

    double collectUpPos = 0;
    double collectDownPos = 0;

    public FlowerCollection(Servo collectServo) {
        this.collectServo = collectServo;
    }

    boolean servoDown = false;

    boolean upServoCalib = true; // Shows whether the servo calibration is on the up or down position

    DButton collectButton = new DButton();

    DButton dpadLeft = new DButton();
    DButton dpadRight = new DButton();

    DButton dpadB = new DButton();

    public void run(Gamepad gamepad, TelemetryMirror telemetryMirror) {
        collectButton.update(gamepad.y);
        telemetryMirror.addData("collectServo",collectServo.getPosition());
        if (collectButton.pressed() && !servoDown) {
            collectServo.setPosition(collectDownPos);
            servoDown = true;
        } else if (collectButton.pressed() && servoDown){
            collectServo.setPosition(collectUpPos);
        }

        dpadB.update(gamepad.b);
        if (dpadB.pressed() && upServoCalib) {
            upServoCalib = false;
        } else if (dpadB.pressed() && !upServoCalib) {
            upServoCalib = true;
        }

        dpadLeft.update(gamepad.dpad_left);
        dpadRight.update(gamepad.dpad_right);
        if (dpadLeft.pressed() && upServoCalib) {
            collectUpPos = collectUpPos - 0.05;
        } else if (dpadRight.pressed() && upServoCalib) {
            collectUpPos = collectUpPos + 0.05;
        } else if (dpadLeft.pressed() && !upServoCalib) {
            collectDownPos = collectDownPos - 0.05;
        } else if (dpadRight.pressed() && !upServoCalib) {
            collectDownPos = collectDownPos +0.05;
        }
    }
}
