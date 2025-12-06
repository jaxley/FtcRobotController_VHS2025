package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Alliance;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.robot.Poses;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

import java.util.function.Supplier;

public abstract class LessSimpleAutonomous extends OpMode {
    public static final boolean USE_PANELS = true;
    private Follower follower;
    protected Alliance alliance;
    private Supplier<PathChain> pathChain;
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;

    private Poses.AlliancePoses poses;

    boolean running = false;
    private TelemetryMirror telemetryMirror;

    protected LessSimpleAutonomous() {

    }

    public LessSimpleAutonomous(Alliance alliance) {
        this.alliance = alliance;
    }

    @Override
    public void init() {
        poses = Poses.forAlliance(alliance);

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(poses.get(Poses.NamedPose.STARTING_TOP_2));
        follower.update();
        telemetryMirror = new TelemetryMirror(telemetry, USE_PANELS);

        pathChain = () -> follower.pathBuilder() //Lazy Curve Generation
                .addPath(new Path(new BezierLine(follower::getPose, poses.get(Poses.NamedPose.LEAVE_TOP))))
                .setHeadingInterpolation(HeadingInterpolator.constant(Math.toRadians(38)))
                .build();
    }

    @Override
    public void loop() {
        //Call this once per loop
        follower.update();
        telemetryMirror.update();
        if (!running) {
            follower.followPath(pathChain.get(), 0.5, USE_PANELS);
            running = USE_PANELS;
        }

        telemetryMirror.addData("position", follower.getPose());
        telemetryMirror.addData("velocity", follower.getVelocity());
    }
}