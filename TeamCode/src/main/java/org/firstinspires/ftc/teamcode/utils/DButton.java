package org.firstinspires.ftc.teamcode.utils;

public class DButton {
    private boolean state = false;
    private boolean pressed = false;
    private boolean released = false;

    public void update(boolean newstate) {
        if (state && !newstate) {
            released = true;
            pressed = false;
            state = false;
            return;
        }
        if (!state && newstate) {
            released = false;
            pressed = true;
            state = true;
            return;
        }
        pressed = false;
        released = false;
    }

    public boolean pressed() {
        return pressed;
    }

    public boolean released() {
        return released;
    }
}
