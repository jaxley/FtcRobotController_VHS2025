package org.firstinspires.ftc.teamcode.pedro.procedures;

import java.util.List;

/**
 * Sanity checks shared by {@link ThreeWheelTuner} and {@link ThreeWheelIMUTuner}.
 * Each check returns a diagnostic message naming the encoder and values, or null when healthy.
 */
final class ThreeWheelChecks {

    // Thresholds. All aborts include the offending encoder name and measured values.
    /** Raw ticks below which a push measurement counts as a dead encoder. */
    static final double MIN_ENCODER_TICKS = 20.0;
    /** Perpendicular-axis ticks above which the push direction looks wrong. */
    static final double MAX_CROSS_AXIS_FRACTION = 0.25;
    static final double MIN_CROSS_AXIS_TICKS = 100.0;
    /** Maximum spread between the three pods' ticks per inch (same wheel type). */
    static final double MAX_TPI_SPREAD = 0.15;
    /** Maximum disagreement between the two offset stages' strafe travel. */
    static final double MAX_OFFSET_STRAFE_DISAGREEMENT_FRACTION = 0.3;
    static final double MAX_OFFSET_STRAFE_DISAGREEMENT_INCHES = 0.5;
    /** turnTicksToRadians must equal forwardTicksToInches for these localizers. */
    static final double MAX_TURN_TO_FORWARD_DIFFERENCE = 0.10;
    /** Tape-measurement validation tolerances. */
    static final double MAX_TAPE_ERROR_FRACTION = 0.12;
    static final double MAX_TAPE_ERROR_INCHES = 0.5;
    /** A strafe pod closer than this to the center line is almost certainly a bad reading. */
    static final double MIN_STRAFE_POD_X = 0.25;

    private ThreeWheelChecks() {
    }

    /** Detects pushing in the wrong direction, a mis-mounted pod, or a dead encoder. */
    static String resolutionFlaw(String pod, String encoderName, ResolutionMeasurement measured, double distance) {
        double primary = Math.abs(measured.primaryTicks);
        double cross = Math.abs(measured.crossTicks);
        if (cross > Math.max(MAX_CROSS_AXIS_FRACTION * primary, MIN_CROSS_AXIS_TICKS)) {
            return pod + " stage: the '" + encoderName + "' encoder read " + fmt(primary) + " ticks while the "
                    + "perpendicular axis read " + fmt(cross) + " ticks over a " + fmt(distance) + " inch push. "
                    + "The robot was pushed in the wrong direction (push " + (pod.equals("Strafe") ? "left" : "forward")
                    + " for this stage) or a pod is mounted in the wrong orientation.";
        }
        if (primary < MIN_ENCODER_TICKS) {
            return pod + " stage: the '" + encoderName + "' encoder read only " + fmt(primary) + " ticks over a "
                    + fmt(distance) + " inch push. The encoder is effectively dead: check that the name matches the "
                    + "port the pod is plugged into, that the cable is fully seated, and that the pod wheel touches the ground.";
        }
        return null;
    }

    /** All three pods use the same wheel type, so their ticks per inch must agree. */
    static String tpiFlaw(String leftEncoderName, String rightEncoderName, String strafeEncoderName,
                          ResolutionMeasurement left, ResolutionMeasurement right, ResolutionMeasurement strafe) {
        double leftTpi = left.ticksPerInch;
        double rightTpi = right.ticksPerInch;
        double strafeTpi = strafe.ticksPerInch;
        double median = median3(leftTpi, rightTpi, strafeTpi);
        double leftDeviation = Math.abs(leftTpi - median);
        double rightDeviation = Math.abs(rightTpi - median);
        double strafeDeviation = Math.abs(strafeTpi - median);
        double worstDeviation = Math.max(leftDeviation, Math.max(rightDeviation, strafeDeviation));
        if (worstDeviation <= MAX_TPI_SPREAD * median) {
            return null;
        }
        String worstPod = worstDeviation == leftDeviation ? "left ('" + leftEncoderName + "')"
                : worstDeviation == rightDeviation ? "right ('" + rightEncoderName + "')"
                : "strafe ('" + strafeEncoderName + "')";
        return "Encoder ticks per inch disagree: left '" + leftEncoderName + "' = " + fmt(leftTpi)
                + ", right '" + rightEncoderName + "' = " + fmt(rightTpi)
                + ", strafe '" + strafeEncoderName + "' = " + fmt(strafeTpi)
                + ". All three pods use the same wheel type, so these must match within a few percent. The " + worstPod
                + " pod is dropping counts, slipping, or has a different wheel.";
    }

