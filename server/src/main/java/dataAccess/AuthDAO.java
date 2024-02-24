package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createAuth(UserData userData) throws DataAccessException;

    AuthData readAuth(String authToken);

    void deleteAuth(String authToken) throws DataAccessException;

    void clear();
}
