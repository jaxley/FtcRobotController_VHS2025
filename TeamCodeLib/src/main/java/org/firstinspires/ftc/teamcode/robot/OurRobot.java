package org.firstinspires.ftc.teamcode.robot;

import org.firstinspires.ftc.teamcode.robot.subsystem.Intake;
import org.firstinspires.ftc.teamcode.robot.subsystem.Shooter;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.subsystems.SubsystemGroup;

public class OurRobot extends SubsystemGroup {
    public static final OurRobot INSTANCE = new OurRobot();

    private OurRobot() {
        super(
                Intake.INSTANCE,
                Shooter.INSTANCE
        );
    }

    public final Command takeAShot = new SequentialGroup(
            Intake.INSTANCE.loadBallToShooter,
            Shooter.INSTANCE.fire
    ).named("Take a shot");
}