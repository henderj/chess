package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import request.RegisterRequest;
import response.ErrorResponse;
import service.AlreadyTakenException;
import service.ClearService;
import service.ServiceException;
import service.UserService;
import spark.*;

public class Server {

    private static final String RESPONSE_TYPE = "application/json";

    private final UserService userService;
    private final ClearService clearService;

    public Server() {
        var userDOA = new MemoryUserDAO();
        var authDOA = new MemoryAuthDAO();
        var gameDOA = new MemoryGameDAO();

        userService = new UserService(userDOA, authDOA);
        clearService = new ClearService(userDOA, authDOA, gameDOA);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::handleClear);
        Spark.post("/user", this::handleRegister);

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

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
