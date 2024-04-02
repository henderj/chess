package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

@WebSocket
public class WebSocketHandler {
    private final GameSessionManager gameSessionManager = new GameSessionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> {
                var joinPlayerCommand = (JoinPlayer) command;
                var gameSession = gameSessionManager.getGameSession(joinPlayerCommand.getGameID());
                gameSession.addPlayer(new Connection(joinPlayerCommand.getAuthString(), session), joinPlayerCommand.getPlayerColor());
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
    }
}