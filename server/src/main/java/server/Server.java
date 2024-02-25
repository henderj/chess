package server;

import com.google.gson.Gson;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import request.*;
import response.ErrorResponse;
import service.ClearService;
import exception.ServiceException;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private static final String RESPONSE_TYPE = "application/json";

    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;

    public Server() {
        var userDOA = new MemoryUserDAO();
        var authDOA = new MemoryAuthDAO();
        var gameDOA = new MemoryGameDAO();

        userService = new UserService(userDOA, authDOA);
        clearService = new ClearService(userDOA, authDOA, gameDOA);
        gameService = new GameService(authDOA, gameDOA);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::handleClear);
        Spark.post("/user", this::handleRegister);
        Spark.post("/session", this::handleLogin);
        Spark.delete("/session", this::handleLogout);
        Spark.post("/game", this::handleCreateGame);
        Spark.put("/game", this::handleJoinGame);
        Spark.get("/game", this::handleListGame);

        Spark.exception(ServiceException.class, this::handleException);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void handleException(ServiceException exception, Request req, Response res) {
        res.status(exception.getStatusCode());
        var errorBody = new ErrorResponse("Error: " + exception.getMessage());
        res.body(new Gson().toJson(errorBody));
    }

    private Object handleClear(Request req, Response res) {
        res.type(RESPONSE_TYPE);
        clearService.clear();
        res.status(200);
        return "{}";
    }

    private Object handleRegister(Request req, Response res) throws ServiceException {
        res.type(RESPONSE_TYPE);
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        var registerResponse = userService.register(registerRequest);
        res.status(200);
        return new Gson().toJson(registerResponse);
    }

    private Object handleLogin(Request req, Response res) throws ServiceException {
        res.type(RESPONSE_TYPE);
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        var loginResponse = userService.login(loginRequest);
        res.status(200);
        return new Gson().toJson(loginResponse);
    }

    private Object handleLogout(Request req, Response res) throws ServiceException {
        res.type(RESPONSE_TYPE);
        var logoutRequest = new LogoutRequest(req.headers("Authorization"));
        var logoutResponse = userService.logout(logoutRequest);
        res.status(200);
        return new Gson().toJson(logoutResponse);
    }

    private Object handleCreateGame(Request req, Response res) throws ServiceException {
        res.type(RESPONSE_TYPE);
        var createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        createGameRequest = new CreateGameRequest(req.headers("Authorization"), createGameRequest.gameName());
        var createGameResponse = gameService.createGame(createGameRequest);
        res.status(200);
        return new Gson().toJson(createGameResponse);
    }

    private Object handleJoinGame(Request req, Response res) throws ServiceException {
        res.type(RESPONSE_TYPE);
        var joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        joinGameRequest = new JoinGameRequest(req.headers("Authorization"), joinGameRequest.playerColor(), joinGameRequest.gameID());
        var joinGameResponse = gameService.joinGame(joinGameRequest);
        res.status(200);
        return new Gson().toJson(joinGameResponse);
    }

    private Object handleListGame(Request req, Response res) throws ServiceException {
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
