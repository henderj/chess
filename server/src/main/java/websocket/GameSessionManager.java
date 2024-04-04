package websocket;

import dataAccess.GameDAO;
import exception.BadRequestException;
import exception.ResponseException;
import service.GameService;
import service.UserService;

import java.util.concurrent.ConcurrentHashMap;

public class GameSessionManager {
    private final ConcurrentHashMap<Integer, GameSession> gameSessions = new ConcurrentHashMap<>();
    private final GameService gameService;
    private final UserService userService;

    public GameSessionManager(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    public GameSession getGameSession(int gameID, String authToken) throws ResponseException {
        if (gameService.readGame(gameID, authToken) == null) {
            throw new BadRequestException("game with id " + gameID + " does not exist");
        }
        if (gameSessions.containsKey(gameID)) {
            return gameSessions.get(gameID);
        }
        var gameSession = new GameSession(gameID, gameService, userService);
        gameSessions.put(gameID, gameSession);
        return gameSession;
    }

}
