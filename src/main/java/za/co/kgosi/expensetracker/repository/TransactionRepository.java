package za.co.kgosi.expensetracker.repository;

import za.co.kgosi.expensetracker.database.DatabaseConnection;
import za.co.kgosi.expensetracker.model.Transaction;
import za.co.kgosi.expensetracker.model.Category;
import za.co.kgosi.expensetracker.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class TransactionRepository {

    private final String databaseUrl;

    public TransactionRepository(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection(databaseUrl);
    }

    public void saveTransaction(Transaction transaction) throws SQLException {

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction cannot be null."
            );
        }

        if (transaction.getId() != null) {
            throw new IllegalArgumentException(
                    "Transaction has already been assigned an ID."
            );
        }

        String sql = """
            INSERT INTO transactions
            (type, amount, category, description, date)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, transaction.getType().name());
            statement.setString(2, transaction.getAmount().toPlainString());
            statement.setString(3, transaction.getCategory().name());
            statement.setString(4, transaction.getDescription());
            statement.setString(5, transaction.getDate().toString());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    transaction.setId(keys.getLong(1));
                } else {
                    throw new SQLException(
                            "Transaction saved, but no ID was returned."
                    );
                }
            }
        }
    }

    public List<Transaction> getAllTransactions() throws SQLException {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT * FROM transactions ORDER BY id";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {

                Transaction transaction = new Transaction(
                        result.getLong("id"),
                        TransactionType.valueOf(result.getString("type")),
                        new BigDecimal(result.getString("amount")),
                        Category.valueOf(result.getString("category")),
                        result.getString("description"),
                        LocalDate.parse(result.getString("date"))
                );

                transactions.add(transaction);
            }
        }

        return transactions;
    }

    public Transaction getTransactionById(long id) throws SQLException {

        String sql = "SELECT * FROM transactions WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new Transaction(
                            result.getLong("id"),
                            TransactionType.valueOf(result.getString("type")),
                            new BigDecimal(result.getString("amount")),
                            Category.valueOf(result.getString("category")),
                            result.getString("description"),
                            LocalDate.parse(result.getString("date"))
                    );
                }
            }
        }

        return null;
    }

    public boolean updateTransaction(Transaction transaction)
            throws SQLException {

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction cannot be null."
            );
        }

        if (transaction.getId() == null) {
            throw new IllegalArgumentException(
                    "Transaction must have an ID to be updated."
            );
        }

        String sql = """
            UPDATE transactions
            SET type = ?,
                amount = ?,
                category = ?,
                description = ?,
                date = ?
            WHERE id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, transaction.getType().name());
            statement.setString(2,
                    transaction.getAmount().toPlainString());
            statement.setString(3, transaction.getCategory().name());
            statement.setString(4, transaction.getDescription());
            statement.setString(5, transaction.getDate().toString());
            statement.setLong(6, transaction.getId());

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;
        }
    }

    public boolean deleteTransaction(long id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Transaction ID must be positive."
            );
        }

        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            int rowsDeleted = statement.executeUpdate();

            return rowsDeleted > 0;
        }
    }
}