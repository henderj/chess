package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ChessBoardUI {

    private static final int CELL_PADDING = 1;

    private static final String COORD_STYLE = EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
    private static final String CELL_BG_1 = EscapeSequences.SET_BG_COLOR + "115m";
    private static final String CELL_BG_2 = EscapeSequences.SET_BG_COLOR + "28m";
    private static final String WHITE_PIECE_COLOR = EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_TEXT_BOLD;
    private static final String BLACK_PIECE_COLOR = EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.SET_TEXT_BOLD;

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        var ui = new ChessBoardUI();
        String[][] board = new String[8][8];
        for (String[] strings : board) {
            Arrays.fill(strings, " ");
        }
        board[2][5] = "K";
        board[0][6] = "q";
        board[5][7] = "R";
        board[4][0] = "b";
        var displayString = ui.buildChessBoardDisplayString(board);
        out.println(displayString);
    }

    public String buildChessBoardDisplayString(String[][] chessBoard) {
        StringBuilder displayString = new StringBuilder();
        displayString.append(buildLetterCoordString(true));
        displayString.append('\n');
        var start_bg_1 = true;
        for (int i = 0; i < chessBoard.length; i++) {
            String[] row = chessBoard[i];
            displayString.append(buildRow(row, chessBoard.length - i, start_bg_1));
            displayString.append('\n');
            start_bg_1 = !start_bg_1;
        }
        displayString.append(buildLetterCoordString(true));
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

    private String buildRow(String[] row, int index, boolean start_bg_1) {
        StringBuilder string = new StringBuilder();

        string.append(COORD_STYLE);
        string.append(applyPadding("" + index));
        string.append(EscapeSequences.RESET_COLOR);

        var bg1 = start_bg_1;
        for (var s : row){
            string.append(bg1 ? CELL_BG_1 : CELL_BG_2);
            string.append(bg1 ? WHITE_PIECE_COLOR : BLACK_PIECE_COLOR);
            string.append(applyPadding(s));
            bg1 = !bg1;
        }

        string.append(COORD_STYLE);
        string.append(applyPadding("" + index));
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
