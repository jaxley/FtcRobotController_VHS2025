package org.firstinspires.ftc.teamcode.robot;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.qualcomm.robotcore.hardware.DcMotorEx;

public class Odometry_3Wheel_Basic {

    private final DcMotorEx encLeft;
    private final DcMotorEx encRight;
    private final DcMotorEx encCenter;

    private int encLeft_Position;
    private int encRight_Position;
    private int encCenter_Position;

    private int heading;

    // Configurable parameters
    int trackwidth;
    int forward_offset;
    private double x_pos;
    private double y_pos;

    private int phi;

    public Odometry_3Wheel_Basic(DcMotorEx encLeft, DcMotorEx encRight, DcMotorEx encCenter) {
        this.encLeft = encLeft;
        this.encRight = encRight;
        this.encCenter = encCenter;

        x_pos = 0;
        y_pos = 0;
        phi = 0;
    }

    // Based on pseudocode from https://gm0.org/en/latest/docs/software/concepts/odometry.html

    /**
     * This should run every time you update motor speeds
     */
    public void update() {
        int delta_left_encoder_pos = encLeft.getCurrentPosition() - encLeft_Position;
        int delta_right_encoder_pos = encLeft.getCurrentPosition() - encRight_Position;
        int delta_center_encoder_pos = encLeft.getCurrentPosition() - encCenter_Position;

        phi = (delta_left_encoder_pos - delta_right_encoder_pos) / trackwidth;
        int delta_middle_pos = (delta_left_encoder_pos + delta_right_encoder_pos) / 2;
        int delta_perp_pos = delta_center_encoder_pos - forward_offset * phi;

        double delta_x = delta_middle_pos * cos(heading) - delta_perp_pos * sin(heading);
        double delta_y = delta_middle_pos * sin(heading) + delta_perp_pos * cos(heading);

        x_pos += delta_x;
        y_pos += delta_y;
        heading += phi;

        encLeft_Position = encLeft.getCurrentPosition();
        encRight_Position = encRight.getCurrentPosition();
        encCenter_Position = encCenter.getCurrentPosition();
    }
}
