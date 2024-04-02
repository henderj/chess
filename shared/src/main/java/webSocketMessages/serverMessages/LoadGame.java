package webSocketMessages.serverMessages;

import model.GameData;

public class LoadGame extends ServerMessage {
    private final GameData gameData;

    public LoadGame(ServerMessageType type, GameData gameData) {
        super(type);
        this.gameData = gameData;
    }

    public GameData getGameData() {
        return gameData;
    }
}
