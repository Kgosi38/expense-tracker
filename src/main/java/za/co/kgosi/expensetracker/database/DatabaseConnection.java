package za.co.kgosi.expensetracker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DATABASE_URL =
            "jdbc:sqlite:expensetracker.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static Connection getConnection(String databaseUrl)
            throws SQLException {

        return DriverManager.getConnection(databaseUrl);
    }
}