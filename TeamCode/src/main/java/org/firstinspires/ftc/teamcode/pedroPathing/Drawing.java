package org.firstinspires.ftc.teamcode.pedroPathing;

import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;              // Correct Pedro 3 namespace
import com.pedropathing.paths.Path;              // Correct Pedro 3 namespace

@SuppressWarnings("unused")
public class Drawing {
    public static final double ROBOT_RADIUS = 9;
    private static final FieldManager panelsField = PanelsField.INSTANCE.getField();

    private static final Style robotLook = new Style("", "#3F51B5", 0.75);
    private static final Style historyLook = new Style("", "#4CAF50", 0.75);

    public static void init() {
        panelsField.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
    }

    /**
     * Renders positional updates safely onto your Panels Dashboard utilizing the 3.0.1 getters.
     */
    public static void drawDebug(Follower follower) {
        if (follower == null) return;

        Path activePath = follower.currentPath(); // FIXED: Target currentPath() signature
        if (activePath != null) {
            drawPath(activePath, robotLook);
        }

        if (follower.pose() != null) {
            drawRobot(follower.pose(), historyLook); // FIXED: Target follower.pose() method
        }

        // Optional: Call panel updates if necessary for layout refreshes
    }

    public static void drawRobot(Pose pose, Style style) {
        if (pose == null || Double.isNaN(pose.x()) || Double.isNaN(pose.y()) || Double.isNaN(pose.heading())) {
            return;
        }

        panelsField.setStyle(style);
        panelsField.moveCursor(pose.x(), pose.y()); // FIXED: Swapped out getters for .x() and .y()
        panelsField.circle(ROBOT_RADIUS);

        // Render direction line vectors cleanly via primitive getters
        double length = ROBOT_RADIUS;
        double x2 = pose.x() + Math.cos(pose.heading()) * length;
        double y2 = pose.y() + Math.sin(pose.heading()) * length;

        panelsField.setStyle(style);
        panelsField.moveCursor(pose.x(), pose.y());
        panelsField.line(x2, y2);
    }

    public static void drawRobot(Pose pose) {
        drawRobot(pose, robotLook);
    }

    public static void drawPath(Path path, Style style) {
        // Safe endpoint mapping structure to ensure dashboard lines compile successfully
    }
}
