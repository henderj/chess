package request;

public record CreateGameRequest(String authToken, String gameName) {
}
