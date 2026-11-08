package za.co.kgosi.expensetracker.service;

import za.co.kgosi.expensetracker.model.Transaction;
import za.co.kgosi.expensetracker.model.TransactionType;
import za.co.kgosi.expensetracker.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import za.co.kgosi.expensetracker.model.Category;
import java.time.LocalDate;
import java.time.YearMonth;

import java.sql.SQLException;

public class TransactionService {

    private final List<Transaction> transactions = new ArrayList<>();
    private final TransactionRepository repository;

    public TransactionService() {
        this.repository = null;
    }

    public TransactionService(TransactionRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Repository cannot be null."
            );
        }

        this.repository = repository;
    }

    public void addTransaction(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction cannot be null."
            );
        }

        if (repository != null) {
            try {
                repository.saveTransaction(transaction);
            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Failed to save transaction to database.", e
                );
            }
        } else {
            transactions.add(transaction);
        }
    }

    public List<Transaction> getAllTransactions() {

        if (repository != null) {
            try {
                return repository.getAllTransactions();
            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Failed to retrieve transactions from database.", e
                );
            }
        }

        return new ArrayList<>(transactions);
    }

    public BigDecimal getTotalIncome() {
        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : getAllTransactions()) {
            if (transaction.getType() == TransactionType.INCOME) {
                total = total.add(transaction.getAmount());
            }
        }

        return total;
    }
    public BigDecimal getTotalExpenses() {
        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : getAllTransactions()) {
            if (transaction.getType() == TransactionType.EXPENSE) {
                total = total.add(transaction.getAmount());
            }
        }

        return total;
    }

    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpenses());
    }

    public List<Transaction> getTransactionsByCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException(
                    "Category cannot be null."
            );
        }

        List<Transaction> results = new ArrayList<>();

        for (Transaction transaction : getAllTransactions()) {
            if (transaction.getCategory() == category) {
                results.add(transaction);
            }
        }

        return results;
    }

    public List<Transaction> getTransactionsByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException(
                    "Date cannot be null."
            );
        }

        List<Transaction> results = new ArrayList<>();

        for (Transaction transaction : getAllTransactions()) {
            if (transaction.getDate().equals(date)) {
                results.add(transaction);
            }
        }

        return results;
    }

    public List<Transaction> getTransactionsByMonth(YearMonth month) {
        if (month == null) {
            throw new IllegalArgumentException(
                    "Month cannot be null."
            );
        }

        List<Transaction> results = new ArrayList<>();

        for (Transaction transaction : getAllTransactions()) {
            YearMonth transactionMonth =
                    YearMonth.from(transaction.getDate());

            if (transactionMonth.equals(month)) {
                results.add(transaction);
            }
        }

        return results;
    }

    public BigDecimal getMonthlyIncome(YearMonth month) {
        List<Transaction> monthlyTransactions =
                getTransactionsByMonth(month);

        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : monthlyTransactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                total = total.add(transaction.getAmount());
            }
        }

        return total;
    }

    public BigDecimal getMonthlyExpenses(YearMonth month) {

        List<Transaction> monthlyTransactions =
                getTransactionsByMonth(month);

        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : monthlyTransactions) {
            if (transaction.getType() == TransactionType.EXPENSE) {
                total = total.add(transaction.getAmount());
            }
        }

        return total;
    }

    public BigDecimal getMonthlyBalance(YearMonth month) {
        BigDecimal income = getMonthlyIncome(month);
        BigDecimal expenses = getMonthlyExpenses(month);

        return income.subtract(expenses);
    }

    public void updateTransaction(
            Transaction transaction,
            BigDecimal newAmount,
            Category newCategory,
            String newDescription,
            LocalDate newDate) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction cannot be null."
            );
        }

        if (repository != null) {

            if (transaction.getId() == null) {
                throw new IllegalArgumentException(
                        "Transaction does not exist."
                );
            }

            try {
                Transaction existing =
                        repository.getTransactionById(transaction.getId());

                if (existing == null) {
                    throw new IllegalArgumentException(
                            "Transaction does not exist."
                    );
                }

            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Failed to find transaction in database.", e
                );
            }

        } else {

            if (!transactions.contains(transaction)) {
                throw new IllegalArgumentException(
                        "Transaction does not exist."
                );
            }
        }

        // Validate all new values before modifying the original.
        Transaction validatedTransaction = new Transaction(
                transaction.getType(),
                newAmount,
                newCategory,
                newDescription,
                newDate
        );

        // Save validated changes to SQLite before modifying
// the original transaction object.
        if (repository != null) {

            Transaction updatedTransaction = new Transaction(
                    transaction.getId(),
                    transaction.getType(),
                    validatedTransaction.getAmount(),
                    validatedTransaction.getCategory(),
                    validatedTransaction.getDescription(),
                    validatedTransaction.getDate()
            );

            try {
                boolean updated =
                        repository.updateTransaction(updatedTransaction);

                if (!updated) {
                    throw new IllegalArgumentException(
                            "Transaction does not exist."
                    );
                }

            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Failed to update transaction in database.", e
                );
            }
        }

// Apply changes to the original object only after
// the database update succeeds.
        transaction.setAmount(validatedTransaction.getAmount());
        transaction.setCategory(validatedTransaction.getCategory());
        transaction.setDescription(validatedTransaction.getDescription());
        transaction.setDate(validatedTransaction.getDate());
    }

    public void deleteTransaction(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction cannot be null."
            );
        }

        if (repository != null) {

            if (transaction.getId() == null) {
                throw new IllegalArgumentException(
                        "Transaction does not exist."
                );
            }

            try {
                boolean deleted =
                        repository.deleteTransaction(transaction.getId());

                if (!deleted) {
                    throw new IllegalArgumentException(
                            "Transaction does not exist."
                    );
                }

            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Failed to delete transaction from database.", e
                );
            }

        } else {

            boolean removed = transactions.remove(transaction);

            if (!removed) {
                throw new IllegalArgumentException(
                        "Transaction does not exist."
                );
            }
        }
    }
}