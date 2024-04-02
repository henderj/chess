package websocket;

import java.util.concurrent.ConcurrentHashMap;

public class GameSessionManager {
    private final ConcurrentHashMap<Integer, GameSession> gameSessions = new ConcurrentHashMap<>();

    public GameSession getGameSession(int gameID) {
        if (gameSessions.containsKey(gameID)) {
            return gameSessions.get(gameID);
        }
        var gameSession = new GameSession(gameID);
        gameSessions.put(gameID, gameSession);
        return gameSession;
    }

}
