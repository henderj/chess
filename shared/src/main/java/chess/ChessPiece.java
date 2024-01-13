package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType type;
    private ChessGame.TeamColor color;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        throw new RuntimeException("Not implemented");
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
        return diagonalMoves(board, myPosition);
    }

    /**
     * Calculates all the diagonal moves available to a chess piece up to the edge of the board
     *
     * @return Collection of diagonal moves
     */
    private static Collection<ChessMove> diagonalMoves(ChessBoard board, ChessPosition position) {
        return diagonalMoves(board, position, 7);
    }

    /**
     * Calculates all the diagonal moves available to a chess piece up to <code>distance</code> squares
     *
     * @return Collection of diagonal moves
     */
    private static Collection<ChessMove> diagonalMoves(ChessBoard board, ChessPosition position, int distance) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int i = 1; i <= distance; i++) {
            var NE = new ChessPosition(position.getRow() - i, position.getColumn() + i);
            if (board.inBounds(NE)) moves.add(new ChessMove(position, NE));

            var SE = new ChessPosition(position.getRow() + i, position.getColumn() + i);
            if (board.inBounds(SE)) moves.add(new ChessMove(position, SE));

            var SW = new ChessPosition(position.getRow() + i, position.getColumn() - i);
            if (board.inBounds(SW)) moves.add(new ChessMove(position, SW));

            var NW = new ChessPosition(position.getRow() - i, position.getColumn() - i);
            if (board.inBounds(NW)) moves.add(new ChessMove(position, NW));
        }
        return moves;
    }
}
