package webSocketMessages.userCommands;

import chess.ChessGame;

import java.util.Objects;

public class JoinPlayer extends UserGameCommand {
    private final int gameID;
    private final ChessGame.TeamColor playerColor;

    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JoinPlayer that = (JoinPlayer) o;
        return gameID == that.gameID && playerColor == that.playerColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameID, playerColor);
    }
}
