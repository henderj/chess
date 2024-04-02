package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public record Connection(String authToken, Session session) {

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
