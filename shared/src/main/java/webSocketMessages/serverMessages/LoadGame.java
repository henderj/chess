package webSocketMessages.serverMessages;

import model.GameData;

import java.util.Objects;

public class LoadGame extends ServerMessage {
    private final GameData game;

    public LoadGame(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public GameData getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LoadGame loadGame = (LoadGame) o;
        return Objects.equals(game, loadGame.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), game);
    }
}
