package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.logging.Logger;

@WebSocket
public class WebSocketHandler {
    private static final Logger logger = Logger.getLogger("WebSocketHandler");
    private final GameSessionManager gameSessionManager;
    private final UserService userService;

    public WebSocketHandler(GameService gameService, UserService userService) {
        gameSessionManager = new GameSessionManager(gameService, userService);
        this.userService = userService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        logger.info("received command from user: " + message);
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case JOIN_PLAYER -> {
                    var joinPlayerCommand = new Gson().fromJson(message, JoinPlayer.class);
                    var authToken = joinPlayerCommand.getAuthString();
                    var username = userService.readUsername(authToken);
                    var gameID = joinPlayerCommand.getGameID();

                    var gameSession = gameSessionManager.getGameSession(gameID);
                    Connection connection = new Connection(authToken, session);
                    gameSession.addPlayer(connection, joinPlayerCommand.getPlayerColor());

                    LoadGame loadGameMessage = new LoadGame(gameSession.getGameData(authToken));
                    String loadGameJson = new Gson().toJson(loadGameMessage);
                    logger.fine("sending load game message to player: " + loadGameJson);
                    session.getRemote().sendString(loadGameJson);

                    var notification = new Notification(
                            username + " joined as " + (joinPlayerCommand.getPlayerColor() == ChessGame.TeamColor.WHITE ? "white" : "black"));
                    gameSession.broadcast(authToken, new Gson().toJson(notification));
                }
                case JOIN_OBSERVER -> {
                    var joinObserverCommand = new Gson().fromJson(message, JoinObserver.class);
                    var authToken = joinObserverCommand.getAuthString();
                    var username = userService.readUsername(authToken);
                    var gameID = joinObserverCommand.getGameID();

                    var gameSession = gameSessionManager.getGameSession(gameID);
                    Connection connection = new Connection(authToken, session);
                    gameSession.addObserver(connection);

                    LoadGame loadGameMessage = new LoadGame(gameSession.getGameData(authToken));
                    String loadGameJson = new Gson().toJson(loadGameMessage);
                    logger.fine("sending load game message to player: " + loadGameJson);
                    session.getRemote().sendString(loadGameJson);

                    var notification = new Notification(username + " joined as an observer");
                    gameSession.broadcast(authToken, new Gson().toJson(notification));
                }
                case MAKE_MOVE -> {
                }
                case LEAVE -> {
                    var leaveCommand = new Gson().fromJson(message, Leave.class);
                    var authToken = leaveCommand.getAuthString();
                    var username = userService.readUsername(authToken);

                    var gameSession = gameSessionManager.getGameSession(leaveCommand.getGameID());
                    gameSession.removeParticipant(authToken);

                    var notification = new Notification(username + " left the game");
                    gameSession.broadcast(null, new Gson().toJson(notification));
                }
                case RESIGN -> {
                }
            }
        } catch (ResponseException | IOException e) {
            var errorMessage = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }
}