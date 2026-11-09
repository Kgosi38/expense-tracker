
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


    @Test
    void shouldReturnMonthlyReport() throws Exception {

        String incomeTransaction = """
        {
            "type": "Income",
            "amount": 5000.00,
            "category": "Salary",
            "description": "November salary",
            "date": "2026-11-05"
        }
        """;

        String expenseTransaction = """
        {
            "type": "Expense",
            "amount": 1100.00,
            "category": "Food",
            "description": "November groceries",
            "date": "2026-11-08"
        }
        """;

        // Create an income transaction
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incomeTransaction))
                .andExpect(status().isCreated());

        // Create an expense transaction
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expenseTransaction))
                .andExpect(status().isCreated());

        // Request the monthly financial report
        mockMvc.perform(get("/api/transactions/report")
                        .param("month", "2026-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2026-11"))
                .andExpect(jsonPath("$.income").value(5000.00))
                .andExpect(jsonPath("$.expenses").value(1100.00))
                .andExpect(jsonPath("$.balance").value(3900.00));
    }


    @Test
    void shouldRejectInvalidMonth() throws Exception {

        mockMvc.perform(get("/api/transactions/report")
                        .param("month", "November"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid month. Use YYYY-MM format."));
    }


    @Test
    void shouldReturnZeroForEmptyMonth() throws Exception {

        mockMvc.perform(get("/api/transactions/report")
                        .param("month", "2027-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2027-01"))
                .andExpect(jsonPath("$.income").value(0))
                .andExpect(jsonPath("$.expenses").value(0))
                .andExpect(jsonPath("$.balance").value(0));
    }



}
