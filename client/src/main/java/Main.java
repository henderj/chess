import chess.*;
import serverFacade.ClientCommunicator;
import serverFacade.ServerFacade;
import ui.MenuUI;

public class Main {
    public static void main(String[] args) {
        var menu = new MenuUI(new ServerFacade(new ClientCommunicator("http://localhost:8080")));
        menu.startMenuLoop();
    }
}