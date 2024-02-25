package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.ClearService;

public class ClearServiceTests {
    @Test
    public void canClear() throws DataAccessException {
        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();
        var clearService = new ClearService(userDAO, authDAO, gameDAO);

        var user = new UserData("name", "pass", "email");
        userDAO.insertUser(user);

        Assertions.assertEquals(user, userDAO.readUser(user.username()));
        clearService.clear();
        Assertions.assertNull(userDAO.readUser("name"));
    }
}
