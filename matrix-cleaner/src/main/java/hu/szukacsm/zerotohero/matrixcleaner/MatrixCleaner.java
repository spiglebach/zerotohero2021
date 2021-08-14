package hu.szukacsm.zerotohero.matrixcleaner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MatrixCleaner {
    private int[][] originalMatrix;
    private int[][] newMatrix;
    private int matrixLength;
    private Queue<MatrixPosition> queue;

    public int[][] cleanWithQueue(int[][] matrix) {
        originalMatrix = matrix;
        matrixLength = originalMatrix.length;

        validateMatrixDimensions();

        newMatrix = new int[matrixLength][matrixLength];

        initializeQueueFromOriginalMatrix();
        while (!queue.isEmpty()) {
            processPosition(queue.remove());
        }

        return newMatrix;
    }

    private void initializeQueueFromOriginalMatrix() {
        queue = new LinkedList<>();
        addMatrixCornersToQueue();
        addMatrixSidesToQueue();
    }

    private void addMatrixCornersToQueue() {
        queue.add(MatrixPosition.corner(0, 0));
        queue.add(MatrixPosition.corner(0, matrixLength - 1));
        queue.add(MatrixPosition.corner(matrixLength - 1, 0));
        queue.add(MatrixPosition.corner(matrixLength - 1, matrixLength - 1));
    }

    private void addMatrixSidesToQueue() {
        for (int index = 1; index < matrixLength - 1; index++) {
            queue.add(MatrixPosition.starterWithOrientation(0, index, Direction.DOWN));
            queue.add(MatrixPosition.starterWithOrientation(matrixLength - 1, index, Direction.UP));
            queue.add(MatrixPosition.starterWithOrientation(index, 0, Direction.RIGHT));
            queue.add(MatrixPosition.starterWithOrientation(index, matrixLength - 1, Direction.LEFT));
        }
    }

    private void processPosition(MatrixPosition position) {
        int row = position.row;
        int column = position.column;
        if (shouldCopyAndCheckNeighbors(row, column)) {
            newMatrix[row][column] = originalMatrix[row][column];
            addNeighborsToQueue(position);
        }
    }

    private boolean shouldCopyAndCheckNeighbors(
            int row, int column) {
        if (isIndexOutOfRange(row)) return false;
        if (isIndexOutOfRange(column)) return false;
        if (originalMatrix[row][column] == 0) return false;
        if (originalMatrix[row][column] == newMatrix[row][column]) return false; // already copied this position
        return true;
    }

    private void addNeighborsToQueue(MatrixPosition position) {
        if (position.isCorner) return;
        if (position.isStarter) {
            queue.add(position.offsetInDirection(position.orientation));
            return;
        }
        if (Direction.DOWN.equals(position.orientation)) {
            queue.add(position.offsetInDirection(Direction.DOWN));
            queue.add(position.offsetInDirection(Direction.RIGHT));
            queue.add(position.offsetInDirection(Direction.LEFT));
        }
        if (Direction.UP.equals(position.orientation)) {
            queue.add(position.offsetInDirection(Direction.UP));
            queue.add(position.offsetInDirection(Direction.RIGHT));
            queue.add(position.offsetInDirection(Direction.LEFT));
        }
        if (Direction.RIGHT.equals(position.orientation)) {
            queue.add(position.offsetInDirection(Direction.UP));
            queue.add(position.offsetInDirection(Direction.RIGHT));
            queue.add(position.offsetInDirection(Direction.DOWN));
        }
        if (Direction.LEFT.equals(position.orientation)) {
            queue.add(position.offsetInDirection(Direction.UP));
            queue.add(position.offsetInDirection(Direction.LEFT));
            queue.add(position.offsetInDirection(Direction.DOWN));
        }
    }

    private void validateMatrixDimensions() {
        assert Arrays.stream(originalMatrix).allMatch(row -> row.length == matrixLength);
    }

    private boolean isIndexOutOfRange(int index) {
        return index < 0 || index >= matrixLength;
    }
}
