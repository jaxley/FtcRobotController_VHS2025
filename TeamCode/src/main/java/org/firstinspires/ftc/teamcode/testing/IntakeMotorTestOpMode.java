package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
@TeleOp
public class IntakeMotorTestOpMode extends LinearOpMode {
    final double testCPR = 28; //*13.7; // speed at output shaft—change for motor model

    public void runOpMode() {
        boolean lastMovement = false, someButtonWasPressed = false;
        double desiredMotorSpeed = 0;
        DcMotorEx testMotor = hardwareMap.get(DcMotorEx.class, "testmotor");


        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        testMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Running");

            lastMovement = someButtonWasPressed;
            someButtonWasPressed = gamepad1.a || gamepad1.y;

            // if someButtonWasPressed, we need to:
            // 1. based on which button, change desired motor speed up or down
            // ONLY change the speed ONCE when a button was pressed
            if (someButtonWasPressed && !lastMovement) {
                // telemetry say whether increasing or decreasing
                String buttonPressed = gamepad1.a ? "A" : "Y";
                telemetry.addData("ButtonPressed", "Button " + buttonPressed + " was pressed");
                double speedChange = gamepad1.a ? -50 : 50;
                desiredMotorSpeed = desiredMotorSpeed + speedChange;
            }

            testMotor.setVelocity(desiredMotorSpeed*testCPR/60);
            telemetry.addData("Speed:", testMotor.getVelocity()*60/testCPR);
            telemetry.addData("target speed:", desiredMotorSpeed);
            telemetry.update();

        }
    }

}