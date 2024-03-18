package schema.request;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {
}
