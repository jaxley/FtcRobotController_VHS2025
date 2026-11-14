package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.pedro.Drawing;
import org.firstinspires.ftc.teamcode.robot.IMecanumDrive;
import org.firstinspires.ftc.teamcode.robot.RobotConstants;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

public abstract class PanelsOpMode extends OpMode {
    protected Follower follower;
    public static final boolean USE_PANELS = true;
    protected TelemetryMirror telemetryMirror;

    @Override
    public void loop() {
        draw();
    }

    @Override
    public void init_loop() {
        drawOnlyCurrent();
    }

    @Override
    public void init() {
        Drawing.init();
        drawOnlyCurrent();
    }

    public PanelsOpMode() {
    }

    /**
     * Zeros the four drive motors directly. In autonomous the drive is owned by the
     * Follower (no IMecanumDrive instance exists), so OpMode stop() overrides use
     * this as the drive failsafe for STOP / driver-station crash.
     */
    protected void stopDriveMotors() {
        IMecanumDrive.stopMotors(
                hardwareMap.get(DcMotor.class, RobotConstants.Wheel.FRONT_LEFT),
                hardwareMap.get(DcMotor.class, RobotConstants.Wheel.FRONT_RIGHT),
                hardwareMap.get(DcMotor.class, RobotConstants.Wheel.BACK_LEFT),
                hardwareMap.get(DcMotor.class, RobotConstants.Wheel.BACK_RIGHT));
    }

    protected void draw() {
        Drawing.drawDebug(follower);
    }

    protected void drawOnlyCurrent() {
        try {
            Drawing.drawRobot(follower.pose());
            Drawing.sendPacket();
        } catch (Exception e) {
            telemetryMirror.addData("Error (Drawing Failed!): ", e.getMessage());
        }
    }
}
