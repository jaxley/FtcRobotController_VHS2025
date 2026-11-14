package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.pedro.Drawing;
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
