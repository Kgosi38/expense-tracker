
package za.co.kgosi.expensetracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import za.co.kgosi.expensetracker.repository.TransactionRepository;
import za.co.kgosi.expensetracker.service.TransactionService;

@Configuration
public class AppConfig {


    @Bean
    public TransactionRepository transactionRepository(
            @Value("${app.database.url:jdbc:sqlite:expensetracker.db}")
            String databaseUrl
    ) {
        return new TransactionRepository(databaseUrl);
    }


    @Bean
    public TransactionService transactionService(
            TransactionRepository repository
    ) {
        return new TransactionService(repository);
    }




}
