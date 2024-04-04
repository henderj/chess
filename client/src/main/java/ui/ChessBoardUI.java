package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ChessBoardUI {

    private static final int CELL_PADDING = 1;

    private static final String COORD_STYLE =
            EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
    private static final String CELL_BG_1 = EscapeSequences.SET_BG_COLOR + "115m";
    private static final String CELL_BG_2 = EscapeSequences.SET_BG_COLOR + "28m";
    private static final String WHITE_PIECE_COLOR =
            EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_TEXT_BOLD;
    private static final String BLACK_PIECE_COLOR =
            EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.SET_TEXT_BOLD;

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        var ui = new ChessBoardUI();
        var board = new ChessBoard();
        board.resetBoard();
        board.addPiece(new ChessPosition(4, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        var displayString = ui.buildChessBoardDisplayString(board, ChessGame.TeamColor.WHITE);
        out.println(displayString);
        out.println();
        displayString = ui.buildChessBoardDisplayString(board, ChessGame.TeamColor.BLACK);
        out.println(displayString);
    }

    public String buildChessBoardDisplayString(ChessBoard chessBoard, ChessGame.TeamColor perspective) {
        var whitePerspective = perspective == ChessGame.TeamColor.WHITE;
        StringBuilder displayString = new StringBuilder();
        displayString.append(buildLetterCoordString(whitePerspective));
        displayString.append('\n');
        var start_bg_1 = true;
        for (int i = 8; i > 0; i--) {
            displayString.append(buildRow(chessBoard, whitePerspective ? i : 9 - i, start_bg_1, whitePerspective));
            displayString.append('\n');
            start_bg_1 = !start_bg_1;
        }
        displayString.append(buildLetterCoordString(whitePerspective));
        return displayString.toString();
    }

    private String buildLetterCoordString(boolean forwards) {
        Character[] letters = {' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', ' '};
        if (!forwards) {
            reverse(letters);
        }

        StringBuilder string = new StringBuilder(letters.length);
        string.append(COORD_STYLE);
        for (var c : letters) {
            string.append(applyPadding(c.toString()));
        }
        string.append(EscapeSequences.RESET_COLOR);
        return string.toString();
    }

    private String buildRow(ChessBoard board, int row, boolean start_bg_1, boolean whitePerspective) {
        StringBuilder string = new StringBuilder();

        string.append(COORD_STYLE);
        string.append(applyPadding("" + row));
        string.append(EscapeSequences.RESET_COLOR);

        var bg1 = start_bg_1;
        for (var col = 1; col <= 8; col++) {
            var piece = board.getPiece(new ChessPosition(row, whitePerspective ? col : 9 - col));
            string.append(bg1 ? CELL_BG_1 : CELL_BG_2);
            if (piece != null) {
                string.append(
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PIECE_COLOR : BLACK_PIECE_COLOR);
                string.append(applyPadding(piece.toString().toUpperCase()));
            } else {
                string.append(applyPadding(" "));
            }
            bg1 = !bg1;
        }

        string.append(COORD_STYLE);
        string.append(applyPadding("" + row));
        string.append(EscapeSequences.RESET_COLOR);

        string.append(EscapeSequences.RESET_COLOR);
        return string.toString();
    }

    private String applyPadding(String str) {
        return " ".repeat(CELL_PADDING) + str + " ".repeat(CELL_PADDING);
    }

    private <T> void reverse(T[] arr) {
        var n = arr.length;
        for (int i = 0; i < n / 2; i++) {
            T temp = arr[i];
            arr[i] = arr[n - i - 1];
            arr[n - i - 1] = temp;
        }
    }

}
