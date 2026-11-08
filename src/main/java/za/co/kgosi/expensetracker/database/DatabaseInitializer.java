package za.co.kgosi.expensetracker.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() throws SQLException {
        initializeDatabase("jdbc:sqlite:expensetracker.db");
    }

    public static void initializeDatabase(String databaseUrl)
            throws SQLException {

        try (Connection connection =
                     DatabaseConnection.getConnection(databaseUrl)) {

            initializeDatabase(connection);
        }
    }

    public static void initializeDatabase(Connection connection)
            throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type TEXT NOT NULL,
                    amount TEXT NOT NULL,
                    category TEXT NOT NULL,
                    description TEXT NOT NULL,
                    date TEXT NOT NULL
                )
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}