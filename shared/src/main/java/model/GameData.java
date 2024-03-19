package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) implements Comparable<GameData> {
    public GameData addWhiteUsername(String newWhiteUsername) {
        return new GameData(gameID, newWhiteUsername, blackUsername, gameName, game);
    }

    public GameData addBlackUsername(String newBlackUsername) {
        return new GameData(gameID, whiteUsername, newBlackUsername, gameName, game);
    }

    @Override
    public String toString() {
        return gameID + ": name = '" + gameName + "', white = '" + (whiteUsername == null ? "" : whiteUsername) +
                "', black = '" + (blackUsername == null ? "" : blackUsername) + "'";
    }

    @Override
    public int compareTo(GameData other) {
        return Integer.compare(this.gameID, other.gameID);
    }
}
