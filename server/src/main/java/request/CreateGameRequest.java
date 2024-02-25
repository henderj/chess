package request;

public record CreateGameRequest(String authToken, String gameName) {
    public CreateGameRequest addAuthToken(String newAuthToken) {
        return new CreateGameRequest(newAuthToken, gameName);
    }
}
