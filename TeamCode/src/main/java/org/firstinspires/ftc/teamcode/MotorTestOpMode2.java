package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.impl.MotorEx;

@TeleOp
public class MotorTestOpMode2 extends NextFTCOpMode {

    private MotorEx backLeft;
    private MotorEx frontRight;
    private MotorEx frontLeft;
    private MotorEx backRight;
    private DcMotor intake;
    private DcMotorEx flywheel;
    private Servo fireServo;

    public MotorTestOpMode2() {
        addComponents(BulkReadComponent.INSTANCE, BindingsComponent.INSTANCE);
    }

    @Override
    public void onStop() {
        super.onStop();
        BindingManager.reset();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        BindingManager.update();
    }

    final double intakeFwdPower = -0.8;
    final double intakeRevPower = 0.2;
    final double triggerDZ = 0.25;

    final double flywheelSpeed = -6000; // RPM
    final double FWRPM2CPS = 28/60;


    final double fireDownPos = 0;
    final double fireUpPos= 0.5;
    final double firePeriod = 800; // ms
    double fireTime = 0;

    //control vars
    boolean intakeRunning = false;
    boolean flywheelRunning = false;

    DButton fireButton = new DButton();

    @Override
    public void onInit() {
        super.onInit();

        frontLeft = new MotorEx("frontLeft").brakeMode();
        frontRight = new MotorEx("frontRight").brakeMode();
        backLeft = new MotorEx("backLeft").brakeMode();
        backRight = new MotorEx("backRight").brakeMode();
        //+IMUEx imu = new IMUEx("imu", Direction.UP, Direction.FORWARD).zeroed();

        intake = hardwareMap.get(DcMotor.class, "intake");
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");

        fireServo = hardwareMap.get(Servo.class, "launch servo");

    }

    @Override
    public void onStartButtonPressed() {
        super.onStartButtonPressed();

        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        mecanumDriveloop();

        double lastMillis = 0;

        // main loop
        while(opModeIsActive()) {
            lastMillis = getRuntime();
            
            intakeRunning = (gamepad2.right_trigger > triggerDZ);
            if(intakeRunning) {
                intake.setPower(intakeFwdPower);
            }
            else if (gamepad2.right_bumper) {
                intake.setPower(intakeRevPower);
            }
            else {
                intake.setPower(0);
            }

            flywheelRunning = (gamepad2.left_trigger > triggerDZ);
            if(flywheelRunning) {
                flywheel.setVelocity(gamepad2.left_trigger * flywheelSpeed * FWRPM2CPS);
            }
            else {
                flywheel.setPower(0);
            }

            fireButton.update(gamepad2.x);
            if(fireButton.pressed()) {
                fireTime = firePeriod;
                fireServo.setPosition(fireUpPos);
            }
            fireTime -= getRuntime() - lastMillis;
        }
        // yield code here
    }

    private void mecanumDriveLoopNextFTC() {
          /*      Command driverControlled = new MecanumDriverControlled(
                frontLeft,
                frontRight,
                backLeft,
                frontRight,
                Gamepads.gamepad1().leftStickY().negate(),
                Gamepads.gamepad1().leftStickX(),
                Gamepads.gamepad1().rightStickX()
        );
        driverControlled.schedule();*/
    }

    public void mecanumDriveloop() {

        // Mecanum drive is controlled with three axes: drive (front-and-back),
        // strafe (left-and-right), and twist (rotating the whole chassis).
        double drive  = gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double twist  = gamepad1.right_stick_x;

        /*
         * If we had a gyro and wanted to do field-oriented control, here
         * is where we would implement it.
         *
         * The idea is fairly simple; we have a robot-oriented Cartesian (x,y)
         * coordinate (strafe, drive), and we just rotate it by the gyro
         * reading minus the offset that we read in the init() method.
         * Some rough pseudocode demonstrating:
         *
         * if Field Oriented Control:
         *     get gyro heading
         *     subtract initial offset from heading
         *     convert heading to radians (if necessary)
         *     new strafe = strafe * cos(heading) - drive * sin(heading)
         *     new drive  = strafe * sin(heading) + drive * cos(heading)
         *
         * If you want more understanding on where these rotation formulas come
         * from, refer to
         * https://en.wikipedia.org/wiki/Rotation_(mathematics)#Two_dimensions
         */

        // You may need to multiply some of these by -1 to invert direction of
        // the motor.  This is not an issue with the calculations themselves.
        double[] speeds = {
                (drive + strafe + twist),
                (drive - strafe - twist),
                (drive - strafe + twist),
                (drive + strafe - twist)
        };

        // Because we are adding vectors and motors only take values between
        // [-1,1] we may need to normalize them.

        // Loop through all values in the speeds[] array and find the greatest
        // *magnitude*.  Not the greatest velocity.
        double max = Math.abs(speeds[0]);
        for(int i = 0; i < speeds.length; i++) {
            if ( max < Math.abs(speeds[i]) ) max = Math.abs(speeds[i]);
        }

        // If and only if the maximum is outside of the range we want it to be,
        // normalize all the other speeds based on the given speed value.
        if (max > 1) {
            for (int i = 0; i < speeds.length; i++) speeds[i] /= max;
        }

        // apply the calculated values to the motors.
        frontLeft.setPower(speeds[0]);
        frontRight.setPower(speeds[1]);
        backLeft.setPower(speeds[2]);
        backRight.setPower(speeds[3]);
    }
}
