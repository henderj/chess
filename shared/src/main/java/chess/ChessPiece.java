package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType type;
    private final ChessGame.TeamColor color;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case BISHOP -> {
                return ChessPieceMoves.diagonalMoves(board, myPosition, color);
            }
            case ROOK -> {
                return ChessPieceMoves.straightMoves(board, myPosition, color);
            }
            case QUEEN -> {
                var moves = ChessPieceMoves.straightMoves(board, myPosition, color);
                moves.addAll(ChessPieceMoves.diagonalMoves(board, myPosition, color));
                return moves;
            }
            case KING -> {
                var moves = ChessPieceMoves.straightMoves(board, myPosition, color, 1);
                moves.addAll(ChessPieceMoves.diagonalMoves(board, myPosition, color, 1));
                return moves;
            }
            case PAWN -> {
                return ChessPieceMoves.pawnMoves(board, myPosition, color);
            }
            case KNIGHT -> {
                return ChessPieceMoves.knightMoves(board, myPosition, color);
            }
            default -> throw new RuntimeException("Not Implemented");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    @Override
    public String toString() {
        String str;
        switch (type) {
            case KING -> str = "K";
            case QUEEN -> str = "Q";
            case BISHOP -> str = "B";
            case KNIGHT -> str = "N";
            case ROOK -> str = "R";
            case PAWN -> str = "P";
            default -> str = "U";
        }
        if (color == ChessGame.TeamColor.BLACK) {
            str = str.toLowerCase();
        }
        return str;
    }
}
