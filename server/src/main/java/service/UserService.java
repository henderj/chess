package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.LogoutResponse;
import response.RegisterResponse;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResponse register(RegisterRequest request) throws ServiceException {
        if (userDAO.readUser(request.username()) != null) {
            throw new AlreadyTakenException("Username " + request.username() + " already taken.");
        }

        UserData user = new UserData(request.username(), request.password(), request.email());
        try {
            userDAO.insertUser(user);
        } catch (DataAccessException e) {
            throw new ServiceException("Internal error: User");
        }

        try {
            var auth = authDAO.createAuth(user);
            return new RegisterResponse(user.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new ServiceException("Internal error: Auth");
        }
    }

    public LoginResponse login(LoginRequest request) throws ServiceException {
        UserData user = userDAO.readUser(request.username());
        if (user == null) {
            throw new NotFoundException("User " + request.username() + " not found.");
        }

        if (!user.password().equals(request.password())) {
            throw new NotAuthorizedException("Password does not match.");
        }

        try {
            var auth = authDAO.createAuth(user);
            return new LoginResponse(user.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new ServiceException("Internal error: Auth");
        }
    }

    public LogoutResponse logout(LogoutRequest request) throws ServiceException {
        if (authDAO.readAuth(request.authToken()) == null) {
            throw new NotAuthorizedException("authToken does not exist: " + request.authToken());
        }

        try {
            authDAO.deleteAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new ServiceException("Internal error: Auth");
        }
        return new LogoutResponse();
    }
}
