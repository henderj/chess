package dataAccess;

import model.AuthData;
import model.UserData;

public class MemoryAuthDAO implements AuthDAO{
    @Override
    public AuthData createAuth(UserData userData) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(AuthData authData){
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
