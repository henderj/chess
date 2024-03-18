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
        if (userDataCollection.stream().anyMatch(u -> u.username().equals(userData.username()))) {
            throw new DataAccessException("Cannot insert user. Username already exists. User: " + userData);
        }
        userDataCollection.add(userData);
        return userData;
    }

    @Override
    public UserData readUser(String username) {
        var user = userDataCollection.stream().filter(u -> u.username().equals(username)).toList();
        if(user.isEmpty()) {
            return null;
        }
        return user.getFirst();
    }

    @Override
    public void clear() {
        userDataCollection.clear();
    }
}
