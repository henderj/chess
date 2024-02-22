package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(GameData gameData) throws DataAccessException;

    GameData readGame(GameData gameData) throws DataAccessException;

    Collection<GameData> listGames();

    GameData updateGame(GameData gameData) throws DataAccessException;

    void clear();
}
