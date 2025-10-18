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
    final double triggerDZ = 0.25;

    DcMotorEx flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
    final double flywheelSpeed = -6000; // RPM
    final double FWRPM2CPS = 28/60;


    Servo fireServo = hardwareMap.get(Servo.class, "launch servo");
    final double fireDownPos = 0;
    final double fireUpPos= 0.5;
    final double firePeriod = 800; // ms
    double fireTime = 0;

    //control vars
    boolean intakeRunning = false;
    boolean flywheelRunning = false;

    DButton fireButton = new DButton();

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
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        double lastMillis = 0;

        // main loop
        while(opModeIsActive()) {
            lastMillis = getRuntime();
            
            intakeRunning = (gamepad2.right_trigger > triggerDZ);
            if(intakeRunning) {
                intake.setPower(intakeFwdPower);
            }
            else if (gamepad2.right_bumper) {
                intake.setPower(intakeRevPower);
            }
            else {
                intake.setPower(0);
            }

            flywheelRunning = (gamepad2.left_trigger > triggerDZ);
            if(flywheelRunning) {
                flywheel.setVelocity(gamepad2.left_trigger * flywheelSpeed * FWRPM2CPS);
            }
            else {
                flywheel.setPower(0);
            }

            fireButton.update(gamepad2.x);
            if(fireButton.pressed()) {
                fireTime = firePeriod;
                fireServo.setPosition(fireUpPos);
            }
            fireTime -= getRuntime() - lastMillis;
        }
        // yield code here
    }
}
