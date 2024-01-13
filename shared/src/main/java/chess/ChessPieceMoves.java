package chess;

import java.util.Collection;
import java.util.HashSet;

public class ChessPieceMoves {
    /**
     * Calculates all the diagonal moves available to a chess piece up to the edge of the board
     *
     * @return Collection of diagonal moves
     */
    static Collection<ChessMove> diagonalMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        return diagonalMoves(board, position, color, 7);
    }

    /**
     * Calculates all the diagonal moves available to a chess piece up to <code>distance</code> squares
     *
     * @return Collection of diagonal moves
     */
    private static Collection<ChessMove> diagonalMoves(ChessBoard board, ChessPosition position,
                                                       ChessGame.TeamColor color, int distance) {
        HashSet<ChessMove> moves = new HashSet<>();

        for (int i = 1; i <= distance; i++) {
            var NE = new ChessPosition(position.getRow() + i, position.getColumn() + i);
            if (addMoveIfValid(board, position, color, moves, NE)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var SE = new ChessPosition(position.getRow() - i, position.getColumn() + i);
            if (addMoveIfValid(board, position, color, moves, SE)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var SW = new ChessPosition(position.getRow() - i, position.getColumn() - i);
            if (addMoveIfValid(board, position, color, moves, SW)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var NW = new ChessPosition(position.getRow() + i, position.getColumn() - i);
            if (addMoveIfValid(board, position, color, moves, NW)) break;
        }

        return moves;
    }

    /**
     * Checks if the given end position is a valid position for the piece to move to. If so, adds it to
     * <code>moves</code>
     *
     * @return <code>true</code> if the piece can't move further. Otherwise, <code>false</code>
     */
    private static boolean addMoveIfValid(ChessBoard board, ChessPosition position, ChessGame.TeamColor color,
                                          HashSet<ChessMove> moves, ChessPosition endPosition) {
        if (!board.inBounds(endPosition)) return true;
        var piece = board.getPiece(endPosition);
        if (piece != null && piece.getTeamColor().equals(color)) return true; // can't capture own team
        moves.add(new ChessMove(position, endPosition));
        return piece != null; // can capture other team, but can't move further
    }
}
