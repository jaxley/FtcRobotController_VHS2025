package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.pedroPathing.Alliance;

import java.security.InvalidParameterException;
import java.util.EnumMap;
import java.util.Map;

public class Poses {

    /**
     * Get poses for a specific alliance
     * @param alliance What alliance our team is on for this match
     * @return the poses appropriate for the alliance
     */
    public static AlliancePoses forAlliance(Alliance alliance) {
        switch(alliance) {
            case RED:
                return RedPoses.getInstance();
            case BLUE:
                return BluePoses.getInstance();
        }
        throw new InvalidParameterException("Invalid alliance: " + alliance);
    }

    public abstract static class AlliancePoses {
        public abstract Pose get(NamedPose pose);
    }

    public static class BluePoses extends AlliancePoses {
        private static BluePoses INSTANCE;

        public BluePoses() {
            posesByName.put(NamedPose.SHOOTING_GOAL_TOP_1,
                    this.get(NamedPose.STARTING_TOP_1)); // alias for the same pose
            posesByName.put(NamedPose.SHOOTING_GOAL_TOP_2,
                    this.get(NamedPose.STARTING_TOP_2)); // alias for the same pos
            posesByName.put(NamedPose.SHOOTING_GOAL_SIDE_1,
                    this.get(NamedPose.STARTING_SIDE_1));
        }

