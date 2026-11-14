package org.firstinspires.ftc.teamcode;

import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.util.ElapsedTime;

import static com.pedropathing.api.Paths.*;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.robot.Intake;
import org.firstinspires.ftc.teamcode.robot.Poses;
import org.firstinspires.ftc.teamcode.robot.RobotBaseAutonomous;
import org.firstinspires.ftc.teamcode.robot.Shooter;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

public class ShootParkAuto extends PanelsOpMode {
    public static final boolean USE_PANELS = true;
    protected Poses.Alliance alliance;

    private Poses poses;
    private RobotBaseAutonomous robotBase;
    private Shooter shooter;
    private Intake intake;
    private Path pathToShoot;
    private Path pathToPark;

    private static final double FLYWHEEL_POWER = 0.76;
    private static final double INTAKE_RUN_SECONDS = 6.0;

    private enum State { START, SHOOTING, PARKING, FINISHED }
    private State state = State.START;

    private final ElapsedTime stateTimer = new ElapsedTime();
    private boolean fired = false;
    private boolean pathToParkStarted = false;

    protected ShootParkAuto () {
    }
    public ShootParkAuto(Poses.Alliance alliance) {
        this.alliance = alliance;
    }

    public void init() {
        poses = new Poses(alliance);
        follower = Constants.create(hardwareMap);

        follower.setPose(poses.get(Poses.NamedPose.UPPER_CORNER_START));
        follower.update();

        telemetryMirror = new TelemetryMirror(telemetry, USE_PANELS);

        robotBase = RobotBaseAutonomous.getInstance(hardwareMap, telemetryMirror);
        shooter = robotBase.getShooter().withAutonomousMode(telemetryMirror);
        intake = robotBase.getIntake();

        pathToShoot = path(
                line(poses.get(Poses.NamedPose.UPPER_CORNER_START), poses.get(Poses.NamedPose.SHOOTING_UPPER_1))
        ).tangent();
        pathToPark = path(
                line(poses.get(Poses.NamedPose.SHOOTING_UPPER_1), poses.get(Poses.NamedPose.PARK_UPPER))
        ).tangent();

        super.init();
    }

    @Override
    public void start() {
        telemetryMirror.setTelemetry(telemetry);
        stateTimer.reset();
    }

    @Override
    public void loop() {
        follower.update();
        telemetryMirror.update();

        switch (state) {
            case START:
                follower.follow(pathToShoot);
                state = State.SHOOTING;
                break;

            case SHOOTING:
                shooter.run(null, telemetryMirror); // initializes PIDF/encoder modes; auto mode never touches the gamepad
                if (follower.isBusy()) {
                    break;
                }
                if (!fired) {
                    follower.hold(follower.pose());

                    shooter.fire(telemetryMirror);
                    fired = true;

                    shooter.startFlywheel(telemetryMirror, FLYWHEEL_POWER);
                    stateTimer.reset();
                }

                intake.loadBallToShooter(telemetryMirror);
                if (stateTimer.seconds() >= INTAKE_RUN_SECONDS) {
                    intake.stop(telemetryMirror);
                    shooter.reset(telemetryMirror);
                    state = State.PARKING;
                }
                break;

            case PARKING:
                if (!pathToParkStarted) {
                    follower.follow(pathToPark);
                    pathToParkStarted = true;
                } else if (!follower.isBusy()) {
                    follower.hold(follower.pose());
                    state = State.FINISHED;
                }
                break;

            case FINISHED:
                shooter.stopFlywheel(telemetryMirror);
                intake.stop(telemetryMirror);
                break;
        }

        telemetryMirror.addData("Auto State", state);
        telemetryMirror.addData("position", follower.pose());
        telemetryMirror.addData("velocity", follower.velocity());
        telemetryMirror.addData("heading", follower.pose().heading());

        super.loop();
    }

    @Override
    public void stop() {
        if (robotBase != null) {
            shooter.stopFlywheel(telemetryMirror);
            intake.stop(telemetryMirror);
            robotBase.stop(telemetryMirror);
        }
        RobotBaseAutonomous.reset();
    }
}
