package chess;

import java.util.ArrayList;
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
            var ne = new ChessPosition(position.row() + i, position.col() + i);
            var move = new ChessMove(position, ne);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var se = new ChessPosition(position.row() - i, position.col() + i);
            var move = new ChessMove(position, se);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var sw = new ChessPosition(position.row() - i, position.col() - i);
            var move = new ChessMove(position, sw);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var nw = new ChessPosition(position.row() + i, position.col() - i);
            var move = new ChessMove(position, nw);
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
            var n = new ChessPosition(position.row() + i, position.col());
            var move = new ChessMove(position, n);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var e = new ChessPosition(position.row(), position.col() + i);
            var move = new ChessMove(position, e);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var s = new ChessPosition(position.row() - i, position.col());
            var move = new ChessMove(position, s);
            if (!isValid(board, move, color)) break;
            moves.add(move);
            if (isCapture(board, move, color)) break;
        }

        for (int i = 1; i <= distance; i++) {
            var w = new ChessPosition(position.row(), position.col() - i);
            var move = new ChessMove(position, w);
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

    public static class EnPassantChessMove extends ChessMove {

        private final ChessPosition capturedPiecePosition;

        public EnPassantChessMove(ChessPosition startPosition, ChessPosition endPosition,
                                  ChessPosition capturedPiecePosition) {
            super(startPosition, endPosition);
            this.capturedPiecePosition = capturedPiecePosition;
        }

        public ChessPosition getCapturedPiecePosition() {
            return capturedPiecePosition;
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    public static EnPassantChessMove enPassantMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color,
                                          ChessPosition enPassantPosition) {
        if (color == ChessGame.TeamColor.WHITE && (enPassantPosition.row() == 3 || position.row() != 5)) return null;
        if (color == ChessGame.TeamColor.BLACK && (enPassantPosition.row() == 6 || position.row() != 4)) return null;

        if (position.col() != enPassantPosition.col() + 1 && position.col() != enPassantPosition.col() - 1) return null;

        return new EnPassantChessMove(position, enPassantPosition, new ChessPosition(position.row(),
                                                                                     enPassantPosition.col()));
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

    public static Collection<ChessMove> kingsideCastleMoves(ChessBoard board, ChessPosition position,
                                                            ChessGame.TeamColor color) {
        var kingsideCastleMoves = new ArrayList<ChessMove>();
        var kingRow = color == ChessGame.TeamColor.WHITE ? 1 : 8;
        var kingPosition = new ChessPosition(kingRow, 5);
        if (!position.equals(kingPosition)) return kingsideCastleMoves;
        var kingside1 = new ChessMove(position, new ChessPosition(position.row(), 6));
        var kingside2 = new ChessMove(position, new ChessPosition(position.row(), 7));
        if (isValid(board, kingside1, color, false) && isValid(board, kingside2, color, false)) {
            kingsideCastleMoves.add(kingside1);
            kingsideCastleMoves.add(kingside2);
        } else {
            return kingsideCastleMoves;
        }

        var rookMove = new ChessMove(new ChessPosition(position.row(), 8), new ChessPosition(position.row(), 6));
        if (isValid(board, rookMove, color, false)) {
            kingsideCastleMoves.add(rookMove);
        }

        return kingsideCastleMoves;
    }

    public static Collection<ChessMove> queensideCastleMoves(ChessBoard board, ChessPosition position,
                                                             ChessGame.TeamColor color) {
        var queensideCastleMoves = new ArrayList<ChessMove>();
        var kingRow = color == ChessGame.TeamColor.WHITE ? 1 : 8;
        var kingPosition = new ChessPosition(kingRow, 5);
        if (!position.equals(kingPosition)) return queensideCastleMoves;
        var queenside1 = new ChessMove(position, new ChessPosition(position.row(), 4));
        var queenside2 = new ChessMove(position, new ChessPosition(position.row(), 3));
        if (isValid(board, queenside1, color, false) && isValid(board, queenside2, color, false)) {
            queensideCastleMoves.add(queenside1);
            queensideCastleMoves.add(queenside2);
        } else {
            return queensideCastleMoves;
        }

        var rookMove1 = new ChessMove(new ChessPosition(position.row(), 1), new ChessPosition(position.row(), 2));
        var rookMove2 = new ChessMove(new ChessPosition(position.row(), 1), new ChessPosition(position.row(), 4));
        if (isValid(board, rookMove1, color, false) && isValid(board, rookMove2, color, false)) {
            queensideCastleMoves.add(rookMove1);
            queensideCastleMoves.add(rookMove2);
        }

        return queensideCastleMoves;
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
    public static Collection<ChessMove> getPromotionMoves(ChessMove move) {
        var moves = new HashSet<ChessMove>();
        moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.QUEEN));
        return moves;
    }

}
