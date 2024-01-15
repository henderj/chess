package chess;

import java.util.Collection;
import java.util.HashSet;

public class ChessPieceMoves {
    /**
     * Calculates all the diagonal moves available to a chess piece up to the edge of the board
     *
     * @return Collection of diagonal moves
     */
    public static Collection<ChessMove> diagonalMoves(ChessBoard board, ChessPosition position,
                                                      ChessGame.TeamColor color) {
        return diagonalMoves(board, position, color, 7);
    }

    /**
     * Calculates all the diagonal moves available to a chess piece up to <code>distance</code> squares
     *
     * @return Collection of diagonal moves
     */
    public static Collection<ChessMove> diagonalMoves(ChessBoard board, ChessPosition position,
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

    public static Collection<ChessMove> straightMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        return straightMoves(board, position, color, 7);
    }

    public static Collection<ChessMove> straightMoves(ChessBoard board, ChessPosition position,
                                                      ChessGame.TeamColor color, int distance) {
        HashSet<ChessMove> moves = new HashSet<>();

        for (int i = 1; i <= distance; i++) {
            var N = new ChessPosition(position.getRow() + i, position.getColumn());
            if (addMoveIfValid(board, position, color, moves, N)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var E = new ChessPosition(position.getRow(), position.getColumn() + i);
            if (addMoveIfValid(board, position, color, moves, E)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var S = new ChessPosition(position.getRow() - i, position.getColumn());
            if (addMoveIfValid(board, position, color, moves, S)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var W = new ChessPosition(position.getRow(), position.getColumn() - i);
            if (addMoveIfValid(board, position, color, moves, W)) break;
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

    public static Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        HashSet<ChessMove> moves = new HashSet<>();
        if (color == ChessGame.TeamColor.WHITE){
            var forward = new ChessPosition(position.getRow() + 1, position.getColumn());
            addMoveIfValid(board, position, color, moves, forward);
        } else {
            var forward = new ChessPosition(position.getRow() - 1, position.getColumn());
            addMoveIfValid(board, position, color, moves, forward);
        }
        return moves;
    }
}