    /** Both 180 degree rotations must produce the same strafe pod travel. */
    static String offsetStageFlaw(List<Double> leftOffsets, List<Double> rightOffsets) {
        double leftTravel = Math.abs(leftOffsets.get(1));
        double rightTravel = Math.abs(rightOffsets.get(1));
        double tolerance = Math.max(MAX_OFFSET_STRAFE_DISAGREEMENT_FRACTION * Math.max(leftTravel, rightTravel),
                MAX_OFFSET_STRAFE_DISAGREEMENT_INCHES);
        if (Math.abs(leftTravel - rightTravel) > tolerance) {
            return "The two 180 degree rotations disagree: strafe pod travel " + fmt(leftTravel) + " vs "
                    + fmt(rightTravel) + " inches. Keep the rotation center fixed and rotate a full 180 degrees each time.";
        }
        return null;
    }

    /** For these localizers, turnTicksToRadians must equal forwardTicksToInches. */
    static String turnFlaw(double turn, double forward) {
        double difference = Math.abs(turn / forward - 1.0);
        if (difference > MAX_TURN_TO_FORWARD_DIFFERENCE) {
            return "turnTicksToRadians (" + turn + ") and forwardTicksToInches (" + forward
                    + ") differ by " + pct(difference) + " but must be equal for this localizer. The 360 degree "
                    + "rotation was not 360 degrees, or a pod dropped counts while rotating. Re-run and spin exactly "
                    + "360 degrees counterclockwise.";
        }
        return null;
    }

    /** Validates the recovered geometry, including optional tape-measured references. */
    static String geometryFlaw(double strafeX, double leftPodY, double rightPodY,
                               double podSeparation, double strafePodDistance) {
        if (Math.abs(strafeX) < MIN_STRAFE_POD_X) {
            return "Measured strafe pod offset: " + fmt(strafeX) + " inches from the robot center (expected several "
                    + "inches). The strafe encoder is misreading. Only continue if the strafe pod truly sits that close "
                    + "to the center line.";
        }
        if (podSeparation > 0.0) {
            double measuredSeparation = Math.abs(leftPodY - rightPodY);
            double tolerance = Math.max(MAX_TAPE_ERROR_FRACTION * podSeparation, MAX_TAPE_ERROR_INCHES);
            if (Math.abs(measuredSeparation - podSeparation) > tolerance) {
                String message = "Measured left/right pod separation: " + fmt(measuredSeparation)
                        + " inches, tape measured " + fmt(podSeparation) + " inches.";
                if (strafePodDistance > 0.0) {
                    double ratio = measuredSeparation / podSeparation;
                    double strafeRatio = Math.abs(strafeX) / strafePodDistance;
                    if (ratio < 1.0 - MAX_TURN_TO_FORWARD_DIFFERENCE && Math.abs(strafeRatio - ratio) < MAX_TURN_TO_FORWARD_DIFFERENCE) {
                        message += " Both offsets are low by about the same factor (" + pct(1.0 - ratio)
                                + "), so the 180 degree rotations were only about "
                                + Math.round(180.0 * ratio) + " degrees. Rotate the full 180 degrees.";
                    }
                }
                return message + " Rotate a full 180 degrees about a fixed robot center.";
            }
        }
        if (strafePodDistance > 0.0
                && Math.abs(Math.abs(strafeX) - strafePodDistance) > Math.max(MAX_TAPE_ERROR_FRACTION * strafePodDistance, MAX_TAPE_ERROR_INCHES)) {
            return "Measured strafe pod offset " + fmt(Math.abs(strafeX)) + " inches, tape measured "
                    + fmt(strafePodDistance) + " inches. Check the 180 degree rotation amount and that the strafe "
                    + "pod wheel rolls freely.";
        }
        return null;
    }

    static double median3(double a, double b, double c) {
        return Math.max(Math.min(a, b), Math.min(Math.max(a, b), c));
    }

    static String fmt(double value) {
        String formatted = String.format(java.util.Locale.US, "%.3f", value);
        return formatted.contains(".") ? formatted.replaceAll("0+$", "").replaceAll("\\.$", "") : formatted;
    }

    static String pct(double fraction) {
        return String.format(java.util.Locale.US, "%.1f%%", 100.0 * fraction);
    }
}
