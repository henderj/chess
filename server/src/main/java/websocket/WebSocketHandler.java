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
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final GameSessionManager gameSessionManager;
    private final UserService userService;

    public WebSocketHandler(GameService gameService, UserService userService) {
        gameSessionManager = new GameSessionManager(gameService, userService);
        this.userService = userService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case JOIN_PLAYER -> {
                    var joinPlayerCommand = (JoinPlayer) command;
                    var authToken = joinPlayerCommand.getAuthString();
                    var username = userService.readUsername(authToken);

                    var gameSession = gameSessionManager.getGameSession(joinPlayerCommand.getGameID());
                    Connection connection = new Connection(authToken, session);
                    gameSession.addPlayer(connection, joinPlayerCommand.getPlayerColor());

                    LoadGame loadGameMessage = new LoadGame(gameSession.getGameData(authToken));
                    session.getRemote().sendString(new Gson().toJson(loadGameMessage));

                    var notification = new Notification(
                            username + " joined as " + (joinPlayerCommand.getPlayerColor() == ChessGame.TeamColor.WHITE ? "white" : "black"));
                    gameSession.broadcast(authToken, new Gson().toJson(notification));
                }
                case JOIN_OBSERVER -> {
                }
                case MAKE_MOVE -> {
                }
                case LEAVE -> {
                }
                case RESIGN -> {
                }
            }
        } catch (ResponseException | IOException e) {
            var errorMessage = new Error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}