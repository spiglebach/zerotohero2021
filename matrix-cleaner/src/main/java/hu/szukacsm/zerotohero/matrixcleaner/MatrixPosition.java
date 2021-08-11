package hu.szukacsm.zerotohero.matrixcleaner;

public class MatrixPosition {
    public final int row;
    public final int column;

    public MatrixPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString() {
        return "[ row: " + row + " ; column: " + column + " ]";
    }
}
