package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.api.PoseFactory; // Pedro Pathing 3 pose factory import
// FIX: Pointing to the new package location of your custom Alliance enum
import org.firstinspires.ftc.teamcode.AutonomousOpMode.Alliance;

@SuppressWarnings("unused") // Silences the "never used" warnings until your auto script references them
public class Poses {

    // The field is 141.5 x 141.5, so the exact center split line is 70.75
    public static final double FIELD_CENTER = 70.75;

    /**
     * Creates a configured PoseFactory for the specified alliance match state.
     * If the team is on the RED alliance, it automatically mirrors all coordinates
     * across the center line.
     */
    public static PoseFactory getFactoryForAlliance(Alliance alliance) {
        // Your code uses Math.toRadians, so create a Radians-based factory
        PoseFactory factory = PoseFactory.radians();

        if (alliance == Alliance.RED) {
            // FIXED: Re-assigning the mirrored factory returned by the method call
            return factory.mirrorX(FIELD_CENTER);
        }

        return factory;
    }

    /**
     * Helper container holding your single set of base coordinates.
     * Your code can reference these directly by passing them into your alliance's factory.
     */
    public static class BaseCoordinates {
        // Starting positions
        public static final double STARTING_WALL_1_X = 12.0;
        public static final double STARTING_WALL_1_Y = 36.0;
        public static final double STARTING_WALL_1_H = 0.0;

        public static final double STARTING_WALL_2_X = 12.0;
        public static final double STARTING_WALL_2_Y = 108.0;
        public static final double STARTING_WALL_2_H = 0.0;

        // Element destinations
        public static final double BLUE_HIVE_X = 24.0;
        public static final double BLUE_HIVE_Y = 72.0;
        public static final double BLUE_HIVE_H = 0.0;

        public static final double FLOWER_1_X = 36.0;
        public static final double FLOWER_1_Y = 36.0;
        public static final double FLOWER_1_H = 0.0;

        public static final double POLLEN_STAGING_A_X = 48.0;
        public static final double POLLEN_STAGING_A_Y = 72.0;
        public static final double POLLEN_STAGING_A_H = Math.PI; // 180 degrees
    }
}
