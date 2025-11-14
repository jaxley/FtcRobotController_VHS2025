package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utils.DButton;

public class PedroPathingMecanumDrive implements IMecanumDrive {
    private final Follower follower;
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;

    private final DcMotor backRight;
    private DButton holdButton = new DButton();
    private boolean initialized;

    public PedroPathingMecanumDrive(DcMotor frontLeft, DcMotor frontRight,
                DcMotor backLeft, DcMotor backRight, Follower follower) {
        this.follower = follower;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }

    public DcMotor getFrontLeft() {
        return frontLeft;
    }

    public DcMotor getFrontRight() {
        return frontRight;
    }

    public DcMotor getBackLeft() {
        return backLeft;
    }

    public DcMotor getBackRight() {
        return backRight;
    }

    public void init(Telemetry telemetry) {
        follower.startTeleOpDrive();
        // TODO - set this
        // follower.setStartingPose();
        this.initialized = true;
    }

    public void run(Gamepad driveGamepad, Telemetry telemetry) {
        if (!initialized) {
            init(telemetry);
        }

        follower.update();

        // TODO - add support for speed adjust button
        follower.setTeleOpDrive(-driveGamepad.left_stick_y,
                -driveGamepad.left_stick_x,
                -driveGamepad.right_stick_x,
                true);

        holdButton.update(driveGamepad.left_trigger != 0);
        if (holdButton.pressed()) {
            hold();
        } else if (holdButton.released()) {
            follower.startTeleOpDrive(); // go back to manual mode
        }
    }

    @Override
    public void stop(Telemetry telemetry) {
        // TODO - stop
    }

    public void hold() {
        follower.holdPoint(follower.getPose());
    }
}
