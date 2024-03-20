package serverFacade;

import exception.ResponseException;
import schema.request.*;
import schema.response.*;

public class ServerFacade {
    private final ClientCommunicator clientCommunicator;

    public ServerFacade(ClientCommunicator clientCommunicator) {
        this.clientCommunicator = clientCommunicator;
    }

    public RegisterResponse register(RegisterRequest request) throws ResponseException {
        return clientCommunicator.makeRequest("POST", "/user", request, RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest request) throws ResponseException {
        return clientCommunicator.makeRequest("POST", "/session", request, LoginResponse.class);
    }

    public void logout(LogoutRequest request) throws ResponseException {
        clientCommunicator.makeRequest("DELETE", "/session", null, LogoutResponse.class, request.authToken());
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws ResponseException {
        return clientCommunicator.makeRequest("POST", "/game", request, CreateGameResponse.class, request.authToken());
    }

    public ListGamesResponse listGames(ListGamesRequest request) throws ResponseException {
        return clientCommunicator.makeRequest("GET", "/game", null, ListGamesResponse.class, request.authToken());
    }

    public JoinGameResponse joinGame(JoinGameRequest request) throws ResponseException {
        return clientCommunicator.makeRequest("PUT", "/game", request, JoinGameResponse.class, request.authToken());
    }
}
