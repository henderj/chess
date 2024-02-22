package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(GameData gameData);

    GameData readGame(GameData gameData);

    Collection<GameData> listGames();

    GameData updateGame(GameData gameData);

    void clear();
}
