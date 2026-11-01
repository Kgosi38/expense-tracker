package za.co.kgosi.expensetracker.model;

public enum Category {
    // Income categories
    SALARY("Salary", TransactionType.INCOME),
    ALLOWANCE("Allowance", TransactionType.INCOME),
    BURSARY("Bursary", TransactionType.INCOME),
    OTHER_INCOME("Other income", TransactionType.INCOME),

    // Expense categories
    FOOD("Food", TransactionType.EXPENSE),
    TRANSPORT("Transport", TransactionType.EXPENSE),
    DATA_AIRTIME("Data and airtime", TransactionType.EXPENSE),
    RENT("Rent", TransactionType.EXPENSE),
    UTILITIES("Utilities", TransactionType.EXPENSE),
    STUDY("Study", TransactionType.EXPENSE),
    HEALTH("Health", TransactionType.EXPENSE),
    ENTERTAINMENT("Entertainment", TransactionType.EXPENSE),
    OTHER_EXPENSE("Other expense", TransactionType.EXPENSE);

    private final String label;
    private final TransactionType type;

    Category(String label, TransactionType type) {
        this.label = label;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public TransactionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return label;
    }
}