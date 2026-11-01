package za.co.kgosi.expensetracker.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Transaction {

    private static final int MAX_DESCRIPTION_LENGTH = 100;
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999.99");

    private Long id;
    private final TransactionType type;
    private BigDecimal amount;
    private Category category;
    private String description;
    private LocalDate date;

    // Use this constructor for a new transaction that is not in the database yet.
    public Transaction(TransactionType type, BigDecimal amount, Category category,
                       String description, LocalDate date) {
        this(null, type, amount, category, description, date);
    }

    // Use this constructor for a transaction that was loaded from the database.
    public Transaction(Long id, TransactionType type, BigDecimal amount, Category category,
                       String description, LocalDate date) {
        if (type == null) {
            throw new IllegalArgumentException("Transaction type is required.");
        }
        this.id = id;
        this.type = type;
        setAmount(amount);
        setCategory(category);
        setDescription(description);
        setDate(date);
    }

    public Long getId() {
        return id;
    }

    // The database creates the id, so it may only be set once.
    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("The id has already been set.");
        }
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required.");
        }
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Amount cannot have more than 2 decimal places.");
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException("Amount is too large.");
        }
        this.amount = amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (category.getType() != type) {
            throw new IllegalArgumentException("The category '" + category.getLabel()
                    + "' is not an " + type.getLabel().toLowerCase() + " category.");
        }
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is required.");
        }
        String trimmed = description.trim();
        if (trimmed.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Description cannot be longer than " + MAX_DESCRIPTION_LENGTH + " characters.");
        }
        this.description = trimmed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date is required.");
        }
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", type=" + type + ", amount=R" + amount
                + ", category=" + category + ", description='" + description
                + "', date=" + date + "}";
    }
}