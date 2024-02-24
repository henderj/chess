package serviceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import service.NotAuthorizedException;
import service.ServiceException;
import service.UserService;

public class UserServiceTests {
    private UserService getUserService() {
        return new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
    }

    @Test
    public void canRegisterNewUser() {
        var userService = getUserService();
        Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("name", "password", "email@email.com")));
    }

    @Test
    public void cantRegisterUserTwice() {
        var userService = getUserService();
        var request = new RegisterRequest("name", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> userService.register(request));
        Assertions.assertThrows(ServiceException.class, () -> userService.register(request));
    }

    @Test
    public void canLoginExistingUser() throws ServiceException {
        var userService = getUserService();
        var username = "name";
        var password = "password";
        var email = "email@email.com";

        userService.register(new RegisterRequest(username, password, email));
        Assertions.assertDoesNotThrow(() -> userService.login(new LoginRequest(username, password)));
    }

    @Test
    public void cannotLoginWithBadPassword() throws ServiceException {
        var userService = getUserService();
        var username = "name";
        var password = "password";
        var badPassword = "bad password";
        var email = "email@email.com";

        userService.register(new RegisterRequest(username, password, email));
        Assertions.assertThrows(NotAuthorizedException.class, () -> userService.login(new LoginRequest(username, badPassword)));
    }
}
