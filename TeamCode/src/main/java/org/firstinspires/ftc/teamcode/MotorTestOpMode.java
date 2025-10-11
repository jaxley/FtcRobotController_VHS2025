package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.MecanumDriverControlled;
import dev.nextftc.hardware.impl.MotorEx;

@TeleOp
public class MotorTestOpMode extends NextFTCOpMode {
    public MotorTestOpMode() {
        addComponents(BulkReadComponent.INSTANCE, BindingsComponent.INSTANCE);
    }

    /*            DcMotorEx wheel1 = hardwareMap.get(DcMotorEx.class, "wheel1");
                DcMotorEx wheel2 = hardwareMap.get(DcMotorEx.class, "wheel2");
                DcMotorEx wheel3 = hardwareMap.get(DcMotorEx.class, "wheel3");
                DcMotorEx wheel4 = hardwareMap.get(DcMotorEx.class, "wheel4");*/
            MotorEx wheel1 = new MotorEx("wheel1").brakeMode();
            MotorEx wheel2 = new MotorEx("wheel2").brakeMode();
            MotorEx wheel3 = new MotorEx("wheel3").brakeMode();
            MotorEx wheel4 = new MotorEx("wheel4").brakeMode();
            //+IMUEx imu = new IMUEx("imu", Direction.UP, Direction.FORWARD).zeroed();


    @Override
    public void onStartButtonPressed() {
        super.onStartButtonPressed();
        Command driverControlled = new MecanumDriverControlled(
                wheel4,
                wheel1,
                wheel3,
                wheel2,
                Gamepads.gamepad1().leftStickY(),
                Gamepads.gamepad1().leftStickX(),
                Gamepads.gamepad1().rightStickX()
        );
        driverControlled.schedule();

    }
}
