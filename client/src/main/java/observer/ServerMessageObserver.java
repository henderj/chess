package observer;

import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageObserver {
    public void sendMessage(ServerMessage message);
}
