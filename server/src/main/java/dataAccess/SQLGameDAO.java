package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;

public class SQLGameDAO implements GameDAO {

    private final DatabaseManager databaseManager;

    public SQLGameDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    private String serializeGame(ChessGame game) {
        return new Gson().toJson(game);
    }

    private ChessGame deserializeGame(String gameJson) {
        return new Gson().fromJson(gameJson, ChessGame.class);
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        try {
            var statement = "INSERT INTO game (gameName, gameState) VALUES (?, ?)";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                var chessGame = new ChessGame();
                preparedStatement.setString(2, serializeGame(chessGame));
                preparedStatement.executeUpdate();

                var resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    var id = resultSet.getInt(1);
                    return new GameData(id, null, null, gameName, chessGame);
                }
                throw new DataAccessException("Could not get generated id");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    @Override
    public GameData readGame(int gameID) throws DataAccessException {
        try {
            var statement = "SELECT whiteUsername, blackUsername, gameName, gameState FROM game WHERE id = ?";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var gameStateJson = rs.getString("gameState");
                        var gameState = deserializeGame(gameStateJson);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, gameState);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error reading game: " + e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, gameState FROM game";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement);
                 var rs = preparedStatement.executeQuery()) {
                Collection<GameData> gameDataCollection = new HashSet<>();
                while (rs.next()) {
                    var id = rs.getInt("id");
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    var gameName = rs.getString("gameName");
                    var gameStateJson = rs.getString("gameState");
                    var gameData = new GameData(id, whiteUsername, blackUsername, gameName,
                                                deserializeGame(gameStateJson));
                    gameDataCollection.add(gameData);
                }
                return gameDataCollection;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error reading game lists: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        try {
            var statement = "UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, gameState=? WHERE id=?";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, gameData.whiteUsername());
                preparedStatement.setString(2, gameData.blackUsername());
                preparedStatement.setString(3, gameData.gameName());
                preparedStatement.setString(4, serializeGame(gameData.game()));
                preparedStatement.setInt(5, gameData.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }

    }

    @Override
    public void clear() throws DataAccessException {
        try {
            var statement = "TRUNCATE TABLE game";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing game table: " + e.getMessage());
        }
    }
}
