package za.co.kgosi.expensetracker;

import za.co.kgosi.expensetracker.database.DatabaseInitializer;
import za.co.kgosi.expensetracker.repository.TransactionRepository;
import za.co.kgosi.expensetracker.service.TransactionService;
import za.co.kgosi.expensetracker.model.Transaction;
import za.co.kgosi.expensetracker.model.TransactionType;
import za.co.kgosi.expensetracker.model.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.YearMonth;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String databaseUrl = "jdbc:sqlite:expensetracker.db";

        try {
            DatabaseInitializer.initializeDatabase(databaseUrl);
        } catch (SQLException e) {
            System.out.println(
                    "Failed to initialize database: " + e.getMessage()
            );
            scanner.close();
            return;
        }

        TransactionRepository repository =
                new TransactionRepository(databaseUrl);

        TransactionService service =
                new TransactionService(repository);

        boolean running = true;

        while (running) {

            System.out.println("\n===== EXPENSE TRACKER =====");
            System.out.println("1. Add Transaction");
            System.out.println("2. View All Transactions");
            System.out.println("3. View Balance");
            System.out.println("4. Update Transaction");
            System.out.println("5. Delete Transaction");
            System.out.println("6. Monthly Financial Report");
            System.out.println("7. Filter Transactions by Category");
            System.out.println("8. Filter Transactions by Month");
            System.out.println("9. Exit");

            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    try {
                        System.out.println("\n--- ADD TRANSACTION ---");

                        System.out.print("Type (INCOME/EXPENSE): ");
                        TransactionType type = TransactionType.valueOf(
                                scanner.nextLine().trim().toUpperCase()
                        );

                        System.out.print("Amount (R): ");
                        BigDecimal amount = new BigDecimal(
                                scanner.nextLine().trim()
                        );

                        System.out.print("Category: ");
                        Category category = Category.valueOf(
                                scanner.nextLine().trim().toUpperCase()
                        );

                        System.out.print("Description: ");
                        String description = scanner.nextLine();

                        System.out.print("Date (YYYY-MM-DD): ");
                        LocalDate date = LocalDate.parse(
                                scanner.nextLine().trim()
                        );

                        Transaction transaction = new Transaction(
                                type,
                                amount,
                                category,
                                description,
                                date
                        );

                        service.addTransaction(transaction);

                        System.out.println(
                                "Transaction saved successfully! ID: "
                                        + transaction.getId()
                        );

                    } catch (DateTimeParseException e) {

                System.out.println(
                        "Invalid date. Please use YYYY-MM-DD."
                );

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "Invalid transaction: " + e.getMessage()
                );

            }

                    break;

                case "2":

                    System.out.println("\n--- ALL TRANSACTIONS ---");

                    List<Transaction> allTransactions =
                            service.getAllTransactions();

                    if (allTransactions.isEmpty()) {

                        System.out.println("No transactions found.");

                    } else {

                        for (Transaction transaction : allTransactions) {

                            System.out.println("----------------------------");

                            System.out.println(
                                    "ID: " + transaction.getId()
                            );

                            System.out.println(
                                    "Type: " + transaction.getType()
                            );

                            System.out.println(
                                    "Amount: R" + transaction.getAmount()
                            );

                            System.out.println(
                                    "Category: " + transaction.getCategory()
                            );

                            System.out.println(
                                    "Description: " + transaction.getDescription()
                            );

                            System.out.println(
                                    "Date: " + transaction.getDate()
                            );
                        }

                        System.out.println("----------------------------");
                    }

                    break;

                case "3":

                    System.out.println("\n--- FINANCIAL SUMMARY ---");

                    BigDecimal totalIncome = service.getTotalIncome();

                    BigDecimal totalExpenses = service.getTotalExpenses();

                    BigDecimal balance = service.getBalance();

                    System.out.println("Total Income:   R" + totalIncome);

                    System.out.println("Total Expenses: R" + totalExpenses);

                    System.out.println("----------------------------");

                    System.out.println("Balance:        R" + balance);

                    break;

                case "4":
                    try {
                        System.out.println("\n--- UPDATE TRANSACTION ---");

                        System.out.print("Enter transaction ID: ");
                        long id = Long.parseLong(scanner.nextLine().trim());

                        Transaction existingTransaction = null;

                        for (Transaction transaction : service.getAllTransactions()) {
                            if (transaction.getId() != null
                                    && transaction.getId() == id) {
                                existingTransaction = transaction;
                                break;
                            }
                        }

                        if (existingTransaction == null) {
                            System.out.println("Transaction not found.");
                            break;
                        }

                        System.out.println(
                                "Current amount: R" + existingTransaction.getAmount()
                        );

                        System.out.print("New amount (R): ");
                        BigDecimal newAmount = new BigDecimal(
                                scanner.nextLine().trim()
                        );

                        System.out.print("New category: ");
                        Category newCategory = Category.valueOf(
                                scanner.nextLine().trim().toUpperCase()
                        );

                        System.out.print("New description: ");
                        String newDescription = scanner.nextLine();

                        System.out.print("New date (YYYY-MM-DD): ");
                        LocalDate newDate = LocalDate.parse(
                                scanner.nextLine().trim()
                        );

                        service.updateTransaction(
                                existingTransaction,
                                newAmount,
                                newCategory,
                                newDescription,
                                newDate
                        );

                        System.out.println("Transaction updated successfully!");

                    }catch (DateTimeParseException e) {

                System.out.println(
                        "Invalid date. Please use YYYY-MM-DD."
                );

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "Unable to update transaction: " + e.getMessage()
                );

            }

                    break;

                case "5":
                    try {
                        System.out.println("\n--- DELETE TRANSACTION ---");

                        System.out.print("Enter transaction ID: ");
                        long id = Long.parseLong(
                                scanner.nextLine().trim()
                        );

                        Transaction transactionToDelete = null;

                        for (Transaction transaction : service.getAllTransactions()) {

                            if (transaction.getId() != null
                                    && transaction.getId() == id) {

                                transactionToDelete = transaction;
                                break;
                            }
                        }

                        if (transactionToDelete == null) {
                            System.out.println("Transaction not found.");
                            break;
                        }

                        System.out.println(
                                "Description: " + transactionToDelete.getDescription()
                        );

                        System.out.println(
                                "Amount: R" + transactionToDelete.getAmount()
                        );

                        System.out.print("Are you sure? (yes/no): ");
                        String confirmation = scanner.nextLine().trim();

                        if (!confirmation.equalsIgnoreCase("yes")) {
                            System.out.println("Deletion cancelled.");
                            break;
                        }

                        service.deleteTransaction(transactionToDelete);

                        System.out.println("Transaction deleted successfully!");

                    } catch (IllegalArgumentException e) {

                        System.out.println(
                                "Unable to delete transaction: " + e.getMessage()
                        );
                    }

                    break;

                case "6":
                    try {
                        System.out.println("\n--- MONTHLY FINANCIAL REPORT ---");

                        System.out.print("Enter month (YYYY-MM): ");

                        YearMonth month = YearMonth.parse(
                                scanner.nextLine().trim()
                        );

                        BigDecimal monthlyIncome =
                                service.getMonthlyIncome(month);

                        BigDecimal monthlyExpenses =
                                service.getMonthlyExpenses(month);

                        BigDecimal monthlyBalance =
                                service.getMonthlyBalance(month);

                        System.out.println("\n===== REPORT: " + month + " =====");

                        System.out.println(
                                "Monthly Income:   R" + monthlyIncome
                        );

                        System.out.println(
                                "Monthly Expenses: R" + monthlyExpenses
                        );

                        System.out.println("----------------------------");

                        System.out.println(
                                "Monthly Balance:  R" + monthlyBalance
                        );

                    } catch (DateTimeParseException e) {

                        System.out.println(
                                "Invalid month. Please use YYYY-MM."
                        );

                    }

                    break;

                case "7":
                    try {
                        System.out.println("\n--- FILTER BY CATEGORY ---");

                        System.out.print("Enter category: ");

                        Category selectedCategory = Category.valueOf(
                                scanner.nextLine().trim().toUpperCase()
                        );

                        List<Transaction> filteredTransactions =
                                service.getTransactionsByCategory(selectedCategory);

                        if (filteredTransactions.isEmpty()) {

                            System.out.println(
                                    "No transactions found for " + selectedCategory
                            );

                        } else {

                            for (Transaction transaction : filteredTransactions) {

                                System.out.println("----------------------------");

                                System.out.println("ID: " + transaction.getId());

                                System.out.println("Type: " + transaction.getType());

                                System.out.println("Amount: R" + transaction.getAmount());

                                System.out.println("Description: " + transaction.getDescription());

                                System.out.println("Date: " + transaction.getDate());
                            }

                            System.out.println("----------------------------");
                        }

                    } catch (IllegalArgumentException e) {

                        System.out.println(
                                "Invalid category. Please enter a valid category name."
                        );
                    }

                    break;

                case "8":
                    try {
                        System.out.println("\n--- FILTER BY MONTH ---");

                        System.out.print("Enter month (YYYY-MM): ");

                        YearMonth selectedMonth = YearMonth.parse(
                                scanner.nextLine().trim()
                        );

                        List<Transaction> monthlyTransactions =
                                service.getTransactionsByMonth(selectedMonth);

                        if (monthlyTransactions.isEmpty()) {

                            System.out.println(
                                    "No transactions found for " + selectedMonth
                            );

                        } else {

                            System.out.println(
                                    "\nTransactions for " + selectedMonth + ":"
                            );

                            for (Transaction transaction : monthlyTransactions) {

                                System.out.println("----------------------------");
                                System.out.println("ID: " + transaction.getId());
                                System.out.println("Type: " + transaction.getType());
                                System.out.println("Amount: R" + transaction.getAmount());
                                System.out.println("Category: " + transaction.getCategory());
                                System.out.println("Description: " + transaction.getDescription());
                                System.out.println("Date: " + transaction.getDate());
                            }

                            System.out.println("----------------------------");
                        }

                    } catch (DateTimeParseException e) {

                        System.out.println(
                                "Invalid month. Please use YYYY-MM."
                        );
                    }

                    break;

                case "9":
                    System.out.println("Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println(
                            "Invalid option. Please choose 1 to 6."
                    );
            }
        }

        scanner.close();
    }
}