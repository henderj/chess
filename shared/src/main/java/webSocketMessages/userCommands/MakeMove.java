package webSocketMessages.userCommands;

import java.util.Objects;

public class MakeMove extends UserGameCommand {
    private final int gameID;

    public MakeMove(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
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
        MakeMove makeMove = (MakeMove) o;
        return gameID == makeMove.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameID);
    }
}
