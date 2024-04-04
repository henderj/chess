package serverFacade;

import com.google.gson.Gson;
import exception.ResponseException;
import observer.ServerMessageObserver;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class WebSocketCommunicator extends Endpoint {
    private static final Logger logger = Logger.getLogger("WebSocketCommunicator");
    private final URI socketUri;
    private Session session;
    private final ServerMessageObserver messageObserver;

    public WebSocketCommunicator(String baseUrl, ServerMessageObserver messageObserver) throws ResponseException {
        try {
            baseUrl = baseUrl.replace("http", "ws");
            this.socketUri = new URI(baseUrl + "/connect");
            this.messageObserver = messageObserver;
            tryConnect();
        } catch (URISyntaxException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void tryConnect() throws ResponseException {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, socketUri);

            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    logger.fine("received message from server: " + message);
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME -> messageObserver.sendMessage(new Gson().fromJson(message, LoadGame.class));
                        case ERROR -> messageObserver.sendMessage(new Gson().fromJson(message, Error.class));
                        case NOTIFICATION ->
                                messageObserver.sendMessage(new Gson().fromJson(message, Notification.class));
                    }
                }
            });
        } catch (IOException | DeploymentException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            if (!session.isOpen()) {
                tryConnect();
            }
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
