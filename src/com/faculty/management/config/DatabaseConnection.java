package com.faculty.management.config;

import com.faculty.management.exception.DatabaseException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton Database Connection Manager for Faculty Management System.
 * Connects to MySQL using pure JDBC.
 */
public class DatabaseConnection {

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final Properties LOCAL_SETTINGS = loadLocalSettings();
    private static final String URL = setting("fms.db.url", "FMS_DB_URL",
            "jdbc:mysql://localhost:3306/faculty_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=5000");
    private static final String USERNAME = setting("fms.db.user", "FMS_DB_USER", "root");
    private static final String PASSWORD = setting("fms.db.password", "FMS_DB_PASSWORD", "");

    private static DatabaseConnection instance;
    private Connection connection;

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                    "MySQL JDBC driver not found on the classpath. Add mysql-connector-j to the project classpath.");
        }
    }

    private DatabaseConnection() {
    }

    private static String setting(String propertyName, String environmentName, String defaultValue) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentName);
        }
        if (value == null || value.isBlank()) {
            value = LOCAL_SETTINGS.getProperty(propertyName);
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static Properties loadLocalSettings() {
        Properties settings = new Properties();
        Path settingsFile = findSettingsFile();
        if (settingsFile == null || !Files.exists(settingsFile)) {
            return settings;
        }

        try (InputStream input = Files.newInputStream(settingsFile)) {
            settings.load(input);
            return settings;
        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Could not read " + settingsFile + ": " + e.getMessage());
        }
    }

    private static Path findSettingsFile() {
        Path[] candidates = {
                Path.of("database", "local.properties"),
                Path.of("src", "database", "local.properties"),
                Path.of(System.getProperty("user.dir"), "database", "local.properties"),
                Path.of(System.getProperty("user.dir"), "src", "database", "local.properties")
        };

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return candidates[0];
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
    public synchronized boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public synchronized Connection getConnection() throws DatabaseException {
        try {
            if (connection != null && !connection.isClosed() && connection.isValid(2)) {
                return connection;
            }

            if (connection != null && !connection.isClosed()) {
                connection.close();
            }

            Class.forName(DRIVER_CLASS);
            Properties properties = new Properties();
            properties.setProperty("user", USERNAME);
            properties.setProperty("password", PASSWORD);
            connection = DriverManager.getConnection(URL, properties);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("MySQL JDBC driver not found on the classpath. Add mysql-connector-j to the project classpath.", e);
        } catch (SQLException e) {
            String guidance = switch (e.getErrorCode()) {
                case 1045 -> "MySQL rejected the configured username or password. Configure "
                        + "FMS_DB_USER/FMS_DB_PASSWORD (or -Dfms.db.user/-Dfms.db.password) "
                        + "with your MySQL credentials.";
                case 1044 -> "The configured MySQL user does not have access to "
                        + "faculty_management_db. Grant database privileges or configure a user "
                        + "that has access.";
                case 1049 -> "The faculty_management_db database does not exist. Run "
                        + "database/01_create_database.sql, then the remaining setup scripts.";
                default -> "Cannot connect to the configured MySQL database. Ensure the MySQL "
                        + "service is running and configure FMS_DB_USER/FMS_DB_PASSWORD (or "
                        + "-Dfms.db.user/-Dfms.db.password).";
            };
            throw new DatabaseException(guidance + " MySQL error " + e.getErrorCode() + ": "
                    + e.getMessage(), e);
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
