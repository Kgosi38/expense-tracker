package za.co.kgosi.expensetracker.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseInitializerTest {

    @Test
    void createsTransactionsTableSuccessfully() throws SQLException {

        String testDatabaseUrl = "jdbc:sqlite::memory:";

        try (Connection connection =
                     DatabaseConnection.getConnection(testDatabaseUrl)) {

            // Call our actual database initializer
            DatabaseInitializer.initializeDatabase(connection);

            // Check whether the transactions table exists
            try (ResultSet result = connection.getMetaData()
                    .getTables(null, null, "transactions", null)) {

                assertTrue(result.next());
            }
        }
    }
}