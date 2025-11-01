package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class RobotBase {
    private static RobotBase INSTANCE;
    protected final SimpleMecanumDrive mecanumDrive;
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
    protected RobotBase(HardwareMap hardwareMap) {
        DcMotor frontLeft = hardwareMap.get(DcMotor.class, Constants.Wheel.FRONT_LEFT);
        DcMotor frontRight = hardwareMap.get(DcMotor.class, Constants.Wheel.FRONT_RIGHT);
        DcMotor backLeft = hardwareMap.get(DcMotor.class, Constants.Wheel.BACK_LEFT);
        DcMotor backRight = hardwareMap.get(DcMotor.class, Constants.Wheel.BACK_RIGHT);

        mecanumDrive = new SimpleMecanumDrive(frontLeft, frontRight, backLeft, backRight);

        intake = new Intake(hardwareMap.get(DcMotor.class, Constants.Motor.INTAKE));

        shooter = new Shooter(hardwareMap.get(DcMotorEx.class, Constants.Motor.FLYWHEEL),
                hardwareMap.get(Servo.class, Constants.Motor.LAUNCH_SERVO));
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
        mecanumDrive.run(driverGamepad, telemetry);
        intake.run(subsystemGamepad, telemetry);
        shooter.run(subsystemGamepad, telemetry);
    }

    public void stop(Telemetry telemetry) {
        mecanumDrive.stop(telemetry);
        intake.stop(telemetry);
        shooter.stop(telemetry);
    }
}
