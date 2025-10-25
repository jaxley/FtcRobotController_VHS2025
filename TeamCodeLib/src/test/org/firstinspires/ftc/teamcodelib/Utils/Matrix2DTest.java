package org.firstinspires.ftc.teamcodelib.Utils;

import java.util.Arrays;

class Matrix2DTest {

    @org.junit.jupiter.api.Test
    void multiplyMatrices() {

        Matrix2D firstMatrix = new Matrix2D(new double[][]{
                new double[]{1d, 5d},
                new double[]{2d, 3d},
                new double[]{1d, 7d}
        });

        Matrix2D secondMatrix = new Matrix2D(new double[][]{
                new double[]{1d, 2d, 3d, 7d},
                new double[]{5d, 2d, 8d, 1d}
        });

        Matrix2D expected = new Matrix2D(new double[][]{
                new double[]{26d, 12d, 43d, 12d},
                new double[]{17d, 10d, 30d, 17d},
                new double[]{36d, 16d, 59d, 14d}
        });

        Matrix2D actual = firstMatrix.multiply(secondMatrix);
        assert Arrays.deepEquals((actual.getMatrix()), expected.getMatrix());
    }

    @org.junit.jupiter.api.Test
    void multiplyMatricesCell() {
    }
}