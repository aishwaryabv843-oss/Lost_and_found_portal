package com.lostandfound.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Database Connection Manager
 * Standard JDBC connectivity framework for 4th Sem DBMS & Advanced Java
 * Project.
 */
public class DBConnection {
    // Standard local MySQL database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/lost_and_found_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = System.getenv("DB_PASSWORD");// Default root password (change according to
                                                                        // your MySQL
    // Setup)

    // Load JDBC Driver class during static initialization
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Connection Error: MySQL Connector/J Driver not found in library classpath!");
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new connection to the MySQL Server
     * 
     * @return Connection object
     * @throws SQLException in case connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
