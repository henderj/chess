package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    private final Collection<AuthData> authDataCollection;

    public MemoryAuthDAO() {
        this.authDataCollection = new HashSet<>();
    }

    @Override
    public AuthData createAuth(UserData userData) throws DataAccessException {
        if (userData == null) {
            throw new DataAccessException("Cannot create authToken for null user.");
        }
        if (userData.username() == null) {
            throw new DataAccessException("Cannot create authToken for null username.");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());
        authDataCollection.add(authData);
        return authData;
    }

    @Override
    public AuthData readAuth(String authToken) {
        var authData = authDataCollection.stream().filter(a -> a.authToken().equals(authToken)).findFirst();
        return authData.orElse(null);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authDataCollection.removeIf(authData -> authData.authToken().equals(authToken));
    }

    @Override
    public void clear() {
        authDataCollection.clear();
    }
}
