package com.faculty.management.config;

import com.faculty.management.exception.DatabaseException;
<<<<<<< HEAD
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
=======
import java.io.File;
import java.io.FileInputStream;
>>>>>>> test
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton Database Connection Manager for Faculty Management System.
 * Supports dual-database mode:
 * 1. MySQL (via XAMPP / MariaDB)
 * 2. Microsoft SQL Server (via SSMS / SQLEXPRESS)
 *
 * Configurable via database/local.properties or auto-detects whichever is online.
 */
public class DatabaseConnection {

<<<<<<< HEAD
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final Properties LOCAL_SETTINGS = loadLocalSettings();
    private static final String URL = setting("fms.db.url", "FMS_DB_URL",
            "jdbc:mysql://localhost:3306/faculty_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=5000");
    private static final String USERNAME = setting("fms.db.user", "FMS_DB_USER", "root");
    private static final String PASSWORD = setting("fms.db.password", "FMS_DB_PASSWORD", "");
=======
    private static String dbType = "auto"; // "auto", "mysql", or "sqlserver"

    // Default MySQL configuration (XAMPP)
    private static String mysqlUrl = "jdbc:mysql://localhost:3306/faculty_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=2000&socketTimeout=3000";
    private static String mysqlUser = "root";
    private static String mysqlPassword = "";

    // Default SQL Server configuration (SSMS / SQLEXPRESS)
    private static String sqlServerUrl = "jdbc:sqlserver://localhost:1433;databaseName=faculty_management_db;user=fms_user;password=FmsPass123!;encrypt=true;trustServerCertificate=true;loginTimeout=2;";
    private static String sqlServerUser = "fms_user";
    private static String sqlServerPassword = "FmsPass123!";

    // Currently active connection details
    private static String activeUrl = mysqlUrl;
    private static String activeUser = mysqlUser;
    private static String activePassword = mysqlPassword;
    private static String activeDatabaseType = "None"; // "MySQL", "SQL Server", or "None"
>>>>>>> test

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
<<<<<<< HEAD
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
=======
        loadConfiguration();
        loadDrivers();
    }

    private void loadDrivers() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
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
                if (props.containsKey("db.type")) {
                    dbType = props.getProperty("db.type").trim().toLowerCase();
                }
                if (props.containsKey("mysql.url")) {
                    mysqlUrl = props.getProperty("mysql.url").trim();
                }
                if (props.containsKey("mysql.username")) {
                    mysqlUser = props.getProperty("mysql.username").trim();
                }
                if (props.containsKey("mysql.password")) {
                    mysqlPassword = props.getProperty("mysql.password");
                }
                if (props.containsKey("sqlserver.url")) {
                    sqlServerUrl = props.getProperty("sqlserver.url").trim();
                }
                if (props.containsKey("sqlserver.username")) {
                    sqlServerUser = props.getProperty("sqlserver.username").trim();
                }
                if (props.containsKey("sqlserver.password")) {
                    sqlServerPassword = props.getProperty("sqlserver.password");
                }

                // Legacy fallback support for db.url / db.username / db.password
                if (props.containsKey("db.url")) {
                    String legacyUrl = props.getProperty("db.url").trim();
                    if (legacyUrl.contains("sqlserver")) {
                        sqlServerUrl = legacyUrl;
                        if (props.containsKey("db.username")) sqlServerUser = props.getProperty("db.username").trim();
                        if (props.containsKey("db.password")) sqlServerPassword = props.getProperty("db.password");
                    } else {
                        mysqlUrl = legacyUrl;
                        if (props.containsKey("db.username")) mysqlUser = props.getProperty("db.username").trim();
                        if (props.containsKey("db.password")) mysqlPassword = props.getProperty("db.password");
                    }
                }
            } catch (Exception e) {
                System.err.println("Note: Could not read database/local.properties: " + e.getMessage());
            }
>>>>>>> test
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
     */
<<<<<<< HEAD
    public synchronized boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

=======
>>>>>>> test
    public synchronized Connection getConnection() throws DatabaseException {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
        } catch (SQLException ignored) {
        }

        // 1. If explicit MySQL requested
        if ("mysql".equals(dbType)) {
            try {
                connection = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword);
                activeUrl = mysqlUrl;
                activeUser = mysqlUser;
                activePassword = mysqlPassword;
                activeDatabaseType = "MySQL";
                return connection;
            } catch (SQLException e) {
                activeDatabaseType = "None";
                throw new DatabaseException("Cannot connect to MySQL database on localhost:3306. " +
                        "Please start MySQL in XAMPP and import 'faculty_management_db'.", e);
            }
        }

<<<<<<< HEAD
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
=======
        // 2. If explicit SQL Server requested
        if ("sqlserver".equals(dbType)) {
            try {
                connection = DriverManager.getConnection(sqlServerUrl, sqlServerUser, sqlServerPassword);
                activeUrl = sqlServerUrl;
                activeUser = sqlServerUser;
                activePassword = sqlServerPassword;
                activeDatabaseType = "SQL Server";
                return connection;
            } catch (SQLException e) {
                activeDatabaseType = "None";
                throw new DatabaseException("Cannot connect to Microsoft SQL Server on localhost:1433. " +
                        "Please ensure SQL Server is running and 'faculty_management_db' is imported.", e);
            }
        }

        // 3. Auto-detection mode: Try MySQL first; fallback to SQL Server
        try {
            connection = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword);
            activeUrl = mysqlUrl;
            activeUser = mysqlUser;
            activePassword = mysqlPassword;
            activeDatabaseType = "MySQL";
            return connection;
        } catch (SQLException mysqlEx) {
            // Try Microsoft SQL Server
            try {
                connection = DriverManager.getConnection(sqlServerUrl, sqlServerUser, sqlServerPassword);
                activeUrl = sqlServerUrl;
                activeUser = sqlServerUser;
                activePassword = sqlServerPassword;
                activeDatabaseType = "SQL Server";
                return connection;
            } catch (SQLException sqlServerEx) {
                activeDatabaseType = "None";
                throw new DatabaseException("No active database detected.\n" +
                        "• For MySQL: Start MySQL in XAMPP.\n" +
                        "• For SQL Server: Ensure SQL Server service is running.\n" +
                        "Make sure 'faculty_management_db' exists.", mysqlEx);
            }
        }
    }

    /**
     * Checks if the database connection can be established.
     */
    public boolean isConnected() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
>>>>>>> test
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

    public synchronized String getActiveDatabaseType() {
        if ("None".equals(activeDatabaseType)) {
            isConnected(); // Attempt detection
        }
        return activeDatabaseType;
    }

    public boolean isSqlServer() {
        return "SQL Server".equalsIgnoreCase(getActiveDatabaseType());
    }

    public static String getUrl() {
        return activeUrl;
    }

    public static String getUsername() {
        return activeUser;
    }
}
