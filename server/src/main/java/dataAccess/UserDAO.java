package dataAccess;

import model.UserData;

public interface UserDAO {
    UserData insertUser(UserData userData);

    UserData readUser(UserData userData);

    void clear();
}
