package hu.szukacsm.zerotohero.matrixcleaner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MatrixCleanerTest {

    @Test
    void clean() {
    }

    @Test
    void testClean1() {
        int[][] matrix = new int[][]{
                new int[]{0, 0, 0},
                new int[]{0, 1, 0},
                new int[]{0, 0, 0}
        };
        int[][] result = new MatrixCleaner().clean(matrix);
        assertArrayEquals(new int[3][3], result);
    }
}