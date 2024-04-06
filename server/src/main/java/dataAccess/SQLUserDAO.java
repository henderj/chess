package dataAccess;

import model.UserData;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    private final DatabaseManager databaseManager;

    public SQLUserDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public UserData insertUser(UserData userData) throws DataAccessException {
        try {
            var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
                preparedStatement.setString(3, userData.email());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1022) {
                throw new DataAccessException(
                        "Cannot insert user. Username already exists. Username: " + userData.username());
            } else {
                throw new DataAccessException("Error inserting user: " + e.getMessage());
            }
        }
        return userData;
    }

    @Override
    public UserData readUser(String username) throws DataAccessException {
        try {
            var statement = "SELECT username, password, email FROM user WHERE username = ?";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var name = rs.getString("username");
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        return new UserData(name, password, email);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error reading user: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            var statement = "TRUNCATE TABLE user";
            var conn = databaseManager.getConnection();
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing user table: " + e.getMessage());
        }
    }
}