        public static AlliancePoses getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new BluePoses();
            }
            return INSTANCE;
        }

        private final EnumMap<NamedPose,Pose> posesByName = new EnumMap<>(Map.ofEntries(
                Map.entry(NamedPose.STARTING_TOP_1, new Pose()),
                Map.entry(NamedPose.STARTING_TOP_2, new Pose()),
                Map.entry(NamedPose.STARTING_SIDE_1, new Pose()),
                Map.entry(NamedPose.SHOOTING_GOAL_TOP_1,
                        this.get(NamedPose.STARTING_TOP_1)), // alias for the same pose
                Map.entry(NamedPose.SHOOTING_GOAL_TOP_2,
                        this.get(NamedPose.STARTING_TOP_2)), // alias for the same pose
                Map.entry(NamedPose.SHOOTING_GOAL_SIDE_1,
                        this.get(NamedPose.STARTING_SIDE_1)), // alias for the same pose
                Map.entry(NamedPose.INTAKE_ROW_1_START, new Pose(40, 37, Math.toRadians(180))),
                Map.entry(NamedPose.INTAKE_ROW_2_START, new Pose(40, 61, Math.toRadians(180))),
                Map.entry(NamedPose.INTAKE_ROW_3_START, new Pose(40, 82.625, Math.toRadians(180))),
                Map.entry(NamedPose.INTAKE_ROW_1_END, new Pose(24, 37, Math.toRadians(180))),
                Map.entry(NamedPose.INTAKE_ROW_2_END, new Pose(24, 61, Math.toRadians(180))),
                Map.entry(NamedPose.INTAKE_ROW_3_END, new Pose(24, 82.625, Math.toRadians(180))),
                Map.entry(NamedPose.LOADING, new Pose(15, 10, Math.toRadians(0))),
                Map.entry(NamedPose.LEAVE_TOP, new Pose()),
                Map.entry(NamedPose.LEAVE_LOW, new Pose(36, 8.5, Math.toRadians(90))),
                Map.entry(NamedPose.STARTING_LOW, new Pose()),
                Map.entry(NamedPose.BASE, new Pose())
        ));

        @Override
        public Pose get(NamedPose pose) {
            return posesByName.get(pose);
        }
    }

    public static class RedPoses extends AlliancePoses {
        private static RedPoses INSTANCE;

        private RedPoses() {
            posesByName.put(NamedPose.SHOOTING_GOAL_TOP_1,
                    this.get(NamedPose.STARTING_TOP_1)); // alias for the same pose
            posesByName.put(NamedPose.SHOOTING_GOAL_TOP_2,
                            this.get(NamedPose.STARTING_TOP_2)); // alias for the same pos
            posesByName.put(NamedPose.SHOOTING_GOAL_SIDE_1,
                            this.get(NamedPose.STARTING_SIDE_1)); // alias for the same pose
        }

        private final EnumMap<NamedPose,Pose> posesByName = new EnumMap<>(Map.ofEntries(
                Map.entry(NamedPose.STARTING_TOP_1, new Pose()),
                Map.entry(NamedPose.STARTING_TOP_2, new Pose(116, 132, Math.toRadians(38))), // in front of red goal
                Map.entry(NamedPose.STARTING_SIDE_1, new Pose()),
                Map.entry(NamedPose.INTAKE_ROW_1_START, new Pose(104, 37, Math.toRadians(0))),
                Map.entry(NamedPose.INTAKE_ROW_2_START, new Pose(104, 61, Math.toRadians(0))),
                Map.entry(NamedPose.INTAKE_ROW_3_START, new Pose(104, 85, Math.toRadians(0))),
                Map.entry(NamedPose.INTAKE_ROW_1_END, new Pose(120, 37, Math.toRadians(0))),
                Map.entry(NamedPose.INTAKE_ROW_2_END, new Pose(120, 61, Math.toRadians(0))),
                Map.entry(NamedPose.INTAKE_ROW_3_END, new Pose(120, 85, Math.toRadians(0))),
                Map.entry(NamedPose.LOADING, new Pose(130, 10, Math.toRadians(180))),
                Map.entry(NamedPose.LEAVE_TOP, new Pose(126, 110, Math.toRadians(38))), // TODO - verify
                Map.entry(NamedPose.LEAVE_LOW, new Pose(108 ,8.5, Math.toRadians(90))),
                Map.entry(NamedPose.STARTING_LOW, new Pose()),
                Map.entry(NamedPose.BASE, new Pose())
        ));

        public static AlliancePoses getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new RedPoses();
            }
            return INSTANCE;
        }

        @Override
        public Pose get(NamedPose pose) {
            return posesByName.get(pose);
        }
    }

    public enum NamedPose {
        STARTING_TOP_1,
        STARTING_TOP_2,
        STARTING_SIDE_1,
        SHOOTING_GOAL_TOP_1,
        SHOOTING_GOAL_TOP_2,
        SHOOTING_GOAL_SIDE_1,
        INTAKE_ROW_1_START,
        INTAKE_ROW_1_END,
        INTAKE_ROW_2_START,
        INTAKE_ROW_2_END,
        INTAKE_ROW_3_START,
        INTAKE_ROW_3_END,
        LOADING,
        LEAVE_TOP,
        LEAVE_LOW,
        STARTING_LOW,
        BASE
    }

    // Verify or create these using https://visualizer.pedropathing.com
    public static final Pose redShootingPose = new Pose(128, 80, Math.toRadians(90)); // TODO - incorrect - this is on top of the first row of balls
    public static final Pose blueleavePose = new Pose(125, 100, Math.toRadians(200)); // This is near the red goal.
    public static final Pose blueShootingPose = new Pose(16, 80, Math.toRadians(90)); // TODO - incorrect. same issue as redShootingPose (on top of first row of balls)
    public static final Pose redStartingPoseTop_D6 = new Pose(81, 135, Math.toRadians(0));
    public static final Pose redStartingPoseTop_E6 = new Pose(110, 135, Math.toRadians(0));
    public static final Pose redStartingPoseLow_D1 = new Pose(83, 8.5, Math.toRadians(90));
    public static final Pose blueStartingPoseTop_C6 = new Pose(63, 135, Math.toRadians(180));
    public static final Pose blueStartingPoseTop_B6 = new Pose(33, 135, Math.toRadians(180));
    public static final Pose blueStartingPoseLow_C1 = new Pose(60, 8.5, Math.toRadians(90));

    public static final Pose redTopLeavePose = new Pose(98, 120, Math.toRadians(0)); // TODO - This is wrong. Still inside the launch zone

    // missing poses:)
    // 3.TODO - we need poses for auto-parking after teleop (red and blue bases)
    // 4. ?
}
