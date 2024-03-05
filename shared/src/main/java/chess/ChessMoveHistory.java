package chess;

import java.util.Objects;

public class ChessMoveHistory {

    private ChessPosition enPassantPosition = null;
    private final ChessPieceMoves.EnPassantChessMove enPassantChessMove = null;
    private boolean whiteRookKingSideMoved = false;
    private boolean whiteRookQueenSideMoved = false;
    private boolean whiteKingMoved = false;
    private boolean blackRookKingSideMoved = false;
    private boolean blackRookQueenSideMoved = false;
    private boolean blackKingMoved = false;

    public ChessMoveHistory() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMoveHistory that = (ChessMoveHistory) o;
        return whiteRookKingSideMoved == that.whiteRookKingSideMoved && whiteRookQueenSideMoved == that.whiteRookQueenSideMoved && whiteKingMoved == that.whiteKingMoved && blackRookKingSideMoved == that.blackRookKingSideMoved && blackRookQueenSideMoved == that.blackRookQueenSideMoved && blackKingMoved == that.blackKingMoved && Objects.equals(
                enPassantPosition, that.enPassantPosition) && Objects.equals(enPassantChessMove,
                                                                             that.enPassantChessMove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enPassantPosition, enPassantChessMove, whiteRookKingSideMoved, whiteRookQueenSideMoved,
                            whiteKingMoved, blackRookKingSideMoved, blackRookQueenSideMoved, blackKingMoved);
    }
}
