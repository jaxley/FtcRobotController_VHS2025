package org.firstinspires.ftc.teamcode.robot;

import static dev.nextftc.ftc.ActiveOpMode.getRuntime;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utils.DButton;

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

    final double flywheelSpeedRpm = -6000; // RPM

    //final double FLYWHEEL_RPM_2_CLICKS_PER_SECOND_CONVERSION = (double) 28 /60;
    final double flywheelSpeed = -6000; // RPM
    double flywheelPower = -0.7;

    final double fireDownPos = 0;
    final double fireUpPos= 0.5;
    final double firePeriodMs = 800; // ms

    final double triggerDZ = 0.25;

    double fireTime = 0;

    //control vars
    boolean flywheelRunning = false;

    DButton fireButton = new DButton();

    public Shooter(DcMotorEx flywheel, Servo fireServo) {
        this.flywheel = flywheel;
        this.fireServo = fireServo;
    }

    private void init(Telemetry telemetry) {
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        reset(telemetry); // Ensure firing servo at the starting position
        initialized = true;
        telemetry.addData(SUBSYSTEM_NAME, INITIALIZED + ": " +
                (TELEOP_MODE ? "TeleOp Mode" : "Auto Mode"));
    }

    public void run(Gamepad gamepad, Telemetry telemetry) {
        if (!initialized) {
            init(telemetry);
        }

        telemetry.addData(SUBSYSTEM_NAME, STARTED);
        //double lastMillis = getRuntime();

        if (TELEOP_MODE) {
            flywheelRunning = (gamepad.left_trigger > triggerDZ);
            if (flywheelRunning) {
                startFlywheel(telemetry,
                        gamepad.left_trigger * flywheelPower);
            } else {
                stop(telemetry);
            }
        }

        // Don't bind keys unless in teleop.
        if (TELEOP_MODE) {
            fireButton.update(gamepad.x);
            if (fireButton.pressed()) {
                fire(telemetry);
            } else if (fireButton.released()) {
                reset(telemetry);
            }
        }
       // fireTime -= getRuntime() - lastMillis;
    }

    public void stop(Telemetry telemetry) {
        stopFlywheel(telemetry);
    }

    public void stopFlywheel(Telemetry telemetry) {
        telemetry.addData(FLYWHEEL, STOPPED);
        flywheel.setPower(0);
    }

    public void startFlywheel(Telemetry telemetry, double power) {
        telemetry.addData(FLYWHEEL, power);
        flywheel.setPower(power);
    }

    public void fire(Telemetry telemetry) {
        telemetry.addData(SUBSYSTEM_NAME, FIRING);
        fireTime = firePeriodMs;
        fireServo.setPosition(fireUpPos);
    }

    public void reset(Telemetry telemetry) {
        telemetry.addData(SUBSYSTEM_NAME, "reset");
        fireServo.setPosition(fireDownPos);
    }


    private boolean TELEOP_MODE = true;
    public Shooter withAutonomousMode(Telemetry telemetry) {
        enableAutonomousMode(telemetry);
        return this;
    }

    private void enableAutonomousMode(Telemetry telemetry) {
        TELEOP_MODE = false;
    }
}
