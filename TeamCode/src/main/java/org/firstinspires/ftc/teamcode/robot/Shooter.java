package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.util.Range;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
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
    private static final int SHOOTER_FLYWHEEL_DIRECTION = -1; // with unspecified motor, negative power moves the wheel clockwise
    private final DcMotorEx flywheel;
    private final Servo fireServo;

    private boolean initialized = false;

    final double flywheelSpeedRpm = 4414; // RPM

    double flywheelPower = 0.8;

    private static final double minFlywheelPower = 0.1;
    private static final double maxFlywheelPower = 1.0;
    final double FLYWHEEL_RPM_2_CLICKS_PER_SECOND_CONVERSION = (double) 28 /60;
    private double targetVelocity = flywheelPower * flywheelSpeedRpm
            * FLYWHEEL_RPM_2_CLICKS_PER_SECOND_CONVERSION;

    private int lastFlywheelPosition = 0;
    private boolean use_encoder = true;

    @Configurable
    public static class FiringServo {
        static double fireDownPos = 0.15;
    }

    final double fireUpPos = 0.35;

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

    private static final double MIN_TARGET_TPS = 100;   // don't sign-check tiny targets
    private static final double WRONG_DIR_TPS  = 50;    // ignore noise below this speed
    private static final double WRONG_DIR_MS   = 300;   // sign mismatch allowed before kill
    private static final double STALL_FRACTION = 0.25;  // "responding" = ≥25% of target
    private static final double STALL_MS       = 4000;  // grace for normal spool-up (~2s)
    private boolean flywheelFaulted = false;
    private final ElapsedTime faultTimer = new ElapsedTime();

    public Shooter(DcMotorEx flywheel, Servo fireServo) {
        this.flywheel = flywheel;
        this.fireServo = fireServo;
    }

    private void killFlywheel(TelemetryMirror tm, String reason) {
        // CRITICAL: leave velocity mode FIRST. setVelocity(0) would NOT stop a
        // rail-locked loop — target 0 vs measured -2060 still commands 100% power.
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel.setPower(0);  // ZeroPowerBehavior.BRAKE gives active braking
        flywheelFaulted = true;
        tm.addData("FLYWHEEL FAULT", reason);
    }

    private void flywheelFailsafe(TelemetryMirror tm) {

        double target = this.targetVelocity;
        double actual = flywheel.getVelocity();

        boolean wrongDir = Math.abs(target) > MIN_TARGET_TPS
                && Math.abs(actual) > WRONG_DIR_TPS
                && Math.signum(actual) != Math.signum(target);        // catches both rail traps

        boolean stalled = Math.abs(target) > MIN_TARGET_TPS
                && Math.abs(actual) < STALL_FRACTION * Math.abs(target); // dead motor/encoder

        if (wrongDir || stalled) {
            if (faultTimer.milliseconds() > (wrongDir ? WRONG_DIR_MS : STALL_MS)) {
                use_encoder = false; // failsafe to stop using encoder
            }
        } else {
            faultTimer.reset();  // healthy this loop
        }
    }


    private void init(TelemetryMirror telemetryMirror) {
        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(use_encoder ? DcMotor.RunMode.RUN_USING_ENCODER : DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        reset(telemetryMirror); // Ensure firing servo at the starting position
        initialized = true;
        telemetryMirror.addData(SUBSYSTEM_NAME, INITIALIZED + ": " +
                (TELEOP_MODE ? "TeleOp Mode" : "Auto Mode"));
        defaultPidCoefficients = flywheel.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    @Configurable
    public static class PID {
        static double P = 22;
        static double I = 3;
        static double D = 0;
        static double F = 0;
    }

    private void logMotorAttributes(DcMotorEx flywheel, TelemetryMirror telemetryMirror)
    {
        MotorConfigurationType type = flywheel.getMotorType();
        telemetryMirror.addData("motor type", type.getName());
        telemetryMirror.addData("xmlTag", type.getXmlTag());
        telemetryMirror.addData("ticksPerRev", type.getTicksPerRev());
        telemetryMirror.addData("gearing", type.getGearing());
        telemetryMirror.addData("maxRPM", type.getMaxRPM());
        telemetryMirror.addData("orientation", type.getOrientation());
        telemetryMirror.addData("software direction", flywheel.getDirection());
        telemetryMirror.addData("active PIDF", flywheel.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER));
    }

    private void getVelocityUsingEncoder(DcMotorEx flywheel, TelemetryMirror telemetryMirror)
    {
        // this calculates the velocity using the encoder. This *should* be consistent with what getVelocity()
        // observes
        double loopSeconds = stopWatch.seconds();
        stopWatch.reset();
        double velocity = (flywheel.getCurrentPosition() - lastFlywheelPosition) / loopSeconds;
        lastFlywheelPosition = flywheel.getCurrentPosition();

        telemetryMirror.addData("Calculated Velocity", velocity);
    }

    public void run(Gamepad gamepad, TelemetryMirror telemetryMirror) {
        if (!initialized) {
            init(telemetryMirror);
        }
        if (use_encoder) {
            flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(PID.P, PID.I, PID.D, PID.F));
        } else if (flywheel.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
            flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        telemetryMirror.addData(SUBSYSTEM_NAME, STARTED);
        //double lastMillis = getRuntime();

        if (TELEOP_MODE) {
            dpadUp.update(gamepad.dpad_up);
            dpadDown.update(gamepad.dpad_down);
            // dpad up and down can change flywheel power by 5% of max power
            if (dpadUp.pressed()) {
                flywheelPower = Range.clip(flywheelPower + 0.025, minFlywheelPower, maxFlywheelPower);
            } else if (dpadDown.pressed()) {
                flywheelPower = Range.clip(flywheelPower - 0.025, minFlywheelPower, maxFlywheelPower);
            }


            if (flywheelFaulted) {
                // Latched kill. Driver releases the trigger to re-arm.
                if (gamepad.left_trigger <= triggerDZ) {
                    flywheelFaulted = false;
                    flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    faultTimer.reset();
                } else {
                    telemetryMirror.addData(FLYWHEEL, "FAULT - release left trigger to reset");
                }
            } else {
                flywheelRunning = (gamepad.left_trigger > triggerDZ);
                if (flywheelRunning) {
                    startFlywheel(telemetryMirror, flywheelPower);
                    flywheelFailsafe(telemetryMirror);
                } else {
                    stop(telemetryMirror);
                    faultTimer.reset();
                }
            }

            telemetryMirror.addData("Current Speed: ", flywheel.getPower());

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
        double error = Math.abs((targetVelocity - flywheel.getVelocity())/ targetVelocity);
        telemetryMirror.addData("Flywheel error", error);
        return error <= 0.1;
    }

    public void stopFlywheel(TelemetryMirror telemetryMirror) {
        telemetryMirror.addData(FLYWHEEL, STOPPED);
        //flywheel.setVelocity(0);
        flywheel.setPower(0);
    }

    public void startFlywheel(TelemetryMirror telemetryMirror, double power) {
        power = Range.clip(power, minFlywheelPower, maxFlywheelPower);  // com.qualcomm.robotcore.util.Range
        telemetryMirror.addData(FLYWHEEL, power);
        this.targetVelocity = power * flywheelSpeedRpm * FLYWHEEL_RPM_2_CLICKS_PER_SECOND_CONVERSION * SHOOTER_FLYWHEEL_DIRECTION;
        if (use_encoder) {
            telemetryMirror.addData("Target Velocity: ", targetVelocity);
            flywheel.setVelocity(targetVelocity);
        } else {
            telemetryMirror.addData("Target Power: ", power);
            flywheel.setPower(power);
        }
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
