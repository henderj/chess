package dataAccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private final String databaseName;
    private final String user;
    private final String password;
    private final String connectionUrl;

    private boolean databaseInitialized = false;

    public DatabaseManager() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) throw new Exception("Unable to load db.properties");
            Properties props = new Properties();
            props.load(propStream);
            databaseName = props.getProperty("db.name");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            var host = props.getProperty("db.host");
            var port = Integer.parseInt(props.getProperty("db.port"));
            connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    private void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void createTables() throws DataAccessException {
        try {
            String[] statements = {
                    """
                    CREATE TABLE IF NOT EXISTS user (
                        username VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        PRIMARY KEY (username)
                    );
                    """,
                    """
                    CREATE TABLE IF NOT EXISTS auth (
                        authToken VARCHAR(255) NOT NULL,
                        username VARCHAR(255) NOT NULL,
                        PRIMARY KEY (authToken),
                        FOREIGN KEY (username) REFERENCES user(username)
                    );
                    """,
                    """
                    CREATE TABLE IF NOT EXISTS game (
                        id INT NOT NULL AUTO_INCREMENT,
                        whiteUsername VARCHAR(255),
                        blackUsername VARCHAR(255),
                        gameName VARCHAR(255) NOT NULL,
                        gameData VARCHAR(1024),
                        PRIMARY KEY (id),
                        FOREIGN KEY (whiteUsername) REFERENCES user(username),
                        FOREIGN KEY (blackUsername) REFERENCES user(username)
                    );
                    """};
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog(databaseName);
            for (var statement : statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void initializeDatabase() throws DataAccessException {
        if (databaseInitialized) return;
        createDatabase();
        createTables();
        databaseInitialized = true;
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public Connection getConnection() throws DataAccessException {
        initializeDatabase();
        try {
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
