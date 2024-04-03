package serverFacade;

import exception.ResponseException;
import schema.request.*;
import schema.response.*;

public class ServerFacade {
    private final HttpCommunicator httpCommunicator;

    public ServerFacade(HttpCommunicator httpCommunicator) {
        this.httpCommunicator = httpCommunicator;
    }

    public RegisterResponse register(RegisterRequest request) throws ResponseException {
        return httpCommunicator.makeRequest("POST", "/user", request, RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest request) throws ResponseException {
        return httpCommunicator.makeRequest("POST", "/session", request, LoginResponse.class);
    }

    public void logout(LogoutRequest request) throws ResponseException {
        httpCommunicator.makeRequest("DELETE", "/session", null, LogoutResponse.class, request.authToken());
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws ResponseException {
        return httpCommunicator.makeRequest("POST", "/game", request, CreateGameResponse.class, request.authToken());
    }

    public ListGamesResponse listGames(ListGamesRequest request) throws ResponseException {
        return httpCommunicator.makeRequest("GET", "/game", null, ListGamesResponse.class, request.authToken());
    }

    public JoinGameResponse joinGame(JoinGameRequest request) throws ResponseException {
        return httpCommunicator.makeRequest("PUT", "/game", request, JoinGameResponse.class, request.authToken());
    }

    public void clear() throws ResponseException {
        httpCommunicator.makeRequest("DELETE", "/db", null, null, null);
    }
}
