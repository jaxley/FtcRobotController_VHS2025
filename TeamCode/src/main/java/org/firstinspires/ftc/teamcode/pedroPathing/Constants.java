package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants;
import com.pedropathing.ftc.localization.localizers.ThreeWheelLocalizer;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robot.Constants.EncoderWheel;

public class Constants {

    public static MecanumConstants mecanumConstants = new MecanumConstants(); // TODO: need to set hardware map names, etc.
    public static FollowerConstants followerConstants = new FollowerConstants();

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    private final static ThreeWheelConstants localizerConstants = new ThreeWheelConstants()
            .leftEncoder_HardwareMapName(EncoderWheel.LEFT)
            .rightEncoder_HardwareMapName(EncoderWheel.RIGHT)
            .strafeEncoder_HardwareMapName(EncoderWheel.CENTER);

    public static Pose startingPose = new Pose(); // TODO: Need a menu of options
    public static Follower createFollower(HardwareMap hardwareMap) { // TODO: Need ability to create followers with differen starting poses
        ThreeWheelLocalizer localizer = new ThreeWheelLocalizer(hardwareMap, localizerConstants, startingPose);
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(mecanumConstants)
                .pathConstraints(pathConstraints)
                .setLocalizer(localizer)
                .build();
    }
}
