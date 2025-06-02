package com.tudor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/javapwmanagerDB";
    private static final String USER = "javapwmanager";
    private static final String PASSWORD = "^xcEXi7g";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("Successfully connected to PostgreSQL database!");
        } catch (SQLException e) {
            System.err.println("Connection error:");
            e.printStackTrace();
        }
    }

    
}
