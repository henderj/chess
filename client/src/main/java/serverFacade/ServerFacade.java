package serverFacade;

import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

public class ServerFacade {
    public RegisterResponse register(RegisterRequest request) {
        return new RegisterResponse("TODO", "TODO");
    }

    public LoginResponse login(LoginRequest request) {
        return new LoginResponse("TODO", "TODO");
    }
}
