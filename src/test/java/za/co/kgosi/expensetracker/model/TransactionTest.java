package za.co.kgosi.expensetracker.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {

    private static final LocalDate DATE = LocalDate.of(2026, 11, 1);

    @Test
    void createsValidExpenseWithTwoDecimalAmount() {
        Transaction transaction = new Transaction(
                TransactionType.EXPENSE, new BigDecimal("48"), Category.FOOD, "  Lunch  ", DATE);

        assertNull(transaction.getId());
        assertEquals(new BigDecimal("48.00"), transaction.getAmount());
        assertEquals("Lunch", transaction.getDescription());
        assertEquals(Category.FOOD, transaction.getCategory());
    }

    @Test
    void rejectsNegativeAmount() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
                new Transaction(TransactionType.EXPENSE, new BigDecimal("-5"), Category.FOOD, "Negative", DATE));

        assertEquals("Amount must be greater than zero.", error.getMessage());
    }

    @Test
    void rejectsZeroAmount() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("0"),
                        Category.FOOD,
                        "Free lunch",
                        DATE
                )
        );

        assertEquals(
                "Amount must be greater than zero.",
                error.getMessage()
        );
    }

    @Test
    void rejectsAmountWithTooManyDecimalPlaces() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("25.555"),
                        Category.FOOD,
                        "Lunch",
                        DATE
                )
        );

        assertEquals(
                "Amount cannot have more than 2 decimal places.",
                error.getMessage()
        );
    }

    @Test
    void rejectsIncomeCategoryForExpense() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("500"),
                        Category.SALARY,
                        "Monthly salary",
                        DATE
                )
        );

        assertEquals(
                "The category 'Salary' is not an expense category.",
                error.getMessage()
        );
    }

    @Test
    void rejectsDescriptionLongerThan100Characters() {
        String longDescription = "A".repeat(101);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("100"),
                        Category.FOOD,
                        longDescription,
                        DATE
                )
        );
    }

    @Test
    void createsValidIncome() {
        Transaction transaction = new Transaction(
                TransactionType.INCOME,
                new BigDecimal("5000"),
                Category.SALARY,
                "Monthly salary",
                DATE
        );

        assertEquals(TransactionType.INCOME, transaction.getType());
        assertEquals(new BigDecimal("5000.00"), transaction.getAmount());
        assertEquals(Category.SALARY, transaction.getCategory());
        assertEquals("Monthly salary", transaction.getDescription());
    }

    @Test
    void rejectsInvalidTransactionIds() {
        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("150"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 5)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> transaction.setId(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> transaction.setId(0L)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> transaction.setId(-1L)
        );

        assertEquals(null, transaction.getId());
    }

    @Test
    void assignsTransactionIdOnlyOnce() {
        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("150"),
                Category.FOOD,
                "Groceries",
                LocalDate.of(2026, 11, 5)
        );

        transaction.setId(1L);

        assertEquals(1L, transaction.getId());

        assertThrows(
                IllegalStateException.class,
                () -> transaction.setId(2L)
        );

        assertEquals(1L, transaction.getId());
    }

    @Test
    void rejectsInvalidIdInConstructor() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        -5L,
                        TransactionType.EXPENSE,
                        new BigDecimal("150"),
                        Category.FOOD,
                        "Groceries",
                        LocalDate.of(2026, 11, 5)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        0L,
                        TransactionType.EXPENSE,
                        new BigDecimal("150"),
                        Category.FOOD,
                        "Groceries",
                        LocalDate.of(2026, 11, 5)
                )
        );
    }
}