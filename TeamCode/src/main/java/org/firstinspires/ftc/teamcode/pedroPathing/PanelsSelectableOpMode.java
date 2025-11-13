package com.pedropathing.telemetry;

import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.telemetry.SelectScope;
import com.pedropathing.telemetry.Selector;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.utils.DButton;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This is a fork of the pedro pathing SelectableOpMode class that integrates with panels
 * for using the virtual gamepads.
 */
public abstract class PanelsSelectableOpMode extends OpMode {
    private final Selector<Supplier<OpMode>> selector;
    private final TelemetryManager telemetryM;
    private OpMode selectedOpMode;

    DButton rBumper = new DButton();
    DButton lBumper = new DButton();
    DButton dpadUp = new DButton();
    DButton dpadDown = new DButton();
    protected Gamepad panelsGamepad;
    private final static String[] MESSAGE = {
            "Use the d-pad to move the cursor.",
            "Press right bumper to select.",
            "Press left bumper to go back."
    };

    public PanelsSelectableOpMode(String name, Consumer<SelectScope<Supplier<OpMode>>> opModes) {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        selector = Selector.create(name, opModes, MESSAGE);
        selector.onSelect(opModeSupplier -> {
            onSelect();
            selectedOpMode = opModeSupplier.get();
            selectedOpMode.gamepad1 = gamepad1;
            selectedOpMode.gamepad2 = gamepad2;
            selectedOpMode.telemetry = telemetry;
            selectedOpMode.hardwareMap = hardwareMap;
            selectedOpMode.init();
        });
    }

    protected void onSelect() {
    }

    protected void onLog(List<String> line) {
    }

    @Override
    public final void init() {
        panelsGamepad = PanelsGamepad.INSTANCE.getFirstManager().asCombinedFTCGamepad(gamepad1);
    }

    @Override
    public final void init_loop() {
        panelsGamepad = PanelsGamepad.INSTANCE.getFirstManager().asCombinedFTCGamepad(gamepad1);
        Gamepad panelsGamepad2 = PanelsGamepad.INSTANCE.getFirstManager().getAsFTCGamepad();
        if (selectedOpMode == null) {
            rBumper.update(panelsGamepad.right_bumper);
            lBumper.update(panelsGamepad.left_bumper);
            dpadUp.update(panelsGamepad.dpad_up);
            dpadDown.update(panelsGamepad.dpad_down);
            if (dpadUp.pressed() || panelsGamepad2.dpadUpWasPressed())
                selector.decrementSelected();
            else if (dpadDown.pressed() || panelsGamepad2.dpadDownWasPressed())
                selector.incrementSelected();
            else if (rBumper.pressed() || panelsGamepad2.rightBumperWasPressed())
                selector.select();
            else if (lBumper.pressed() || panelsGamepad2.leftBumperWasPressed())
                selector.goBack();

            List<String> lines = selector.getLines();
            for (String line : lines) {
                telemetryM.addLine(line);
            }
            onLog(lines);
            telemetryM.update(telemetry);
        } else selectedOpMode.init_loop();
    }

    @Override
    public final void start() {
        if (selectedOpMode == null) throw new RuntimeException("No OpMode selected!");
        selectedOpMode.start();
    }

    @Override
    public final void loop() {
        selectedOpMode.loop();
    }

    @Override
    public final void stop() {
        if (selectedOpMode != null) selectedOpMode.stop();
    }
}
