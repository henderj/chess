package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;
import request.RegisterRequest;
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


}
