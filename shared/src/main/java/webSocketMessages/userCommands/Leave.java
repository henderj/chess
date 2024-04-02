package webSocketMessages.userCommands;

import java.util.Objects;

public class Leave extends UserGameCommand {
    private final int gameID;

    public Leave(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Leave leave = (Leave) o;
        return gameID == leave.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameID);
    }
}
