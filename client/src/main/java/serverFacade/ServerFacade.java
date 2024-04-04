package serverFacade;

import chess.ChessGame;
import exception.ResponseException;
import schema.request.*;
import schema.response.*;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.Leave;

public class ServerFacade {
    private final HttpCommunicator httpCommunicator;
    private WebSocketCommunicator webSocketCommunicator;

    public ServerFacade(HttpCommunicator httpCommunicator) {
        this.httpCommunicator = httpCommunicator;
    }

    public void setWebSocketCommunicator(WebSocketCommunicator webSocketCommunicator) {
        this.webSocketCommunicator = webSocketCommunicator;
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
        var response = httpCommunicator.makeRequest("PUT", "/game", request, JoinGameResponse.class,
                                                    request.authToken());

        if (request.playerColor() == null) {
            var command = new JoinObserver(request.authToken(), request.gameID());
            webSocketCommunicator.sendCommand(command);
        } else {
            var command = new JoinPlayer(request.authToken(), request.gameID(),
                                         request.playerColor().equals("WHITE") ? ChessGame.TeamColor.WHITE :
                                                 ChessGame.TeamColor.BLACK);
            webSocketCommunicator.sendCommand(command);
        }


        return response;
    }

    public void leaveGame(Leave command) throws ResponseException {
        webSocketCommunicator.sendCommand(command);
    }

    public void clear() throws ResponseException {
        httpCommunicator.makeRequest("DELETE", "/db", null, null, null);
    }
}
