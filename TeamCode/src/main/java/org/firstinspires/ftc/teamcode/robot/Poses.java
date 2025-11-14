package org.firstinspires.ftc.teamcode.robot;

import com.pedropathing.geometry.Pose;

public class Poses {
    // Verify or create these using https://visualizer.pedropathing.com
    public static final Pose redShootingPose = new Pose(128, 80, Math.toRadians(90)); // TODO - incorrect - this is on top of the first row of balls
    public static final Pose redLoadingPose = new Pose(130, 10, Math.toRadians(180)); // TODO - correct location WRONG alliance. This is on the blue alliance side (goals are opposite the alliance areas)
    public static final Pose blueleavePose = new Pose(125, 100, Math.toRadians(200)); // TODO - correct location WRONG alliance. This is near the red goal.
    //Pose blueShootingPose = new Pose(72, 84, Math.toRadians(135));
    public static final Pose blueShootingPose = new Pose(16, 80, Math.toRadians(90)); // TODO - incorrect. same issue as redShootingPose but opposite side
    public static final Pose blueLoadingPose = new Pose(15, 10, Math.toRadians(0)); // TODO - correct location WRONG alliance.
    public static final Pose blueLowLeavePose = new Pose(20, 8.5, Math.toRadians(330)); // TODO - incorrect. 20 is too far left. make it mirror of redLowLeavePose
    public static final Pose redIntakePoseR1 = new Pose(104, 37, Math.toRadians(0)); // correct. R1 is bottom of the field
    public static final Pose blueIntakePoseR1 = new Pose(40, 37, Math.toRadians(180)); // correct.
    public static final Pose redIntakePoseR2 = new Pose(104, 61, Math.toRadians(0)); // correct.
    public static final Pose blueIntakePoseR2 = new Pose(40, 61, Math.toRadians(180)); // correct.
    public static final Pose redIntakePoseR3 = new Pose(104, 85, Math.toRadians(0)); // correct.
    public static final Pose blueIntakePoseR3 = new Pose(40, 85, Math.toRadians(180)); //correct.
    public static final Pose blueStartingPose1 = new Pose(60, 8.5, Math.toRadians(90)); // TODO - correct location - WRONG alliance. This is red side. lower launch zone.
    public static final Pose redStartingPose2 = new Pose(108, 134, Math.toRadians(37)); // TODO - WRONG alliance - this is in front of red goal.
    public static final Pose redStartingPose1 = new Pose(83, 8.5, Math.toRadians(90)); // TODO - correct location - WRONG aliance. Swap with blueStartingPose1
    public static final Pose blueStartingPose2 = new Pose(24, 128, Math.toRadians(37)); // TODO - WRONG alliance. in front of blue goal. Swap with redStartingPose2
    public static final Pose redLowLeavePose = new Pose(108 ,8.5, Math.toRadians(90)); // TODO - correct location. WRONG alliance. Swap with blueLowLeavePose
    public static final Pose redTopLeavePose = new Pose(98, 120, Math.toRadians(0)); // TODO - This is wrong. Still inside the launch zone

    // missing poses:
    // 1.TODO - New red and blue STARTING poses in the launch zone touching the top edge of the field - ready to fire. (90 or 180 degree heading)
    // 2.TODO - New red and blue SHOOTING poses in the launch zone touching the top edge of the field (can we shoot from starting pose?) (90 or 180 degree heading)
    // 3.TODO - we need poses for auto-parking after teleop (red and blue bases)
    // 4. ?
}
