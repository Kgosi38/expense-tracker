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
}