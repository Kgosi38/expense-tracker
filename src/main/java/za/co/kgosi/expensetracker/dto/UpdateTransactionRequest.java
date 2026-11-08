
package za.co.kgosi.expensetracker.dto;

import za.co.kgosi.expensetracker.model.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionRequest(
        BigDecimal amount,
        Category category,
        String description,
        LocalDate date
) {
}
