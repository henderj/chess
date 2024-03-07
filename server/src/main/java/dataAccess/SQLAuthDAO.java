package dataAccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {

    private final DatabaseManager databaseManager;

    public SQLAuthDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public AuthData createAuth(UserData userData) throws DataAccessException {
        if (userData == null) {
            throw new DataAccessException("Cannot create authToken for null user.");
        }
        if (userData.username() == null) {
            throw new DataAccessException("Cannot create authToken for null username.");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());
        try {
            var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());

                preparedStatement.executeUpdate();
            }
            return authData;
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error inserting authToken for username " + userData.username() + ": " + e.getMessage());
        }
    }

    @Override
    public AuthData readAuth(String authToken) throws DataAccessException {
        try {
            var statement = "SELECT username FROM auth WHERE authToken = ?";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var username = rs.getString("username");
                        return new AuthData(authToken, username);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error reading auth: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            var statement = "DELETE FROM auth WHERE authToken = ?";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            var statement = "TRUNCATE TABLE auth";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth table: " + e.getMessage());
        }
    }
}
