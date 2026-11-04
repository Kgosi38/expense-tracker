package za.co.kgosi.expensetracker.service;

import org.junit.jupiter.api.Test;
import za.co.kgosi.expensetracker.model.Category;
import za.co.kgosi.expensetracker.model.Transaction;
import za.co.kgosi.expensetracker.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


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
}