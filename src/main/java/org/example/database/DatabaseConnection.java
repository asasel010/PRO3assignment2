package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{
    private static DatabaseConnection instance;
    DatabaseConnection() throws SQLException
    {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres?currentSchema=slaughter_house",
            "postgres", "DataBase2025");
    }


    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
