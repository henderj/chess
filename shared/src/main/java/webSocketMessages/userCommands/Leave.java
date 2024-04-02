package webSocketMessages.userCommands;

public class Leave extends UserGameCommand {
    private final int gameID;

    public Leave(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
