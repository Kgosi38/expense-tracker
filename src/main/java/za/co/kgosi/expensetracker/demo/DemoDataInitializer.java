
package za.co.kgosi.expensetracker.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import za.co.kgosi.expensetracker.model.Category;
import za.co.kgosi.expensetracker.model.Transaction;
import za.co.kgosi.expensetracker.model.TransactionType;
import za.co.kgosi.expensetracker.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@Profile("demo")
public class DemoDataInitializer {

    @Bean
    public ApplicationRunner loadDemoData(
            TransactionService transactionService,
            @Value("${app.demo.seed-data:false}") boolean seedData
    ) {
        return args -> {

            if (!seedData) {
                System.out.println("Demo data seeding is disabled.");
                return;
            }

            if (!transactionService.getAllTransactions().isEmpty()) {
                System.out.println(
                        "Demo database already contains transactions. Skipping sample data."
                );
                return;
            }

            LocalDate today = LocalDate.now();

            transactionService.addTransaction(new Transaction(
                    TransactionType.INCOME,
                    new BigDecimal("15000.00"),
                    Category.SALARY,
                    "Monthly salary (demo)",
                    today.withDayOfMonth(1)
            ));

            transactionService.addTransaction(new Transaction(
                    TransactionType.INCOME,
                    new BigDecimal("3500.00"),
                    Category.OTHER_INCOME,
                    "Freelance website project (demo)",
                    today.withDayOfMonth(3)
            ));

            transactionService.addTransaction(new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("2200.00"),
                    Category.FOOD,
                    "Monthly groceries (demo)",
                    today.withDayOfMonth(5)
            ));

            transactionService.addTransaction(new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("1200.00"),
                    Category.TRANSPORT,
                    "Transport expenses (demo)",
                    today.withDayOfMonth(7)
            ));

            transactionService.addTransaction(new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("900.00"),
                    Category.CLOTHING,
                    "Clothing purchase (demo)",
                    today.withDayOfMonth(8)
            ));

            transactionService.addTransaction(new Transaction(
                    TransactionType.EXPENSE,
                    new BigDecimal("700.00"),
                    Category.DATA_AIRTIME,
                    "Internet and mobile data (demo)",
                    today.withDayOfMonth(9)
            ));

            System.out.println("Demo sample transactions created successfully.");
        };
    }
}
