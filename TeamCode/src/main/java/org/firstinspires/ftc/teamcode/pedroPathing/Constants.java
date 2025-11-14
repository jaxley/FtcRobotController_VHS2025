package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants;
import com.pedropathing.ftc.localization.localizers.ThreeWheelLocalizer;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robot.RobotConstants;
import org.firstinspires.ftc.teamcode.robot.RobotConstants.EncoderWheel;

public class Constants {

    public static double AUTONOMOUS_MOTOR_MAX_POWER = 0.5;
    public static double TELEOP_MOTOR_MAX_POWER = 0.8;

    public static MecanumConstants mecanumConstants = new MecanumConstants()
            .maxPower(AUTONOMOUS_MOTOR_MAX_POWER)
            .rightFrontMotorName(RobotConstants.Wheel.FRONT_RIGHT)
            .rightRearMotorName(RobotConstants.Wheel.BACK_RIGHT)
            .leftFrontMotorName(RobotConstants.Wheel.FRONT_LEFT)
            .leftRearMotorName(RobotConstants.Wheel.BACK_LEFT)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .xVelocity(71.7335037882414)
            .yVelocity(45.0864789564824);

    public static final double PROGRAMMING_BASE_MASS = 6.713;
    public static final double COMPETITION_BASE_MASS = 10.16047; // TODO - set this back for competition. should be runtime configurable
    public static final double ROBOT_BASE_MASS = PROGRAMMING_BASE_MASS;


    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(ROBOT_BASE_MASS)
            .forwardZeroPowerAcceleration(-38.72702008081527)
            .lateralZeroPowerAcceleration(-32.344756938835744)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.09,0,0.0015,0.021))
            .headingPIDFCoefficients(new PIDFCoefficients(0,0,0,0.033));

    public static PathConstraints pathConstraints = new PathConstraints(
            0.99,
            100,
            1.5,
            1);

    // Note: offsets are in INCHES from the robot CENTER to the CENTER of the odometry wheel.
    // Encoder direction:
    // Forward: X pods should INCREASE
    // LEFT: Y pods should INCREASE
    // NOTE: when tuning, perform the action of moving the robot AFTER init but BEFORE start.
    private final static ThreeWheelConstants localizerConstants = new ThreeWheelConstants()
            .leftEncoder_HardwareMapName(EncoderWheel.LEFT)
            .rightEncoder_HardwareMapName(EncoderWheel.RIGHT)
            .strafeEncoder_HardwareMapName(EncoderWheel.CENTER)
            .leftEncoderDirection(Encoder.REVERSE)
            .rightEncoderDirection(Encoder.REVERSE)
            .strafeEncoderDirection(Encoder.FORWARD)
            .leftPodY(2.625)
            .rightPodY(-3)
            .strafePodX(-7.25)
            .forwardTicksToInches(-0.0020663255536204467)
            .strafeTicksToInches(-0.0019800424427746676)
            .turnTicksToInches(0.0019798844520130614);

    public static Follower createFollower(HardwareMap hardwareMap) {
        ThreeWheelLocalizer localizer = new ThreeWheelLocalizer(hardwareMap, localizerConstants);
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(mecanumConstants)
                .pathConstraints(pathConstraints)
                .setLocalizer(localizer)
                .build();
    }
}
