package com.backend;

import com.backend.entity.Customer;
import com.backend.entity.Portfolio;
import com.backend.repository.CustomerRepository;
import com.backend.repository.InvestmentRepository;
import com.backend.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InvestmentControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private InvestmentRepository investmentRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void addInvestment_createsRecordAndReturnsCustomerInvestments() throws Exception {
        Long customerId = createCustomerWithPortfolio("investor.one@example.com");

        String payload = """
                {
                  "assetName": "Microsoft",
                  "assetType": "Stock",
                  "ticker": "MSFT",
                  "quantity": 5.5,
                  "purchasePrice": 300,
                  "currentPrice": 350,
                  "purchaseDate": "2025-06-01"
                }
                """;

        mockMvc.perform(post("/customers/{customerId}/investments", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.assetName").value("Microsoft"));

        mockMvc.perform(get("/customers/{customerId}/investments", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assetName").value("Microsoft"));

        assertThat(investmentRepository.findByPortfolioCustomerId(customerId)).hasSize(1);
    }

    @Test
    void addInvestment_forUnknownCustomer_returnsNotFound() throws Exception {
        String payload = """
                {
                  "assetName": "Microsoft",
                  "assetType": "Stock",
                  "ticker": "MSFT",
                  "quantity": 5.5,
                  "purchasePrice": 300,
                  "currentPrice": 350,
                  "purchaseDate": "2025-06-01"
                }
                """;

        mockMvc.perform(post("/customers/99999/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Portfolio not found for customer id: 99999"));
    }

    @Test
    void addInvestment_withValidationErrors_returnsBadRequest() throws Exception {
        Long customerId = createCustomerWithPortfolio("investor.two@example.com");

        String invalidPayload = """
                {
                  "assetName": "",
                  "assetType": "Stock",
                  "quantity": 0,
                  "purchasePrice": -2,
                  "currentPrice": 0,
                  "purchaseDate": "2030-01-01"
                }
                """;

        mockMvc.perform(post("/customers/{customerId}/investments", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.assetName").value("Asset name is required"))
                .andExpect(jsonPath("$.fieldErrors.quantity").value("Quantity must be greater than 0"));
    }

    @Test
    void updateAndDeleteInvestment_runsFullLifecycle() throws Exception {
        Long customerId = createCustomerWithPortfolio("investor.three@example.com");

        String createPayload = """
                {
                  "assetName": "Apple",
                  "assetType": "Stock",
                  "ticker": "AAPL",
                  "quantity": 3,
                  "purchasePrice": 100,
                  "currentPrice": 120,
                  "purchaseDate": "2024-06-01"
                }
                """;

        mockMvc.perform(post("/customers/{customerId}/investments", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated());

        Long investmentId = investmentRepository.findByPortfolioCustomerId(customerId)
                .stream()
                .findFirst()
                .orElseThrow()
                .getId();

        String updatePayload = """
                {
                  "assetName": "Apple Inc",
                  "assetType": "Stock",
                  "ticker": "AAPL",
                  "quantity": 3,
                  "purchasePrice": 100,
                  "currentPrice": 130,
                  "purchaseDate": "2024-06-01"
                }
                """;

        mockMvc.perform(put("/investments/{id}", investmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetName").value("Apple Inc"))
                .andExpect(jsonPath("$.currentPrice").value(130));

        mockMvc.perform(delete("/investments/{id}", investmentId))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/investments/{id}", investmentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Investment not found with id: " + investmentId));
    }

    private Long createCustomerWithPortfolio(String email) {
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail(email);
        customer.setPhone("1112223333");
        customer.setRiskProfile("Moderate");
        customer.setInvestmentGoal("Growth");
        Customer savedCustomer = customerRepository.save(customer);

        Portfolio portfolio = new Portfolio();
        portfolio.setCustomer(savedCustomer);
        portfolioRepository.save(portfolio);

        return savedCustomer.getId();
    }

    @Test
    void getAllInvestments_andTextPlainPaths_areHandled() throws Exception {
        Long customerId = createCustomerWithPortfolio("investor.four@example.com");

        String textPayload = """
                {
                  "assetName": "Nvidia",
                  "assetType": "Stock",
                  "ticker": "NVDA",
                  "quantity": 2,
                  "purchasePrice": 600,
                  "currentPrice": 720,
                  "purchaseDate": "2025-01-01"
                }
                """;

        mockMvc.perform(post("/customers/{customerId}/investments", customerId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(textPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assetName").value("Nvidia"));

        Long investmentId = investmentRepository.findByPortfolioCustomerId(customerId)
                .stream()
                .findFirst()
                .orElseThrow()
                .getId();

        String updateText = """
                {
                  "assetName": "Nvidia Updated",
                  "assetType": "Stock",
                  "ticker": "NVDA",
                  "quantity": 2,
                  "purchasePrice": 600,
                  "currentPrice": 750,
                  "purchaseDate": "2025-01-01"
                }
                """;

        mockMvc.perform(put("/investments/{id}", investmentId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(updateText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetName").value("Nvidia Updated"));

        mockMvc.perform(get("/investments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void invalidTextPayload_andUnknownUpdate_areReported() throws Exception {
        Long customerId = createCustomerWithPortfolio("investor.five@example.com");

        mockMvc.perform(post("/customers/{customerId}/investments", customerId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body. Expected JSON payload."));

        String validPayload = """
                {
                  "assetName": "Tesla",
                  "assetType": "Stock",
                  "ticker": "TSLA",
                  "quantity": 1,
                  "purchasePrice": 200,
                  "currentPrice": 210,
                  "purchaseDate": "2025-02-01"
                }
                """;

        mockMvc.perform(put("/investments/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Investment not found with id: 99999"));
    }
}

