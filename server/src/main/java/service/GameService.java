package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.NotAuthorizedException;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import schema.request.CreateGameRequest;
import schema.request.JoinGameRequest;
import schema.request.ListGamesRequest;
import schema.response.CreateGameResponse;
import schema.response.JoinGameResponse;
import schema.response.ListGamesResponse;

import java.util.Collection;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws ResponseException {
        authenticate(request.authToken());

        if (request.gameName() == null) {
            throw new BadRequestException();
        }

        try {
            GameData gameData = gameDAO.createGame(request.gameName());
            return new CreateGameResponse(gameData.gameID());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Internal error: " + e.getMessage());
        }
    }

    public JoinGameResponse joinGame(JoinGameRequest request) throws ResponseException {
        var authData = authenticate(request.authToken());

        if (request.gameID() == 0) {
            throw new BadRequestException();
        }

        if (request.playerColor() != null
                && !(request.playerColor().equals("WHITE") || request.playerColor().equals("BLACK"))) {
            throw new BadRequestException("playerColor must be 'WHITE' or 'BLACK' or null");
        }

        try {
            var game = gameDAO.readGame(request.gameID());
            if (game == null) {
                throw new BadRequestException("Game with id " + request.gameID() + " does not exist");
            }

            var username = authDAO.readAuth(request.authToken()).username();

            if (request.playerColor() != null) {
                if (request.playerColor().equals("WHITE")) {
                    if (game.whiteUsername() != null && !game.whiteUsername().equals(username)) {
                        throw new AlreadyTakenException("White user has already joined");
                    }
                    game = game.addWhiteUsername(authData.username());
                } else {
                    if (game.blackUsername() != null && !game.blackUsername().equals(username)) {
                        throw new AlreadyTakenException("Black user has already joined");
                    }
                    game = game.addBlackUsername(authData.username());
                }
            }

            gameDAO.updateGame(game);
            return new JoinGameResponse(game);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Internal error: game");
        }
    }

    public ListGamesResponse listGames(ListGamesRequest request) throws ResponseException {
        authenticate(request.authToken());

        try {
            Collection<GameData> gameDataCollection = gameDAO.listGames();
            return new ListGamesResponse(gameDataCollection.toArray(new GameData[0]));
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error reading games: " + e.getMessage());
        }
    }

    public GameData readGame(int gameID, String authToken) throws ResponseException {
        authenticate(authToken);

        try {
            return gameDAO.readGame(gameID);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Internal error: " + e.getMessage());
        }
    }

    private AuthData authenticate(String authToken) throws ResponseException {
        if (authToken == null) {
            throw new NotAuthorizedException();
        }
        try {
            AuthData authData = authDAO.readAuth(authToken);
            if (authData == null) {
                throw new NotAuthorizedException();
            }
            return authData;
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Internal error: Auth");
        }
    }

}
