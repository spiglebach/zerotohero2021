package hu.szukacsm.zerotohero.matrixcleaner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MatrixCleaner {
    private int[][] originalMatrix;
    private int[][] newMatrix;
    Set<MatrixPosition> alreadyVisitedPositions;

    int matrixLength;

    public int[][] clean(int[][] matrix) { // recursive // todo make one with a queue and compare their speed
        originalMatrix = matrix;
        matrixLength = originalMatrix.length;

        validateMatrixDimensions();

        newMatrix = new int[matrixLength][matrixLength];
        alreadyVisitedPositions = new HashSet<>();

        checkPosition(0, 0); // check top left
        checkPosition(0, matrixLength - 1); // check top right
        checkPosition(matrixLength - 1, 0); // check bottom left
        checkPosition(matrixLength - 1, matrixLength - 1); // check bottom right
        for (int index = 1; index < matrixLength - 1; index++) {
            checkPosition(0, index, Direction.DOWN); // check rest of first row downward
            checkPosition(matrixLength - 1, index, Direction.UP); // check rest of last row upward
            checkPosition(index, 0, Direction.RIGHT); // check leftmost column to the right
            checkPosition(index, matrixLength - 1, Direction.LEFT); // check rightmost column to the left
        }
        return newMatrix;
    }

    private void validateMatrixDimensions() {
        assert Arrays.stream(originalMatrix).allMatch(row -> row.length == matrixLength);
    }

    private void checkPosition(
            int row, int column,
            Direction ... possibleDirections) {
        if (isIndexOutOfRange(row)) return;
        if (isIndexOutOfRange(column)) return;
        if (originalMatrix[row][column] == 0) return;
        MatrixPosition position = new MatrixPosition(row, column);
        if (alreadyVisitedPositions.contains(position)) return;
        newMatrix[row][column] = originalMatrix[row][column];
        alreadyVisitedPositions.add(position);
        checkNeighborsInPossibleDirections(row, column, possibleDirections);
    }

    private boolean isIndexOutOfRange(int index) {
        return index < 0 || index >= originalMatrix.length;
    }

    private void checkNeighborsInPossibleDirections(int row, int column, Direction ... possibleDirections) {
        for (Direction direction : possibleDirections) {
            if (Direction.UP.equals(direction)) {
                checkPosition(row - 1, column, Direction.UP, Direction.RIGHT, Direction.LEFT);
            }
            if (Direction.RIGHT.equals(direction)) {
                checkPosition(row, column + 1, Direction.RIGHT, Direction.DOWN, Direction.UP);
            }
            if (Direction.DOWN.equals(direction)) {
                checkPosition(row + 1, column, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
            }
            if (Direction.LEFT.equals(direction)) {
                checkPosition(row, column - 1, Direction.LEFT, Direction.DOWN, Direction.UP);
            }
        }
    }
}
