package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.NotAuthorizedException;
import exception.ServiceException;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import response.CreateGameResponse;
import response.JoinGameResponse;
import response.ListGamesResponse;

import java.util.Collection;

public class GameService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws ServiceException {
        authenticate(request.authToken());

        if (request.gameName() == null) {
            throw new BadRequestException();
        }

        try {
            GameData gameData = gameDAO.createGame(request.gameName());
            return new CreateGameResponse(gameData.gameID());
        } catch (DataAccessException e) {
            throw new ServiceException(500, "Internal error: Game");
        }
    }

    public JoinGameResponse joinGame(JoinGameRequest request) throws ServiceException {
        var authData = authenticate(request.authToken());

        if (request.gameID() == 0) {
            throw new BadRequestException();
        }

        if (request.playerColor() != null
                && !(request.playerColor().equals("WHITE") || request.playerColor().equals("BLACK"))) {
            throw new BadRequestException("playerColor must be 'WHITE' or 'BLACK' or null");
        }

        var game = gameDAO.readGame(request.gameID());
        if (game == null) {
            throw new BadRequestException("Game with id " + request.gameID() + " does not exist");
        }

        if (request.playerColor() != null) {
            if (request.playerColor().equals("WHITE")) {
                if (game.whiteUsername() != null) {
                    throw new AlreadyTakenException("White user has already joined");
                }
                game = game.addWhiteUsername(authData.username());
            } else {
                if (game.blackUsername() != null) {
                    throw new AlreadyTakenException("Black user has already joined");
                }
                game = game.addBlackUsername(authData.username());
            }
        }

        try {
            gameDAO.updateGame(game);
            return new JoinGameResponse();
        } catch (DataAccessException e) {
            throw new ServiceException(500, "Internal error: game");
        }
    }

    public ListGamesResponse listGames(ListGamesRequest request) throws ServiceException {
        authenticate(request.authToken());

        Collection<GameData> gameDataCollection = gameDAO.listGames();

        return new ListGamesResponse(gameDataCollection.toArray(new GameData[0]));
    }

    private AuthData authenticate(String authToken) throws NotAuthorizedException {
        if (authToken == null) {
            throw new NotAuthorizedException();
        }
        AuthData authData = authDAO.readAuth(authToken);
        if (authData == null) {
            throw new NotAuthorizedException();
        }
        return authData;
    }

}
