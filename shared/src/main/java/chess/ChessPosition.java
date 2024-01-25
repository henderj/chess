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
        return "{" + row + "," + col + '}';
    }
}
