package serverFacade;


import schema.request.LoginRequest;
import schema.request.RegisterRequest;
import schema.response.LoginResponse;
import schema.response.RegisterResponse;

public class ServerFacade {
    public RegisterResponse register(RegisterRequest request) {
        return new RegisterResponse("TODO", "TODO");
    }

    public LoginResponse login(LoginRequest request) {
        return new LoginResponse("TODO", "TODO");
    }
}
