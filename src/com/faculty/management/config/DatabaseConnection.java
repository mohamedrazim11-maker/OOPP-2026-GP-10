package com.faculty.management.config;

import com.faculty.management.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Database Connection Manager for Faculty Management System.
 * Connects to MySQL using pure JDBC.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/faculty_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Rsn@4478";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found in classpath: " + e.getMessage());
        }
    }

    /**
     * Retrieves the singleton instance.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Gets an active database connection.
     * Reconnects automatically if the previous connection is closed or null.
     *
     * @return Active SQL Connection
     * @throws DatabaseException If unable to connect to the database
     */
    public Connection getConnection() throws DatabaseException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to MySQL database: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if the database connection can be established.
     *
     * @return true if database is reachable, false otherwise
     */
    public boolean isConnected() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Closes the connection cleanly.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
