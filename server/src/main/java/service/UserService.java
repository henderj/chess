package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import exception.*;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import schema.request.LoginRequest;
import schema.request.LogoutRequest;
import schema.request.RegisterRequest;
import schema.response.LoginResponse;
import schema.response.LogoutResponse;
import schema.response.RegisterResponse;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResponse register(RegisterRequest request) throws ResponseException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException();
        }
        try {
            if (userDAO.readUser(request.username()) != null) {
                throw new AlreadyTakenException("Username " + request.username() + " already taken.");
            }
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Internal error: " + e.getMessage());
        }

        UserData user = new UserData(request.username(), encryptPassword(request.password()), request.email());
        try {
            userDAO.insertUser(user);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Internal error: " + e.getMessage());
        }

        try {
            var auth = authDAO.createAuth(user);
            return new RegisterResponse(user.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Internal error: " + e.getMessage());
        }
    }

    public LoginResponse login(LoginRequest request) throws ResponseException {
        if (request.username() == null || request.password() == null) {
            throw new BadRequestException();
        }
        try {
            UserData user = userDAO.readUser(request.username());
            if (user == null) {
                throw new NotFoundException("User " + request.username() + " not found.");
            }

            if (!passwordsMatch(request.password(), user.password())) {
                throw new NotAuthorizedException("Password does not match.");
            }

            var auth = authDAO.createAuth(user);
            return new LoginResponse(user.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Internal error: Auth");
        }
    }

    public LogoutResponse logout(LogoutRequest request) throws ResponseException {
        if (request.authToken() == null) {
            throw new BadRequestException();
        }
        try {
            if (authDAO.readAuth(request.authToken()) == null) {
                throw new NotAuthorizedException("authToken does not exist: " + request.authToken());
            }

            authDAO.deleteAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Internal error: Auth");
        }
        return new LogoutResponse();
    }

    public String readUsername(String authToken) throws ResponseException {
        try {
            return authDAO.readAuth(authToken).username();
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Internal error: " + e.getMessage());
        }
    }

    private String encryptPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private boolean passwordsMatch(String clearTextPassword, String hashedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(clearTextPassword, hashedPassword);
    }
}
