package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

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

                /*DcMotorEx wheel1 = hardwareMap.get(DcMotorEx.class, "wheel1");
                DcMotorEx wheel2 = hardwareMap.get(DcMotorEx.class, "wheel2");
                DcMotorEx wheel3 = hardwareMap.get(DcMotorEx.class, "wheel3");
                DcMotorEx wheel4 = hardwareMap.get(DcMotorEx.class, "wheel4");*/
            MotorEx frontLeft = new MotorEx("frontLeft").brakeMode();
            MotorEx frontRight = new MotorEx("frontRight").brakeMode();
            MotorEx backLeft = new MotorEx("backLeft").brakeMode();
            MotorEx backRight = new MotorEx("backRight").brakeMode();
            //+IMUEx imu = new IMUEx("imu", Direction.UP, Direction.FORWARD).zeroed();

    DcMotor intake = hardwareMap.get(DcMotor.class, "intake");
    final double intakeFwdPower = -0.8;
    final double intakeRevPower = 0.2;

    DcMotor flywheel = hardwareMap.get(DcMotor.class, "flywheel");
    final double flywheelPower = -0.9;


    Servo launchServo = hardwareMap.get(Servo.class, "launch servo");
    final double launchDownPos = 0;
    final double launchUpPos= 0.5;

    //control vars
    boolean intakeRunning = false;

    @Override
    public void onStartButtonPressed() {
        super.onStartButtonPressed();
        Command driverControlled = new MecanumDriverControlled(
                backRight,
                frontLeft,
                backLeft,
                frontRight,
                Gamepads.gamepad1().leftStickY(),
                Gamepads.gamepad1().leftStickX(),
                Gamepads.gamepad1().rightStickX()
        );
        driverControlled.schedule();
        // main loop
        while(opModeIsActive()) {
            intakeRunning = (gamepad2.right_trigger > 0.25);
            if(intakeRunning) {
                intake.setPower(intakeFwdPower);
            }


        }
        // yield code here
    }
}
