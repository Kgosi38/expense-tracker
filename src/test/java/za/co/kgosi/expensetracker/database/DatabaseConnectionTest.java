package za.co.kgosi.expensetracker.database;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void connectsToDatabaseSuccessfully() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {

            assertNotNull(connection);

            assertFalse(connection.isClosed());
        }
    }
}