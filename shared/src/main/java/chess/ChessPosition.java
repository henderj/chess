package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public final class ChessPosition {
    private final int row;
    private final int col;

    /**
     *
     */
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        final char[] columnNames = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        return "" + columnNames[col - 1] + row;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public ChessPosition north(){
        return new ChessPosition(row + 1, col);
    }

    public ChessPosition south() {
        return new ChessPosition(row - 1, col);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ChessPosition) obj;
        return this.row == that.row &&
                this.col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

}
