package com.faculty.management.config;

import com.faculty.management.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton Database Connection Manager for Faculty Management System.
 * Connects to MySQL using pure JDBC.
 */
public class DatabaseConnection {

    private static final String URL = setting("fms.db.url", "FMS_DB_URL",
            "jdbc:mysql://localhost:3306/faculty_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=5000");
    private static final String USERNAME = setting("fms.db.user", "FMS_DB_USER", "root");
    private static final String PASSWORD = setting("fms.db.password", "FMS_DB_PASSWORD", "");

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
    }

    private static String setting(String propertyName, String environmentName, String defaultValue) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentName);
        }
        return value == null || value.isBlank() ? defaultValue : value;
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
    public synchronized Connection getConnection() throws DatabaseException {
        try {
            if (connection != null && !connection.isClosed() && connection.isValid(2)) {
                return connection;
            }

            if (connection != null && !connection.isClosed()) {
                connection.close();
            }

            Properties properties = new Properties();
            properties.setProperty("user", USERNAME);
            properties.setProperty("password", PASSWORD);
            connection = DriverManager.getConnection(URL, properties);
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Cannot connect to the configured MySQL database. Start the MySQL service, create "
                            + "faculty_management_db, and configure "
                            + "FMS_DB_USER/FMS_DB_PASSWORD (or -Dfms.db.user/-Dfms.db.password). "
                            + "Details: " + e.getMessage(),
                    e);
        }
    }

    /**
     * Closes the connection cleanly.
     */
    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
