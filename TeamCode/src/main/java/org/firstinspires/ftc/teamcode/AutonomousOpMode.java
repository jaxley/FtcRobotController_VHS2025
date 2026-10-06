package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.paths.Path;
import com.pedropathing.api.PoseFactory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.robot.RobotBaseAutonomous;
import org.firstinspires.ftc.teamcode.utils.TelemetryMirror;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public abstract class AutonomousOpMode extends OpMode {

    public static final String AUTONOMOUS_OP_MODE = "AutonomousOpMode";
    public static final String ALLIANCE = "Alliance";
    public static final double FLYWHEEL_POWER = -0.825;
    private boolean reset = true;

    protected AutonomousOpMode() {
    }

    protected AutonomousOpMode(Alliance alliance) {
        this.alliance = alliance;
    }

    public static final boolean USE_PANELS = true;
    protected Follower follower;
    protected final PoseFactory p = PoseFactory.degrees();

    private RobotBaseAutonomous robotBase;
    private Alliance alliance;
    private PathState pathState;
    private TelemetryMirror telemetryMirror;

    protected Path autoPaths;
    int shotCount = 0;
    double lastFiringTimeMs;
    double lastResetTimeMs;

    protected ElapsedTime pathTimer;

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate(telemetryMirror);

        telemetryMirror.addData("path state", pathState);
        telemetryMirror.addData("x", follower.pose().x());
        telemetryMirror.addData("y", follower.pose().y());
        telemetryMirror.addData("heading", Math.toDegrees(follower.pose().heading()));
        telemetryMirror.update();
        draw();
    }

    @Override
    public void init() {
        // Commented out to prevent compilation errors if the custom helper class is missing
        // Drawing.init();

        telemetryMirror = new TelemetryMirror(telemetry, USE_PANELS);
        pathTimer = new ElapsedTime();

        // FIX: Updated method to Pedro Pathing 3 syntax
        follower = Constants.create(hardwareMap);
        drawOnlyCurrent();
    }

    @Override
    public void init_loop() {
        telemetryMirror.addData("Code Version", BuildConfig.VERSION_NAME);
        telemetryMirror.addData("Code Build Time", BuildConfig.APP_BUILD_TIME);
        telemetryMirror.addData(ALLIANCE, alliance.name());
        telemetryMirror.addData(AUTONOMOUS_OP_MODE, "initialized");

        telemetryMirror.addData("Starting X ", follower.pose().x());
        telemetryMirror.addData("Starting Y ", follower.pose().y());
        telemetryMirror.update();

        follower.update();
        drawOnlyCurrent();
    }

    @Override
    public void start() {
        setNextPathState(PathState.SCORE_PRELOADED);
        robotBase = RobotBaseAutonomous.getInstance(hardwareMap, telemetryMirror);

        telemetryMirror.addData(ALLIANCE, alliance.name());
        telemetryMirror.addData(AUTONOMOUS_OP_MODE, "started");
        telemetryMirror.update();

        if (autoPaths != null) {
            follower.follow(autoPaths);
        }

        follower.update();
    }

    @Override
    public void stop() {
        // Keeping empty method structure for subclass overrides if needed
    }

    public void autonomousPathUpdate(TelemetryMirror telemetryMirror) {
        if (pathState == PathState.SCORE_PRELOADED) {
            robotBase.getShooter().startFlywheel(telemetryMirror, FLYWHEEL_POWER);
            telemetryMirror.addData("Fired", shotCount);

            telemetryMirror.addData("Last Time Fired", lastFiringTimeMs);
            telemetryMirror.addData("Last Time Reset", lastResetTimeMs);
            telemetryMirror.addData("Ready to Fire", robotBase.getShooter().readyToFire(telemetryMirror));

            if (pathTimer.milliseconds() >= (lastResetTimeMs + 500)) {
                if (robotBase.getShooter().readyToFire(telemetryMirror) && reset) {
                    shotCount += 1;
                    robotBase.getShooter().fire(telemetryMirror);
                    lastFiringTimeMs = pathTimer.milliseconds();
                    reset = false;
                }
            }
            if (shotCount == 2) {
                robotBase.getIntake().loadBallToShooter(telemetryMirror);
            }
            if (pathTimer.milliseconds() >= (lastFiringTimeMs + 800) && !reset) {
                robotBase.getShooter().reset(telemetryMirror);
                lastResetTimeMs = pathTimer.milliseconds();
                reset = true;
            }

            if (shotCount == 3 && reset && pathTimer.milliseconds() >= (lastResetTimeMs + 100)) {
                robotBase.getShooter().reset(telemetryMirror);
                robotBase.getShooter().stop(telemetryMirror);
                setNextPathState(PathState.INTAKE_ROW3);
                robotBase.getIntake().stop(telemetryMirror);
                shotCount = 0;
            }
        }
    }

    protected void setNextPathState(PathState state) {
        this.pathState = state;
        if (pathTimer != null) pathTimer.reset();
    }

    // Inner custom enums and blank helper implementations
    protected enum PathState { SCORE_PRELOADED, INTAKE_ROW3 }
    public enum Alliance { RED, BLUE }
    protected void draw() { }
    protected void drawOnlyCurrent() { draw(); }
}
