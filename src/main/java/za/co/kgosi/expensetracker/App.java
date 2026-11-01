package za.co.kgosi.expensetracker;

import za.co.kgosi.expensetracker.model.Category;
import za.co.kgosi.expensetracker.model.Transaction;
import za.co.kgosi.expensetracker.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class App {

    public static void main(String[] args) {
        LocalDate today = LocalDate.of(2026, 11, 1);

        Transaction lunch = new Transaction(
                TransactionType.EXPENSE, new BigDecimal("48"), Category.FOOD, "  Lunch  ", today);
        System.out.println(lunch);

        try {
            new Transaction(TransactionType.EXPENSE, new BigDecimal("500"), Category.SALARY, "Wrong category", today);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }

        try {
            new Transaction(TransactionType.EXPENSE, new BigDecimal("-5"), Category.FOOD, "Negative", today);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }

        try {
            new Transaction(TransactionType.EXPENSE, new BigDecimal("10.555"), Category.FOOD, "Too precise", today);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}