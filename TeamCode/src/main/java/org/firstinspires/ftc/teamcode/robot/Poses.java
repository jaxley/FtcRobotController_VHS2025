package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

import java.util.EnumMap;
import java.util.Map;

public class Poses {

    // Define the Alliance enum here so it resolves locally without external imports
    public enum Alliance {
        RED,
        BLUE
    }

    public static final double FIELD_CENTER = 70.75;
    private final EnumMap<NamedPose, Pose> posesByName;

    /**
     * Constructor that initializes all poses using a single master set of Blue Alliance coordinates.
     * The PoseFactory dynamically flips X, Y, and heading across the center point if the match is Red Alliance.
     */
    public Poses(Alliance alliance) {
        PoseFactory p = getFactoryForAlliance(alliance);

        // Define your baseline Blue Alliance coordinates here.
        // The factory automatically handles point-reflection for the Red Alliance!
        posesByName = new EnumMap<>(Map.ofEntries(
                Map.entry(NamedPose.SHOOTING_LOWER_1, p.of(59, 9, Math.toRadians(90))),
                Map.entry(NamedPose.SHOOTING_UPPER_1, p.of(59, 9, Math.toRadians(270))),
                Map.entry(NamedPose.PARK_LOWER, p.of(10.5, 94, Math.toRadians(0))),
                Map.entry(NamedPose.PARK_UPPER, p.of(10, 118, Math.toRadians(0))),
                Map.entry(NamedPose.UPPER_FLOWER, p.of(47, 127, Math.toRadians(90))),
                Map.entry(NamedPose.LOWER_FLOWER, p.of(15, 47, Math.toRadians(180))),
                Map.entry(NamedPose.UPPER_CORNER_START, p.of(59, 133, Math.toRadians(270))),
                Map.entry(NamedPose.LOWER_WALL_START, p.of(57, 8, Math.toRadians(90)))
                ));

    }

    /**
     * Retrieves a generated pose by its name.
     */
    public Pose get(NamedPose pose) {
        return posesByName.get(pose);
    }

    /**
     * Creates a configured PoseFactory for the specified alliance match state.
     * If the team is on the RED alliance, it automatically mirrors all coordinates
     * across both the X and Y center lines to form a perfect point reflection.
     */
    public static PoseFactory getFactoryForAlliance(Alliance alliance) {
        PoseFactory factory = PoseFactory.radians();

        if (alliance == Alliance.RED) {
            return factory.mirrorX(FIELD_CENTER);
        }
        return factory;
    }

    public enum NamedPose {
        SHOOTING_LOWER_1,
        SHOOTING_UPPER_1,
        PARK_LOWER,
        PARK_UPPER,
        UPPER_FLOWER,
        LOWER_FLOWER,
        UPPER_CORNER_START,
        LOWER_WALL_START
    }
}
