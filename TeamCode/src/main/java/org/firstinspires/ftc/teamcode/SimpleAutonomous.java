package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Alliance;
import org.firstinspires.ftc.teamcode.robot.RobotConstants;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

import java.util.concurrent.TimeUnit;
public class SimpleAutonomous extends OpMode {

    private final Alliance alliance;
    private final double motorDirection;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private static ElapsedTime stopWatch = new ElapsedTime();
    private double startTime = 0;

    private boolean running = false;
    private TelemetryMirror telemetryMirror;

    public SimpleAutonomous(Alliance alliance) {
        this.alliance = alliance;
        if (alliance == Alliance.BLUE) {
            this.motorDirection = 1.0;
        } else {
            this.motorDirection = -1.0;
        }
    }

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.FRONT_LEFT);
        frontRight = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.FRONT_RIGHT);
        backLeft = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.BACK_LEFT);
        backRight = hardwareMap.get(DcMotor.class, RobotConstants.Wheel.BACK_RIGHT);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        telemetryMirror = new TelemetryMirror(telemetry, false);
        telemetryMirror.addData("Alliance", alliance);
        telemetryMirror.update();
    }

    @Override
    public void loop() {
        telemetryMirror.setTelemetry(telemetry);
        if (startTime == 0) {
            stopWatch.startTime();
            startTime = stopWatch.nanoseconds();
            telemetryMirror.addData("StartTime:", startTime);
        }
        telemetryMirror.addData("Elapsed: ", stopWatch.nanoseconds());
        double elapsedTime = stopWatch.nanoseconds() - startTime;
        if (TimeUnit.NANOSECONDS.toSeconds((long)elapsedTime)<= 5) {
            running = true;
            frontLeft.setPower(-0.2 * motorDirection);
            frontRight.setPower(0.2 * motorDirection);
            backLeft.setPower(0.2 * motorDirection);
            backRight.setPower(-0.2 * motorDirection);
            telemetryMirror.addData("Time left until stop: ", elapsedTime);
            telemetryMirror.addData("Running? ", running);
        }
        else {
            running = false;
            frontLeft.setPower(0);
            frontRight.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);
            telemetryMirror.addData("Status: ", "Stopped.");
        }
        telemetryMirror.update();
    }
}
