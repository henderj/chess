package dataAccessTests;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class AuthDAOTests {


    static Stream<AuthDAO> implementations() {
        return Stream.of(new MemoryAuthDAO(), new SQLAuthDAO(new DatabaseManager()));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void canCreateAndGetAuth(AuthDAO authDAO) throws DataAccessException {
        authDAO.clear();
        UserData user = new UserData("name", "password", "email@email.com");
        AuthData auth = authDAO.createAuth(user);
        Assertions.assertEquals(auth, authDAO.readAuth(auth.authToken()));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void canCreateTwoAuths(AuthDAO authDAO) throws DataAccessException {
        authDAO.clear();
        UserData user = new UserData("name", "password", "email@email.com");
        authDAO.createAuth(user);
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(user));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void getNonexistentAuthReturnsNull(AuthDAO authDAO) throws DataAccessException {
        authDAO.clear();
        Assertions.assertNull(authDAO.readAuth("not_an_auth"));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void canDeleteAuth(AuthDAO authDAO) throws DataAccessException {
        authDAO.clear();
        UserData user = new UserData("name", "password", "email@email.com");
        AuthData auth = authDAO.createAuth(user);
        Assertions.assertEquals(auth, authDAO.readAuth(auth.authToken()));
        authDAO.deleteAuth(auth.authToken());
        Assertions.assertNull(authDAO.readAuth(auth.authToken()));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void clearRemovesAuths(AuthDAO authDAO) throws DataAccessException {
        authDAO.clear();
        UserData user = new UserData("name", "password", "email@email.com");
        AuthData auth = authDAO.createAuth(user);
        Assertions.assertEquals(auth, authDAO.readAuth(auth.authToken()));
        authDAO.clear();
        Assertions.assertNull(authDAO.readAuth(auth.authToken()));
    }
}
