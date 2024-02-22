package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createAuth(UserData userData);

    AuthData getAuth(AuthData authData);

    void deleteAuth(AuthData authData);
}
