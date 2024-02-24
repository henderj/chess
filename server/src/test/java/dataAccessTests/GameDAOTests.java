package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.stream.Stream;

public class GameDAOTests {

    static Stream<GameDAO> implementations() {
        return Stream.of(new MemoryGameDAO()/*, new SQLGameDAO() */);
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void canCreateAndGetGame(GameDAO gameDAO) throws DataAccessException {
        GameData gameData = gameDAO.createGame("new game");
        Assertions.assertEquals(gameData, gameDAO.readGame(gameData.gameID()));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void canUpdateGame(GameDAO gameDAO) throws DataAccessException {
        GameData gameData = gameDAO.createGame("new game");
        Assertions.assertNull(gameDAO.readGame(gameData.gameID()).whiteUsername());
        gameData = gameData.addWhiteUsername("white");
        gameDAO.updateGame(gameData);
        Assertions.assertEquals(gameData, gameDAO.readGame(gameData.gameID()));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void canListGames(GameDAO gameDAO) throws DataAccessException {
        GameData gameData = gameDAO.createGame("new game");
        GameData gameData2 = gameDAO.createGame("another new game");
        Collection<GameData> gameDataCollection = gameDAO.listGames();
        Assertions.assertTrue(gameDataCollection.contains(gameData));
        Assertions.assertTrue(gameDataCollection.contains(gameData2));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void clearRemovesGames(GameDAO gameDAO) throws DataAccessException {
        GameData gameData = gameDAO.createGame("new game");
        Assertions.assertEquals(gameData, gameDAO.readGame(gameData.gameID()));
        gameDAO.clear();
        Assertions.assertNull(gameDAO.readGame(gameData.gameID()));
    }
}
