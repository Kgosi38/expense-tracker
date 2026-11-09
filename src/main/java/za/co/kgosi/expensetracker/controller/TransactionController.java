
package za.co.kgosi.expensetracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import za.co.kgosi.expensetracker.dto.UpdateTransactionRequest;
import za.co.kgosi.expensetracker.model.Category;


import za.co.kgosi.expensetracker.model.Transaction;
import za.co.kgosi.expensetracker.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.core.env.Environment;

import za.co.kgosi.expensetracker.dto.CreateTransactionRequest;

import java.util.List;
import java.math.BigDecimal;
import java.util.Map;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final Environment environment;

    public TransactionController(
            TransactionService transactionService,
            Environment environment
    ) {
        this.transactionService = transactionService;
        this.environment = environment;
    }

    private boolean isDemoMode() {
        return environment.matchesProfiles("demo");
    }


    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
    @GetMapping("/balance")
        public Map<String, BigDecimal> getBalance() {
            return Map.of(
                    "income", transactionService.getTotalIncome(),
                    "expenses", transactionService.getTotalExpenses(),
                    "balance", transactionService.getBalance()
            );
        }

    @PostMapping
    public ResponseEntity<?> createTransaction(
            @RequestBody CreateTransactionRequest request
    ) {
        if (isDemoMode()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error",
                            "Read-only demo: creating transactions is disabled."
                    ));
        }

        try {
            Transaction transaction = new Transaction(
                    request.type(),
                    request.amount(),
                    request.category(),
                    request.description(),
                    request.date()
            );

            transactionService.addTransaction(transaction);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(transaction);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable("id") Long id,
            @RequestBody UpdateTransactionRequest request
    ) {
        if (isDemoMode()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error",
                            "Read-only demo: editing transactions is disabled."
                    ));
        }

        try {
            Transaction existingTransaction = transactionService
                    .getAllTransactions()
                    .stream()
                    .filter(transaction -> id.equals(transaction.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingTransaction == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Transaction not found."
                        ));
            }

            transactionService.updateTransaction(
                    existingTransaction,
                    request.amount(),
                    request.category(),
                    request.description(),
                    request.date()
            );

            return ResponseEntity.ok(existingTransaction);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(
            @PathVariable("id") Long id
    ) {
        if (isDemoMode()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error",
                            "Read-only demo: deleting transactions is disabled."
                    ));
        }

        try {
            Transaction existingTransaction = transactionService
                    .getAllTransactions()
                    .stream()
                    .filter(transaction -> id.equals(transaction.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingTransaction == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Transaction not found."
                        ));
            }

            transactionService.deleteTransaction(existingTransaction);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/report")
    public ResponseEntity<?> getMonthlyReport(
            @RequestParam("month") String month
    ) {
        try {
            YearMonth selectedMonth = YearMonth.parse(month);

            BigDecimal income =
                    transactionService.getMonthlyIncome(selectedMonth);

            BigDecimal expenses =
                    transactionService.getMonthlyExpenses(selectedMonth);

            BigDecimal balance =
                    transactionService.getMonthlyBalance(selectedMonth);

            return ResponseEntity.ok(Map.of(
                    "month", selectedMonth.toString(),
                    "income", income,
                    "expenses", expenses,
                    "balance", balance
            ));

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid month. Use YYYY-MM format.")
            );
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getSpendingAnalytics(
            @RequestParam("month") String month
    ) {
        try {
            YearMonth selectedMonth = YearMonth.parse(month);

            Map<Category, BigDecimal> categoryTotals =
                    transactionService.getMonthlyExpensesByCategory(
                            selectedMonth
                    );

            return ResponseEntity.ok(Map.of(
                    "month", selectedMonth.toString(),
                    "categories", categoryTotals
            ));

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid month. Use YYYY-MM format.")
            );
        }
    }





}

