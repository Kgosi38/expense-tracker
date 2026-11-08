package za.co.kgosi.expensetracker.service;

import org.junit.jupiter.api.Test;
import za.co.kgosi.expensetracker.model.Category;
import za.co.kgosi.expensetracker.model.Transaction;
import za.co.kgosi.expensetracker.model.TransactionType;
import za.co.kgosi.expensetracker.repository.TransactionRepository;
import za.co.kgosi.expensetracker.database.DatabaseInitializer;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.time.YearMonth;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;





class TransactionServiceTest {

    @Test
    void addsAndRetrievesTransaction() {
        TransactionService service = new TransactionService();

        Transaction expense = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("150"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 4)
        );

        service.addTransaction(expense);

        assertEquals(1, service.getAllTransactions().size());
        assertEquals(expense, service.getAllTransactions().get(0));
    }

    @Test
    void rejectsNullTransaction() {
        TransactionService service = new TransactionService();

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.addTransaction(null)
        );

        assertEquals(
                "Transaction cannot be null.",
                error.getMessage()
        );

        assertEquals(0, service.getAllTransactions().size());
    }

    @Test
    void clearingReturnedListDoesNotDeleteTransactions() {
        TransactionService service = new TransactionService();

        Transaction expense = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("200"),
                Category.FOOD,
                "Dinner",
                LocalDate.of(2026, 11, 4)
        );

        service.addTransaction(expense);

        List<Transaction> returnedList = service.getAllTransactions();

        returnedList.clear();

        assertEquals(1, service.getAllTransactions().size());
    }

    @Test
    void calculatesTotalIncome() {
        TransactionService service = new TransactionService();

        Transaction salary = new Transaction(
                TransactionType.INCOME,
                new BigDecimal("5000"),
                Category.SALARY,
                "Monthly salary",
                LocalDate.of(2026, 11, 4)
        );

        Transaction bonus = new Transaction(
                TransactionType.INCOME,
                new BigDecimal("1500"),
                Category.SALARY,
                "Bonus",
                LocalDate.of(2026, 11, 4)
        );

        service.addTransaction(salary);
        service.addTransaction(bonus);

        assertEquals(
                0,
                new BigDecimal("6500.00").compareTo(service.getTotalIncome())
        );
    }

    @Test
    void excludesExpensesFromTotalIncome() {
        TransactionService service = new TransactionService();

        Transaction salary = new Transaction(
                TransactionType.INCOME,
                new BigDecimal("5000"),
                Category.SALARY,
                "Monthly salary",
                LocalDate.of(2026, 11, 4)
        );

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 4)
        );

        service.addTransaction(salary);
        service.addTransaction(groceries);

        assertEquals(
                0,
                new BigDecimal("5000.00").compareTo(service.getTotalIncome())
        );
    }

    @Test
    void calculatesTotalExpenses() {
        TransactionService service = new TransactionService();

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 4)
        );

        Transaction dinner = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("200"),
                Category.FOOD,
                "Dinner",
                LocalDate.of(2026, 11, 4)
        );

        service.addTransaction(groceries);
        service.addTransaction(dinner);

        assertEquals(
                0,
                new BigDecimal("1000.00").compareTo(service.getTotalExpenses())
        );
    }

    @Test
    void excludesIncomeFromTotalExpenses() {
        TransactionService service = new TransactionService();

        Transaction salary = new Transaction(
                TransactionType.INCOME,
                new BigDecimal("5000"),
                Category.SALARY,
                "Monthly salary",
                LocalDate.of(2026, 11, 4)
        );

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 4)
        );

        service.addTransaction(salary);
        service.addTransaction(groceries);

        assertEquals(
                0,
                new BigDecimal("800.00").compareTo(service.getTotalExpenses())
        );
    }
    @Test
    void calculatesBalanceCorrectly() {
        TransactionService service = new TransactionService();

        Transaction salary = new Transaction(
                TransactionType.INCOME,
                new BigDecimal("5000"),
                Category.SALARY,
                "Monthly salary",
                LocalDate.of(2026, 11, 4)
        );

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 4)
        );

        service.addTransaction(salary);
        service.addTransaction(groceries);

        assertEquals(
                0,
                new BigDecimal("4200.00").compareTo(service.getBalance())
        );
    }

    @Test
    void calculatesNegativeBalance() {
        TransactionService service = new TransactionService();

        Transaction salary = new Transaction(
                TransactionType.INCOME,
                new BigDecimal("3000"),
                Category.SALARY,
                "Salary",
                LocalDate.of(2026, 11, 4)
        );

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("3500"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 4)
        );

        service.addTransaction(salary);
        service.addTransaction(groceries);

        assertEquals(
                0,
                new BigDecimal("-500.00").compareTo(service.getBalance())
        );
    }

    @Test
    void filtersTransactionsByCategory() {
        TransactionService service = new TransactionService();

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 4)
        );

        Transaction dinner = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("200"),
                Category.FOOD,
                "Dinner",
                LocalDate.of(2026, 11, 4)
        );

        Transaction salary = new Transaction(
                TransactionType.INCOME,
                new BigDecimal("5000"),
                Category.SALARY,
                "Salary",
                LocalDate.of(2026, 11, 4)
        );

        service.addTransaction(groceries);
        service.addTransaction(dinner);
        service.addTransaction(salary);

        List<Transaction> foodTransactions =
                service.getTransactionsByCategory(Category.FOOD);

        assertEquals(2, foodTransactions.size());
        assertEquals(groceries, foodTransactions.get(0));
        assertEquals(dinner, foodTransactions.get(1));
    }

    @Test
    void rejectsNullCategoryFilter() {
        TransactionService service = new TransactionService();

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.getTransactionsByCategory(null)
        );

        assertEquals(
                "Category cannot be null.",
                error.getMessage()
        );
    }

    @Test
    void filtersTransactionsByDate() {
        TransactionService service = new TransactionService();

        LocalDate targetDate = LocalDate.of(2026, 11, 4);
        LocalDate otherDate = LocalDate.of(2026, 11, 3);

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                targetDate
        );

        Transaction dinner = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("200"),
                Category.FOOD,
                "Dinner",
                otherDate
        );

        Transaction transport = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("150"),
                Category.TRANSPORT,
                "Taxi fare",
                targetDate
        );

        service.addTransaction(groceries);
        service.addTransaction(dinner);
        service.addTransaction(transport);

        List<Transaction> results =
                service.getTransactionsByDate(targetDate);

        assertEquals(2, results.size());
        assertEquals(groceries, results.get(0));
        assertEquals(transport, results.get(1));
    }

    @Test
    void rejectsNullDateFilter() {
        TransactionService service = new TransactionService();

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.getTransactionsByDate(null)
        );

        assertEquals(
                "Date cannot be null.",
                error.getMessage()
        );
    }

    @Test
    void filtersTransactionsByMonth() {
        TransactionService service = new TransactionService();

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 4)
        );

        Transaction dinner = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("200"),
                Category.FOOD,
                "Dinner",
                LocalDate.of(2026, 11, 15)
        );

        Transaction transport = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("150"),
                Category.TRANSPORT,
                "Taxi fare",
                LocalDate.of(2026, 10, 30)
        );

        service.addTransaction(groceries);
        service.addTransaction(dinner);
        service.addTransaction(transport);

        List<Transaction> results =
                service.getTransactionsByMonth(YearMonth.of(2026, 11));

        assertEquals(2, results.size());
        assertEquals(groceries, results.get(0));
        assertEquals(dinner, results.get(1));
    }

    @Test
    void rejectsNullMonthFilter() {
        TransactionService service = new TransactionService();

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.getTransactionsByMonth(null)
        );

        assertEquals(
                "Month cannot be null.",
                error.getMessage()
        );
    }

    @Test
    void calculatesMonthlyIncome() {
        TransactionService service = new TransactionService();

        service.addTransaction(new Transaction(
                TransactionType.INCOME,
                new BigDecimal("5000"),
                Category.SALARY,
                "November salary",
                LocalDate.of(2026, 11, 4)
        ));

        service.addTransaction(new Transaction(
                TransactionType.INCOME,
                new BigDecimal("1500"),
                Category.SALARY,
                "November bonus",
                LocalDate.of(2026, 11, 15)
        ));

        service.addTransaction(new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 10)
        ));

        service.addTransaction(new Transaction(
                TransactionType.INCOME,
                new BigDecimal("3000"),
                Category.SALARY,
                "October salary",
                LocalDate.of(2026, 10, 20)
        ));

        BigDecimal result =
                service.getMonthlyIncome(YearMonth.of(2026, 11));

        assertEquals(
                0,
                new BigDecimal("6500").compareTo(result)
        );
    }

    @Test
    void calculatesMonthlyExpenses() {
        TransactionService service = new TransactionService();

        service.addTransaction(new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 4)
        ));

        service.addTransaction(new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("1200"),
                Category.CLOTHING,
                "Clothing",
                LocalDate.of(2026, 11, 12)
        ));

        service.addTransaction(new Transaction(
                TransactionType.INCOME,
                new BigDecimal("5000"),
                Category.SALARY,
                "Salary",
                LocalDate.of(2026, 11, 15)
        ));

        service.addTransaction(new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("300"),
                Category.FOOD,
                "October groceries",
                LocalDate.of(2026, 10, 20)
        ));

        BigDecimal result =
                service.getMonthlyExpenses(YearMonth.of(2026, 11));

        assertEquals(
                0,
                new BigDecimal("2000").compareTo(result)
        );
    }

    @Test
    void calculatesMonthlyBalance() {
        TransactionService service = new TransactionService();

        service.addTransaction(new Transaction(
                TransactionType.INCOME,
                new BigDecimal("5000"),
                Category.SALARY,
                "November salary",
                LocalDate.of(2026, 11, 4)
        ));

        service.addTransaction(new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 10)
        ));

        service.addTransaction(new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("1200"),
                Category.CLOTHING,
                "Clothing",
                LocalDate.of(2026, 11, 12)
        ));

        service.addTransaction(new Transaction(
                TransactionType.INCOME,
                new BigDecimal("3000"),
                Category.SALARY,
                "October salary",
                LocalDate.of(2026, 10, 20)
        ));

        BigDecimal result =
                service.getMonthlyBalance(YearMonth.of(2026, 11));

        assertEquals(
                0,
                new BigDecimal("3000").compareTo(result)
        );
    }

    @Test
    void calculatesNegativeMonthlyBalance() {
        TransactionService service = new TransactionService();

        service.addTransaction(new Transaction(
                TransactionType.INCOME,
                new BigDecimal("3000"),
                Category.SALARY,
                "November salary",
                LocalDate.of(2026, 11, 4)
        ));

        service.addTransaction(new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("2000"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 10)
        ));

        service.addTransaction(new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("1500"),
                Category.CLOTHING,
                "Clothing",
                LocalDate.of(2026, 11, 12)
        ));

        BigDecimal result =
                service.getMonthlyBalance(YearMonth.of(2026, 11));

        assertEquals(
                0,
                new BigDecimal("-500").compareTo(result)
        );
    }

    @Test
    void returnsZeroForMonthWithNoTransactions() {
        TransactionService service = new TransactionService();

        service.addTransaction(new Transaction(
                TransactionType.INCOME,
                new BigDecimal("5000"),
                Category.SALARY,
                "November salary",
                LocalDate.of(2026, 11, 4)
        ));

        YearMonth emptyMonth = YearMonth.of(2026, 12);

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        service.getMonthlyIncome(emptyMonth)
                )
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        service.getMonthlyExpenses(emptyMonth)
                )
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        service.getMonthlyBalance(emptyMonth)
                )
        );
    }

    @Test
    void rejectsNullMonthForFinancialCalculations() {
        TransactionService service = new TransactionService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getMonthlyIncome(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getMonthlyExpenses(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getMonthlyBalance(null)
        );
    }

    @Test
    void updatesExistingTransaction() {
        TransactionService service = new TransactionService();

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 5)
        );

        service.addTransaction(groceries);

        service.updateTransaction(
                groceries,
                new BigDecimal("80"),
                Category.FOOD,
                "Weekly groceries",
                LocalDate.of(2026, 11, 6)
        );

        assertEquals(
                0,
                new BigDecimal("80").compareTo(groceries.getAmount())
        );

        assertEquals(Category.FOOD, groceries.getCategory());
        assertEquals("Weekly groceries", groceries.getDescription());
        assertEquals(LocalDate.of(2026, 11, 6), groceries.getDate());

        assertEquals(1, service.getAllTransactions().size());
    }

    @Test
    void rejectsUpdatingTransactionNotInService() {
        TransactionService service = new TransactionService();

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 5)
        );

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateTransaction(
                        groceries,
                        new BigDecimal("80"),
                        Category.FOOD,
                        "Weekly groceries",
                        LocalDate.of(2026, 11, 6)
                )
        );

        assertEquals(
                "Transaction does not exist.",
                error.getMessage()
        );

        assertEquals(
                0,
                new BigDecimal("800").compareTo(groceries.getAmount())
        );
    }

    @Test
    void invalidUpdateDoesNotChangeOriginalTransaction() {
        TransactionService service = new TransactionService();

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 5)
        );

        service.addTransaction(groceries);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateTransaction(
                        groceries,
                        new BigDecimal("80"),
                        Category.SALARY,
                        "Weekly groceries",
                        LocalDate.of(2026, 11, 6)
                )
        );

        assertEquals(
                0,
                new BigDecimal("800").compareTo(groceries.getAmount())
        );

        assertEquals(Category.FOOD, groceries.getCategory());
        assertEquals("Groceries", groceries.getDescription());
        assertEquals(LocalDate.of(2026, 11, 5), groceries.getDate());
    }

    @Test
    void rejectsUpdatingNullTransaction() {
        TransactionService service = new TransactionService();

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateTransaction(
                        null,
                        new BigDecimal("80"),
                        Category.FOOD,
                        "Weekly groceries",
                        LocalDate.of(2026, 11, 6)
                )
        );

        assertEquals(
                "Transaction cannot be null.",
                error.getMessage()
        );
    }

    @Test
    void deletesExistingTransaction() {
        TransactionService service = new TransactionService();

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 5)
        );

        Transaction dinner = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("200"),
                Category.FOOD,
                "Dinner",
                LocalDate.of(2026, 11, 5)
        );

        service.addTransaction(groceries);
        service.addTransaction(dinner);

        service.deleteTransaction(groceries);

        assertEquals(1, service.getAllTransactions().size());
        assertEquals(dinner, service.getAllTransactions().get(0));

        assertEquals(
                0,
                new BigDecimal("200").compareTo(
                        service.getTotalExpenses()
                )
        );
    }

    @Test
    void rejectsDeletingTransactionNotInService() {
        TransactionService service = new TransactionService();

        Transaction groceries = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("800"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 5)
        );

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteTransaction(groceries)
        );

        assertEquals(
                "Transaction does not exist.",
                error.getMessage()
        );

        assertEquals(0, service.getAllTransactions().size());
    }

    @Test
    void rejectsDeletingNullTransaction() {
        TransactionService service = new TransactionService();

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteTransaction(null)
        );

        assertEquals(
                "Transaction cannot be null.",
                error.getMessage()
        );

        assertEquals(0, service.getAllTransactions().size());
    }

    @Test
    void acceptsRepositoryThroughConstructor() {

        TransactionRepository repository =
                new TransactionRepository("jdbc:sqlite::memory:");

        TransactionService service =
                new TransactionService(repository);

        assertNotNull(service);
    }

    @Test
    void rejectsNullRepositoryThroughConstructor() {

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new TransactionService(null)
        );

        assertEquals(
                "Repository cannot be null.",
                error.getMessage()
        );
    }

    @Test
    void savesTransactionToDatabaseThroughService() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-service-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            // Create the SQLite table
            DatabaseInitializer.initializeDatabase(databaseUrl);

            // Create the repository
            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // Connect the service to the repository
            TransactionService databaseService =
                    new TransactionService(repository);

            // Create an expense
            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("850.00"),
                    Category.FOOD,
                    "Weekly groceries",
                    LocalDate.of(2026, 11, 8)
            );

            // Save through the service, not directly through the repository
            databaseService.addTransaction(groceries);

            // Read directly from SQLite to verify persistence
            Transaction saved =
                    repository.getTransactionById(groceries.getId());

            assertNotNull(saved);
            assertEquals(groceries.getId(), saved.getId());
            assertEquals("Weekly groceries", saved.getDescription());
            assertEquals(
                    0,
                    new BigDecimal("850.00")
                            .compareTo(saved.getAmount())
            );
            assertEquals(Category.FOOD, saved.getCategory());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void retrievesSavedTransactionsThroughNewServiceInstance() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-service-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            // Create the database table
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            // First service instance
            TransactionService firstService =
                    new TransactionService(repository);

            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("850.00"),
                    Category.FOOD,
                    "Weekly groceries",
                    LocalDate.of(2026, 11, 8)
            );

            // Save through the first service
            firstService.addTransaction(groceries);

            // Create a completely new service instance
            TransactionService secondService =
                    new TransactionService(
                            new TransactionRepository(databaseUrl)
                    );

            // Retrieve the previously saved transaction
            List<Transaction> transactions =
                    secondService.getAllTransactions();

            assertEquals(1, transactions.size());

            Transaction retrieved = transactions.get(0);

            assertEquals(groceries.getId(), retrieved.getId());
            assertEquals("Weekly groceries", retrieved.getDescription());
            assertEquals(Category.FOOD, retrieved.getCategory());
            assertEquals(
                    0,
                    new BigDecimal("850.00")
                            .compareTo(retrieved.getAmount())
            );

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void calculatesBalanceUsingSQLiteTransactions() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-balance-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            TransactionService databaseService =
                    new TransactionService(repository);

            Transaction salary = new Transaction(
                    TransactionType.INCOME,
                    new BigDecimal("5000.00"),
                    Category.SALARY,
                    "Monthly salary",
                    LocalDate.of(2026, 11, 8)
            );

            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("850.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 8)
            );

            databaseService.addTransaction(salary);
            databaseService.addTransaction(groceries);

            assertEquals(
                    0,
                    new BigDecimal("5000.00")
                            .compareTo(databaseService.getTotalIncome())
            );

            assertEquals(
                    0,
                    new BigDecimal("850.00")
                            .compareTo(databaseService.getTotalExpenses())
            );

            assertEquals(
                    0,
                    new BigDecimal("4150.00")
                            .compareTo(databaseService.getBalance())
            );

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void filtersSQLiteTransactionsByCategory() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-category-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            TransactionService databaseService =
                    new TransactionService(repository);

            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("850.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 8)
            );

            Transaction transport = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("200.00"),
                    Category.TRANSPORT,
                    "Taxi fare",
                    LocalDate.of(2026, 11, 8)
            );

            Transaction lunch = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("120.00"),
                    Category.FOOD,
                    "Lunch",
                    LocalDate.of(2026, 11, 8)
            );

            databaseService.addTransaction(groceries);
            databaseService.addTransaction(transport);
            databaseService.addTransaction(lunch);

            List<Transaction> foodTransactions =
                    databaseService.getTransactionsByCategory(Category.FOOD);

            assertEquals(2, foodTransactions.size());

            assertTrue(foodTransactions.stream()
                    .anyMatch(t -> t.getDescription().equals("Groceries")));

            assertTrue(foodTransactions.stream()
                    .anyMatch(t -> t.getDescription().equals("Lunch")));

            assertFalse(foodTransactions.stream()
                    .anyMatch(t -> t.getDescription().equals("Taxi fare")));

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void filtersSQLiteTransactionsByDate() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-date-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            TransactionService databaseService =
                    new TransactionService(repository);

            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("850.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 8)
            );

            Transaction transport = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("200.00"),
                    Category.TRANSPORT,
                    "Taxi fare",
                    LocalDate.of(2026, 11, 7)
            );

            Transaction lunch = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("120.00"),
                    Category.FOOD,
                    "Lunch",
                    LocalDate.of(2026, 11, 8)
            );

            databaseService.addTransaction(groceries);
            databaseService.addTransaction(transport);
            databaseService.addTransaction(lunch);

            List<Transaction> results =
                    databaseService.getTransactionsByDate(
                            LocalDate.of(2026, 11, 8)
                    );

            assertEquals(2, results.size());

            assertTrue(results.stream()
                    .anyMatch(t -> t.getDescription().equals("Groceries")));

            assertTrue(results.stream()
                    .anyMatch(t -> t.getDescription().equals("Lunch")));

            assertFalse(results.stream()
                    .anyMatch(t -> t.getDescription().equals("Taxi fare")));

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void filtersSQLiteTransactionsByMonth() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-month-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            TransactionService databaseService =
                    new TransactionService(repository);

            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("850.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 8)
            );

            Transaction transport = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("200.00"),
                    Category.TRANSPORT,
                    "Taxi fare",
                    LocalDate.of(2026, 11, 15)
            );

            Transaction octoberExpense = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("500.00"),
                    Category.FOOD,
                    "October groceries",
                    LocalDate.of(2026, 10, 30)
            );

            databaseService.addTransaction(groceries);
            databaseService.addTransaction(transport);
            databaseService.addTransaction(octoberExpense);

            List<Transaction> novemberTransactions =
                    databaseService.getTransactionsByMonth(
                            YearMonth.of(2026, 11)
                    );

            assertEquals(2, novemberTransactions.size());

            assertTrue(novemberTransactions.stream()
                    .anyMatch(t -> t.getDescription().equals("Groceries")));

            assertTrue(novemberTransactions.stream()
                    .anyMatch(t -> t.getDescription().equals("Taxi fare")));

            assertFalse(novemberTransactions.stream()
                    .anyMatch(t -> t.getDescription().equals("October groceries")));

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void calculatesMonthlyFinancialReportUsingSQLite() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-report-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionService databaseService =
                    new TransactionService(
                            new TransactionRepository(databaseUrl)
                    );

            databaseService.addTransaction(new Transaction(
                    TransactionType.INCOME,
                    new BigDecimal("8000.00"),
                    Category.SALARY,
                    "November salary",
                    LocalDate.of(2026, 11, 1)
            ));

            databaseService.addTransaction(new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("850.00"),
                    Category.FOOD,
                    "November groceries",
                    LocalDate.of(2026, 11, 8)
            ));

            databaseService.addTransaction(new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("200.00"),
                    Category.TRANSPORT,
                    "November transport",
                    LocalDate.of(2026, 11, 15)
            ));

            databaseService.addTransaction(new Transaction(
                    TransactionType.INCOME,
                    new BigDecimal("3000.00"),
                    Category.SALARY,
                    "October salary",
                    LocalDate.of(2026, 10, 1)
            ));

            YearMonth november = YearMonth.of(2026, 11);

            assertEquals(
                    0,
                    new BigDecimal("8000.00")
                            .compareTo(databaseService.getMonthlyIncome(november))
            );

            assertEquals(
                    0,
                    new BigDecimal("1050.00")
                            .compareTo(databaseService.getMonthlyExpenses(november))
            );

            assertEquals(
                    0,
                    new BigDecimal("6950.00")
                            .compareTo(databaseService.getMonthlyBalance(november))
            );

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void updatesSQLiteTransactionThroughService() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-update-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            TransactionService databaseService =
                    new TransactionService(repository);

            // Create and save the original transaction
            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("850.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 8)
            );

            databaseService.addTransaction(groceries);

            Long transactionId = groceries.getId();

            // Update the transaction through the service
            databaseService.updateTransaction(
                    groceries,
                    new BigDecimal("950.00"),
                    Category.FOOD,
                    "Updated groceries",
                    LocalDate.of(2026, 11, 9)
            );

            // Read directly from SQLite
            Transaction updated =
                    repository.getTransactionById(transactionId);

            assertNotNull(updated);

            assertEquals(
                    0,
                    new BigDecimal("950.00")
                            .compareTo(updated.getAmount())
            );

            assertEquals("Updated groceries", updated.getDescription());
            assertEquals(LocalDate.of(2026, 11, 9), updated.getDate());
            assertEquals(transactionId, updated.getId());

            // Confirm that the service also returns the updated record
            TransactionService newService =
                    new TransactionService(
                            new TransactionRepository(databaseUrl)
                    );

            List<Transaction> savedTransactions =
                    newService.getAllTransactions();

            assertEquals(1, savedTransactions.size());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void rejectsInvalidSQLiteUpdateWithoutChangingSavedTransaction()
            throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-invalid-update-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            TransactionService databaseService =
                    new TransactionService(repository);

            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("850.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 8)
            );

            databaseService.addTransaction(groceries);

            Long transactionId = groceries.getId();

            // Attempt an invalid update
            assertThrows(
                    IllegalArgumentException.class,
                    () -> databaseService.updateTransaction(
                            groceries,
                            new BigDecimal("-950.00"),
                            Category.FOOD,
                            "Invalid groceries",
                            LocalDate.of(2026, 11, 9)
                    )
            );

            // Retrieve the transaction from SQLite
            Transaction saved =
                    repository.getTransactionById(transactionId);

            assertNotNull(saved);

            // Original values must remain unchanged
            assertEquals(
                    0,
                    new BigDecimal("850.00")
                            .compareTo(saved.getAmount())
            );

            assertEquals("Groceries", saved.getDescription());

            assertEquals(
                    LocalDate.of(2026, 11, 8),
                    saved.getDate()
            );

            // The original Java object must also remain unchanged
            assertEquals(
                    0,
                    new BigDecimal("850.00")
                            .compareTo(groceries.getAmount())
            );

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void deletesSQLiteTransactionThroughService() throws Exception {

        Path databaseFile = Files.createTempFile(
                "expense-tracker-delete-test-", ".db"
        );

        String databaseUrl = "jdbc:sqlite:" + databaseFile;

        try {
            // Create the database table
            DatabaseInitializer.initializeDatabase(databaseUrl);

            TransactionRepository repository =
                    new TransactionRepository(databaseUrl);

            TransactionService databaseService =
                    new TransactionService(repository);

            // Create a transaction
            Transaction groceries = new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("850.00"),
                    Category.FOOD,
                    "Groceries",
                    LocalDate.of(2026, 11, 8)
            );

            // Save the transaction to SQLite
            databaseService.addTransaction(groceries);

            Long transactionId = groceries.getId();

            // Verify it exists before deletion
            assertNotNull(
                    repository.getTransactionById(transactionId)
            );

            // Delete through the service
            databaseService.deleteTransaction(groceries);

            // Verify the transaction is gone from SQLite
            assertNull(
                    repository.getTransactionById(transactionId)
            );

            // Verify a new service instance also sees no transactions
            TransactionService newService =
                    new TransactionService(
                            new TransactionRepository(databaseUrl)
                    );

            assertEquals(0, newService.getAllTransactions().size());

        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }
}