package za.co.kgosi.expensetracker.repository;

import org.junit.jupiter.api.Test;
import za.co.kgosi.expensetracker.database.DatabaseConnection;
import za.co.kgosi.expensetracker.database.DatabaseInitializer;
import za.co.kgosi.expensetracker.model.Category;
import za.co.kgosi.expensetracker.model.Transaction;
import za.co.kgosi.expensetracker.model.TransactionType;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryTest {

    @Test
    void savesTransactionSuccessfully() throws Exception {

        // Create a separate temporary database file
        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            // Create the transactions table
            DatabaseInitializer.initializeDatabase(databaseUrl);

            // Create our repository
            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Create a groceries transaction
            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("800.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 5)
            );

            // Save the transaction
            repository.saveTransaction(groceries);

            // Check that SQLite assigned an ID
            assertNotNull(groceries.getId());
            assertTrue(groceries.getId() > 0);

            // Check the saved record
            try (Connection connection =
                         DatabaseConnection.getConnection(databaseUrl);
                 Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(
                         "SELECT * FROM transactions"
                 )) {

                assertTrue(result.next());

                assertEquals("EXPENSE", result.getString("type"));
                assertEquals("800.00", result.getString("amount"));
                assertEquals("FOOD", result.getString("category"));
                assertEquals("Groceries", result.getString("description"));
                assertEquals("2026-11-05", result.getString("date"));

                // Make sure only one record was saved
                assertFalse(result.next());
            }

        } finally {
            // Remove the temporary test database
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void rejectsSavingNullTransaction() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> repository.saveTransaction(null)
            );

            assertEquals(
                    "Transaction cannot be null.",
                    error.getMessage()
            );

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void rejectsSavingTransactionTwice() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("800.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 5)
            );

            // Save the transaction for the first time
            repository.saveTransaction(groceries);

            // Attempt to save the same transaction again
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> repository.saveTransaction(groceries)
            );

            assertEquals(
                    "Transaction has already been assigned an ID.",
                    error.getMessage()
            );

            // Verify that SQLite contains only one transaction
            try (Connection connection =
                         DatabaseConnection.getConnection(databaseUrl);
                 Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(
                         "SELECT COUNT(*) FROM transactions"
                 )) {

                assertTrue(result.next());
                assertEquals(1, result.getInt(1));
            }

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void retrievesAllSavedTransactions() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            // Create the database table
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Create two transactions
            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("800.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 5)
            );

            Transaction salary = new Transaction(
                    TransactionType.INCOME,
                    new BigDecimal("5000.00"),
                    Category.SALARY,
                    "Monthly salary",
                    LocalDate.of(2026, 11, 6)
            );

            // Save both transactions
            repository.saveTransaction(groceries);
            repository.saveTransaction(salary);

            // Retrieve transactions from SQLite
            java.util.List<Transaction> transactions =
                    repository.getAllTransactions();

            // Verify the number of transactions
            assertEquals(2, transactions.size());

            // Verify the first transaction
            Transaction first = transactions.get(0);

            assertEquals(groceries.getId(), first.getId());
            assertEquals(TransactionType.EXPENSE, first.getType());
            assertEquals(0,
                    new BigDecimal("800.00").compareTo(first.getAmount()));
            assertEquals(Category.FOOD, first.getCategory());
            assertEquals("Groceries", first.getDescription());
            assertEquals(LocalDate.of(2026, 11, 5), first.getDate());

            // Verify the second transaction
            Transaction second = transactions.get(1);

            assertEquals(salary.getId(), second.getId());
            assertEquals(TransactionType.INCOME, second.getType());
            assertEquals(0,
                    new BigDecimal("5000.00").compareTo(second.getAmount()));
            assertEquals(Category.SALARY, second.getCategory());
            assertEquals("Monthly salary", second.getDescription());
            assertEquals(LocalDate.of(2026, 11, 6), second.getDate());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void returnsEmptyListWhenDatabaseHasNoTransactions() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            // Create the transactions table
            DatabaseInitializer.initializeDatabase(databaseUrl);

            // Create our repository
            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Retrieve transactions from an empty database
            java.util.List<Transaction> transactions =
                    repository.getAllTransactions();

            // Verify that the list is empty
            assertNotNull(transactions);
            assertTrue(transactions.isEmpty());
            assertEquals(0, transactions.size());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void retrievesTransactionById() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            // Initialize the database
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Create two transactions
            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("800.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 5)
            );

            Transaction salary = new Transaction(
                    TransactionType.INCOME,
                    new BigDecimal("5000.00"),
                    Category.SALARY,
                    "Monthly salary",
                    LocalDate.of(2026, 11, 6)
            );

            // Save both transactions
            repository.saveTransaction(groceries);
            repository.saveTransaction(salary);

            // Retrieve only the salary transaction
            Transaction result =
                    repository.getTransactionById(salary.getId());

            // Verify the correct transaction was returned
            assertNotNull(result);
            assertEquals(salary.getId(), result.getId());
            assertEquals(TransactionType.INCOME, result.getType());
            assertEquals(
                    0,
                    new BigDecimal("5000.00")
                            .compareTo(result.getAmount())
            );
            assertEquals(Category.SALARY, result.getCategory());
            assertEquals("Monthly salary", result.getDescription());
            assertEquals(LocalDate.of(2026, 11, 6), result.getDate());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void returnsNullWhenTransactionIdDoesNotExist() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            // Initialize the database
            DatabaseInitializer.initializeDatabase(databaseUrl);

            // Create repository
            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Search for a transaction that doesn't exist
            Transaction result =
                    repository.getTransactionById(999L);

            // Verify that no transaction was found
            assertNull(result);

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void updatesExistingTransactionSuccessfully() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Create a transaction
            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("800.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 5)
            );

            // Save the original transaction
            repository.saveTransaction(groceries);

            // Create an updated transaction with the same ID
            Transaction updatedGroceries = new Transaction(
                    groceries.getId(),
                    TransactionType.EXPENSE,
                    new BigDecimal("850.00"),
                    Category.FOOD,
                    "Weekly groceries",
                    LocalDate.of(2026, 11, 6)
            );

            // Update the database
            boolean updated =
                    repository.updateTransaction(updatedGroceries);

            assertTrue(updated);

            // Retrieve the transaction again from SQLite
            Transaction result =
                    repository.getTransactionById(groceries.getId());

            // Verify that the updated values were saved
            assertNotNull(result);
            assertEquals(groceries.getId(), result.getId());
            assertEquals(
                    0,
                    new BigDecimal("850.00")
                            .compareTo(result.getAmount())
            );
            assertEquals("Weekly groceries", result.getDescription());
            assertEquals(LocalDate.of(2026, 11, 6), result.getDate());
            assertEquals(Category.FOOD, result.getCategory());

            // Verify that we still have only one transaction
            assertEquals(1, repository.getAllTransactions().size());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void returnsFalseWhenUpdatingNonExistentTransaction() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            // Create the database table
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Create a transaction with an ID that does not exist
            Transaction transaction = new Transaction(
                    999L,
                    TransactionType.EXPENSE,
                    new BigDecimal("200.00"),
                    Category.FOOD,
                    "Lunch",
                    LocalDate.of(2026, 11, 8)
            );

            // Attempt to update it
            boolean updated =
                    repository.updateTransaction(transaction);

            // Verify that the update was unsuccessful
            assertFalse(updated);

            // Verify that no transaction was inserted
            assertTrue(repository.getAllTransactions().isEmpty());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void rejectsUpdatingTransactionWithoutId() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Create a transaction without saving it
            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("800.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 8)
            );

            // Verify that the transaction has no ID
            assertNull(groceries.getId());

            // Attempt to update the unsaved transaction
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> repository.updateTransaction(groceries)
            );

            // Verify the error message
            assertEquals(
                    "Transaction must have an ID to be updated.",
                    error.getMessage()
            );

            // Verify that the database remains empty
            assertTrue(repository.getAllTransactions().isEmpty());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void deletesExistingTransactionSuccessfully() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Create two transactions
            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("800.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 8)
            );

            Transaction salary = new Transaction(
                    TransactionType.INCOME,
                    new BigDecimal("5000.00"),
                    Category.SALARY,
                    "Monthly salary",
                    LocalDate.of(2026, 11, 8)
            );

            // Save both transactions
            repository.saveTransaction(groceries);
            repository.saveTransaction(salary);

            // Confirm both were saved
            assertEquals(2, repository.getAllTransactions().size());

            // Delete groceries
            boolean deleted =
                    repository.deleteTransaction(groceries.getId());

            // Confirm deletion succeeded
            assertTrue(deleted);

            // Confirm groceries no longer exist
            assertNull(
                    repository.getTransactionById(groceries.getId())
            );

            // Confirm salary still exists
            Transaction remaining =
                    repository.getTransactionById(salary.getId());

            assertNotNull(remaining);
            assertEquals("Monthly salary", remaining.getDescription());

            // Only one transaction should remain
            assertEquals(1, repository.getAllTransactions().size());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void returnsFalseWhenDeletingNonExistentTransaction() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            // Initialize the database
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Attempt to delete a transaction that doesn't exist
            boolean deleted = repository.deleteTransaction(999L);

            // Verify that deletion was unsuccessful
            assertFalse(deleted);

            // Verify that the database remains empty
            assertTrue(repository.getAllTransactions().isEmpty());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void rejectsDeletingInvalidTransactionIds() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Test ID zero
            IllegalArgumentException zeroError = assertThrows(
                    IllegalArgumentException.class,
                    () -> repository.deleteTransaction(0L)
            );

            assertEquals(
                    "Transaction ID must be positive.",
                    zeroError.getMessage()
            );

            // Test a negative ID
            IllegalArgumentException negativeError = assertThrows(
                    IllegalArgumentException.class,
                    () -> repository.deleteTransaction(-5L)
            );

            assertEquals(
                    "Transaction ID must be positive.",
                    negativeError.getMessage()
            );

            // Verify that the database remains empty
            assertTrue(repository.getAllTransactions().isEmpty());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }
}