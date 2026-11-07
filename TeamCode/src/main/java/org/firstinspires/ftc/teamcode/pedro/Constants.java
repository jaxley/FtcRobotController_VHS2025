package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.follower.Follower;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.Encoder;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.revhub.localizers.ThreeWheelConfig;
import com.pedropathing.revhub.localizers.ThreeWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static final MecanumConfig DRIVETRAIN_CONFIG = new MecanumConfig(c -> {
        c.frontLeftName.set("frontLeft");
        c.frontRightName.set("frontRight");
        c.backLeftName.set("backLeft");
        c.backRightName.set("backRight");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });
    public static final ThreeWheelConfig LOCALIZER_CONFIG = new ThreeWheelConfig(c -> {
        c.leftEncoderName.set("frontLeft");
        c.rightEncoderName.set("frontRight");
        c.strafeEncoderName.set("backLeft");
        c.leftPodY.set(1.6654660355967688);
        c.rightPodY.set(-4.035092871445847);
        c.strafePodX.set(-0.010641253490732056);
        c.forwardTicksToInches.set(0.0019751664259094496);
        c.strafeTicksToInches.set(6.857142857142857);
        c.turnTicksToRadians.set(0.0024763321327047625);
        c.leftEncoderDirection.set(Encoder.FORWARD);
        c.rightEncoderDirection.set(Encoder.FORWARD);
        c.strafeEncoderDirection.set(Encoder.REVERSE);
    });

    public static Follower create(HardwareMap h) {
   /*     return new Follower(
                new ThreeWheelLocalizer(h, LOCALIZER_CONFIG),
                new Mecanum(h, DRIVETRAIN_CONFIG),
                new Foresight(FORESIGHT_CONFIG)
        );*/
return null;
    }
}