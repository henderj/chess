package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor turn = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var piece = board.getPiece(startPosition);
        if (piece == null) return null;

        var potentialMoves = piece.pieceMoves(board, startPosition);
        var iterator = potentialMoves.iterator();
        while (iterator.hasNext()) {
            var move = iterator.next();
            var appliedMove = board.applyMove(move);
            if (isInCheck(piece.getTeamColor())) {
                iterator.remove();
            }
            board.unApplyMove(appliedMove);
        }

        return potentialMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var piece = board.getPiece(move.getStartPosition());
        if (piece == null) throw new InvalidMoveException("No piece at start position: " + move);

        if (piece.getTeamColor() != turn) throw new InvalidMoveException("Not this team's turn: " + move);

        var validMoves = validMoves(move.getStartPosition());
        if (validMoves == null || !validMoves.contains(move)){
            throw new InvalidMoveException("Not a valid move: " + move);
        }

        var appliedMove = board.applyMove(move);

        setTeamTurn(turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var kingPosition = board.getKingPosition(teamColor);

        var inCheck = false;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var position = new ChessPosition(row, col);
                var piece = board.getPiece(position);

                if (piece == null) continue;
                if (piece.getTeamColor() == teamColor) continue;

                var attackKingMove = new ChessMove(position, kingPosition);
                var attackKingPromotionMoves = ChessPieceMoves.getPromotionMoves(attackKingMove);
                var moves = piece.pieceMoves(board, position);

                if (moves.contains(attackKingMove) || moves.stream().anyMatch(attackKingPromotionMoves::contains)) {
                    inCheck = true;
                    break;
                }
            }
        }

        return inCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var position = new ChessPosition(row, col);
                var piece = board.getPiece(position);
                if (piece == null || piece.getTeamColor() != teamColor) continue;

                var validMoves = validMoves(position);
                if (validMoves != null && !validMoves.isEmpty()) return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) return false;

        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++) {
                var position = new ChessPosition(row, col);
                var piece = board.getPiece(position);
                if (piece == null) continue;
                if (piece.getTeamColor() != teamColor) continue;

                var moves = validMoves(position);
                if (!moves.isEmpty()) return false;
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
