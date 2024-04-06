package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import observer.ServerMessageObserver;
import serverFacade.ServerFacade;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.Resign;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Logger;

import static ui.MenuUI.ERROR_TRY_AGAIN;

public class GameUI implements ServerMessageObserver {
    private static final Logger logger = Logger.getLogger("GameUI");

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

        try {
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
                    throw new RuntimeException("Not implemented");
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
        } catch (InputMismatchException ex) {
            out.println("Please enter a number from 1-6. Enter 1 for help.");
            return NextState.Game;
        }
    }

    private void doResignGame() {
        var command = new Resign(authToken, currentGame.gameID());
        try {
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
