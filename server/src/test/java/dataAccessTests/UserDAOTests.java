package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class UserDAOTests {
    static Stream<UserDAO> implementations() {
        return Stream.of(new MemoryUserDAO()/*, new SQLUserDAO() */);
    }

    @ParameterizedTest
    @MethodSource("implementations")
    public void CantInsertUserTwice(UserDAO userDAO) {
        UserData user = new UserData("name", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> {
            userDAO.insertUser(user);
        });

        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.insertUser(user);
        });
    }

}
