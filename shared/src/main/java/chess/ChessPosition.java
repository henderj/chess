package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public record ChessPosition(int row, int col) {

    @Override
    public String toString() {
        final char[] columnNames = {'a','b','c','d','e','f','g','h'};
        return "" + columnNames[col-1] + row;
    }
}
