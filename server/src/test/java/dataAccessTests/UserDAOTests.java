package dataAccessTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class UserDAOTests {

    static Stream<UserDAO> implementations() {
        return Stream.of(new MemoryUserDAO(), new SQLUserDAO(new DatabaseManager()));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void cantInsertUserTwice(UserDAO userDAO) throws DataAccessException {
        userDAO.clear();
        UserData user = new UserData("name", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> userDAO.insertUser(user));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.insertUser(user));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void cantInsertUserWithSameUsername(UserDAO userDAO) throws DataAccessException {
        userDAO.clear();
        UserData user = new UserData("name", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> userDAO.insertUser(user));
        UserData user2 = new UserData("name", "password2", "email2@email.com");
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.insertUser(user2));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void canCreateAndGetUser(UserDAO userDAO) throws DataAccessException {
        userDAO.clear();
        UserData user = new UserData("name", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> userDAO.insertUser(user));
        Assertions.assertEquals(user, userDAO.readUser(user.username()));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void getNonexistentUserReturnsNull(UserDAO userDAO) throws DataAccessException {
        userDAO.clear();
        Assertions.assertNull(userDAO.readUser("not_a_name"));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void clearRemovesUsers(UserDAO userDAO) throws DataAccessException {
        userDAO.clear();
        UserData user = new UserData("name", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> userDAO.insertUser(user));
        userDAO.clear();
        Assertions.assertNull(userDAO.readUser(user.username()));
    }

}
