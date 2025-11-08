package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants;
import com.pedropathing.ftc.localization.localizers.ThreeWheelLocalizer;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robot.Constants.EncoderWheel;

public class Constants {

    public static MecanumConstants mecanumConstants = new MecanumConstants()
            .maxPower(0.5)
            .rightFrontMotorName(org.firstinspires.ftc.teamcode.robot.Constants.Wheel.FRONT_RIGHT)
            .rightRearMotorName(org.firstinspires.ftc.teamcode.robot.Constants.Wheel.BACK_RIGHT)
            .leftFrontMotorName(org.firstinspires.ftc.teamcode.robot.Constants.Wheel.FRONT_LEFT)
            .leftRearMotorName(org.firstinspires.ftc.teamcode.robot.Constants.Wheel.BACK_LEFT)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE);
    public static final double PROGRAMMING_BASE_MASS = 6.713;
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(PROGRAMMING_BASE_MASS);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    // Note: offsets are in INCHES from the robot CENTER to the CENTER of the odometry wheel.
    // Encoder direction:
    // Forward: X pods should INCREASE
    // LEFT: Y pods should INCREASE
    private final static ThreeWheelConstants localizerConstants = new ThreeWheelConstants()
            .leftEncoder_HardwareMapName(EncoderWheel.LEFT)
            .rightEncoder_HardwareMapName(EncoderWheel.RIGHT)
            .strafeEncoder_HardwareMapName(EncoderWheel.CENTER);

    public static Pose startingPose = new Pose(24, 128, Math.toRadians(37)); // TODO: Need a menu of options
    public static Follower createFollower(HardwareMap hardwareMap) { // TODO: Need ability to create followers with different starting poses
        ThreeWheelLocalizer localizer = new ThreeWheelLocalizer(hardwareMap, localizerConstants, startingPose);
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(mecanumConstants)
                .pathConstraints(pathConstraints)
                .setLocalizer(localizer)
                .build();
    }
}
