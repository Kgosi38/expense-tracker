
package za.co.kgosi.expensetracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.BeforeAll;
import za.co.kgosi.expensetracker.database.DatabaseInitializer;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import tools.jackson.databind.ObjectMapper;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @BeforeAll
    static void initializeTestDatabase() throws Exception {
        DatabaseInitializer.initializeDatabase(
                "jdbc:sqlite:expensetracker-test.db"
        );
    }


    @BeforeEach
    void cleanTestDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite:expensetracker-test.db");
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM transactions");
        }
    }




    @Test
    void shouldGetAllTransactions() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }


    @Test
    void shouldCreateTransaction() throws Exception {

        String requestBody = """
            {
                "type": "Expense",
                "amount": 100.00,
                "category": "Food",
                "description": "Automated POST test",
                "date": "2026-11-08"
            }
            """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.description")
                        .value("Automated POST test"));
    }


    @Test
    void shouldUpdateTransaction() throws Exception {

        String createRequest = """
            {
                "type": "Expense",
                "amount": 100.00,
                "category": "Food",
                "description": "Original expense",
                "date": "2026-11-08"
            }
            """;

        String createResponse = mockMvc.perform(
                        post("/api/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long transactionId = objectMapper
                .readTree(createResponse)
                .get("id")
                .asLong();

        String updateRequest = """
            {
                "amount": 250.00,
                "category": "Food",
                "description": "Updated expense",
                "date": "2026-11-08"
            }
            """;

        mockMvc.perform(put("/api/transactions/{id}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.description")
                        .value("Updated expense"));
    }


    @Test
    void shouldDeleteTransaction() throws Exception {

        String createRequest = """
            {
                "type": "Expense",
                "amount": 75.00,
                "category": "Food",
                "description": "Temporary expense",
                "date": "2026-11-08"
            }
            """;

        String createResponse = mockMvc.perform(
                        post("/api/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();

        long transactionId = objectMapper
                .readTree(createResponse)
                .get("id")
                .asLong();

        mockMvc.perform(delete("/api/transactions/{id}", transactionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    void shouldRejectNegativeAmount() throws Exception {

        String requestBody = """
            {
                "type": "Expense",
                "amount": -100.00,
                "category": "Food",
                "description": "Invalid expense",
                "date": "2026-11-08"
            }
            """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Amount must be greater than zero."));
    }


    @Test
    void shouldRejectMissingAmount() throws Exception {

        String requestBody = """
            {
                "type": "Expense",
                "category": "Food",
                "description": "Missing amount test",
                "date": "2026-11-08"
            }
            """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Amount is required."));
    }


    @Test
    void shouldReturnNotFoundWhenDeletingMissingTransaction()
            throws Exception {

        mockMvc.perform(
                        delete("/api/transactions/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Transaction not found."));
    }







}
