package hu.szukacsm.zerotohero.matrixcleaner;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatrixCleanerTest {

    @Test
    void testCleanOnEmptyMatrix() {
        int[][] matrix = new int[][]{};
        int[][] result = new MatrixCleaner().cleanWithQueue(matrix);
        assertArrayEquals(matrix, result);
    }

    @Test
    void testCleanOn1x1Matrix() {
        testCleanOnNxNMatrixFilledWithOnes(1);
    }

    @Test
    void testCleanOn2x2MatrixFilledWithZeros() {
        testCleanOnNxNMatrixFilledWithZeros(2);
    }

    @Test
    void testCleanOn3x3Matrix() {
        int[][] matrix = new int[][]{
                new int[]{0, 0, 0},
                new int[]{0, 1, 0},
                new int[]{0, 0, 0}
        };
        int[][] result = new MatrixCleaner().cleanWithQueue(matrix);
        assertArrayEquals(new int[3][3], result);
    }

    @Test
    void testCleanOn5x5Matrix() {
        int[][] matrix = new int[][]{
                new int[]{0, 0, 1, 0, 0},
                new int[]{0, 0, 1, 0, 0},
                new int[]{0, 0, 1, 1, 0},
                new int[]{0, 0, 1, 0, 0},
                new int[]{0, 0, 0, 0, 0}
        };
        int[][] result = new MatrixCleaner().cleanWithQueue(matrix);
        assertArrayEquals(matrix, result);
    }

    @Test
    void testCleanOn6x6Matrix() {
        int[][] matrix = new int[][]{
                new int[]{1, 0, 0, 0, 0, 0},
                new int[]{0, 1, 0, 1, 1, 1},
                new int[]{0, 0, 1, 0, 1, 0},
                new int[]{1, 1, 0, 0, 1, 0},
                new int[]{1, 0, 1, 1, 0, 0},
                new int[]{1, 0, 0, 0, 0, 1}
        };
        int[][] result = new MatrixCleaner().cleanWithQueue(matrix);
        int[][] expected = new int[][]{
                new int[]{1, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 1, 1, 1},
                new int[]{0, 0, 0, 0, 1, 0},
                new int[]{1, 1, 0, 0, 1, 0},
                new int[]{1, 0, 0, 0, 0, 0},
                new int[]{1, 0, 0, 0, 0, 1}
        };
        assertArrayEquals(expected, result);
    }

    @Test
    void testCleanOn10000x10000MatrixFilledWithOnes() {
        testCleanOnNxNMatrixFilledWithOnes(10000);
    }

    @Test
    void testCleanOn10000x10000MatrixFilledWithZeros() {
        testCleanOnNxNMatrixFilledWithZeros(10000);
    }

    @Test
    void testCleanOnNonQuadraticMatrices() {
        int[][] matrix = new int[][]{
                new int[]{1, 0, 0, 0},
                new int[]{1, 0, 0, 0, 1, 1, 0},
                new int[]{1, 1}
        };
        assertThrows(AssertionError.class,
                () -> new MatrixCleaner().cleanWithQueue(matrix));
        assertThrows(AssertionError.class,
                () -> new MatrixCleaner().cleanWithQueue(new int[][]{new int[]{}}));
    }

    void testCleanOnNxNMatrixFilledWithZeros(int matrixLength) {
        int[][] matrix = new int[matrixLength][matrixLength];
        int[][] result = new MatrixCleaner().cleanWithQueue(matrix);
        assertArrayEquals(matrix, result);
    }

    void testCleanOnNxNMatrixFilledWithOnes(int matrixLength) {
        int[][] matrix = new int[matrixLength][matrixLength];
        for (int i = 0; i < matrixLength; i++) {
            Arrays.fill(matrix[i], 1);
        }
        int[][] result = new MatrixCleaner().cleanWithQueue(matrix);
        assertArrayEquals(matrix, result);
    }
}