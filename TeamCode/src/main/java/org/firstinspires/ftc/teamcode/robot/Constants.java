package org.firstinspires.ftc.teamcode.robot;

public class Constants {

    public static final class Wheel {
        public static final String FRONT_LEFT = "frontLeft"; // expansion hub port 0
        public static final String FRONT_RIGHT = "frontRight"; // expansion hub port 1
        public static final String BACK_LEFT = "backLeft"; // expansion hub port 2
        public static final String BACK_RIGHT = "backRight"; // expansion hub port 3
    }

    public static final class Motor {
        public static final String INTAKE_BASE = "intake";
        public static final String FLYWHEEL = "flywheel";
        public static final String LAUNCH_SERVO = "launch servo";
        public static final String INTAKE_ASSISTANT = "intakeAssistant";
    }

    public static final class EncoderWheel {
        public static final String RIGHT = Wheel.FRONT_LEFT; // "enc_right"; // port 0
        public static final String LEFT = Wheel.BACK_LEFT; // "enc_left"; // port 2
        public static final String CENTER = Wheel.FRONT_RIGHT; // "enc_center"; // port 1
    }

}
