package serverFacade;

import com.google.gson.Gson;
import exception.ResponseException;
import observer.ServerMessageObserver;
import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {
    private Session session;
    private ServerMessageObserver messageObserver;

    public WebSocketCommunicator(String baseUrl, ServerMessageObserver messageObserver) throws ResponseException {
        try {
            baseUrl = baseUrl.replace("http", "ws");
            URI socketUri = new URI(baseUrl + "/connect");
            this.messageObserver = messageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketUri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    messageObserver.sendMessage(serverMessage);
                }
            });
        } catch (IOException | DeploymentException | URISyntaxException e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
