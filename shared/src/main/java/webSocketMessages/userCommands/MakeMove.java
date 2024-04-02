package webSocketMessages.userCommands;

public class MakeMove extends UserGameCommand {
    private final int gameID;

    public MakeMove(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
