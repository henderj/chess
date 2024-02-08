package chess;

import java.util.Objects;

public final class AppliedChessMove {
    private final ChessMove move;
    private final ChessPiece piece;
    private final ChessPiece capturedPiece;
    private final ChessPosition capturedPosition;

    public AppliedChessMove(ChessMove move, ChessPiece piece, ChessPiece capturedPiece,
                            ChessPosition capturedPosition) {
        this.move = move;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.capturedPosition = capturedPosition;
    }

    public AppliedChessMove(ChessMove move, ChessPiece piece, ChessPiece capturedPiece) {
        this(move, piece, capturedPiece, capturedPiece == null ? null : move.getEndPosition());
    }

    public AppliedChessMove(ChessMove move, ChessPiece piece) {
        this(move, piece, null, null);
    }

    public boolean isCastleMove() {
        return piece.getPieceType() == ChessPiece.PieceType.KING && (Math.abs(
                move.getStartPosition().col() - move.getEndPosition().col()) == 2);
    }

    public boolean isDoublePawnMove() {
        return piece.getPieceType() == ChessPiece.PieceType.PAWN && (Math.abs(
                move.getStartPosition().row() - move.getEndPosition().row()) == 2);
    }

    public boolean isEnPassantMove() {
        return piece.getPieceType() == ChessPiece.PieceType.PAWN && capturedPiece != null &&
                capturedPiece.getPieceType() == ChessPiece.PieceType.PAWN && move.getEndPosition() != capturedPosition;
    }

    public ChessMove getMove() {
        return move;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public ChessPiece getCapturedPiece() {
        return capturedPiece;
    }

    public ChessPosition getCapturedPosition() {
        return capturedPosition;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AppliedChessMove) obj;
        return Objects.equals(this.move, that.move) &&
                Objects.equals(this.piece, that.piece) &&
                Objects.equals(this.capturedPiece, that.capturedPiece) &&
                Objects.equals(this.capturedPosition, that.capturedPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move, piece, capturedPiece, capturedPosition);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            builder.append(piece);
        }
        if (capturedPiece != null) {
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
                builder.append(move.getStartPosition().toString().charAt(0));
            }
            builder.append('x');
        }
        builder.append(move.getEndPosition().toString());
        if (move.getPromotionPiece() != null){
            builder.append('=');
            builder.append(move.getPromotionPiece().toString());
        }
        return builder.toString();
    }

}
