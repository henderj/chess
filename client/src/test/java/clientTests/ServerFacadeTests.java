package clientTests;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import schema.request.*;
import schema.response.RegisterResponse;
import server.Server;
import serverFacade.ClientCommunicator;
import serverFacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(new ClientCommunicator("http://localhost:" + port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        facade.clear();
    }


    @Test
    public void register() throws ResponseException {
        var response = facade.register(new RegisterRequest("test", "test", "test"));
        Assertions.assertTrue(response.authToken().length() > 10);
    }

    @Test
    public void cannotRegisterSameUsernameTwice() throws ResponseException {
        var username = "name";
        RegisterRequest request = new RegisterRequest(username, "test", "test");
        facade.register(request);
        Assertions.assertThrows(ResponseException.class, () -> facade.register(request));
    }

    @Test
    public void login() throws ResponseException {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        facade.register(request);
        var response = facade.login(new LoginRequest(request.username(), request.password()));
        Assertions.assertTrue(response.authToken().length() > 10);
    }

    @Test
    public void cannotLoginWithBadPassword() throws ResponseException {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        facade.register(request);
        Assertions.assertThrows(ResponseException.class,
                                () -> facade.login(new LoginRequest(request.username(), "wrong password")));
    }

    @Test
    public void logout() throws ResponseException {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        facade.register(request);
        var response = facade.login(new LoginRequest(request.username(), request.password()));
        Assertions.assertDoesNotThrow(() -> facade.logout(new LogoutRequest(response.authToken())));
    }

    @Test
    public void cannotLogoutWithBadAuth() throws ResponseException {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        facade.register(request);
        var response = facade.login(new LoginRequest(request.username(), request.password()));
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(new LogoutRequest("not it")));
    }

    @Test
    public void createGame() throws ResponseException {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        facade.register(request);
        var response = facade.login(new LoginRequest(request.username(), request.password()));
        Assertions.assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest(response.authToken(), "game")));
    }

    @Test
    public void cannotCreateGameWithBadAuth() throws ResponseException {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        facade.register(request);
        var response = facade.login(new LoginRequest(request.username(), request.password()));
        Assertions.assertThrows(ResponseException.class,
                                () -> facade.createGame(new CreateGameRequest("not it", "game")));
    }

    @Test
    public void listGames() throws ResponseException {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        facade.register(request);
        var response = facade.login(new LoginRequest(request.username(), request.password()));
        facade.createGame(new CreateGameRequest(response.authToken(), "game"));
        var listResponse = facade.listGames(new ListGamesRequest(response.authToken()));
        assert listResponse.games() != null;
        Assertions.assertTrue(listResponse.games().length > 0);
    }

    @Test
    public void cannotListGamesWithBadAuth() throws ResponseException {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        facade.register(request);
        var response = facade.login(new LoginRequest(request.username(), request.password()));
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames(new ListGamesRequest("not it")));
    }

    @Test
    public void joinGame() throws ResponseException {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        facade.register(request);
        var response = facade.login(new LoginRequest(request.username(), request.password()));
        var gameResponse = facade.createGame(new CreateGameRequest(response.authToken(), "game"));
        var joinResponse = facade.joinGame(new JoinGameRequest(response.authToken(), "WHITE", gameResponse.gameID()));
        Assertions.assertEquals(gameResponse.gameID(), joinResponse.gameData().gameID());
    }

    @Test
    public void cannotJoinGameWithBadGameId() throws ResponseException {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        facade.register(request);
        var response = facade.login(new LoginRequest(request.username(), request.password()));
        Assertions.assertThrows(ResponseException.class,
                                () -> facade.joinGame(new JoinGameRequest(response.authToken(), "WHITE", 123)));
    }

    @Test
    public void canClear() {
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

}
