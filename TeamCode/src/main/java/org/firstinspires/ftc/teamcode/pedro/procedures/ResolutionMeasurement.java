package org.firstinspires.ftc.teamcode.pedro.procedures;

/** Push-stage result: ticks per inch, direction multiplier, and both raw axis readings in ticks. */
class ResolutionMeasurement {

    final double ticksPerInch;
    final double direction;
    final double primaryTicks;
    final double crossTicks;

    ResolutionMeasurement(double ticksPerInch, double direction, double primaryTicks, double crossTicks) {
        this.ticksPerInch = ticksPerInch;
        this.direction = direction;
        this.primaryTicks = primaryTicks;
        this.crossTicks = crossTicks;
    }
}
