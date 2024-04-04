import exception.ResponseException;
import serverFacade.HttpCommunicator;
import serverFacade.ServerFacade;
import serverFacade.WebSocketCommunicator;
import ui.MenuUI;

public class Main {
    public static void main(String[] args) throws ResponseException {

        String url = "http://localhost:8080";
        HttpCommunicator httpCommunicator = new HttpCommunicator(url);
        ServerFacade serverFacade = new ServerFacade(httpCommunicator);
        var menu = new MenuUI(serverFacade);
        WebSocketCommunicator webSocketCommunicator = new WebSocketCommunicator(url, menu);
        serverFacade.setWebSocketCommunicator(webSocketCommunicator);
        menu.startMenuLoop();
    }
}