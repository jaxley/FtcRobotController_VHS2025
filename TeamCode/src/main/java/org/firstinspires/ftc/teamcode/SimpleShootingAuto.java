package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.AutonomousOpMode.FLYWHEEL_POWER;

import com.bylazar.panels.Panels;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.robot.IMecanumDrive;
import org.firstinspires.ftc.teamcode.robot.Intake;
import org.firstinspires.ftc.teamcode.robot.RobotBase;
import org.firstinspires.ftc.teamcode.robot.RobotBaseAutonomous;
import org.firstinspires.ftc.teamcode.robot.RobotConstants;
import org.firstinspires.ftc.teamcode.robot.Shooter;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

import java.util.concurrent.TimeUnit;

public class SimpleShootingAuto extends PanelsOpMode {
    private final Alliance alliance;
    private final double motorDirection;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private RobotBaseAutonomous robotBase;
    private Shooter shooter;
    private Intake intake;


    private static ElapsedTime stopWatch = new ElapsedTime();
    private double startTime = 0;

    private boolean running = false;

    public SimpleShootingAuto(Alliance alliance) {
        this.alliance = alliance;
        if (alliance == Alliance.BLUE) {
            this.motorDirection = 1.0;
        } else {
            this.motorDirection = -1.0;
        }
    }

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        telemetryMirror = new TelemetryMirror(telemetry, true);

        frontLeft = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.FRONT_LEFT);
        frontRight = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.FRONT_RIGHT);
        backLeft = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.BACK_LEFT);
        backRight = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.BACK_RIGHT);

        robotBase = RobotBaseAutonomous.getInstance(hardwareMap, telemetryMirror);
        shooter = robotBase.getShooter().withAutonomousMode(telemetryMirror);
        intake = robotBase.getIntake();

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        telemetryMirror.addData("Alliance", alliance);
        telemetryMirror.update();
    }

    @Override
    public void start() {
        shooter.run(null, telemetryMirror);

        shooter.fire(telemetryMirror);
        shooter.startFlywheel(telemetryMirror, FLYWHEEL_POWER);
    }

    @Override
    public void stop() {
        // STOP or a driver-station crash must not leave motors running: zero all four
        // drive motors this OpMode commands directly.
        IMecanumDrive.stopMotors(frontLeft, frontRight, backLeft, backRight);
        robotBase.stop(telemetryMirror);
        RobotBaseAutonomous.reset();
    }

    private enum State {
        SHOOTING,
        END_SHOOTING,
        TURNING,
        PARKING,
        FINISH
    };
    private State AutoState = State.SHOOTING;

    double elapsedTime;

    @Override
    public void loop() {
        telemetryMirror.setTelemetry(telemetry);
        if (startTime == 0) {
            stopWatch.startTime();
            startTime = stopWatch.nanoseconds();
            telemetryMirror.addData("StartTime:", startTime);
        }

        elapsedTime = stopWatch.nanoseconds() - startTime;
        telemetryMirror.addData("Current path state: ", AutoState);
        telemetryMirror.addData("Total Elapsed Time (Seconds): ", TimeUnit.NANOSECONDS.toSeconds((long)elapsedTime));

        switch (AutoState) {
            case SHOOTING:
                if (shooter.readyToFire(telemetryMirror)) {
                    follower.hold(follower.pose());

                    intake.loadBallToShooter(telemetryMirror);
                }

                if (TimeUnit.NANOSECONDS.toSeconds((long)elapsedTime) >= 8) {
                    AutoState = State.END_SHOOTING;
                }
                break;
            case END_SHOOTING:
                intake.stop(telemetryMirror);
                shooter.stop(telemetryMirror);
                shooter.reset(telemetryMirror);
                AutoState = State.TURNING;
                break;
            case TURNING:
                if (TimeUnit.NANOSECONDS.toSeconds((long)elapsedTime) <= 8.15) {
                    // Rotates the robot left (counter-clockwise) in place.
                    frontLeft.setPower(-0.2 * motorDirection);
                    backLeft.setPower(-0.2 * motorDirection);
                    frontRight.setPower(0.2 * motorDirection);
                    backRight.setPower(0.2 * motorDirection);
                } else {
                    // Stop the turning
                    frontLeft.setPower(0);
                    backLeft.setPower(0);
                    frontRight.setPower(0);
                    backRight.setPower(0);

                    // Move on to parking
                    AutoState = State.PARKING;
                }
                break;
            case PARKING:
                if (TimeUnit.NANOSECONDS.toSeconds((long)elapsedTime) <= 12.3) {
                    running = true;
                    frontLeft.setPower(0.4 * motorDirection);
                    frontRight.setPower(0.4 * motorDirection);
                    backLeft.setPower(0.4 * motorDirection);
                    backRight.setPower(0.4 * motorDirection);
                    telemetryMirror.addData("Time left until stop: ", elapsedTime);
                    telemetryMirror.addData("Running? ", running);
                } else {
                    AutoState = State.FINISH;
                }
                break;
            case FINISH:
                running = false;
                frontLeft.setPower(0);
                frontRight.setPower(0);
                backLeft.setPower(0);
                backRight.setPower(0);
                telemetryMirror.addData("Status: ", "Stopped.");
                break;
            default:
                AutoState = State.SHOOTING;
                break;
        }
        telemetryMirror.update();
    }
}
