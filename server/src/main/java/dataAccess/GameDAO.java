package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;

    GameData readGame(int gameID);

    Collection<GameData> listGames();

    void updateGame(GameData gameData) throws DataAccessException;

    void clear();
}
