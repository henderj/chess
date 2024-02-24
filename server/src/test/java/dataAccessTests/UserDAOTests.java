package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class UserDAOTests {
    static Stream<UserDAO> implementations() {
        return Stream.of(new MemoryUserDAO()/*, new SQLUserDAO() */);
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void cantInsertUserTwice(UserDAO userDAO) {
        UserData user = new UserData("name", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> userDAO.insertUser(user));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.insertUser(user));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void cantInsertUserWithSameUsername(UserDAO userDAO) {
        UserData user = new UserData("name", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> userDAO.insertUser(user));
        UserData user2 = new UserData("name", "password2", "email2@email.com");
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.insertUser(user2));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void canCreateAndGetUser(UserDAO userDAO) {
        UserData user = new UserData("name", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> userDAO.insertUser(user));
        Assertions.assertDoesNotThrow(() -> Assertions.assertEquals(user, userDAO.readUser(user.username())));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void getNonexistentUserReturnsNull(UserDAO userDAO) {
       Assertions.assertDoesNotThrow(() -> Assertions.assertNull(userDAO.readUser("not_a_name")));
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void clearRemovesUsers(UserDAO userDAO) {
        UserData user = new UserData("name", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> userDAO.insertUser(user));
        userDAO.clear();
        Assertions.assertDoesNotThrow(() -> Assertions.assertNull(userDAO.readUser(user.username())));
    }

}
