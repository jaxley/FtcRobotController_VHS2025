package org.firstinspires.ftc.teamcode.robot.subsystem;

import org.firstinspires.ftc.teamcode.robot.OurRobot;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.RunToPosition;

/**
 * The Intake is our robot subsystem that collects balls from the field that can be loaded into
 * the Shooter.
 * This contains all commands relevant to the Intake system
 */
public class Intake implements Subsystem {

    private ControlSystem controlSystem; // TODO: decide on control system for intake
    public static final Intake INSTANCE = new Intake();

    // TODO: this is just a placeholder
    /**
     * We probably need a way to load a ball to the shooter
     * TODO: does intake load TO the shooter, or should shooter load FROM intake?
     */
    public final Command loadBallToShooter = new RunToPosition(controlSystem, 0)
            .requires(this); // IMPORTANT!!! requires() is how you control which
                                          // commands are mutually-exclusive so they don't conflict

    /**
     * We probably need a way to tell the intake mechanism to stop running
     */
    public final Command stop = null;

    /**
     * We probably need a way to tell the intake mechanism to start running
     */
    public final Command start = null;
}
