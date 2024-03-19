package serverFacade;

import exception.ResponseException;
import model.GameData;
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

    public LogoutResponse logout(LogoutRequest request) throws ResponseException {
        return clientCommunicator.makeRequest("DELETE", "/session", null, LogoutResponse.class, request.authToken());
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws ResponseException {
        return clientCommunicator.makeRequest("POST", "/game", request, CreateGameResponse.class, request.authToken());
    }

    public ListGamesResponse listGames(ListGamesRequest request) {
        // TODO
        return new ListGamesResponse(new GameData[]{new GameData(1234, null, null, "TODO", null)});
    }

    public JoinGameResponse joinGame(JoinGameRequest request) {
        // TODO
        return new JoinGameResponse();
    }
}
