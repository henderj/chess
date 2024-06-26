package serviceTests;

import dataAccess.*;
import exception.NotAuthorizedException;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schema.request.CreateGameRequest;
import schema.request.JoinGameRequest;
import schema.request.ListGamesRequest;
import service.AuthService;
import service.GameService;

public class GameServiceTests {

    private GameService gameService;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    @BeforeEach
    public void setup() {
//        gameDAO = new MemoryGameDAO();
//        authDAO = new MemoryAuthDAO();
//        gameService = new GameService(authDAO, gameDAO);

        var db = new DatabaseManager();
        gameDAO = new SQLGameDAO(db);
        authDAO = new SQLAuthDAO(db);
        var authService = new AuthService(authDAO);
        gameService = new GameService(gameDAO, authService);
    }

    @Test
    public void canCreateGame() throws DataAccessException {
        AuthData auth = authDAO.createAuth(new UserData("name", "pass", "email"));
        Assertions.assertDoesNotThrow(() -> gameService.createGame(new CreateGameRequest(auth.authToken(), "game")));
    }

    @Test
    public void cannotCreateGameBadAuth() {
        Assertions.assertThrows(NotAuthorizedException.class,
                                () -> gameService.createGame(new CreateGameRequest("not an auth", "game")));
    }

    @Test
    public void canJoinGame() throws DataAccessException {
        AuthData auth = authDAO.createAuth(new UserData("name", "pass", "email"));
        GameData game = gameDAO.createGame("game");
        Assertions.assertDoesNotThrow(
                () -> gameService.joinGame(new JoinGameRequest(auth.authToken(), "WHITE", game.gameID())));
    }

    @Test
    public void cannotJoinGameBadGameID() throws DataAccessException {
        AuthData auth = authDAO.createAuth(new UserData("name", "pass", "email"));
        Assertions.assertThrows(ResponseException.class,
                                () -> gameService.joinGame(new JoinGameRequest(auth.authToken(), "WHITE", 12)));
    }

    @Test
    public void canListGames() throws DataAccessException {
        AuthData auth = authDAO.createAuth(new UserData("name", "pass", "email"));
        Assertions.assertDoesNotThrow(() -> gameService.listGames(new ListGamesRequest(auth.authToken())));
    }

    @Test
    void cannotListGamesBadAuth() {
        Assertions.assertThrows(NotAuthorizedException.class,
                                () -> gameService.listGames(new ListGamesRequest("not an auth")));
    }
}
