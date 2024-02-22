package dataAccess;

import model.UserData;

public interface UserDAO {
    void insertUser(UserData u);

    UserData readUser(UserData u);

    void clear();
}
