package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board;
    private ChessPosition kingPositionWhite, kingPositionBlack;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[8 - position.row()][position.col() - 1] = piece;
        if (piece == null) return;
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                kingPositionWhite = position;
            } else {
                kingPositionBlack = position;
            }
        }
    }

    public ChessPiece removePiece(ChessPosition position) {
        var piece = board[8 - position.row()][position.col() - 1];
        board[8 - position.row()][position.col() - 1] = null;
        return piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[8 - position.row()][position.col() - 1];
    }

    public interface PieceOperation {
        void apply(ChessPosition position, ChessPiece piece);
    }

    public void forEveryPiece(PieceOperation operation) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var position = new ChessPosition(row, col);
                var piece = getPiece(position);
                operation.apply(position, piece);
            }
        }
    }

    /**
     * Gets the position of the king.
     *
     * @param color
     * @return the position of the king for the given color. null if there is no king.
     */
    public ChessPosition getKingPosition(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return kingPositionWhite;
        }
        return kingPositionBlack;
    }

    public record AppliedChessMove(ChessMove move, ChessPiece piece, ChessPiece capturedPiece) {
    }

    public AppliedChessMove applyMove(ChessMove move) {
        var piece = removePiece(move.getStartPosition());
        var capturedPiece = removePiece(move.getEndPosition());
        if (move.getPromotionPiece() != null) {
            addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        } else {
            addPiece(move.getEndPosition(), piece);
        }
        return new AppliedChessMove(move, piece, capturedPiece);
    }

    public void unApplyMove(AppliedChessMove move) {
        addPiece(move.move.getStartPosition(), move.piece);
        addPiece(move.move.getEndPosition(), move.capturedPiece);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        for (int r = 3; r <= 6; r++) {
            for (int c = 1; c <= 8; c++) {
                addPiece(new ChessPosition(r, c), null);
            }
        }

        for (int c = 1; c <= 8; c++) {
            addPiece(new ChessPosition(7, c), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
    }

    /**
     * Checks whether a position is in bounds of the board or not
     *
     * @param position The position to check
     * @return true if the position is in the bounds of the board. Otherwise, false
     */
    public boolean inBounds(ChessPosition position) {
        return position.row() > 0 && position.row() <= 8 && position.col() > 0 && position.col() <= 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }
}
