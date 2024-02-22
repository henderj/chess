package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createAuth(UserData userData) throws DataAccessException;

    AuthData getAuth(AuthData authData) throws DataAccessException;

    void deleteAuth(AuthData authData) throws DataAccessException;

    void clear();
}
