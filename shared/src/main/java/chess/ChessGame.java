package chess;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor turn = TeamColor.WHITE;
    private Deque<ChessBoard.AppliedChessMove> moveHistory;

    private boolean whiteRook1Moved = false;
    private boolean whiteRook2Moved = false;
    private boolean whiteKingMoved = false;
    private boolean blackRook1Moved = false;
    private boolean blackRook2Moved = false;
    private boolean blackKingMoved = false;

    public ChessGame() {
        moveHistory = new ArrayDeque<>();
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
            if (color == TeamColor.WHITE && startPosition.equals(new ChessPosition(1, 5))) {
                if (!whiteKingMoved) {
                    if (!whiteRook1Moved) {
                        var queensideCastleMoves = ChessPieceMoves.queensideCastleMoves(board, startPosition, color);
                        var addQueensideCastle = !queensideCastleMoves.isEmpty();
                        for (var move : queensideCastleMoves) {
                            var appliedMove = board.applyMove(move);
                            if (isInCheck(color)) {
                                addQueensideCastle = false;
                                board.unApplyMove(appliedMove);
                                break;
                            }
                            board.unApplyMove(appliedMove);
                        }
                        if (addQueensideCastle) {
                            potentialMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.row(), 3)));
                        }
                    }
                    if (!whiteRook2Moved) {
                        var kingsideCastleMoves = ChessPieceMoves.kingsideCastleMoves(board, startPosition, color);
                        var addKingsideCastle = !kingsideCastleMoves.isEmpty();
                        for (var move : kingsideCastleMoves) {
                            var appliedMove = board.applyMove(move);
                            if (isInCheck(color)) {
                                addKingsideCastle = false;
                                board.unApplyMove(appliedMove);
                                break;
                            }
                            board.unApplyMove(appliedMove);
                        }
                        if (addKingsideCastle) {
                            potentialMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.row(), 7)));
                        }
                    }
                }
            }
            if (color == TeamColor.BLACK && startPosition.equals(new ChessPosition(8, 5))) {
                if (!blackKingMoved) {
                    if (!blackRook1Moved) {
                        var queensideCastleMoves = ChessPieceMoves.queensideCastleMoves(board, startPosition, color);
                        var addQueensideCastle = !queensideCastleMoves.isEmpty();
                        for (var move : queensideCastleMoves) {
                            var appliedMove = board.applyMove(move);
                            if (isInCheck(color)) {
                                addQueensideCastle = false;
                                board.unApplyMove(appliedMove);
                                break;
                            }
                            board.unApplyMove(appliedMove);
                        }
                        if (addQueensideCastle) {
                            potentialMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.row(), 3)));
                        }
                    }
                    if (!blackRook2Moved) {
                        var kingsideCastleMoves = ChessPieceMoves.kingsideCastleMoves(board, startPosition, color);
                        var addKingsideCastle = !kingsideCastleMoves.isEmpty();
                        for (var move : kingsideCastleMoves) {
                            var appliedMove = board.applyMove(move);
                            if (isInCheck(color)) {
                                addKingsideCastle = false;
                                board.unApplyMove(appliedMove);
                                break;
                            }
                            board.unApplyMove(appliedMove);
                        }
                        if (addKingsideCastle) {
                            potentialMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.row(), 7)));
                        }
                    }
                }
            }
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
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Not a valid move: " + move);
        }

        var appliedMove = board.applyMove(move);
        if (appliedMove.isCastleMove()) {
            var newRookCol = move.getEndPosition().col() == 7 ? 6 : 4;
            var oldRookCol = newRookCol == 6 ? 8 : 1;
            var rookMove = new ChessMove(new ChessPosition(move.getStartPosition().row(), oldRookCol),
                                         new ChessPosition(move.getStartPosition().row(), newRookCol));
            var appliedRookMove = board.applyMove(rookMove);
            moveHistory.push(appliedRookMove);
        }
        moveHistory.push(appliedMove);

        if (appliedMove.piece().getPieceType() == ChessPiece.PieceType.KING) {
            if (turn == TeamColor.WHITE) whiteKingMoved = true;
            else blackKingMoved = true;
        }
        if (appliedMove.piece().getPieceType() == ChessPiece.PieceType.ROOK) {
            if (appliedMove.move().getStartPosition().equals(new ChessPosition(1, 1))) whiteRook1Moved = true;
            if (appliedMove.move().getStartPosition().equals(new ChessPosition(1, 8))) whiteRook2Moved = true;
            if (appliedMove.move().getStartPosition().equals(new ChessPosition(8, 1))) blackRook1Moved = true;
            if (appliedMove.move().getStartPosition().equals(new ChessPosition(8, 8))) blackRook2Moved = true;
        }

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
        whiteKingMoved = false;
        whiteRook1Moved = false;
        whiteRook2Moved = false;
        blackKingMoved = false;
        blackRook1Moved = false;
        blackRook2Moved = false;
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
