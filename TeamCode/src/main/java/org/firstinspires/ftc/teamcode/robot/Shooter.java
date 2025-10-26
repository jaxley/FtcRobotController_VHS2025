package org.firstinspires.ftc.teamcode.robot;

import static dev.nextftc.ftc.ActiveOpMode.getRuntime;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.DButton;

public class Shooter {
    public static final String STOPPED = "stopped";
    public static final String FIRING = "firing";
    public static final String SUBSYSTEM_NAME = "Shooter";
    public static final String FLYWHEEL = SUBSYSTEM_NAME + " flywheel";
    public static final String STARTED = "started";
    public static final String INITIALIZED = "initialized";
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

    public <T> Shooter(DcMotorEx flywheel, Servo fireServo) {
        this.flywheel = flywheel;
        this.fireServo = fireServo;
    }

    private void init(Telemetry telemetry) {
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fireServo.setPosition(fireDownPos); // Ensure it's at the starting position
        initialized = true;
        telemetry.addData(SUBSYSTEM_NAME, INITIALIZED);
    }

    public void run(Gamepad gamepad, Telemetry telemetry) {
        if (!initialized) {
            init(telemetry);
        }

        telemetry.addData(SUBSYSTEM_NAME, STARTED);
        double lastMillis = getRuntime();

        flywheelRunning = (gamepad.left_trigger > triggerDZ);
        if (flywheelRunning) {
            flywheel.setVelocity(gamepad.left_trigger * flywheelSpeed * FWRPM2CPS);
        } else {
            stop(telemetry);
        }

        fireButton.update(gamepad.x);
        if (fireButton.pressed()) {
            telemetry.addData(SUBSYSTEM_NAME, FIRING);
            fireTime = firePeriod;
            fireServo.setPosition(fireUpPos);
        } else if (fireButton.released()) {
            telemetry.addData(SUBSYSTEM_NAME, "reset");
            fireServo.setPosition(fireDownPos);
        }
        fireTime -= getRuntime() - lastMillis;
    }

    public void stop(Telemetry telemetry) {
        telemetry.addData(FLYWHEEL, STOPPED);
        flywheel.setPower(0);
    }
}
