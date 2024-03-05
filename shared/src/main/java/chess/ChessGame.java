package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor turn = TeamColor.WHITE;
    private ChessMoveHistory moveHistory;

    private ChessPieceMoves.EnPassantChessMove enPassantMove = null;

    public ChessGame() {
        moveHistory = new ChessMoveHistory();
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
        var color = piece.getTeamColor();

        var potentialMoves = piece.pieceMoves(board, startPosition);

        var enPassantPosition = moveHistory.getEnPassantPosition();
        if (enPassantPosition != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            enPassantMove = ChessPieceMoves.enPassantMove(board, startPosition, color, enPassantPosition);
            if (enPassantMove != null) potentialMoves.add(enPassantMove);
        }

        var iterator = potentialMoves.iterator();
        while (iterator.hasNext()) {
            var move = iterator.next();
            var appliedMove = board.applyMove(move);
            if (isInCheck(piece.getTeamColor())) {
                iterator.remove();
            }
            board.unApplyMove(appliedMove);
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING && !isInCheck(color)) {
            if (moveHistory.canCastleKingSide(color)) {
                var kingSideCastleMoves = ChessPieceMoves.kingsideCastleMoves(board, startPosition, color);
                if (isCastleMoveValid(kingSideCastleMoves, color)) {
                    potentialMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.row(), 7)));
                }
            }
            if (moveHistory.canCastleQueenSide(color)) {
                var queenSideCastleMoves = ChessPieceMoves.queensideCastleMoves(board, startPosition, color);
                if (isCastleMoveValid(queenSideCastleMoves, color)) {
                    potentialMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.row(), 3)));
                }
            }
        }

        if (enPassantMove != null && potentialMoves.contains(enPassantMove)) {
            potentialMoves.remove(enPassantMove);
            potentialMoves.add(new ChessMove(enPassantMove.getStartPosition(), enPassantMove.getEndPosition()));
        } else {
            enPassantMove = null;
        }

        return potentialMoves;
    }

    private boolean isCastleMoveValid(Collection<ChessMove> castleMoves, TeamColor color) {
        if (castleMoves == null || castleMoves.isEmpty()) return false;
        for (var move : castleMoves) {
            var appliedMoved = board.applyMove(move);
            if (isInCheck(color)) {
                board.unApplyMove(appliedMoved);
                return false;
            }
            board.unApplyMove(appliedMoved);
        }
        return true;
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
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Not a valid move: " + move);
        }

        var appliedMove = enPassantMove == null ? board.applyMove(move) : board.applyMove(enPassantMove);
        if (appliedMove.isCastleMove()) {
            var newRookCol = move.getEndPosition().col() == 7 ? 6 : 4;
            var oldRookCol = newRookCol == 6 ? 8 : 1;
            var rookMove = new ChessMove(new ChessPosition(move.getStartPosition().row(), oldRookCol),
                                         new ChessPosition(move.getStartPosition().row(), newRookCol));
            var appliedRookMove = board.applyMove(rookMove);
            moveHistory.pushMove(appliedRookMove);
        }
        moveHistory.pushMove(appliedMove);

        if (enPassantMove != null) enPassantMove = null;

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

        for (int row = 1; row <= 8; row++) {
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
        this.moveHistory = new ChessMoveHistory();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn && Objects.equals(
                moveHistory, chessGame.moveHistory) && Objects.equals(enPassantMove, chessGame.enPassantMove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn, moveHistory, enPassantMove);
    }
}
