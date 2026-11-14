package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.teamcode.robot.RobotConstants;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * System check op mode: enumerates every device on the hardware map and
 * verifies that each expected device is present and of the expected type.
 * Reports a per-device PASS/FAIL summary plus full enumeration on telemetry.
 *
 * Non-destructive: never commands motors or servos, only reads configuration.
 */
@TeleOp(name = "System Check", group = "Utility")
public class SystemCheckOpMode extends LinearOpMode {

    private int passCount = 0;
    private int failCount = 0;

    @Override
    public void runOpMode() {
        telemetry.addLine("=== SYSTEM CHECK ===");
        telemetry.addLine("Press START to run the check");
        telemetry.update();
        waitForStart();

        Map<String, Class<? extends HardwareDevice>> expected = new LinkedHashMap<>();
        expected.put(RobotConstants.Wheel.FRONT_LEFT, DcMotor.class);
        expected.put(RobotConstants.Wheel.FRONT_RIGHT, DcMotor.class);
        expected.put(RobotConstants.Wheel.BACK_LEFT, DcMotor.class);
        expected.put(RobotConstants.Wheel.BACK_RIGHT, DcMotor.class);
        expected.put(RobotConstants.Motor.INTAKE_BASE, DcMotor.class);
        expected.put(RobotConstants.Motor.INTAKE_ASSISTANT, DcMotor.class);
        expected.put(RobotConstants.Motor.FLYWHEEL, DcMotorEx.class);
        expected.put(RobotConstants.Motor.LAUNCH_SERVO, Servo.class);
        expected.put(RobotConstants.Motor.COLLECT_SERVO, Servo.class);
        expected.put("limelight", Limelight3A.class);

        telemetry.addLine("\n--- Expected device checks ---");
        for (Map.Entry<String, Class<? extends HardwareDevice>> e : expected.entrySet()) {
            checkDevice(e.getKey(), e.getValue());
        }

        telemetry.addLine("\nAll configured devices:");
        for (HardwareMap.DeviceMapping<? extends HardwareDevice> mapping : hardwareMap.allDeviceMappings) {
            Class<? extends HardwareDevice> type = mapping.getDeviceTypeClass();
            for (Map.Entry<String, ? extends HardwareDevice> entry : mapping.entrySet()) {
                String name = entry.getKey();
                HardwareDevice device = entry.getValue();
                String detail = describe(device);
                boolean wasExpected = expected.containsKey(name);
                String expectedNote = wasExpected ? "" : " [UNEXPECTED - not referenced by code]";
                telemetry.addData(String.format("%-14s %-10s", name, simpleName(type)), detail + expectedNote);
            }
        }

        telemetry.addLine("\nControl hubs:");
        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            String label = hardwareMap.getNamesOf(module).stream().findFirst().orElse("hub " + module.getModuleAddress());
            telemetry.addData(label,
                    String.format("fw=%s voltage=%.2fV current=%.2fA",
                            module.getFirmwareVersionString(),
                            module.getInputVoltage(VoltageUnit.VOLTS),
                            module.getCurrent(CurrentUnit.AMPS)));
        }

        telemetry.addLine("\n==============================");
        telemetry.addData("RESULT", "%d PASS, %d FAIL %s",
                passCount, failCount, failCount == 0 ? "- SYSTEM OK" : "- SEE FAILURES ABOVE");
        telemetry.update();

        while (opModeIsActive()) {
            idle();
        }
    }

    private void checkDevice(String name, Class<? extends HardwareDevice> expectedClass) {
        HardwareDevice device = hardwareMap.tryGet(expectedClass, name);

        if (device == null) {
            HardwareDevice any = hardwareMap.tryGet(HardwareDevice.class, name);
            if (any != null) {
                fail(String.format("%-14s WRONG TYPE: configured as %s, code expects %s",
                        name, simpleName(any.getClass()), simpleName(expectedClass)));
            } else {
                fail(String.format("%-14s MISSING from hardware configuration", name));
            }
            return;
        }

        StringBuilder msg = new StringBuilder(simpleName(device.getClass()));

        DcMotor motor = asOrNull(device, DcMotor.class);
        if (motor != null) {
            MotorConfigurationType type = motor.getMotorType();
            msg.append(String.format(" | type=%s ticks/rev=%.0f gearing=%.1f",
                    type.getXmlTag(), type.getTicksPerRev(), type.getGearing()));
            DcMotorSimple.Direction dir = motor.getDirection();
            msg.append(String.format(" | dir=%s (config default; Pedro sets %s in software)",
                    dir, name.equals(RobotConstants.Wheel.FRONT_RIGHT) || name.equals(RobotConstants.Wheel.BACK_RIGHT)
                            ? "REVERSE" : "FORWARD"));
        }
        Servo servo = asOrNull(device, Servo.class);
        if (servo != null) {
            msg.append(String.format(" | dir=%s", servo.getDirection()));
        }

        pass(String.format("%-14s %s", name, msg));
    }

    private String describe(HardwareDevice device) {
        StringBuilder sb = new StringBuilder(simpleName(device.getClass()));
        DcMotor motor = asOrNull(device, DcMotor.class);
        if (motor != null) {
            MotorConfigurationType type = motor.getMotorType();
            sb.append(String.format(" | ticks/rev=%.0f gearing=%.1f dir=%s",
                    type.getTicksPerRev(), type.getGearing(), motor.getDirection()));
        }
        Servo servo = asOrNull(device, Servo.class);
        if (servo != null) {
            sb.append(String.format(" | dir=%s", servo.getDirection()));
        }
        return sb.toString();
    }

    private void pass(String msg) {
        passCount++;
        telemetry.addData("PASS", msg);
    }

    private void fail(String msg) {
        failCount++;
        telemetry.addData("FAIL", msg);
    }

    private static String simpleName(Class<?> c) {
        String n = c.getSimpleName();
        return n.isEmpty() ? c.getName() : n;
    }

    @SuppressWarnings("unchecked")
    private static <T> T asOrNull(Object o, Class<T> type) {
        return type.isInstance(o) ? (T) o : null;
    }
}
