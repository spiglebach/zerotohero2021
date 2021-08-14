package hu.szukacsm.zerotohero.matrixcleaner;

public class MatrixPosition {
    public final int row;
    public final int column;
    public final Direction orientation;
    public final boolean isStarter;
    public final boolean isCorner;

    private MatrixPosition(
            int row, int column,
            Direction orientation,
            boolean isStarter, boolean isCorner) {
        this.row = row;
        this.column = column;
        this.orientation = orientation;
        this.isStarter = isStarter;
        this.isCorner = isCorner;
    }

    private MatrixPosition(int row, int column, Direction orientation) {
        this.row = row;
        this.column = column;
        this.orientation = orientation;
        this.isStarter = false;
        this.isCorner = false;
    }

    public static MatrixPosition starterWithOrientation(
            int row, int column, Direction orientation) {
        return new MatrixPosition(row, column, orientation, true, false);
    }

    public static MatrixPosition corner(int row, int column) {
        return new MatrixPosition(row, column, null, false, true);
    }

    public MatrixPosition offsetInDirection(Direction direction) {
        switch (direction) {
            case DOWN:
                return new MatrixPosition(row + 1, column, Direction.DOWN);
            case UP:
                return new MatrixPosition(row - 1, column, Direction.UP);
            case LEFT:
                return new MatrixPosition(row, column - 1, Direction.LEFT);
            default:
                return new MatrixPosition(row, column + 1, Direction.RIGHT);
        }
    }

    @Override
    public String toString() {
        return "[ row: " + row + " ; column: " + column + " ]";
    }
}
