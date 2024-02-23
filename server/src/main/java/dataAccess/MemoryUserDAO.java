package dataAccess;

import model.UserData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryUserDAO implements UserDAO{

    private Collection<UserData> userDataCollection;

    public MemoryUserDAO(){
        this.userDataCollection = new HashSet<>();
    }

    @Override
    public UserData insertUser(UserData userData) throws DataAccessException {
        if (userDataCollection.contains(userData)){
            throw new DataAccessException("Cannot insert user. User already exists. User: " + userData);
        }
        userDataCollection.add(userData);
        return userData;
    }

    @Override
    public UserData readUser(UserData userData) {
        // change input to just string?
        return null;
    }

    @Override
    public void clear() {

    }
}
