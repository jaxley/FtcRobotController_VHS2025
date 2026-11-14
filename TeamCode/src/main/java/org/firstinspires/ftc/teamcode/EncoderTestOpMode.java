package org.firstinspires.ftc.teamcode;

import com.pedropathing.revhub.localizers.Encoder;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.robot.RobotConstants;
import org.firstinspires.ftc.teamcode.robot.RobotBase;

@TeleOp(name = "EncoderTest")
public class EncoderTestOpMode extends LinearOpMode {
    private Encoder leftEncoder;
    private Encoder rightEncoder;
    private Encoder strafeEncoder;
    private RobotBase robotBase;

    @Override
    public void runOpMode() throws InterruptedException {

        robotBase = RobotBase.getInstance(hardwareMap);

        rightEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, RobotConstants.EncoderWheel.RIGHT));
        leftEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, RobotConstants.EncoderWheel.LEFT));
        strafeEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, RobotConstants.EncoderWheel.CENTER));

        leftEncoder.setDirection(Encoder.FORWARD);
        rightEncoder.setDirection(Encoder.FORWARD);
        strafeEncoder.setDirection(Encoder.REVERSE);

        waitForStart();
        while(opModeIsActive()) {
            telemetry.addData("Right bumper: ", "stop");
            telemetry.addData("X: ", "frontLeft power 1.0");
            telemetry.addData("Y: ", "backLeft power 1.0");
            telemetry.addData("A: ", "stop");
            telemetry.addData("B:", "stop");
            get_ticks();
            if (gamepad1.right_bumper) {
                stopall();
            }
            if (gamepad1.x) {
                stopall();
                robotBase.getMecanumDrive().getFrontLeft().setPower(1.0);
            }
            if (gamepad1.y) {
                stopall();
                robotBase.getMecanumDrive().getBackLeft().setPower(1.0);
            }
            if (gamepad1.a) {
                stopall();
                robotBase.getMecanumDrive().getFrontRight().setPower(1.0);
            }
            if (gamepad1.b) {
                stopall();
                robotBase.getMecanumDrive().getBackRight().setPower(1.0);
            }
            telemetry.update();
        }
    }

    public void stopall()
    {
        robotBase.getMecanumDrive().getFrontRight().setPower(0);
        robotBase.getMecanumDrive().getBackRight().setPower(0);
        robotBase.getMecanumDrive().getBackLeft().setPower(0);
        robotBase.getMecanumDrive().getFrontLeft().setPower(0);
    }


    public void get_ticks() {
        rightEncoder.update();
        leftEncoder.update();
        strafeEncoder.update();
        telemetry.addData("rightEncoder", rightEncoder.getDeltaPosition());
        telemetry.addData("LeftEncoder", leftEncoder.getDeltaPosition());
        telemetry.addData("StrafeEncoder", strafeEncoder.getDeltaPosition());
        telemetry.addData("Right ticks -- rightFront", robotBase.getMecanumDrive().getFrontRight().getCurrentPosition());
        telemetry.addData("Left ticks -- BackLeft", robotBase.getMecanumDrive().getBackLeft().getCurrentPosition());
        telemetry.addData("BackRight ticks -- rightRear", robotBase.getMecanumDrive().getBackRight().getCurrentPosition());
    }
}