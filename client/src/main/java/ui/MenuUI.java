package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import observer.ServerMessageObserver;
import schema.request.*;
import schema.response.RegisterResponse;
import serverFacade.HttpCommunicator;
import serverFacade.ServerFacade;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.Leave;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class MenuUI implements ServerMessageObserver {
    private static final Logger logger = Logger.getLogger("MenuUI");

    public static final String ERROR_TRY_AGAIN = "An unexpected error occurred. Please try again.";


    enum NextState {
        PreLogin,
        PostLogin,
        Game, Quit
    }

    private PrintStream out;
    private Scanner in;
    private final ServerFacade facade;
    private final ChessBoardUI chessBoardUI;
    private ChessGame.TeamColor perspective;
    private boolean isObserver;
    private String username;
    private String authToken;
    private List<GameData> games;
    private GameData currentGame;

    public MenuUI(ServerFacade serverFacade) {
        this.facade = serverFacade;
        chessBoardUI = new ChessBoardUI();
    }

    public static void main(String[] args) {
        var menu = new MenuUI(new ServerFacade(new HttpCommunicator("http://localhost:8080")));
        menu.startMenuLoop();
    }

    public void startMenuLoop() {
        out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        in = new Scanner(System.in);

        displayWelcome();

        var nextState = NextState.PreLogin;
        while (nextState != NextState.Quit) {
            switch (nextState) {
                case PreLogin -> nextState = displayPreLoginUI();
                case PostLogin -> nextState = displayPostLoginUI();
                case Game -> nextState = displayGameUI();
            }
        }

        displayGoodbye();
    }

    @Override
    public void sendMessage(ServerMessage message) {
        logger.fine("received message from server: " + message);
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                var loadGameMessage = (LoadGame) message;
                currentGame = loadGameMessage.getGameData();
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

    private void displayWelcome() {
        out.println("Welcome to 240 Chess!");
    }

    private void displayGoodbye() {
        out.println("Goodbye!");
    }

    private NextState displayPreLoginUI() {
        out.println();
        out.println("What would you like to do? (enter a number 1-4)");
        out.println();
        out.println("1. Register");
        out.println("2. Login");
        out.println("3. Quit");
        out.println("4. Help");
        out.print("> ");

        try {
            var input = in.nextInt();
            switch (input) {
                case 1 -> {
                    return doRegisterUser();
                }
                case 2 -> {
                    return doLoginUser();
                }
                case 3 -> {
                    return NextState.Quit;
                }
                case 4 -> {
                    out.println("Enter 1 to register a new user. Enter 2 to login with an existing user.");
                    out.println("Enter 3 to quit the program. Enter 4 to display this message.");
                    return NextState.PreLogin;
                }
                default -> {
                    out.println("Please enter a number from 1-4.");
                    return NextState.PreLogin;
                }
            }
        } catch (InputMismatchException ex) {
            out.println("Please enter a number from 1-4.");
            return NextState.PreLogin;
        }
    }

    private NextState doRegisterUser() {
        out.println("Register new user");
        out.print("Enter username: ");
        String username = in.next();
        out.print("Enter password: ");
        String password = in.next();
        out.print("Enter email: ");
        String email = in.next();
        out.println("Registering user...");

        RegisterRequest request = new RegisterRequest(username, password, email);
        try {
            RegisterResponse response = facade.register(request);
            this.username = response.username();
            authToken = response.authToken();
            out.println("User registered!");

            return NextState.PostLogin;
        } catch (ResponseException exception) {
            switch (exception.getStatusCode()) {
                case 400 -> out.println("Please enter a valid username, password, and email.");
                case 403 -> out.println("Username already taken.");
                default -> out.println(ERROR_TRY_AGAIN);
            }
            return NextState.PreLogin;
        }
    }

    private NextState doLoginUser() {
        out.println("Login");
        out.print("Enter username: ");
        String username = in.next();
        out.print("Enter password: ");
        String password = in.next();
        out.println("Logging in...");

        var request = new LoginRequest(username, password);
        try {
            var response = facade.login(request);
            this.username = response.username();
            authToken = response.authToken();
            out.println("User logged in!");

            return NextState.PostLogin;
        } catch (ResponseException e) {
            switch (e.getStatusCode()) {
                case 400 -> out.println("Please enter a valid username and password.");
                case 401 -> out.println("Incorrect username or password");
                default -> out.println(ERROR_TRY_AGAIN);
            }
            return NextState.PreLogin;
        }
    }

    private NextState displayPostLoginUI() {
        out.println();
        out.println("[" + username + "] What would you like to do? (enter a number 1-7)");
        out.println();
        out.println("1. Create game");
        out.println("2. List games");
        out.println("3. Join game");
        out.println("4. Observe game");
        out.println("5. Logout");
        out.println("6. Quit");
        out.println("7. Help");
        out.print("> ");

        try {
            var input = in.nextInt();
            switch (input) {
                case 1 -> {
                    doCreateGame();
                    return NextState.PostLogin;
                }
                case 2 -> {
                    doListGames();
                    return NextState.PostLogin;
                }
                case 3 -> {
                    return doJoinGame();
                }
                case 4 -> {
                    return doObserveGame();
                }
                case 5 -> {
                    return doLogout();
                }
                case 6 -> {
                    return NextState.Quit;
                }
                case 7 -> {
                    out.println(
                            "Enter 1 to create a new game (does not join the game). Enter 2 to list all existing " +
                                    "games.");
                    out.println(
                            "Enter 3 to join an existing game. Enter 4 to observe an existing game. Enter 5 to logout" +
                                    ".");
                    out.println("Enter 6 to quit. Enter 7 to display this message.");
                    return NextState.PostLogin;
                }
                default -> {
                    out.println("Please enter a number from 1-7.");
                    return NextState.PostLogin;
                }
            }
        } catch (InputMismatchException ex) {
            out.println("Please enter a number from 1-7.");
            return NextState.PostLogin;
        }
    }

    private void doCreateGame() {
        out.println("Create game.");
        out.print("Enter a name for the new game (no spaces): ");
        var gameName = in.next();
        if (gameName == null || gameName.isEmpty()) {
            out.println("Please enter a valid game name");
            return;
        }
        CreateGameRequest request = new CreateGameRequest(authToken, gameName);
        out.println("Creating game...");
        try {
            facade.createGame(request);
            out.println("Game created!");
        } catch (ResponseException e) {
            out.println(ERROR_TRY_AGAIN);
        }
    }

    private void doListGames() {
        out.println("List games.");
        try {
            var response = facade.listGames(new ListGamesRequest(authToken));
            games = Arrays.stream(response.games()).toList();
            for (int i = 0; i < games.size(); i++) {
                var game = games.get(i);
                out.println((i + 1) + ": " + game);
            }
        } catch (ResponseException e) {
            out.println(ERROR_TRY_AGAIN);
        }
    }

    private NextState doJoinGame() {
        out.println("Join game.");

        var gameId = getGameId();
        if (gameId == null) return NextState.PostLogin;

        out.print("Do you want to join as white or black? (w/b): ");
        var input = in.next();
        if (!Objects.equals(input, "w") && !Objects.equals(input, "b")) {
            out.println("Please enter 'w' or 'b'.");
            return NextState.PostLogin;
        }
        var color = input.equals("w") ? "WHITE" : "BLACK";

        var request = new JoinGameRequest(authToken, color, gameId);
        try {
            facade.joinGame(request);
            perspective = color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            isObserver = false;
            return NextState.Game;
        } catch (ResponseException e) {
            switch (e.getStatusCode()) {
                case 400 -> out.println("That game does not exist.");
                case 403 -> out.println("Cannot join game as " + color + ". Already taken.");
                default -> out.println(ERROR_TRY_AGAIN);
            }
        }
        return NextState.PostLogin;
    }

    private Integer getGameId() {
        out.print("Enter the number of the game you want to join: ");
        var gameNum = in.nextInt();
        if (gameNum < 1 || gameNum > games.size()) {
            out.println("Please enter a valid game number. Use 'List games' to see valid game numbers.");
            return null;
        }
        return games.get(gameNum - 1).gameID();
    }

    private NextState doObserveGame() {
        out.println("Observe game.");

        var gameId = getGameId();
        if (gameId == null) return NextState.PostLogin;

        var request = new JoinGameRequest(authToken, null, gameId);
        try {
            facade.joinGame(request);
            perspective = ChessGame.TeamColor.WHITE;
            isObserver = true;
            return NextState.Game;
        } catch (ResponseException e) {
            if (e.getStatusCode() == 400) {
                out.println("That game does not exists.");
            } else {
                out.println(ERROR_TRY_AGAIN);
            }
        }
        return NextState.PostLogin;
    }

    private NextState doLogout() {
        out.println("Logging out...");
        try {
            facade.logout(new LogoutRequest(authToken));
            out.println("Logged out!");
            return NextState.PreLogin;
        } catch (ResponseException e) {
            out.println(ERROR_TRY_AGAIN);
            return NextState.PostLogin;
        }
    }

    private NextState displayGameUI() {
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
                    throw new RuntimeException("Not implemented");
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
