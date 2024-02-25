package response;

import model.GameData;

public record ListGamesResponse(GameData[] games) {
}
