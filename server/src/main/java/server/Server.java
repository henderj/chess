package server;

import com.google.gson.Gson;
import dataAccess.*;
import schema.request.*;
import schema.response.ErrorResponse;
import service.AuthService;
import service.ClearService;
import exception.ResponseException;
import service.GameService;
import service.UserService;
import spark.*;
import websocket.WebSocketHandler;

import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger("Server");

    private static final String RESPONSE_TYPE = "application/json";

    private final AuthService authService;
    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;

    public Server() {
        var databaseManager = new DatabaseManager();

        var userDOA = new SQLUserDAO(databaseManager);
        var authDOA = new SQLAuthDAO(databaseManager);
        var gameDOA = new SQLGameDAO(databaseManager);

        authService = new AuthService(authDOA);
        userService = new UserService(userDOA, authDOA);
        clearService = new ClearService(userDOA, authDOA, gameDOA);
        gameService = new GameService(gameDOA, authService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/connect", new WebSocketHandler(gameService, userService, authService));

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::handleClear);
        Spark.post("/user", this::handleRegister);
        Spark.post("/session", this::handleLogin);
        Spark.delete("/session", this::handleLogout);
        Spark.post("/game", this::handleCreateGame);
        Spark.put("/game", this::handleJoinGame);
        Spark.get("/game", this::handleListGame);

        Spark.exception(ResponseException.class, this::handleException);

        Spark.awaitInitialization();
        logger.fine("Server started on port " + Spark.port());
        return Spark.port();
    }

    private void handleException(ResponseException exception, Request req, Response res) {
        logger.warning("Error while handling request. path: " + req.pathInfo() + "; body: " + req.body() +
                               "; exception: " + exception.getMessage());
        res.status(exception.getStatusCode());
        var errorBody = new ErrorResponse("Error: " + exception.getMessage());
        res.body(new Gson().toJson(errorBody));
    }

    private Object handleClear(Request req, Response res) throws ResponseException {
        res.type(RESPONSE_TYPE);
        clearService.clear();
        res.status(200);
        return "{}";
    }

    private Object handleRegister(Request req, Response res) throws ResponseException {
        logger.fine("Got register request with body: " + req.body());
        res.type(RESPONSE_TYPE);
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        var registerResponse = userService.register(registerRequest);
        res.status(200);
        return new Gson().toJson(registerResponse);
    }

    private Object handleLogin(Request req, Response res) throws ResponseException {
        res.type(RESPONSE_TYPE);
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        var loginResponse = userService.login(loginRequest);
        res.status(200);
        return new Gson().toJson(loginResponse);
    }

    private Object handleLogout(Request req, Response res) throws ResponseException {
        res.type(RESPONSE_TYPE);
        var logoutRequest = new LogoutRequest(req.headers("Authorization"));
        var logoutResponse = userService.logout(logoutRequest);
        res.status(200);
        return new Gson().toJson(logoutResponse);
    }

    private Object handleCreateGame(Request req, Response res) throws ResponseException {
        res.type(RESPONSE_TYPE);
        var createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        createGameRequest = new CreateGameRequest(req.headers("Authorization"), createGameRequest.gameName());
        var createGameResponse = gameService.createGame(createGameRequest);
        res.status(200);
        return new Gson().toJson(createGameResponse);
    }

    private Object handleJoinGame(Request req, Response res) throws ResponseException {
        res.type(RESPONSE_TYPE);
        var joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        joinGameRequest = new JoinGameRequest(req.headers("Authorization"), joinGameRequest.playerColor(),
                                              joinGameRequest.gameID());
        var joinGameResponse = gameService.joinGame(joinGameRequest);
        res.status(200);
        return new Gson().toJson(joinGameResponse);
    }

    private Object handleListGame(Request req, Response res) throws ResponseException {
        res.type(RESPONSE_TYPE);
        var listGameRequest = new ListGamesRequest(req.headers("Authorization"));
        var listGameResponse = gameService.listGames(listGameRequest);
        res.status(200);
        return new Gson().toJson(listGameResponse);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
