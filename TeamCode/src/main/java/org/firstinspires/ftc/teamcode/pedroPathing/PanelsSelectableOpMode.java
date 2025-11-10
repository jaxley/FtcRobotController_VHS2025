package com.pedropathing.telemetry;

import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.telemetry.SelectScope;
import com.pedropathing.telemetry.Selector;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class PanelsSelectableOpMode extends OpMode {
    private final Selector<Supplier<OpMode>> selector;
    private OpMode selectedOpMode;
    private final static String[] MESSAGE = {
            "Use the d-pad to move the cursor.",
            "Press right bumper to select.",
            "Press left bumper to go back."
    };

    public PanelsSelectableOpMode(String name, Consumer<SelectScope<Supplier<OpMode>>> opModes) {
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
        telemetry = PanelsTelemetry.INSTANCE.getFtcTelemetry();
    }

    @Override
    public final void init_loop() {
        Gamepad panelGamepad = PanelsGamepad.INSTANCE.getFirstManager().asCombinedFTCGamepad(gamepad1);
        this.gamepad1 = panelGamepad;
        if (selectedOpMode == null) {
            if (gamepad1.dpadUpWasPressed() || gamepad2.dpadUpWasPressed())
                selector.decrementSelected();
            else if (gamepad1.dpadDownWasPressed() || gamepad2.dpadDownWasPressed())
                selector.incrementSelected();
            else if (gamepad1.rightBumperWasPressed() || gamepad2.rightBumperWasPressed())
                selector.select();
            else if (gamepad1.leftBumperWasPressed() || gamepad2.leftBumperWasPressed())
                selector.goBack();

            List<String> lines = selector.getLines();
            for (String line : lines) {
                telemetry.addLine(line);
            }
            onLog(lines);
            telemetry.update();
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
