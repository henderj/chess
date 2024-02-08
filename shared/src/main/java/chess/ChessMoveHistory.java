package chess;

import java.util.ArrayDeque;
import java.util.Deque;

public class ChessMoveHistory {
    private Deque<AppliedChessMove> moveHistory;

    private ChessPosition enPassantPosition = null;
    private ChessPieceMoves.EnPassantChessMove enPassantChessMove = null;
    private boolean whiteRookKingSideMoved = false;
    private boolean whiteRookQueenSideMoved = false;
    private boolean whiteKingMoved = false;
    private boolean blackRookKingSideMoved = false;
    private boolean blackRookQueenSideMoved = false;
    private boolean blackKingMoved = false;

    public ChessMoveHistory() {
        moveHistory = new ArrayDeque<>();
    }

    public boolean canCastleKingSide(ChessGame.TeamColor color) {
        return color == ChessGame.TeamColor.WHITE ? !(whiteKingMoved || whiteRookKingSideMoved) :
                !(blackKingMoved || blackRookKingSideMoved);
    }

    public boolean canCastleQueenSide(ChessGame.TeamColor color) {
        return color == ChessGame.TeamColor.WHITE ? !(whiteKingMoved || whiteRookQueenSideMoved) :
                !(blackKingMoved || blackRookQueenSideMoved);
    }

    public void pushMove(AppliedChessMove move) {
        moveHistory.push(move);

        var piece = move.getPiece();
        var type = piece.getPieceType();
        var color = piece.getTeamColor();

        if (type == ChessPiece.PieceType.KING) {
            if (color == ChessGame.TeamColor.WHITE) whiteKingMoved = true;
            else blackKingMoved = true;
        }

        var startPosition = move.getMove().getStartPosition();

        if (type == ChessPiece.PieceType.ROOK) {
            if (color == ChessGame.TeamColor.WHITE) {
                if (startPosition.equals(new ChessPosition(1, 1)))
                    whiteRookQueenSideMoved = true;
                if (startPosition.equals(new ChessPosition(1, 8)))
                    whiteRookKingSideMoved = true;
            } else {
                if (startPosition.equals(new ChessPosition(8, 1)))
                    blackRookQueenSideMoved = true;
                if (startPosition.equals(new ChessPosition(8, 8)))
                    blackRookKingSideMoved = true;
            }
        }

        if (move.isDoublePawnMove()) {
            enPassantPosition = color == ChessGame.TeamColor.WHITE ? startPosition.north() : startPosition.south();
        } else {
            enPassantPosition = null;
        }
    }

    public ChessPosition getEnPassantPosition() {
        return enPassantPosition;
    }
}
