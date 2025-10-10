package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class MotorTestOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        while(opModeIsActive()) {
            throw new RuntimeException("Boom");
        }
    }
}
