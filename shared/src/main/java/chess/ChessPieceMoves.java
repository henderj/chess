package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

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
            var NE = new ChessPosition(position.row() + i, position.col() + i);
            var move = new ChessMove(position, NE);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var SE = new ChessPosition(position.row() - i, position.col() + i);
            var move = new ChessMove(position, SE);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var SW = new ChessPosition(position.row() - i, position.col() - i);
            var move = new ChessMove(position, SW);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var NW = new ChessPosition(position.row() + i, position.col() - i);
            var move = new ChessMove(position, NW);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        return moves;
    }

    /**
     * Calculates all the straight moves available to a chess piece up to the edge of the board
     *
     * @return Collection of straight moves
     */
    public static Collection<ChessMove> straightMoves(ChessBoard board, ChessPosition position,
                                                      ChessGame.TeamColor color) {
        return straightMoves(board, position, color, 7);
    }

    /**
     * Calculates all the straight moves available to a chess piece up to <code>distance</code> squares
     *
     * @return Collection of straight moves
     */
    public static Collection<ChessMove> straightMoves(ChessBoard board, ChessPosition position,
                                                      ChessGame.TeamColor color, int distance) {
        HashSet<ChessMove> moves = new HashSet<>();

        for (int i = 1; i <= distance; i++) {
            var N = new ChessPosition(position.row() + i, position.col());
            var move = new ChessMove(position, N);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var E = new ChessPosition(position.row(), position.col() + i);
            var move = new ChessMove(position, E);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var S = new ChessPosition(position.row() - i, position.col());
            var move = new ChessMove(position, S);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var W = new ChessPosition(position.row(), position.col() - i);
            var move = new ChessMove(position, W);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        return moves;
    }

    /**
     * Calculates the pawn moves for a given position on the board
     *
     * @return Collection of pawn moves
     */
    public static Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        HashSet<ChessMove> moves = new HashSet<>();
        var row = position.row();
        var col = position.col();
        var forward = 1;
        var startingRow = 2;
        var endRow = 8;
        if (color == ChessGame.TeamColor.BLACK) {
            forward = -1;
            startingRow = 7;
            endRow = 1;
        }
        var forwardMove = new ChessMove(position, new ChessPosition(row + forward, col));
        if (isValid(board, forwardMove, color, false)) {
            if (forwardMove.getEndPosition().row() == endRow) {
                moves.addAll(getPromotionMoves(forwardMove));
            } else {
                moves.add(forwardMove);
            }
            if (row == startingRow) {
                var doubleForwardMove = new ChessMove(position, new ChessPosition(row + forward * 2, col));
                if (isValid(board, doubleForwardMove, color, false)) moves.add(doubleForwardMove);
            }
        }

        var diagonalCaptureLeft = new ChessMove(position, new ChessPosition(row + forward, col - 1));
        if (isValid(board, diagonalCaptureLeft, color) && isCapture(board, diagonalCaptureLeft, color)) {
            if (diagonalCaptureLeft.getEndPosition().row() == endRow) {
                moves.addAll(getPromotionMoves(diagonalCaptureLeft));
            } else {
                moves.add(diagonalCaptureLeft);
            }
        }

        var diagonalCaptureRight = new ChessMove(position, new ChessPosition(row + forward, col + 1));
        if (isValid(board, diagonalCaptureRight, color) && isCapture(board, diagonalCaptureRight, color)) {
            if (diagonalCaptureRight.getEndPosition().row() == endRow) {
                moves.addAll(getPromotionMoves(diagonalCaptureRight));
            } else {
                moves.add(diagonalCaptureRight);
            }
        }

        return moves;
    }

    public static Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition position,
                                                    ChessGame.TeamColor color) {
        var moves = new HashSet<ChessMove>();
        int col = position.col();
        int row = position.row();
        moves.add(new ChessMove(position, new ChessPosition(row + 2, col + 1))); // NNE
        moves.add(new ChessMove(position, new ChessPosition(row + 1, col + 2))); // ENE
        moves.add(new ChessMove(position, new ChessPosition(row - 1, col + 2))); // ESE
        moves.add(new ChessMove(position, new ChessPosition(row - 2, col + 1))); // SSE
        moves.add(new ChessMove(position, new ChessPosition(row - 2, col - 1))); // SSW
        moves.add(new ChessMove(position, new ChessPosition(row - 1, col - 2))); // WSW
        moves.add(new ChessMove(position, new ChessPosition(row + 1, col - 2))); // WNW
        moves.add(new ChessMove(position, new ChessPosition(row + 2, col - 1))); // NNW
        return moves.stream().filter(m -> isValid(board, m, color)).collect(Collectors.toSet());
    }

    /**
     * Checks if the given move is a valid move for the piece
     *
     * @param canCapture if the piece can capture with this move
     * @return <code>true</code> if the piece can move further there. Otherwise, <code>false</code>
     */
    private static boolean isValid(ChessBoard board, ChessMove move, ChessGame.TeamColor color, boolean canCapture) {
        if (!board.inBounds(move.getEndPosition())) return false;
        var piece = board.getPiece(move.getEndPosition());
        if (piece == null) return true;
        if (piece.getTeamColor().equals(color)) return false; // can't capture own team
        return !piece.getTeamColor().equals(color) && canCapture;
    }

    /**
     * Checks if the given move is a valid move for the piece. Assumes the piece can capture
     * enemy pieces at the end position.
     *
     * @return <code>true</code> if the move is valid. Otherwise, <code>false</code>
     */
    private static boolean isValid(ChessBoard board, ChessMove move, ChessGame.TeamColor color) {
        return isValid(board, move, color, true);
    }

    /**
     * Checks if a given <code>ChessMove</code> is a capture by checking if there is a piece at the end position
     * and if that piece is the opposite color
     *
     * @return <code>true</code> if the move is a capture. Otherwise, <code>false</code>
     */
    private static boolean isCapture(ChessBoard board, ChessMove move, ChessGame.TeamColor color) {
        var piece = board.getPiece(move.getEndPosition());
        return piece != null && piece.getTeamColor() != color;
    }

    /**
     * Constructs the promotions moves for a given moves. It adds promotions to Bishop, Knight, Rook, and Queen
     *
     * @return Collection of promotion moves
     */
    private static Collection<ChessMove> getPromotionMoves(ChessMove move) {
        var moves = new HashSet<ChessMove>();
        moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.QUEEN));
        return moves;
    }

}
