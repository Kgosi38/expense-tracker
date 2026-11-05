package za.co.kgosi.expensetracker.service;

import za.co.kgosi.expensetracker.model.Transaction;
import za.co.kgosi.expensetracker.model.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import za.co.kgosi.expensetracker.model.Category;
import java.time.LocalDate;
import java.time.YearMonth;

public class TransactionService {

    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction cannot be null."
            );
        }

        transactions.add(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public BigDecimal getTotalIncome() {
        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                total = total.add(transaction.getAmount());
            }
        }

        return total;
    }
    public BigDecimal getTotalExpenses() {
        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
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

        for (Transaction transaction : transactions) {
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

        for (Transaction transaction : transactions) {
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

        for (Transaction transaction : transactions) {
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
}