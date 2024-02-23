package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDAOTests {
    public UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
    }

    @Test
    public void CantInsertUserTwice() {
        UserData user = new UserData("name", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> {
            userDAO.insertUser(user);
        });

        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.insertUser(user);
        });
    }

}
