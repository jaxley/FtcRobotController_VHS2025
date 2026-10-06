package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants;
import com.pedropathing.ftc.localization.localizers.ThreeWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robot.RobotConstants;
import org.firstinspires.ftc.teamcode.robot.RobotConstants.EncoderWheel;

public class Constants {

    public static double AUTONOMOUS_MOTOR_MAX_POWER = 0.5;
    public static double TELEOP_MOTOR_MAX_POWER = 0.8;

    // Preserved your exact wheel hardware maps, directions, and velocity constraints
    public static MecanumConstants mecanumConstants = new MecanumConstants()
            .maxPower(TELEOP_MOTOR_MAX_POWER)
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

    public static final double COMPETITION_BASE_MASS = 10.16047;
    public static final double ROBOT_BASE_MASS = COMPETITION_BASE_MASS;

    // Preserved your exact three-wheel odometry pod names, encoder directions, ticks-to-inches coefficients, and physical geometries
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
            .turnTicksToInches(-0.0019898844520130614);


    // Make sure this block is declared inside the class so createFollower can see it
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                com.pedropathing.controllers.Controller primaryTranslationalForward = com.pedropathing.controllers.Controller.proportional(0.3);
                com.pedropathing.controllers.Controller secondaryTranslationalForward = com.pedropathing.controllers.Controller.proportional(0.1);
                com.pedropathing.controllers.Controller primaryTranslationalLateral = com.pedropathing.controllers.Controller.proportional(0.3);
                com.pedropathing.controllers.Controller secondaryTranslationalLateral = com.pedropathing.controllers.Controller.proportional(0.1);
                c.forwardTranslational.set(com.pedropathing.controllers.Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(com.pedropathing.controllers.Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));
                c.coast.set(com.pedropathing.controllers.Controller.proportionalFeedforward(0.01));
                c.brake.set(com.pedropathing.controllers.Controller.proportionalFeedforward(0.01));
                c.headingFeedback.set(com.pedropathing.controllers.Controller.proportional(5.0));
                c.headingBrakeCoefficients.set(com.pedropathing.math.Vector2D.cartesian(0.05, 0.006));
                c.linearBrakeCoefficients.set(com.pedropathing.math.Matrix.diag(0.1, 0.08));
                c.quadraticBrakeCoefficients.set(com.pedropathing.math.Matrix.diag(0.001, 0.001));
                c.maxAchievableForwardVelocity.set(71.7335037882414);
                c.maxAchievableStrafeVelocity.set(45.0864789564824);
                c.naturalForwardDeceleration.set(85.0);
                c.naturalStrafeDeceleration.set(104.0);
            }
    );

    public static MecanumConfig mecanumConfig = new MecanumConfig(
            c -> {
                c.frontLeftName.set("lf");
                c.backLeftName.set("lr");
                c.frontRightName.set("rf");
                c.backRightName.set("rr");
                c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
                c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
                c.manualBrakeMode.set(true);
            }
    );

    /**
     * Initializes the Follower instance by supplying your hardware maps and configurations
     * through the official two-argument constructor pattern to eliminate compilation errors.
     */
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new Follower(
                new ThreeWheelLocalizer(hardwareMap, localizerConstants),
                new Mecanum(hardwareMap, mecanumConfig),
                new Foresight(foresightConfig)
        );
    }
    /**
     * Shorthand creator mapper to support your generated filedump initialization classes seamlessly.
     */
    public static Follower create(HardwareMap hardwareMap) {
        return createFollower(hardwareMap);
    }
}
