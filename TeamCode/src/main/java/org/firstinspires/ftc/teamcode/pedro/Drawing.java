package org.firstinspires.ftc.teamcode.pedro;

import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.paths.Path;

/**
 * This is the Drawing class. It handles the drawing of stuff on Panels Dashboard, like the robot.
 *
 * Ported from the 2025-code-compile-fix branch (Pedro Pathing 2.0.4) to Pedro Pathing 3 /
 * Panels field 0.3.2. PP3's Follower does not expose a pose history, so that overlay is gone;
 * paths are drawn by sampling the path instead of getPanelsDrawingPoints().
 */
public class Drawing {
    public static final double ROBOT_RADIUS = 9; // woah
    public static final int PATH_SAMPLES = 100;

    private static final FieldManager panelsField = PanelsField.INSTANCE.getField();

    private static final Style robotLook = new Style(
            "", "#3F51B5", 0.75
    );
    private static final Style historyLook = new Style(
            "", "#4CAF50", 0.75
    );

    /**
     * This prepares Panels Field for using Pedro Offsets
     */
    public static void init() {
        panelsField.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
    }

    /**
     * This draws everything used for debugging the follower: the current path,
     * the robot's closest point on it, and the robot itself.
     *
     * @param follower Pedro Follower instance.
     */
    public static void drawDebug(Follower follower) {
        if (follower.currentPath() != null) {
            drawPath(follower.currentPath(), robotLook);
            Pose closestPoint = follower.closestPose();
            drawRobot(new Pose(closestPoint.x(), closestPoint.y(), follower.currentPath().heading(follower.completion())), robotLook);
        }
        drawRobot(follower.pose(), historyLook);

        sendPacket();
    }

    /**
     * This draws a robot at a specified Pose with a specified
     * look. The heading is represented as a line.
     *
     * @param pose  the Pose to draw the robot at
     * @param style the parameters used to draw the robot with
     */
    public static void drawRobot(Pose pose, Style style) {
        if (pose == null || Double.isNaN(pose.x()) || Double.isNaN(pose.y()) || Double.isNaN(pose.heading())) {
            return;
        }

        panelsField.setStyle(style);
        panelsField.moveCursor(pose.x(), pose.y());
        panelsField.circle(ROBOT_RADIUS);

        Vector2D v = Vector2D.unit(pose.heading()).times(ROBOT_RADIUS);
        double x1 = pose.x() + v.x() / 2, y1 = pose.y() + v.y() / 2;
        double x2 = pose.x() + v.x(), y2 = pose.y() + v.y();

        panelsField.setStyle(style);
        panelsField.moveCursor(x1, y1);
        panelsField.line(x2, y2);
    }

    /**
     * This draws a robot at a specified Pose. The heading is represented as a line.
     *
     * @param pose the Pose to draw the robot at
     */
    public static void drawRobot(Pose pose) {
        drawRobot(pose, robotLook);
    }

    /**
     * This draws a Path with a specified look by sampling it.
     *
     * @param path  the Path to draw
     * @param style the parameters used to draw the Path with
     */
    public static void drawPath(Path path, Style style) {
        Pose first = path.get(0);
        panelsField.setStyle(style);
        panelsField.moveCursor(first.x(), first.y());
        for (int i = 1; i <= PATH_SAMPLES; i++) {
            Pose p = path.get((double) i / PATH_SAMPLES);
            panelsField.line(p.x(), p.y());
            panelsField.moveCursor(p.x(), p.y());
        }
    }

    /**
     * This tries to send the current packet to FTControl Panels.
     */
    public static void sendPacket() {
        panelsField.update();
    }
}
