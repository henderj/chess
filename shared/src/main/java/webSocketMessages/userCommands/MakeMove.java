package webSocketMessages.userCommands;

import chess.ChessMove;

import java.util.Objects;

public class MakeMove extends UserGameCommand {
    private final int gameID;
    private final ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.move = move;
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
        return gameID == makeMove.gameID && Objects.equals(move, makeMove.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameID, move);
    }

    public ChessMove getMove() {
        return move;
    }
}
