package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class RobotBase {
    protected boolean TELEOP_MODE = true;
    private static RobotBase INSTANCE;
    protected IMecanumDrive mecanumDrive;
    protected Intake intake;
    protected Shooter shooter;

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

        DcMotor frontLeft = hardwareMap.get(DcMotor.class, Constants.Wheel.FRONT_LEFT);
        DcMotor frontRight = hardwareMap.get(DcMotor.class, Constants.Wheel.FRONT_RIGHT);
        DcMotor backLeft = hardwareMap.get(DcMotor.class, Constants.Wheel.BACK_LEFT);
        DcMotor backRight = hardwareMap.get(DcMotor.class, Constants.Wheel.BACK_RIGHT);

        if (TELEOP_MODE) {
            Follower follower = org.firstinspires.ftc.teamcode.pedroPathing.Constants.createFollower(hardwareMap);
   //         mecanumDrive = new SimpleMecanumDrive(frontLeft, frontRight, backLeft, backRight, follower);
            mecanumDrive = new PedroPathingMecanumDrive(frontLeft, frontRight, backLeft, backRight, follower);
        }

        intake = new Intake(hardwareMap.get(DcMotor.class, Constants.Motor.INTAKE_BASE),
                hardwareMap.get(DcMotor.class, Constants.Motor.INTAKE_ASSISTANT));

        shooter = new Shooter(hardwareMap.get(DcMotorEx.class, Constants.Motor.FLYWHEEL),
                hardwareMap.get(Servo.class, Constants.Motor.LAUNCH_SERVO));
    }

    protected RobotBase(HardwareMap hardwareMap) {
        this(hardwareMap, true);
    }

    public Shooter getShooter() {
        return shooter;
    }

    public Intake getIntake() {
        return intake;
    }

    public void takeAShot(Telemetry telemetry) {
        // TODO: verify this logic
        //intake.loadBallToShooter(telemetry);
        shooter.fire(telemetry);
    }

    public void run(Gamepad driverGamepad, Gamepad subsystemGamepad, Telemetry telemetry) {
        if (TELEOP_MODE) {
            mecanumDrive.run(driverGamepad, telemetry);
        }
        intake.run(subsystemGamepad, telemetry);
        shooter.run(subsystemGamepad, telemetry);
    }

    public void stop(Telemetry telemetry) {
        if (TELEOP_MODE) {
            mecanumDrive.stop(telemetry);
        }
        intake.stop(telemetry);
        shooter.stop(telemetry);
    }
}
