package org.firstinspires.ftc.teamcode.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Unit tests for {@link Shooter#flywheelFailsafe(TelemetryMirror)} (invoked via
 * reflection because the method is private).
 *
 * The failsafe must detect:
 *  - stalls: a dead motor, or a dead/disconnected encoder, keeps |velocity| below
 *    25% of target for longer than STALL_MS
 *  - reversed direction: motor or encoder wired backwards makes velocity spin away
 *    from the commanded sign for longer than WRONG_DIR_MS
 *
 * On detection it disengages closed-loop encoder control by clearing the private
 * {@code use_encoder} flag (observed here via reflection, plus behaviorally through
 * {@link Shooter#startFlywheel(TelemetryMirror, double)} switching from
 * setVelocity() to setPower()).
 *
 * Time is injected by backdating the real {@code faultTimer}'s protected
 * {@code nsStartTime} field, so no test sleeps in wall-clock time. Constants below
 * mirror Shooter's private tuning values; they are the spec under test.
 */
@DisplayName("Shooter.flywheelFailsafe")
public class ShooterFlywheelFailsafeTest {

    // Mirror of Shooter's private failsafe tuning constants (the spec under test)
    private static final double MIN_TARGET_TPS = 100;   // don't sign-check tiny targets
    private static final double WRONG_DIR_TPS = 50;     // ignore noise below this speed
    private static final double WRONG_DIR_MS = 300;     // sign mismatch allowed before kill
    private static final double STALL_FRACTION = 0.25;  // "responding" = >= 25% of target
    private static final double STALL_MS = 4000;        // grace for normal spool-up

    private FakeFlywheel motor;
    private Shooter shooter;

    @BeforeEach
    public void setUp() throws Exception {
        motor = new FakeFlywheel();
        shooter = new Shooter(motor, new FakeServo());
        assertTrue(encoderInUse(), "test harness: shooter must start with encoder engaged");
    }

    // ------------------------------------------------------------------
    // Harness

    /** TelemetryMirror that drops all output; nothing here touches real telemetry. */
    static class SilentMirror extends TelemetryMirror {
        SilentMirror() {
            super(null, false);
        }

        @Override
        public void addData(String key, String value) {
        }

        @Override
        public void addData(String key, Object value) {
        }

        @Override
        public void addLine(String line) {
        }

        @Override
        public void update() {
        }
    }

    /** DcMotorEx double: velocity is the simulated encoder reading, everything else records commands. */
    static class FakeFlywheel implements DcMotorEx {
        double velocity;
        double commandedVelocity;
        int setVelocityCount;
        double commandedPower;
        int setPowerCount;
        DcMotor.RunMode mode = DcMotor.RunMode.RUN_USING_ENCODER;

        @Override
        public void setVelocity(double angularRate) {
            commandedVelocity = angularRate;
            setVelocityCount++;
        }

        @Override
        public double getVelocity() {
            return velocity;
        }

        @Override
        public void setPower(double power) {
            commandedPower = power;
            setPowerCount++;
        }

        @Override
        public double getPower() {
            return commandedPower;
        }

        @Override
        public void setMode(DcMotor.RunMode runMode) {
            mode = runMode;
        }

        @Override
        public DcMotor.RunMode getMode() {
            return mode;
        }

        @Override
        public void setVelocity(double angularRate, AngleUnit unit) {
        }

        @Override
        public double getVelocity(AngleUnit unit) {
            return velocity;
        }

        @Override
        public void setDirection(DcMotorSimple.Direction direction) {
        }

        @Override
        public DcMotorSimple.Direction getDirection() {
            return DcMotorSimple.Direction.FORWARD;
        }

        @Override
        public DcMotorController getController() {
            return null;
        }

        @Override
        public int getPortNumber() {
            return 0;
        }

        @Override
        public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        }

        @Override
        public DcMotor.ZeroPowerBehavior getZeroPowerBehavior() {
            return DcMotor.ZeroPowerBehavior.BRAKE;
        }

        @Override
        public void setPowerFloat() {
        }

        @Override
        public boolean getPowerFloat() {
            return false;
        }

        @Override
        public void setTargetPosition(int position) {
        }

        @Override
        public int getTargetPosition() {
            return 0;
        }

        @Override
        public boolean isBusy() {
            return false;
        }

        @Override
        public int getCurrentPosition() {
            return 0;
        }

        @Override
        public MotorConfigurationType getMotorType() {
            return null;
        }

        @Override
        public void setMotorType(MotorConfigurationType type) {
        }

        @Override
        public void setMotorEnable() {
        }

        @Override
        public void setMotorDisable() {
        }

        @Override
        public boolean isMotorEnabled() {
            return true;
        }

        @Override
        public void setPIDCoefficients(DcMotor.RunMode mode, PIDCoefficients pidCoefficients) {
        }

        @Override
        public void setPIDFCoefficients(DcMotor.RunMode mode, PIDFCoefficients pidfCoefficients) {
        }

        @Override
        public void setVelocityPIDFCoefficients(double p, double i, double d, double f) {
        }

        @Override
        public void setPositionPIDFCoefficients(double p) {
        }

        @Override
        public PIDCoefficients getPIDCoefficients(DcMotor.RunMode mode) {
            return null;
        }

        @Override
        public PIDFCoefficients getPIDFCoefficients(DcMotor.RunMode mode) {
            return null;
        }

        @Override
        public void setTargetPositionTolerance(int tolerance) {
        }

        @Override
        public int getTargetPositionTolerance() {
            return 0;
        }

        @Override
        public double getCurrent(CurrentUnit unit) {
            return 0;
        }

        @Override
        public double getCurrentAlert(CurrentUnit unit) {
            return 0;
        }

        @Override
        public void setCurrentAlert(double current, CurrentUnit unit) {
        }

        @Override
        public boolean isOverCurrent() {
            return false;
        }

        @Override
        public HardwareDevice.Manufacturer getManufacturer() {
            return null;
        }

        @Override
        public String getDeviceName() {
            return "FakeFlywheel";
        }

        @Override
        public String getConnectionInfo() {
            return "";
        }

        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public void resetDeviceConfigurationForOpMode() {
        }

        @Override
        public void close() {
        }
    }

    /** Servo double: records commanded positions, nothing else. */
    static class FakeServo implements Servo {
        double position;

        @Override
        public void setPosition(double position) {
            this.position = position;
        }

        @Override
        public double getPosition() {
            return position;
        }

        @Override
        public ServoController getController() {
            return null;
        }

        @Override
        public int getPortNumber() {
            return 0;
        }

        @Override
        public void setDirection(Direction direction) {
        }

        @Override
        public Direction getDirection() {
            return Direction.FORWARD;
        }

        @Override
        public void scaleRange(double min, double max) {
        }

        @Override
        public HardwareDevice.Manufacturer getManufacturer() {
            return null;
        }

        @Override
        public String getDeviceName() {
            return "FakeServo";
        }

        @Override
        public String getConnectionInfo() {
            return "";
        }

        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public void resetDeviceConfigurationForOpMode() {
        }

        @Override
        public void close() {
        }
    }

    // ------------------------------------------------------------------
    // Reflection plumbing (failsafe and its state are private; no production
    // code was changed to make this testable)

    private void failsafeLoop() throws Exception {
        Method m = Shooter.class.getDeclaredMethod("flywheelFailsafe", TelemetryMirror.class);
        m.setAccessible(true);
        m.invoke(shooter, new SilentMirror());
    }

    /** Sets the private targetVelocity field directly (ticks/sec), for targets startFlywheel can't produce. */
    private void setTargetVelocity(double tps) throws Exception {
        Field f = Shooter.class.getDeclaredField("targetVelocity");
        f.setAccessible(true);
        f.setDouble(shooter, tps);
    }

    private double targetVelocity() throws Exception {
        Field f = Shooter.class.getDeclaredField("targetVelocity");
        f.setAccessible(true);
        return f.getDouble(shooter);
    }

    private boolean encoderInUse() throws Exception {
        Field f = Shooter.class.getDeclaredField("use_encoder");
        f.setAccessible(true);
        return f.getBoolean(shooter);
    }

    /**
     * Makes the shooter's real faultTimer read (approximately) the given elapsed
     * milliseconds, without sleeping: backdates ElapsedTime.nsStartTime.
     */
    private void atMillis(double ms) throws Exception {
        Field timerField = Shooter.class.getDeclaredField("faultTimer");
        timerField.setAccessible(true);
        ElapsedTime timer = (ElapsedTime) timerField.get(shooter);
        Field startTime = ElapsedTime.class.getDeclaredField("nsStartTime");
        startTime.setAccessible(true);
        startTime.setLong(timer, System.nanoTime() - Math.round(ms * 1_000_000.0));
    }

    /** Runs the real public spool-up path: sets targetVelocity via production math (negative sign) and commands the motor. */
    private void startFlywheelAtPower(double power) {
        shooter.startFlywheel(new SilentMirror(), power);
    }

    // Healthy flywheel

    @Nested
    @DisplayName("healthy flywheel")
    class Healthy {

        @Test
        @DisplayName("velocity matching a negative target never disengages the encoder")
        public void matchingVelocityNeverDisengagesEncoder() throws Exception {
            startFlywheelAtPower(0.8);
            assertTrue(targetVelocity() < 0,
                    "production wiring (SHOOTER_FLYWHEEL_DIRECTION = -1) commands a negative target");
            motor.velocity = targetVelocity();

            atMillis(10 * STALL_MS);
            failsafeLoop();

            assertTrue(encoderInUse(), "healthy flywheel must never trip the failsafe");
        }

        @Test
        @DisplayName("slow spool-up inside the stall grace window does not trip")
        public void spoolUpWithinGraceDoesNotTrip() throws Exception {
            startFlywheelAtPower(0.8); // target ~ -1648 tps
            motor.velocity = -100;     // only ~6% of target, right sign: still spooling

            atMillis(STALL_MS - 1);
            failsafeLoop();

            assertTrue(encoderInUse(),
                    "the stall window is grace for spool-up; " + (STALL_MS - 1) + "ms must not trip");
        }
    }

    // Reversed direction (motor wired backwards, or encoder wired backwards)

    @Nested
    @DisplayName("reversed motor/encoder direction")
    class ReversedDirection {

        @Test
        @DisplayName("negative command, positive velocity (reversed motor or encoder) trips after 300ms")
        public void negativeCommandPositiveVelocityTrips() throws Exception {
            startFlywheelAtPower(0.8); // target ~ -1648 tps
            motor.velocity = 1600;     // spinning hard, opposite of commanded: rail trap

            atMillis(WRONG_DIR_MS - 1);
            failsafeLoop();
            assertTrue(encoderInUse(), "sign mismatch is tolerated for the " + WRONG_DIR_MS + "ms window");

            atMillis(WRONG_DIR_MS + 1);
            failsafeLoop();
            assertFalse(encoderInUse(),
                    "sustained wrong-direction spin past " + WRONG_DIR_MS + "ms must disengage the encoder");
        }

        @Test
        @DisplayName("positive command, negative velocity (opposite wiring) also trips")
        public void positiveCommandNegativeVelocityTrips() throws Exception {
            setTargetVelocity(1648);
            motor.velocity = -1600;

            atMillis(WRONG_DIR_MS + 50);
            failsafeLoop();

            assertFalse(encoderInUse(), "both sign-mismatch combinations must be caught");
        }

        @Test
        @DisplayName("wrong sign but below the 50 tps noise floor is a stall, not a direction fault")
        public void wrongSignBelowNoiseFloorUsesStallTiming() throws Exception {
            startFlywheelAtPower(0.8); // target ~ -1648 tps
            motor.velocity = 40;       // wrong sign, but |40| < WRONG_DIR_TPS

            atMillis(WRONG_DIR_MS + 50);
            failsafeLoop();
            assertTrue(encoderInUse(),
                    "below " + WRONG_DIR_TPS + " tps the reading is noise: no fast direction kill");

            atMillis(STALL_MS + 100);
            failsafeLoop();
            assertFalse(encoderInUse(),
                    "a near-zero wrong-sign reading is still a stall and trips after the stall window");
        }

        @Test
        @DisplayName("exactly 50 tps wrong-sign against a 200 tps target is healthy (both thresholds exclusive)")
        public void exactlyAtNoiseFloorAndStallFractionIsHealthy() throws Exception {
            setTargetVelocity(-200);   // 25% of 200 = 50 = WRONG_DIR_TPS
            motor.velocity = 50;       // wrong sign, but not >50 (no wrongDir), not <50 (no stall)

            atMillis(60_000);
            failsafeLoop();

            assertTrue(encoderInUse(),
                    "50 tps is neither above the noise floor nor below the stall fraction: treated as responding");
        }

        @Test
        @DisplayName("just above the noise floor with wrong sign trips at direction speed")
        public void justAboveNoiseFloorWrongSignTripsFast() throws Exception {
            setTargetVelocity(-200);
            motor.velocity = 60;       // wrong sign, > WRONG_DIR_TPS, > 25% of target

            atMillis(WRONG_DIR_MS - 1);
            failsafeLoop();
            assertTrue(encoderInUse());

            atMillis(WRONG_DIR_MS + 1);
            failsafeLoop();
            assertFalse(encoderInUse(), "60 tps wrong-sign is a direction fault, killed after 300ms");
        }

        @Test
        @DisplayName("wrong direction AND stalled at once: the 300ms direction window wins")
        public void wrongDirAndStallTogetherUseDirectionTiming() throws Exception {
            startFlywheelAtPower(0.8); // target ~ -1648 tps
            motor.velocity = 300;      // wrong sign (>50) AND below 25% of target: both flags true

            atMillis(WRONG_DIR_MS + 50);
            failsafeLoop();

            assertFalse(encoderInUse(),
                    "when both faults are present the failsafe must apply the wrong-direction window");
        }
    }

    // Stalls: dead motor, dead/disconnected encoder, insufficient response

    @Nested
    @DisplayName("stall detection")
    class Stall {

        @Test
        @DisplayName("dead motor (reads 0) trips after the 4s stall window, not before")
        public void deadMotorTripsAfterStallWindow() throws Exception {
            startFlywheelAtPower(0.8); // target ~ -1648 tps
            motor.velocity = 0;        // motor not moving at all

            atMillis(STALL_MS - 1);
            failsafeLoop();
            assertTrue(encoderInUse(), "stall grace covers normal spool-up: no trip before " + STALL_MS + "ms");

            atMillis(STALL_MS + 1);
            failsafeLoop();
            assertFalse(encoderInUse(), "zero velocity against a real target must trip after the stall window");
        }

        @Test
        @DisplayName("disconnected encoder (reads 0 regardless of motor) trips the same way")
        public void encoderDisconnectedTripsAfterStallWindow() throws Exception {
            startFlywheelAtPower(1.0); // target ~ -2060 tps
            motor.velocity = 0;        // encoder dead: reports zero even if the wheel spins

            atMillis(STALL_MS + 1);
            failsafeLoop();

            assertFalse(encoderInUse(),
                    "a dead encoder is indistinguishable from a stall and must fail safe");
        }

        @Test
        @DisplayName("24% of target is stalled")
        public void belowStallFractionTrips() throws Exception {
            setTargetVelocity(-1000);
            motor.velocity = -240;     // 24% of target, correct sign

            atMillis(STALL_MS + 1);
            failsafeLoop();

            assertFalse(encoderInUse(), "under 25% response for 4s is a stall");
        }

        @Test
        @DisplayName("exactly 25% of target counts as responding and never trips")
        public void atStallFractionIsHealthy() throws Exception {
            setTargetVelocity(-1000);
            motor.velocity = -250;     // exactly STALL_FRACTION: the comparison is strict <

            atMillis(60_000);
            failsafeLoop();

            assertTrue(encoderInUse(), "25% response is alive; the stall comparison must stay strict");
        }
    }

    // Small-target gate: MIN_TARGET_TPS

    @Nested
    @DisplayName("minimum target gate")
    class SmallTargets {

        @Test
        @DisplayName("targets at or below 100 tps skip all fault checks")
        public void targetAtMinTpsSkipsChecks() throws Exception {
            setTargetVelocity(100);    // exactly MIN_TARGET_TPS: check is strict >

            motor.velocity = 0;        // would be a stall...
            atMillis(60_000);
            failsafeLoop();
            assertTrue(encoderInUse(), "tiny targets are exempt from the stall check");

            motor.velocity = -500;     // ...and a direction fault
            atMillis(60_000);
            failsafeLoop();
            assertTrue(encoderInUse(), "tiny targets are exempt from the direction check too");
        }

        @Test
        @DisplayName("targets just above 100 tps are stall-checked")
        public void targetJustAboveMinTpsIsChecked() throws Exception {
            setTargetVelocity(101);
            motor.velocity = 0;

            atMillis(STALL_MS + 1);
            failsafeLoop();

            assertFalse(encoderInUse(), "101 tps is above the gate and a dead motor must trip");
        }
    }

    // Recovery and latching

    @Nested
    @DisplayName("recovery and latching")
    class RecoveryAndLatching {

        @Test
        @DisplayName("fault below the window, then healthy: the timer restarts from zero")
        public void faultBelowWindowThenHealthyResetsTimer() throws Exception {
            startFlywheelAtPower(0.8);

            motor.velocity = 1600;
            atMillis(250);
            failsafeLoop();
            assertTrue(encoderInUse(), "transient fault inside the window must not trip");

            motor.velocity = targetVelocity();
            failsafeLoop();
            assertTrue(encoderInUse());

            motor.velocity = 1600;
            atMillis(250);
            failsafeLoop();
            assertTrue(encoderInUse(), "the window must restart after a healthy loop, not accumulate");

            atMillis(WRONG_DIR_MS + 1);
            failsafeLoop();
            assertFalse(encoderInUse());
        }

        @Test
        @DisplayName("once tripped, the encoder stays disengaged even when velocity recovers")
        public void trippedEncoderStaysDisabledWhenVelocityRecovers() throws Exception {
            startFlywheelAtPower(0.8);
            motor.velocity = 1600;
            atMillis(WRONG_DIR_MS + 1);
            failsafeLoop();
            assertFalse(encoderInUse());

            motor.velocity = targetVelocity();

            failsafeLoop();

            assertFalse(encoderInUse(),
                    "the failsafe latches; only the teleop re-arm path may re-engage the encoder");
        }
    }

    // Observable effect through the public API

    @Nested
    @DisplayName("failsafe effect on startFlywheel")
    class FailsafeEffect {

        @Test
        @DisplayName("after a trip, startFlywheel falls back to open-loop power, not setVelocity")
        public void tripFallsBackToOpenLoopPower() throws Exception {
            startFlywheelAtPower(0.8);
            assertEquals(1, motor.setVelocityCount, "healthy spool-up commands velocity");
            assertEquals(0, motor.setPowerCount);

            motor.velocity = 1600;
            atMillis(WRONG_DIR_MS + 100);
            failsafeLoop();
            assertFalse(encoderInUse());

            startFlywheelAtPower(0.8);
            assertEquals(1, motor.setVelocityCount, "after the failsafe no new velocity command may be issued");
            assertEquals(1, motor.setPowerCount, "after the failsafe the motor must be driven by raw power");
            assertEquals(0.8, motor.commandedPower, 1e-9);
        }
    }

    // Documented detection limit

    @Nested
    @DisplayName("detection limits")
    class DetectionLimits {

        @Test
        @DisplayName("motor AND encoder both reversed reads healthy (the two reversals cancel)")
        public void motorAndEncoderBothReversedReadsHealthy() throws Exception {
            startFlywheelAtPower(0.8);   // target ~ -1648 tps
            motor.velocity = targetVelocity();
            // A doubly-reversed setup reports the commanded sign: physically spinning
            // the wrong way, but indistinguishable from healthy to this failsafe.
            atMillis(60_000);
            failsafeLoop();

            assertTrue(encoderInUse(),
                    "by design the sign check cannot see a double reversal; this pins that limit");
        }
    }
}
