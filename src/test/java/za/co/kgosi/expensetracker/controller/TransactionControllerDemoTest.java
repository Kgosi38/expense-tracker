
package za.co.kgosi.expensetracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(properties = {
        "app.database.url=jdbc:sqlite:expensetracker-demo-test.db",
        "app.demo.seed-data=false"
})
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class TransactionControllerDemoTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldBlockCreatingTransactionsInDemoMode() throws Exception {

        String requestBody = """
                {
                    "type": "Expense",
                    "amount": 100.00,
                    "category": "Food",
                    "description": "Demo security test",
                    "date": "2026-11-09"
                }
                """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error")
                        .value("Read-only demo: creating transactions is disabled."));
    }

    @Test
    void shouldBlockUpdatingTransactionsInDemoMode() throws Exception {

        String requestBody = """
            {
                "amount": 250.00,
                "category": "Food",
                "description": "Attempted demo update",
                "date": "2026-11-09"
            }
            """;

        mockMvc.perform(put("/api/transactions/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error")
                        .value("Read-only demo: editing transactions is disabled."));
    }

    @Test
    void shouldBlockDeletingTransactionsInDemoMode() throws Exception {

        mockMvc.perform(delete("/api/transactions/{id}", 1L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error")
                        .value("Read-only demo: deleting transactions is disabled."));
    }

    @Test
    void shouldAllowViewingTransactionsInDemoMode() throws Exception {

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldAllowViewingBalanceInDemoMode() throws Exception {

        mockMvc.perform(get("/api/transactions/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").exists())
                .andExpect(jsonPath("$.expenses").exists())
                .andExpect(jsonPath("$.balance").exists());
    }

    @Test
    void shouldAllowViewingMonthlyReportInDemoMode() throws Exception {

        mockMvc.perform(get("/api/transactions/report")
                        .param("month", "2026-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2026-11"))
                .andExpect(jsonPath("$.income").exists())
                .andExpect(jsonPath("$.expenses").exists())
                .andExpect(jsonPath("$.balance").exists());
    }

    @Test
    void shouldAllowViewingSpendingAnalyticsInDemoMode() throws Exception {

        mockMvc.perform(get("/api/transactions/analytics")
                        .param("month", "2026-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2026-11"))
                .andExpect(jsonPath("$.categories").exists());
    }



}
