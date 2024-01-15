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
            default -> throw new RuntimeException("Not Implemented");
        }
    }

}
