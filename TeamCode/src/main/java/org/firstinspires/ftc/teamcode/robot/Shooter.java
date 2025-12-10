package org.firstinspires.ftc.teamcode.robot;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.utils.DButton;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

public class Shooter {
    public static final String STOPPED = "stopped";
    public static final String FIRING = "firing";
    public static final String SUBSYSTEM_NAME = "Shooter";
    public static final String FLYWHEEL = "Flywheel";
    public static final String STARTED = "started";
    public static final String INITIALIZED = "initialized";
    private final DcMotorEx flywheel;
    private final Servo fireServo;

    private boolean initialized = false;

    final double flywheelSpeedRpm = -4414; // RPM

    double flywheelPower = -0.8;
    final double FLYWHEEL_RPM_2_CLICKS_PER_SECOND_CONVERSION = (double) 28 /60;
    private double targetVelocity = flywheelPower * flywheelSpeedRpm
            * FLYWHEEL_RPM_2_CLICKS_PER_SECOND_CONVERSION;

    @Configurable
    public static class FiringServo {
        static double fireDownPos = 0.15;
    }

    final double fireUpPos= 0.5;

    final double firePeriodMs = 800; // ms
    double fireTime = 0;
    private static ElapsedTime stopWatch = new ElapsedTime();

    final double triggerDZ = 0.25;
    //control vars
    boolean flywheelRunning = false;

    DButton fireButton = new DButton();

    DButton dpadUp = new DButton();
    DButton dpadDown = new DButton();
    private PIDFCoefficients defaultPidCoefficients;

    public Shooter(DcMotorEx flywheel, Servo fireServo) {
        this.flywheel = flywheel;
        this.fireServo = fireServo;
    }

    private void init(TelemetryMirror telemetryMirror) {
        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        reset(telemetryMirror); // Ensure firing servo at the starting position
        initialized = true;
        telemetryMirror.addData(SUBSYSTEM_NAME, INITIALIZED + ": " +
                (TELEOP_MODE ? "TeleOp Mode" : "Auto Mode"));
        defaultPidCoefficients = flywheel.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    @Configurable
    public static class PID {
        static double P = 16;
        static double I = 3;
        static double D = 0;
        static double F = 0;
    }
    public void run(Gamepad gamepad, TelemetryMirror telemetryMirror) {
        if (!initialized) {
            init(telemetryMirror);
        }
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(PID.P, PID.I, PID.D, PID.F));

        telemetryMirror.addData(SUBSYSTEM_NAME, STARTED);
        //double lastMillis = getRuntime();

        if (TELEOP_MODE) {
            dpadUp.update(gamepad.dpad_up);
            dpadDown.update(gamepad.dpad_down);
            // dpad up and down can change flywheel power by 5% of max power
            if (dpadUp.pressed()) {
                flywheelPower = flywheelPower - 0.025;
            } else if (dpadDown.pressed()) {
                flywheelPower = flywheelPower + 0.025;
            }

            telemetryMirror.addData(FLYWHEEL + " power", flywheelPower);

            flywheelRunning = (gamepad.left_trigger > triggerDZ);
            if (flywheelRunning) {
                startFlywheel(telemetryMirror,
                        gamepad.left_trigger * flywheelPower);
            } else {
                stop(telemetryMirror);
            }
            telemetryMirror.addData(FLYWHEEL + " velocity", flywheel.getVelocity());
        }

        // Don't bind keys unless in teleop.
        if (TELEOP_MODE) {
            fireButton.update(gamepad.x);
            if (fireButton.pressed()) {
                fire(telemetryMirror);
            } else if (fireButton.released()) {
                reset(telemetryMirror);
            }
        }
       // fireTime -= getRuntime() - lastMillis;
    }

    public void stop(TelemetryMirror telemetry) {
        stopFlywheel(telemetry);
    }

    public boolean readyToFire(TelemetryMirror telemetryMirror) {
        // Compare targetVelocity to current velocity - when within tolerance, it's up to expected speed
        double error = Math.abs(targetVelocity - flywheel.getVelocity() / targetVelocity);
        telemetryMirror.addData("Flywheel error", error);
        return error <= 0.05;
    }

    public void stopFlywheel(TelemetryMirror telemetryMirror) {
        telemetryMirror.addData(FLYWHEEL, STOPPED);
        flywheel.setVelocity(0);
        //flywheel.setPower(0);
    }

    public void startFlywheel(TelemetryMirror telemetryMirror, double power) {
        telemetryMirror.addData(FLYWHEEL, power);
        this.targetVelocity = power * flywheelSpeedRpm * FLYWHEEL_RPM_2_CLICKS_PER_SECOND_CONVERSION;
        telemetryMirror.addData("Target Velocity: ", targetVelocity);
        flywheel.setVelocity(targetVelocity);
    }

    public void fire(TelemetryMirror telemetryMirror) {
        telemetryMirror.addData(SUBSYSTEM_NAME, FIRING);
        fireTime = firePeriodMs;
        fireServo.setPosition(fireUpPos);
    }

    public void reset(TelemetryMirror telemetryMirror) {
        telemetryMirror.addData(SUBSYSTEM_NAME, "reset");
        fireServo.setPosition(FiringServo.fireDownPos);
    }


    private boolean TELEOP_MODE = true;
    public Shooter withAutonomousMode(TelemetryMirror telemetryMirror) {
        enableAutonomousMode(telemetryMirror);
        return this;
    }

    private void enableAutonomousMode(TelemetryMirror telemetryMirror) {
        TELEOP_MODE = false;
    }
}
