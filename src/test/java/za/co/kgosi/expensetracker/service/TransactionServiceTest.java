package za.co.kgosi.expensetracker.service;

import org.junit.jupiter.api.Test;
import za.co.kgosi.expensetracker.model.Category;
import za.co.kgosi.expensetracker.model.Transaction;
import za.co.kgosi.expensetracker.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.time.YearMonth;



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
}