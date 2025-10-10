package org.firstinspires.ftc.teamcode.robot.subsystem;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;

/**
 * A shooter is the subsystem on our robot that accepts balls from the Intake
 * and can then fire those balls into the goal on command
 * It contains all of the commands relevant to the subsystem
 */
public class Shooter implements Subsystem {

    private ControlSystem controlSystem; // TODO: decide on control system for intake

    public static final Shooter INSTANCE = new Shooter();

    // TODO: this is just placeholder
    /**
     * We need a way to tell the shooter to fire a ball into the goal
     */
    public final Command fire = new RunToPosition(controlSystem, 0)
            .requires(this); // IMPORTANT!!! requires() is how you control which
                                          // commands are mutually-exclusive so they don't conflict

    /**
     * We probably need a way to tell the shooter hardware to stop running
     */
    public final Command stop = null;

    /**
     * We probably need a way to tell the shooter hardware to stop running
     */
    public final Command start = null;
}
