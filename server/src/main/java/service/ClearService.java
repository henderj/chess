package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import exception.ServiceException;

public class ClearService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() throws ServiceException {
        try {
            gameDAO.clear();
            authDAO.clear();
            userDAO.clear();
        } catch (DataAccessException e) {
            throw new ServiceException(500, "Internal error: Clear");
        }
    }
}
