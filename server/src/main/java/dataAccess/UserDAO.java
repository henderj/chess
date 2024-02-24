package dataAccess;

import model.UserData;

public interface UserDAO {
    UserData insertUser(UserData userData) throws DataAccessException;

    UserData readUser(String username);

    void clear();
}
