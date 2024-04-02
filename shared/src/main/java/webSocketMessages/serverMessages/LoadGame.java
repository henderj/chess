package webSocketMessages.serverMessages;

import model.GameData;

import java.util.Objects;

public class LoadGame extends ServerMessage {
    private final GameData gameData;

    public LoadGame(ServerMessageType type, GameData gameData) {
        super(type);
        this.gameData = gameData;
    }

    public GameData getGameData() {
        return gameData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LoadGame loadGame = (LoadGame) o;
        return Objects.equals(gameData, loadGame.gameData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameData);
    }
}
