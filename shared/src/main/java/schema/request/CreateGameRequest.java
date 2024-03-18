package schema.request;

public record CreateGameRequest(String authToken, String gameName) {
}
