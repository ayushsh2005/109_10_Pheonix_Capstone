package com.backend;

import com.backend.entity.Customer;
import com.backend.entity.Investment;
import com.backend.entity.Portfolio;
import com.backend.repository.CustomerRepository;
import com.backend.repository.InvestmentRepository;
import com.backend.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SuggestionControllerIntegrationTest {

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
    void customerSuggestions_coverDiversificationRiskAndOpportunityBranches() throws Exception {
        Long customerId = createCustomerWithPortfolio("branch.user@example.com", "Conservative");

        createInvestment(customerId, "Tech Alpha", "Stock", new BigDecimal("10"),
                new BigDecimal("100"), new BigDecimal("200"));
        createInvestment(customerId, "Bond Beta", "Bond", new BigDecimal("1"),
                new BigDecimal("100"), new BigDecimal("100"));

        mockMvc.perform(get("/customers/{id}/suggestions", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.type=='Diversification')].severity", hasItem("High")))
                .andExpect(jsonPath("$[?(@.type=='Risk')].severity", hasItem("High")))
                .andExpect(jsonPath("$[?(@.type=='Opportunity')].severity", hasItem("Low")));
    }

    @Test
        void customerSuggestions_forUnknownAndEmptyCustomer_coversNotFoundAndEmpty() throws Exception {
        mockMvc.perform(get("/customers/99999/suggestions"))
                                .andExpect(status().isNotFound());

        Long customerId = createCustomerWithPortfolio("empty.user@example.com", "Moderate");

        mockMvc.perform(get("/customers/{id}/suggestions", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void customerSuggestions_withZeroPortfolioValue_returnsEmpty() throws Exception {
        Long customerId = createCustomerWithPortfolio("zero.user@example.com", "Moderate");

        createInvestment(customerId, "Zero Asset", "Stock", new BigDecimal("2"),
                new BigDecimal("100"), BigDecimal.ZERO);

        mockMvc.perform(get("/customers/{id}/suggestions", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void allSuggestions_aggregatesAcrossCustomers() throws Exception {
        Long customerOneId = createCustomerWithPortfolio("all.one@example.com", "Conservative");
        Long customerTwoId = createCustomerWithPortfolio("all.two@example.com", "Moderate");

        createInvestment(customerOneId, "Growth Stock", "Stock", new BigDecimal("5"),
                new BigDecimal("100"), new BigDecimal("160"));
        createInvestment(customerTwoId, "Energy Stock", "Stock", new BigDecimal("3"),
                new BigDecimal("100"), new BigDecimal("140"));

        mockMvc.perform(get("/suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[*].customerId", hasItem(customerOneId.intValue())))
                .andExpect(jsonPath("$[*].customerId", hasItem(customerTwoId.intValue())));
    }

    private Long createCustomerWithPortfolio(String email, String riskProfile) {
        Customer customer = new Customer();
        customer.setName("Suggestion User");
        customer.setEmail(email);
        customer.setPhone("1231231234");
        customer.setRiskProfile(riskProfile);
        customer.setInvestmentGoal("Long term");
        Customer saved = customerRepository.save(customer);

        Portfolio portfolio = new Portfolio();
        portfolio.setCustomer(saved);
        portfolioRepository.save(portfolio);

        return saved.getId();
    }

    private void createInvestment(Long customerId, String assetName, String assetType, BigDecimal quantity,
                                  BigDecimal purchasePrice, BigDecimal currentPrice) {
        Portfolio portfolio = portfolioRepository.findByCustomerId(customerId).orElseThrow();

        Investment investment = new Investment();
        investment.setPortfolio(portfolio);
        investment.setAssetName(assetName);
        investment.setAssetType(assetType);
        investment.setTicker(assetName.substring(0, 3).toUpperCase());
        investment.setQuantity(quantity);
        investment.setPurchasePrice(purchasePrice);
        investment.setCurrentPrice(currentPrice);
        investment.setPurchaseDate(LocalDate.now().minusMonths(2));
        investmentRepository.save(investment);
    }
}

