package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

public class RobotBase {
    protected boolean TELEOP_MODE = true;
    private static RobotBase INSTANCE;
    protected IMecanumDrive mecanumDrive;
    protected Intake intake;
    protected Shooter shooter;
    protected Limelight limelight;

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

    protected RobotBase(HardwareMap hardwareMap, boolean teleop) {
        this.TELEOP_MODE = teleop;

        DcMotor frontLeft = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.FRONT_LEFT);
        DcMotor frontRight = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.FRONT_RIGHT);
        DcMotor backLeft = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.BACK_LEFT);
        DcMotor backRight = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.BACK_RIGHT);

        if (TELEOP_MODE) {
            Follower follower = org.firstinspires.ftc.teamcode.pedro.Constants.create(hardwareMap);
            mecanumDrive = new PedroPathingMecanumDrive(frontLeft, frontRight, backLeft, backRight, follower);
        }

        intake = new Intake(hardwareMap.get(DcMotor.class, RobotConstants.Motor.INTAKE_BASE),
                hardwareMap.get(DcMotor.class, RobotConstants.Motor.INTAKE_ASSISTANT));

        shooter = new Shooter(hardwareMap.get(DcMotorEx.class, RobotConstants.Motor.FLYWHEEL),
                hardwareMap.get(Servo.class, RobotConstants.Motor.LAUNCH_SERVO));

        this.limelight = new Limelight(hardwareMap);
    }

    protected RobotBase(HardwareMap hardwareMap) {
        this(hardwareMap, true);
    }

    public Shooter getShooter() { return shooter; }
    public Intake getIntake() { return intake; }
    public IMecanumDrive getMecanumDrive() { return mecanumDrive; }

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
    }

    public void stop(TelemetryMirror telemetry) {
        if (TELEOP_MODE) {
            mecanumDrive.stop(telemetry);
            limelight.stop(telemetry);
            limelight.shutdownCamera();
        }
        intake.stop(telemetry);
        shooter.stop(telemetry);
    }
}
