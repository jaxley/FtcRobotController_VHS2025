package org.firstinspires.ftc.teamcode.utils;

/**
 * Debounce-free edge detector for a single button.
 *
 * <p>Call {@link #update(boolean)} once per loop with the raw button state. The class separates
 * the button's <b>level</b> (held right now) from its <b>edges</b> (transitions that happened on
 * this update only):
 *
 * <ul>
 *   <li>{@link #isHeld()} - level: true on every loop while the button is held down.
 *       Use for continuous behavior (e.g. hold-to-slow-drive).
 *   <li>{@link #pressed()} - rising edge: true only on the single loop where the button went
 *       from up to down. Use for one-shot actions so holding the button does not repeat them.
 *   <li>{@link #released()} - falling edge: true only on the single loop where the button went
 *       from down to up.
 * </ul>
 */
public class DButton {
    private boolean state = false;
    private boolean pressed = false;
    private boolean released = false;

    /**
     * Feeds the current raw button state into the edge detector. Must be called once per loop,
     * before reading any of the getters, or the edge flags will not update.
     *
     * @param newstate the raw button state this loop (true = down)
     */
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
    
    /**
     * Level: is the button held down right now?
     *
     * <p>True on every loop while the button is down, false the whole time it is up - unlike
     * {@link #pressed()}, which is true for one loop only.
     *
     * @return true while the button is currently held
     */
    public boolean isHeld() {
        return state;
    }

    /**
     * Rising edge: did the button go from up to down on the most recent {@link #update}?
     *
     * <p>True for exactly one loop per click, then false again while the button stays held.
     * Use for one-shot actions; use {@link #isHeld()} for behavior that continues while held.
     *
     * @return true only on the loop the button was first pressed
     */
    public boolean pressed() {
        return pressed;
    }

    /**
     * Falling edge: did the button go from down to up on the most recent {@link #update}?
     *
     * <p>True for exactly one loop per release. Useful for act-on-release semantics.
     *
     * @return true only on the loop the button was released
     */
    public boolean released() {
        return released;
    }
}
