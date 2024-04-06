package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import exception.NotAuthorizedException;
import exception.ResponseException;
import model.AuthData;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthData authenticate(String authToken) throws ResponseException {
        // TODO: refactor
        if (authToken == null) {
            throw new NotAuthorizedException();
        }
        try {
            AuthData authData = authDAO.readAuth(authToken);
            if (authData == null) {
                throw new NotAuthorizedException();
            }
            return authData;
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Internal error: Auth");
        }
    }
}
