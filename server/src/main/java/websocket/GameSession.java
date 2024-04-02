package websocket;

import chess.ChessGame;
import model.GameData;

import java.util.concurrent.ConcurrentHashMap;

public class GameSession {
    private Connection whitePlayerConnection;
    private Connection blackPlayerConnection;
    private int gameID;
    private GameData gameData;
    private final ConcurrentHashMap<String, Connection> observers = new ConcurrentHashMap<>();

    public GameSession(int gameID) {
        this.gameID = gameID;
    }

    public void addPlayer(Connection connection, ChessGame.TeamColor color) {}

    public void addObserver(Connection connection) {}

    public void removePlayer() {}

    public void removeObserver(String authToken) {}
}
