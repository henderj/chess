package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import observer.ServerMessageObserver;
import serverFacade.ServerFacade;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.MakeMove;
import webSocketMessages.userCommands.Resign;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ui.MenuUI.ERROR_TRY_AGAIN;

public class GameUI implements ServerMessageObserver {
    private static final Logger logger = Logger.getLogger("GameUI");
    private static final Pattern movePattern = Pattern.compile("^[a-hA-H][1-8][a-hA-H][1-8](?:=?[qrbnQRBN])?$");

    private final ChessBoardUI chessBoardUI;
    private ChessGame.TeamColor perspective;


    private final PrintStream out;
    private final Scanner in;
    private final ServerFacade facade;
    private String username;
    private String authToken;
    private GameData currentGame;


    public GameUI(PrintStream out, Scanner in, ServerFacade facade) {
        this.chessBoardUI = new ChessBoardUI();
        this.out = out;
        this.in = in;
        this.facade = facade;
    }

    @Override
    public void sendMessage(ServerMessage message) {
        logger.fine("received message from server: " + message);
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                var loadGameMessage = (LoadGame) message;
                currentGame = loadGameMessage.getGame();
                drawCurrentBoard();
            }
            case ERROR -> {
                var error = (Error) message;
                out.println("Error: " + error.getErrorMessage());
            }
            case NOTIFICATION -> {
                var notification = (Notification) message;
                out.println(notification.getMessage());
            }
        }
    }

    public void initialize(ChessGame.TeamColor perspective, String username, String authToken) {
        this.perspective = perspective;
        this.username = username;
        this.authToken = authToken;
    }

    public NextState displayGameUI() {
        if (currentGame != null) {
            out.print("[" + username + ", " + currentGame.gameName() + "] ");
        }
        out.print("> ");

        if (!in.hasNextInt()) {
            in.next();
            out.println("Please enter a number from 1-6. Enter 1 for help.");
            return NextState.Game;
        }
        var input = in.nextInt();
        switch (input) {
            case 1 -> {
                out.println("Enter 1 to display this help message. Enter 2 to redraw the chess board.");
                out.println("Enter 3 to leave the game. Enter 4 to make a move.");
                out.println("Enter 5 to resign. Enter 6 to highlight legal moves.");
                return NextState.Game;
            }
            case 2 -> {
                drawCurrentBoard();
                return NextState.Game;
            }
            case 3 -> {
                doLeaveGame();
                return NextState.PostLogin;
            }
            case 4 -> {
                doMakeMove();
                return NextState.Game;
            }
            case 5 -> {
                doResignGame();
                return NextState.Game;
            }
            case 6 -> {
                throw new RuntimeException("Not implemented");
            }
            default -> {
                out.println("Please enter a number from 1-6. Enter 1 for help.");
                return NextState.Game;
            }
        }
    }

    private void doMakeMove() {
        out.println("Enter the move you want to make:");
        try {
            var inputLine = in.next();
            Matcher matcher = movePattern.matcher(inputLine);
            if (!matcher.find()) {
                out.println("Enter a move in the patter source-column source-row target-column target-row," +
                                    " with no spaces in between. For example, e3e5");
                out.println("If you are making a promotion move, add =? to the end of your move, " +
                                    "where ? is the piece you want to promote to.");
                out.println("Q = queen, R = rook, B = bishop, and N = knight");
                return;
            }
            var input = matcher.group();
            input = input.toLowerCase();
            var startPosition = new ChessPosition(input.charAt(1) - '1' + 1, input.charAt(0) - 'a' + 1);
            var endPosition = new ChessPosition(input.charAt(3) - '1' + 1, input.charAt(0) - 'a' + 1);
            ChessPiece.PieceType promotionPiece = null;
            if (input.length() > 4) {
                switch (input.charAt(4)) {
                    case 'q' -> promotionPiece = ChessPiece.PieceType.QUEEN;
                    case 'r' -> promotionPiece = ChessPiece.PieceType.ROOK;
                    case 'b' -> promotionPiece = ChessPiece.PieceType.BISHOP;
                    case 'n' -> promotionPiece = ChessPiece.PieceType.KNIGHT;
                }
            }
            var move = new ChessMove(startPosition, endPosition, promotionPiece);
            var makeMoveCommand = new MakeMove(authToken, currentGame.gameID(), move);
            facade.makeMove(makeMoveCommand);
        } catch (ResponseException e) {
            out.println(ERROR_TRY_AGAIN);
        }
    }

    private void doResignGame() {
        var command = new Resign(authToken, currentGame.gameID());
        try {
            // TODO: confirm resignation
            facade.resignGame(command);
        } catch (ResponseException e) {
            out.println(ERROR_TRY_AGAIN);
        }
    }

    private void doLeaveGame() {
        var command = new Leave(authToken, currentGame.gameID());
        try {
            facade.leaveGame(command);
            perspective = null;
            currentGame = null;
        } catch (ResponseException e) {
            out.println(ERROR_TRY_AGAIN);
        }
    }

    private void drawCurrentBoard() {
        var boardString = chessBoardUI.buildChessBoardDisplayString(currentGame.game().getBoard(), perspective);
        out.println();
        out.println(boardString);
    }
}
