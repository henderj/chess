package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.ResponseException;
import model.GameData;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class GameSession {
    private static final Logger logger = Logger.getLogger("GameSession");
    private final GameService gameService;
    private final UserService userService;
    private final int gameID;
    private Connection whitePlayerConnection;
    private Connection blackPlayerConnection;
    private final ConcurrentHashMap<String, Connection> observers = new ConcurrentHashMap<>();

    public GameSession(int gameID, GameService gameService, UserService userService) {
        this.gameID = gameID;
        this.gameService = gameService;
        this.userService = userService;
    }

    public void broadcast(String excludeAuthToken, String message) throws IOException {
        cleanUpConnections();

        if (whitePlayerConnection != null && !whitePlayerConnection.authToken().equals(excludeAuthToken)) {
            logger.fine("broadcasting message to white player: " + message);
            whitePlayerConnection.send(message);
        }
        if (blackPlayerConnection != null && !blackPlayerConnection.authToken().equals(excludeAuthToken)) {
            logger.fine("broadcasting message to black player: " + message);
            blackPlayerConnection.send(message);
        }

        for (Map.Entry<String, Connection> entry : observers.entrySet()) {
            String authToken = entry.getKey();
            Connection connection = entry.getValue();
            if (!authToken.equals(excludeAuthToken)) {
                logger.fine("broadcasting message to observer: " + message);
                connection.send(message);
            }
        }
    }

    public GameData getGameData(String authToken) throws ResponseException {
        return gameService.readGame(gameID, authToken);
    }

    public void endGame(String authToken) throws ResponseException {
        cleanUpConnections();
        if ((whitePlayerConnection == null || !authToken.equals(whitePlayerConnection.authToken())) &&
                (blackPlayerConnection == null || !authToken.equals(blackPlayerConnection.authToken()))) {
            throw new BadRequestException("Only a player can end the game.");
        }
        var gameData = getGameData(authToken);
        if (gameData.game().isEnded()) {
            throw new BadRequestException("Game has already ended.");
        }
        gameData.game().endGame();
        gameService.updateGame(gameData, authToken);
    }

    public void addPlayer(Connection connection, ChessGame.TeamColor color) throws ResponseException {
        cleanUpConnections();

        String authToken = connection.authToken();
        var username = userService.readUsername(authToken);
        var gameData = getGameData(authToken);

        if (color == ChessGame.TeamColor.WHITE) {
            if (!username.equals(gameData.whiteUsername())) {
                throw new AlreadyTakenException("White player spot is reserved for someone else");
            }
            if (whitePlayerConnection != null) {
                throw new AlreadyTakenException("White player spot is already taken");
            }
            whitePlayerConnection = connection;
        } else {
            if (!username.equals(gameData.blackUsername())) {
                throw new AlreadyTakenException("Black player spot is reserved for someone else");
            }
            if (blackPlayerConnection != null) {
                throw new AlreadyTakenException("Black player spot is already taken");
            }
            blackPlayerConnection = connection;
        }
    }

    public void addObserver(Connection connection) {
        cleanUpConnections();
        observers.put(connection.authToken(), connection);
    }

    public void removeParticipant(String authToken) {
        cleanUpConnections();
        if (whitePlayerConnection != null && whitePlayerConnection.authToken().equals(authToken)) {
            whitePlayerConnection.session().close();
            whitePlayerConnection = null;
        } else if (blackPlayerConnection != null && blackPlayerConnection.authToken().equals(authToken)) {
            blackPlayerConnection.session().close();
            blackPlayerConnection = null;
        } else {
            var conn = observers.remove(authToken);
            conn.session().close();
        }
    }

    public void cleanUpConnections() {
        if (whitePlayerConnection != null && !whitePlayerConnection.session().isOpen()) {
            whitePlayerConnection.session().close();
            whitePlayerConnection = null;
            logger.fine("cleared white player connection");
        }
        if (blackPlayerConnection != null && !blackPlayerConnection.session().isOpen()) {
            blackPlayerConnection.session().close();
            blackPlayerConnection = null;
            logger.fine("cleared black player connection");
        }

        var removeList = new ArrayList<Connection>();
        for (var c : observers.values()) {
            if (!c.session().isOpen()) {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            observers.remove(c.authToken());
            logger.fine("cleared observer connection");
        }
    }

    public Notification makeMove(String authToken, ChessMove move) throws ResponseException {
        ChessGame.TeamColor team = null;
        if (whitePlayerConnection != null && authToken.equals(whitePlayerConnection.authToken())) {
            team = ChessGame.TeamColor.WHITE;
        } else if (blackPlayerConnection != null && authToken.equals(blackPlayerConnection.authToken())) {
            team = ChessGame.TeamColor.BLACK;
        }

        if (team == null) {
            throw new BadRequestException("Only players can make moves.");
        }

        var gameData = getGameData(authToken);
        var game = gameData.game();
        if (game.isEnded()) {
            throw new BadRequestException("The game is over. No more moves are allowed.");
        }
        if (team != game.getTeamTurn()) {
            throw new BadRequestException("You can only move on your turn.");
        }

        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new BadRequestException("That is not a valid move.");
        }

        Notification notification = null;
        if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            game.endGame();
            notification = new Notification("Game over: Stalemate");
        } else if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            game.endGame();
            notification = new Notification(
                    gameData.whiteUsername() + " is in checkmate! " + gameData.blackUsername() + " won!");
        } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            game.endGame();
            notification = new Notification(
                    gameData.blackUsername() + " is in checkmate! " + gameData.whiteUsername() + " won!");
        } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            notification = new Notification(gameData.whiteUsername() + " is in check!");
        } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            notification = new Notification(gameData.blackUsername() + " is in check!");
        }

        gameService.updateGame(gameData, authToken);
        return notification;
    }
}
