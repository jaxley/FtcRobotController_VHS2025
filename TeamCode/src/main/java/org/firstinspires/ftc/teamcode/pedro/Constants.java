package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.config.Configuration;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.Encoder;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.revhub.localizers.ThreeWheelConfig;
import com.pedropathing.revhub.localizers.ThreeWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.robot.RobotBase;

public class Constants {
    public static final MecanumConfig DRIVETRAIN_CONFIG = new MecanumConfig(c -> {
        c.frontLeftName.set("frontLeft");
        c.frontRightName.set("frontRight");
        c.backLeftName.set("backLeft");
        c.backRightName.set("backRight");
        c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.frontRightDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);
    });
    // 6 inches L and R; -6 3/4 strafe
    public static ThreeWheelConfig LOCALIZER_CONFIG_ALPHA = new ThreeWheelConfig(c -> {
        c.leftEncoderName.set("frontLeft");
        c.rightEncoderName.set("frontRight");
        c.strafeEncoderName.set("backLeft");
        c.leftPodY.set(6.0);
        c.rightPodY.set(-6.0);
        c.strafePodX.set(6.75); // flipped 2026-11-13: sign was backwards, caused arc during in-place spin
        c.forwardTicksToInches.set(0.0021440409972891573);
        c.strafeTicksToInches.set(0.00198); // 12 from tuning.... sign via strafeEncoderDirection; positive so left-strafe reads +y
        c.turnTicksToRadians.set(0.002200094356695412);
        c.leftEncoderDirection.set(Encoder.FORWARD);
        c.rightEncoderDirection.set(Encoder.FORWARD);
        c.strafeEncoderDirection.set(Encoder.REVERSE);
    });

    public static ThreeWheelConfig LOCALIZER_CONFIG_BETA = new ThreeWheelConfig(c -> {
        c.leftEncoderName.set("frontLeft");
        c.rightEncoderName.set("frontRight");
        c.strafeEncoderName.set("backLeft");
        c.leftPodY.set(2.625);
        c.rightPodY.set(-3.0);
        c.strafePodX.set(7.25);
        c.forwardTicksToInches.set(0.0019179912279850189);
        c.strafeTicksToInches.set(0.00198);
        c.turnTicksToRadians.set(0.0023111587484619923);
        c.leftEncoderDirection.set(Encoder.REVERSE);
        c.rightEncoderDirection.set(Encoder.REVERSE);
        c.strafeEncoderDirection.set(Encoder.FORWARD);
    });

    /**
     * Picks the localizer config from the robot identified by RobotBase so the same
     * build runs on both robots. Unknown robots fall back to alphabot's config.
     */
    protected static ThreeWheelConfig localizerConfigFor(HardwareMap h) {
        switch (RobotBase.robotId(h)) {
            case BETABOT:
                return LOCALIZER_CONFIG_BETA;
            case ALPHABOT:
            default:
                return LOCALIZER_CONFIG_ALPHA;
        }
    }

    public static ForesightConfig FORESIGHT_CONFIG = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.29045322951002);
                Controller secondaryTranslationalForward = Controller.proportional(0.10731470725072254);
                Controller primaryTranslationalLateral = Controller.proportional(1.5288827609437323);
                Controller secondaryTranslationalLateral = Controller.proportional(0.5648813276689453);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.01622539416515143));
                c.brake.set(Controller.proportionalFeedforward(0.013791585040378716));

                c.headingFeedback.set(Controller.proportional(3.8324365674526284));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.06055424695733336, 0.01343716271217678));

                c.linearBrakeCoefficients.set(Matrix.diag(0.05873012126447079, 0.07580736505734514));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.003993049831582597, 0.0029256752426710386));

                c.maxAchievableForwardVelocity.set(59.37039510186936);
                c.maxAchievableStrafeVelocity.set(60.505292675616644);
                c.naturalForwardDeceleration.set(19.62400987197542);
                c.naturalStrafeDeceleration.set(37.46025346907823);
            }
    );

    public static Follower create(HardwareMap h) {
        return new Follower(
                new ThreeWheelLocalizer(h, localizerConfigFor(h)),
                new Mecanum(h, DRIVETRAIN_CONFIG),
                new Foresight(FORESIGHT_CONFIG)
        );
        //return null;
    }
}
