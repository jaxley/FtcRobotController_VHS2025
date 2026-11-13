package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import static com.pedropathing.api.Paths.*;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.robot.Poses;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

import java.util.function.Supplier;

@SuppressWarnings({"unused"})
public abstract class BottomUpAutonomous extends OpMode {
    public static final boolean USE_PANELS = true;
    private Follower follower;

    protected Poses.Alliance alliance;
    private Supplier<Path> autonomousPath;

    private Poses poses;
    boolean running = false;
    private TelemetryMirror telemetryMirror;

    protected BottomUpAutonomous() {
        // Required for reflection
    }

    public BottomUpAutonomous(Poses.Alliance alliance) {
        this.alliance = alliance;
    }

    @Override
    public void init() {
        poses = new Poses(alliance);
        follower = Constants.create(hardwareMap);

        // Correctly sets up localizer starting position
        follower.setPose(poses.get(Poses.NamedPose.LOWER_WALL_START));
        follower.update();

        telemetryMirror = new TelemetryMirror(telemetry, USE_PANELS);

        // Set interpolation using .constant() inside the path instantiation
        autonomousPath = () -> path(
                line(poses.get(Poses.NamedPose.LOWER_WALL_START), poses.get(Poses.NamedPose.PARK_LOWER))
        ).tangent();
    }

    @Override
    public void loop() {
        follower.update();
        telemetryMirror.update();

        if (!running) {
            follower.follow(autonomousPath.get());
            running = USE_PANELS;
        }

        // Correct properties for Pedro 3 telemetry mapping
        telemetryMirror.addData("position", follower.pose());
        telemetryMirror.addData("velocity", follower.velocity());
        telemetryMirror.addData("heading", follower.pose().heading());
    }
}
