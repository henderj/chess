package websocket;

import chess.ChessGame;
import exception.AlreadyTakenException;
import exception.ResponseException;
import model.GameData;
import service.GameService;
import service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameSession {
    private final GameService gameService;
    private final UserService userService;
    private final int gameID;
    private Connection whitePlayerConnection;
    private Connection blackPlayerConnection;
    private final ConcurrentHashMap<String, Connection> observers = new ConcurrentHashMap<>();

    public GameSession(int gameID, GameService gameService, UserService userService) {
        this.gameID = gameID;
        this.gameService = gameService;
        this.userService = userService;
    }

    public void broadcast(String excludeAuthToken, String message) throws IOException {
        cleanUpConnections();

        if (whitePlayerConnection != null && !whitePlayerConnection.authToken().equals(excludeAuthToken)) {
            whitePlayerConnection.send(message);
        }
        if (blackPlayerConnection != null && !blackPlayerConnection.authToken().equals(excludeAuthToken)) {
            blackPlayerConnection.send(message);
        }

        for (Map.Entry<String, Connection> entry : observers.entrySet()) {
            String authToken = entry.getKey();
            Connection connection = entry.getValue();
            if (!authToken.equals(excludeAuthToken)) {
                connection.send(message);
            }
        }
    }

    public GameData getGameData(String authToken) throws ResponseException {
        return gameService.readGame(gameID, authToken);
    }

    public void addPlayer(Connection connection, ChessGame.TeamColor color) throws ResponseException {
        cleanUpConnections();

        String authToken = connection.authToken();
        var username = userService.readUsername(authToken);
        var gameData = getGameData(authToken);

        if (color == ChessGame.TeamColor.WHITE) {
            if (!username.equals(gameData.whiteUsername())) {
                throw new AlreadyTakenException("White player spot is reserved for someone else");
            }
            if (whitePlayerConnection != null) {
                throw new AlreadyTakenException("White player spot is already taken");
            }
            whitePlayerConnection = connection;
        } else {
            if (!username.equals(gameData.blackUsername())) {
                throw new AlreadyTakenException("Black player spot is reserved for someone else");
            }
            if (blackPlayerConnection != null) {
                throw new AlreadyTakenException("Black player spot is already taken");
            }
            blackPlayerConnection = connection;
        }
    }

    public void addObserver(Connection connection) {
        observers.put(connection.authToken(), connection);
    }

    public void removePlayer() {
    }

    public void removeObserver(String authToken) {
        observers.remove(authToken);
    }

    public void cleanUpConnections() {
        if (whitePlayerConnection != null && !whitePlayerConnection.session().isOpen()) {
            whitePlayerConnection.session().close();
            whitePlayerConnection = null;
        }
        if (blackPlayerConnection != null && !blackPlayerConnection.session().isOpen()) {
            blackPlayerConnection.session().close();
            blackPlayerConnection = null;
        }

        var removeList = new ArrayList<Connection>();
        for (var c : observers.values()) {
            if (!c.session().isOpen()) {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            observers.remove(c.authToken());
        }
    }
}
