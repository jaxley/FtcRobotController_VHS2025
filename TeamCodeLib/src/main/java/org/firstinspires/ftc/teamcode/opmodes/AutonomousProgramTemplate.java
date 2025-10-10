package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.robot.OurRobot;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

@Autonomous(name = "NextFTC Autonomous Program Java")
public class AutonomousProgramTemplate extends NextFTCOpMode {
    public AutonomousProgramTemplate() {
        addComponents(
                new SubsystemComponent(OurRobot.INSTANCE),
                BulkReadComponent.INSTANCE
        );
    }

    public Command moveToShootingPosition() {
        // TODO: code up the pedro path
        return new SequentialGroup();
    }

    public Command takeAShot() {
        return OurRobot.INSTANCE.takeAShot;
    }

    @Override
    public void onStartButtonPressed() {
        // TODO: this is where we will code all that we want to happen during autonomous
        // https://nextftc.dev/guide/opmodes/autonomous
        moveToShootingPosition().schedule();
    }

}

