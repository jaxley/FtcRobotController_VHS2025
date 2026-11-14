package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.ftccommon.configuration.RobotConfigFileManager;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

public class RobotBase {

    /** Which robot this build is running on, identified by hub serial numbers. */
    public enum RobotId {
        ALPHABOT, BETABOT, UNKNOWN
    }

    /**
     * Expansion hub serial numbers, one per robot. These are unique per physical hub
     * (unlike module numbers, which can collide between robots, or the control hub's
     * serial, which reports as the placeholder "embedded"). Read them from the
     * RobotLog "hub serial ... -> ..." line on each robot.
     */
    private static final String SERIAL_ALPHABOT = "ALPHABOT-SERIAL"; // TODO: set to alphabot's expansion hub serial
    private static final String SERIAL_BETABOT = "BETABOT-SERIAL";  // TODO: set to betabot's expansion hub serial

    /** Configuration file each robot should have active (they differ per robot). */
    private static final String CONFIG_ALPHABOT = "2026 new";
    private static final String CONFIG_BETABOT = "2026-plan-a";

    private static RobotId CACHED_ID;
    private static String CACHED_HUB;
    private static final java.util.Map<String, RobotId> ROBOT_BY_SERIAL =
            java.util.Map.of(SERIAL_ALPHABOT, RobotId.ALPHABOT, SERIAL_BETABOT, RobotId.BETABOT);

    /**
     * Identifies the robot from its expansion hub serial so one build runs on both
     * robots. Looked up once per process and logged; callers can use it to pick
     * per-robot constants (e.g. pedro localizer configs).
     */
    public static RobotId robotId(HardwareMap hardwareMap) {
        if (CACHED_ID == null) {
            CACHED_ID = RobotId.UNKNOWN;
            for (LynxModule hub : hardwareMap.getAll(LynxModule.class)) {
                String serial = hub.getSerialNumber().toString();
                RobotId id = ROBOT_BY_SERIAL.get(serial);
                if (id != null) {
                    CACHED_ID = id;
                    CACHED_HUB = "Expansion Hub " + hub.getModuleAddress() + " (" + serial + ")";
                    break;
                }
            }
            if (CACHED_ID == RobotId.UNKNOWN) {
                RobotLog.ww("RobotBase", "no expansion hub serial matched " + hubSerials(hardwareMap) + "; robot id UNKNOWN");
            }
            RobotLog.ii("RobotBase", "hub " + CACHED_HUB + " -> " + CACHED_ID);
        }
        return CACHED_ID;
    }

    /** All hub serials on this robot, for the log line when nothing matched. */
    private static java.util.List<String> hubSerials(HardwareMap hardwareMap) {
        return hardwareMap.getAll(LynxModule.class).stream()
                .map(hub -> hub.getModuleAddress() + ":" + hub.getSerialNumber())
                .collect(java.util.stream.Collectors.toList());
    }

    /** Name of the hub that identified the robot (null if unknown or not yet resolved). */
    public static String lastIdentifyingHub() {
        return CACHED_HUB;
    }

    /** Name of the currently active configuration file, as shown on the Driver Station. */
    public static String activeConfigName(HardwareMap hardwareMap) {
        android.app.Activity activity = (android.app.Activity) hardwareMap.appContext;
        return new RobotConfigFileManager(activity).getActiveConfig().getName();
    }

    /** The config file that should be active on the given robot (null when unknown). */
    public static String expectedConfigName(RobotId id) {
        switch (id) {
            case ALPHABOT: return CONFIG_ALPHABOT;
            case BETABOT:  return CONFIG_BETABOT;
            default:       return null;
        }
    }

    protected boolean TELEOP_MODE = true;
    private static RobotBase INSTANCE;
    protected IMecanumDrive mecanumDrive;
    protected Intake intake;
    protected Shooter shooter;
    protected FlowerCollection flowerCollection;
    protected Limelight limelight;
    protected Follower follower;

    /**
     * Singleton accessor
     * @param hardwareMap
     * @return singleton instance of the robot base
     */
    public static RobotBase getInstance(HardwareMap hardwareMap) {
        if (INSTANCE == null) {
            INSTANCE = new RobotBase(hardwareMap);
        }
        return INSTANCE;
    }

    /**
     * Discards the singleton so the next OpMode run rebuilds all hardware from a fresh
     * HardwareMap. Hardware device objects belong to the OpMode run that created them;
     * reusing them across runs leaves the Follower/localizer/motors bound to a dead
     * HardwareMap and drives erratically until the robot is power cycled.
     */
    public static void reset() {
        INSTANCE = null;
    }

    protected RobotBase(HardwareMap hardwareMap, boolean teleop) {
        this.TELEOP_MODE = teleop;

        DcMotor frontLeft = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.FRONT_LEFT);
        DcMotor frontRight = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.FRONT_RIGHT);
        DcMotor backLeft = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.BACK_LEFT);
        DcMotor backRight = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.BACK_RIGHT);

        if (TELEOP_MODE) {
            Follower follower = org.firstinspires.ftc.teamcode.pedro.Constants.create(hardwareMap);
            this.follower = follower;
            mecanumDrive = new PedroPathingMecanumDrive(frontLeft, frontRight, backLeft, backRight, follower);
        }

        intake = new Intake(hardwareMap.get(DcMotor.class, RobotConstants.Motor.INTAKE_BASE),
                hardwareMap.get(DcMotor.class, RobotConstants.Motor.INTAKE_ASSISTANT));

        shooter = new Shooter(hardwareMap.get(DcMotorEx.class, RobotConstants.Motor.FLYWHEEL),
                hardwareMap.get(Servo.class, RobotConstants.Motor.LAUNCH_SERVO));

        flowerCollection = new FlowerCollection(hardwareMap.get(Servo.class, RobotConstants.Motor.COLLECT_SERVO));

        this.limelight = new Limelight(hardwareMap);
    }

    protected RobotBase(HardwareMap hardwareMap) {
        this(hardwareMap, true);
    }

    public Shooter getShooter() { return shooter; }
    public Intake getIntake() { return intake; }
    public FlowerCollection getFlowerCollection() { return flowerCollection; }
    public IMecanumDrive getMecanumDrive() { return mecanumDrive; }
    public Follower getFollower() { return follower; }

    public void run(Gamepad driverGamepad, Gamepad subsystemGamepad, TelemetryMirror telemetry) {
        // Process camera tracking algorithms and button edges cleanly
        if (TELEOP_MODE) {
            limelight.run(subsystemGamepad, telemetry);
        }

        // Delegate control loops smoothly without modifying active hardware gamepad variables
        if (TELEOP_MODE) {
            if (limelight.isTargetingActive()) {
                // Route the custom tracking gamepad to Pedro Pathing to safely execute rotation commands
                mecanumDrive.run(limelight.getTrackingGamepad(), telemetry);
                telemetry.addData("Drive Mode", "Auto-Targeting Overrides Engaged");
            } else {
                // Hand total standard layout stick control back to the driver normally
                mecanumDrive.run(driverGamepad, telemetry);
            }
        }

        intake.run(subsystemGamepad, telemetry);
        shooter.run(subsystemGamepad, telemetry);
        flowerCollection.run(subsystemGamepad, telemetry);
    }

    public void stop(TelemetryMirror telemetry) {
        if (TELEOP_MODE) {
            mecanumDrive.stop(telemetry);
            limelight.stop(telemetry);
            limelight.shutdownCamera();
        }
        intake.stop(telemetry);
        shooter.stop(telemetry);
        flowerCollection.stop(telemetry);
    }
}
