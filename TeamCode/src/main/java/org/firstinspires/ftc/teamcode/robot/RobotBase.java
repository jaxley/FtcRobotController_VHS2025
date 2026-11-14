package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

public class RobotBase {

    /** Which robot this build is running on, identified by hub serial numbers. */
    public enum RobotId {
        ALPHABOT, BETABOT, UNKNOWN
    }

    /**
     * Expansion hub module addresses, one per robot. The control hub's serial is not
     * usable ("embedded"), so identity comes from the expansion hub's module number,
     * which is part of its configured name (e.g. "Expansion Hub 2" -> address 2).
     */
    private static final int MODULE_ALPHABOT = 2;
    private static final int MODULE_BETABOT = -1; // TODO: set to betabot's expansion hub module number

    private static RobotId CACHED_ID;
    private static String CACHED_HUB;
    private static final java.util.Map<Integer, RobotId> ROBOT_BY_MODULE =
            java.util.Map.of(MODULE_ALPHABOT, RobotId.ALPHABOT, MODULE_BETABOT, RobotId.BETABOT);

    /**
     * Identifies the robot from its expansion hub module number so one build runs on
     * both robots. Looked up once per process and logged; callers can use it to pick
     * per-robot constants (e.g. pedro localizer configs).
     */
    public static RobotId robotId(HardwareMap hardwareMap) {
        if (CACHED_ID == null) {
            CACHED_ID = RobotId.UNKNOWN;
            for (LynxModule hub : hardwareMap.getAll(LynxModule.class)) {
                RobotId id = ROBOT_BY_MODULE.get(hub.getModuleAddress());
                if (id != null && !hub.getSerialNumber().toString().equals("embedded")) {
                    CACHED_ID = id;
                    CACHED_HUB = "Expansion Hub " + hub.getModuleAddress();
                    break;
                }
            }
            if (CACHED_ID == RobotId.UNKNOWN) {
                RobotLog.ww("RobotBase", "no expansion hub module matched " + hubModules(hardwareMap) + "; robot id UNKNOWN");
            }
            RobotLog.ii("RobotBase", "hub " + CACHED_HUB + " -> " + CACHED_ID);
        }
        return CACHED_ID;
    }

    /** All hub module numbers on this robot, for the log line when nothing matched. */
    private static java.util.List<Integer> hubModules(HardwareMap hardwareMap) {
        return hardwareMap.getAll(LynxModule.class).stream()
                .map(LynxModule::getModuleAddress)
                .collect(java.util.stream.Collectors.toList());
    }

    /** Name of the hub that identified the robot (null if unknown or not yet resolved). */
    public static String lastIdentifyingHub() {
        return CACHED_HUB;
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
