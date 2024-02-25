package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{

    private final Collection<GameData> gameDataCollection;

    public MemoryGameDAO() {
        this.gameDataCollection = new HashSet<>();
    }

    @Override
    public GameData createGame(String gameName) {
        GameData data = new GameData(gameDataCollection.size() + 1, null, null, gameName, new ChessGame());
        gameDataCollection.add(data);
        return data;
    }

    @Override
    public GameData readGame(int gameID) {
        var gameData = gameDataCollection.stream().filter(g -> g.gameID() == gameID).findFirst();
        return gameData.orElse(null);
    }

    @Override
    public Collection<GameData> listGames() {
        return gameDataCollection;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if (gameDataCollection.stream().noneMatch(g -> g.gameID() == gameData.gameID())) {
            throw new DataAccessException("Cannot update game with gameID " + gameData.gameID() + ". Game does not exist.");
        }
        gameDataCollection.removeIf(g -> g.gameID() == gameData.gameID());
        gameDataCollection.add(gameData);
    }

    @Override
    public void clear() {
        gameDataCollection.clear();
    }
}
