package org.firstinspires.ftc.teamcode.robot;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
import static dev.nextftc.ftc.ActiveOpMode.getRuntime;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.DButton;

public class Shooter {
    private final Gamepad gamepad;
    private final DcMotorEx flywheel;
    private final Servo fireServo;

    private boolean initialized = false;

    final double flywheelSpeed = -6000; // RPM
    final double FWRPM2CPS = (double) 28 /60;

    final double fireDownPos = 0;
    final double fireUpPos= 0.5;
    final double firePeriod = 800; // ms

    final double triggerDZ = 0.25;

    double fireTime = 0;

    //control vars
    boolean flywheelRunning = false;

    DButton fireButton = new DButton();
    private Telemetry telemetry;

    public <T> Shooter(Gamepad gamepad, DcMotorEx flywheel, Servo fireServo) {
        this.gamepad = gamepad;
        this.flywheel = flywheel;
        this.fireServo = fireServo;
    }

    private void init() {
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        initialized = true;
        telemetry.addData("Shooter", "initialized");
    }

    public void setTelemetry(Telemetry telemetry){
        this.telemetry = telemetry;
    }

    public void run() {
        if (!initialized) {
            init();
        }

        telemetry.addData("Shooter", "started");
        double lastMillis = getRuntime();

        flywheelRunning = (gamepad.left_trigger > triggerDZ);
        telemetry.addData("Shooter flywheel running", flywheelRunning);
        if (flywheelRunning) {
            flywheel.setVelocity(gamepad.left_trigger * flywheelSpeed * FWRPM2CPS);
        } else {
            stop();
        }

        fireButton.update(gamepad.x);
        if (fireButton.pressed()) {
            telemetry.addData("Shooter", "firing");
            fireTime = firePeriod;
            fireServo.setPosition(fireUpPos);
        }
        fireTime -= getRuntime() - lastMillis;
    }

    public void stop() {
        telemetry.addData("Intake", "Flywheel stopped");
        flywheel.setPower(0);
    }
}
