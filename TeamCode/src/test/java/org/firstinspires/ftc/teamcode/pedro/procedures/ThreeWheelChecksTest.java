package org.firstinspires.ftc.teamcode.pedro.procedures;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for {@link ThreeWheelChecks}, the sanity checks shared by
 * ThreeWheelTuner and ThreeWheelIMUTuner. The failing numbers are the
 * 2026-11-20 tuning run with a dead strafe encoder; the healthy numbers
 * mirror the working LOCALIZER_CONFIG_ALPHA constants.
 */
class ThreeWheelChecksTest {

    private static final String LEFT_NAME = "frontLeft";
    private static final String RIGHT_NAME = "frontRight";
    private static final String STRAFE_NAME = "backLeft";

    @Test
    void deadStrafeEncoderIsFlaggedWithNameAndTicks() {
        ResolutionMeasurement strafe = new ResolutionMeasurement(7.0 / 48.0, -1.0, 7.0, 12.0);
        String problem = ThreeWheelChecks.resolutionFlaw("Strafe", STRAFE_NAME, strafe, 48.0);
        assertNotNull(problem);
        assertTrue(problem.contains(STRAFE_NAME), "message must name the encoder: " + problem);
        assertTrue(problem.contains("7"), "message must contain the tick count: " + problem);
    }

    @Test
    void wrongPushDirectionIsFlaggedBeforeDeadEncoder() {
        // strafe stage pushed forward by mistake: primary ~0, perpendicular axis large
        ResolutionMeasurement strafe = new ResolutionMeasurement(0.02, 1.0, 1.0, 24288.0);
        String problem = ThreeWheelChecks.resolutionFlaw("Strafe", STRAFE_NAME, strafe, 48.0);
        assertNotNull(problem);
        assertTrue(problem.contains("push left"), "message must say which direction to push: " + problem);
    }

    @Test
    void healthyPushPassesResolutionFlaw() {
        // 48 inch push at ~506 ticks per inch, ~0.8 degree of drift on the cross axis
        ResolutionMeasurement left = new ResolutionMeasurement(506.0, 1.0, 24288.0, 420.0);
        assertNull(ThreeWheelChecks.resolutionFlaw("Left", LEFT_NAME, left, 48.0));
    }

    @Test
    void tpiSpreadNamesTheOutlierPod() {
        ResolutionMeasurement left = new ResolutionMeasurement(506.3, 1.0, 24302.0, 100.0);
        ResolutionMeasurement right = new ResolutionMeasurement(506.4, 1.0, 24307.0, 100.0);
        ResolutionMeasurement strafe = new ResolutionMeasurement(7.0 / 48.0, -1.0, 7.0, 12.0);
        String problem = ThreeWheelChecks.tpiFlaw(LEFT_NAME, RIGHT_NAME, STRAFE_NAME, left, right, strafe);
        assertNotNull(problem);
        assertTrue(problem.contains("strafe"), "message must name the outlier pod: " + problem);
        assertTrue(problem.contains("0.146"), "message must contain the strafe ticks per inch: " + problem);
    }

    @Test
    void matchingTpiPasses() {
        ResolutionMeasurement left = new ResolutionMeasurement(506.0, 1.0, 24288.0, 100.0);
        ResolutionMeasurement right = new ResolutionMeasurement(507.0, 1.0, 24336.0, 100.0);
        ResolutionMeasurement strafe = new ResolutionMeasurement(505.0, -1.0, 24240.0, 100.0);
        assertNull(ThreeWheelChecks.tpiFlaw(LEFT_NAME, RIGHT_NAME, STRAFE_NAME, left, right, strafe));
    }

    @Test
    void offsetStageDisagreementIsFlagged() {
        String problem = ThreeWheelChecks.offsetStageFlaw(List.of(1.0, 6.0), List.of(-1.0, 2.0));
        assertNotNull(problem);
        assertTrue(problem.contains("6") && problem.contains("2"), "message must contain both travels: " + problem);
    }

    @Test
    void agreeingOffsetStagesPass() {
        assertNull(ThreeWheelChecks.offsetStageFlaw(List.of(1.0, 6.7), List.of(-1.0, 6.8)));
    }

    @Test
    void turnMismatchFromFailedRunIsFlagged() {
        // tuned values from the 2026-11-20 run: 25.4% apart
        assertNotNull(ThreeWheelChecks.turnFlaw(0.0024763321327047625, 0.0019751664259094496));
    }

    @Test
    void healthyAlphaTurnPasses() {
        // working ALPHA constants: 2.6% apart
        assertNull(ThreeWheelChecks.turnFlaw(0.002200094356695412, 0.0021440409972891573));
    }

    @Test
    void deadStrafePodOffsetIsFlagged() {
        String problem = ThreeWheelChecks.geometryFlaw(
                -0.010641253490732056, 1.6654660355967688, -4.035092871445847, 12.0, 6.75);
        assertNotNull(problem);
        assertTrue(problem.contains("0.011"), "message must contain the measured offset: " + problem);
    }

    @Test
    void podSeparationMismatchIsFlagged() {
        String problem = ThreeWheelChecks.geometryFlaw(6.9, 1.6654660355967688, -4.035092871445847, 12.0, 0.0);
        assertNotNull(problem);
        assertTrue(problem.contains("5.7"), "message must contain the measured separation: " + problem);
        assertTrue(problem.contains("12"), "message must contain the tape separation: " + problem);
    }

    @Test
    void healthyAlphaGeometryPasses() {
        assertNull(ThreeWheelChecks.geometryFlaw(6.9, 6.0, -6.0, 12.0, 6.75));
    }

    @Test
    void missingTapeValuesSkipTapeChecks() {
        // zero tape inputs: only the strafe offset floor applies
        assertNull(ThreeWheelChecks.geometryFlaw(6.9, 1.6654660355967688, -4.035092871445847, 0.0, 0.0));
    }
}
