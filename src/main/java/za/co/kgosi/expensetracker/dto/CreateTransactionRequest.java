
package za.co.kgosi.expensetracker.dto;

import za.co.kgosi.expensetracker.model.Category;
import za.co.kgosi.expensetracker.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
        TransactionType type,
        BigDecimal amount,
        Category category,
        String description,
        LocalDate date
) {
}
