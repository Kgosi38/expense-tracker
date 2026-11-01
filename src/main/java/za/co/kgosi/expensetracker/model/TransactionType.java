package za.co.kgosi.expensetracker.model;

public enum TransactionType {
    INCOME("Income"),
    EXPENSE("Expense");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
