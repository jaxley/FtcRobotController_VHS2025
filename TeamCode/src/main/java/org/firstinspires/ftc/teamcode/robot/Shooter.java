package org.firstinspires.ftc.teamcode.robot;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

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

    double flywheelPower = 0.8;
    final double FLYWHEEL_RPM_2_CLICKS_PER_SECOND_CONVERSION = (double) 28 /60;
    private double targetVelocity = flywheelPower * flywheelSpeedRpm
            * FLYWHEEL_RPM_2_CLICKS_PER_SECOND_CONVERSION;

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

    // Encoder direction is inverted vs power (positive power -> negative encoder
    // velocity), so the hub velocity PID runs away. We close the loop in software
    // using the sign-corrected measurement. If the encoder phases ever get swapped,
    // set this to false (and the hub RUN_USING_ENCODER loop becomes usable again).
    private static final boolean ENCODER_INVERTED = true;

    // Software velocity controller state
    private double integrator = 0;
    private final ElapsedTime controlTimer = new ElapsedTime();

    /** Encoder velocity with the wiring inversion removed (matches commanded target sign). */
    private double correctedVelocity() {
        double measured = flywheel.getVelocity();
        return ENCODER_INVERTED ? -measured : measured;
    }

    public Shooter(DcMotorEx flywheel, Servo fireServo) {
        this.flywheel = flywheel;
        this.fireServo = fireServo;
    }

    private void init(TelemetryMirror telemetryMirror) {
        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Software velocity loop: encoder direction is inverted vs power (positive power
        // -> negative encoder ticks), which the hub's RUN_USING_ENCODER PID cannot
        // compensate (it runs away). Read the encoder ourselves and write power directly.
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        reset(telemetryMirror); // Ensure firing servo at the starting position
        initialized = true;
        telemetryMirror.addData(SUBSYSTEM_NAME, INITIALIZED + ": " +
                (TELEOP_MODE ? "TeleOp Mode" : "Auto Mode"));
    }
    @Configurable
    public static class PID {
        // Gains for the SOFTWARE velocity loop (power per ticks/sec of error).
        // F = 1/freeSpeedTicksPerSec = 1/2800 for the goBILDA 6000RPM @ 28 PPR.
        static double P = 0.0005;
        static double I = 0.0001;
        static double D = 0;
        static double F = 0.00036;
    }
    public void run(Gamepad gamepad, TelemetryMirror telemetryMirror) {
        if (!initialized) {
            init(telemetryMirror);
        }

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
            telemetryMirror.addData(FLYWHEEL + " velocity", correctedVelocity());
            telemetryMirror.addData(FLYWHEEL + " velocity (raw encoder)", flywheel.getVelocity());
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
        // Compare targetVelocity to corrected velocity - when within tolerance, it's up to expected speed
        if (targetVelocity == 0) {
            telemetryMirror.addData("Flywheel error", 1.0);
            return false;
        }
        double error = Math.abs((targetVelocity - correctedVelocity()) / targetVelocity);
        telemetryMirror.addData("Flywheel error", error);
        return error <= 0.1;
    }

    public void stopFlywheel(TelemetryMirror telemetryMirror) {
        telemetryMirror.addData(FLYWHEEL, STOPPED);
        flywheel.setPower(0);
        integrator = 0;
        controlTimer.reset();
        //flywheel.setVelocity(0);
    }

    public void startFlywheel(TelemetryMirror telemetryMirror, double power) {
        this.targetVelocity = power * flywheelSpeedRpm * FLYWHEEL_RPM_2_CLICKS_PER_SECOND_CONVERSION;
        updateVelocityControl(telemetryMirror);
    }

    /**
     * One iteration of the software velocity PID. Must be called every loop while the
     * flywheel is running (startFlywheel does this for both teleop and autonomous).
     */
    private void updateVelocityControl(TelemetryMirror telemetryMirror) {
        double dt = controlTimer.seconds();
        controlTimer.reset();
        dt = Range.clip(dt, 0, 0.1); // ignore huge gaps (e.g. first call after stop)

        double error = targetVelocity - correctedVelocity();

        double feedforward = PID.F * targetVelocity;
        double proportional = PID.P * error;
        // simple anti-windup: only integrate when the P+F output isn't saturated
        if (Math.abs(feedforward + proportional) < 1.0) {
            integrator += PID.I * error * dt;
            integrator = Range.clip(integrator, -1.0, 1.0);
        }
        double out = Range.clip(feedforward + proportional + integrator, -1.0, 1.0);
        flywheel.setPower(out);

        telemetryMirror.addData(FLYWHEEL, out);
        telemetryMirror.addData("Target Velocity: ", targetVelocity);
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
