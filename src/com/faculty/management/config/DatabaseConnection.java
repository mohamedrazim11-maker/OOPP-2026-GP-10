package com.faculty.management.config;

import com.faculty.management.exception.DatabaseException;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton Database Connection Manager for Faculty Management System.
 * Connects directly to MySQL using pure JDBC.
 * Supports configurable connection properties and automatic driver loading.
 */
public class DatabaseConnection {

    private static String url = "jdbc:mysql://localhost:3306/faculty_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=3000&socketTimeout=5000";
    private static String username = "root";
    private static String password = "";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        loadConfiguration();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * Loads database configuration from database/local.properties if present.
     */
    private void loadConfiguration() {
        File configFile = new File("database/local.properties");
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                Properties props = new Properties();
                props.load(fis);
                if (props.containsKey("db.url")) {
                    url = props.getProperty("db.url");
                }
                if (props.containsKey("db.username")) {
                    username = props.getProperty("db.username");
                }
                if (props.containsKey("db.password")) {
                    password = props.getProperty("db.password");
                }
            } catch (Exception e) {
                System.err.println("Note: Could not read database/local.properties: " + e.getMessage());
            }
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
    public synchronized Connection getConnection() throws DatabaseException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, username, password);
            }
            return connection;
        } catch (SQLException e) {
            String errorMsg = "Cannot connect to MySQL database on localhost:3306. " +
                    "Please start your MySQL service (e.g. XAMPP / WAMP) and import 'faculty_management_db'.";
            throw new DatabaseException(errorMsg, e);
        }
    }

    /**
     * Checks if the database connection can be established.
     *
     * @return true if database is reachable, false otherwise
     */
    public boolean isConnected() {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
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

    public static String getUrl() {
        return url;
    }

    public static String getUsername() {
        return username;
    }
}
