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

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DashboardControllerIntegrationTest {

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
    void dashboardSummary_returnsExpectedAggregates() throws Exception {
        seedDashboardData();

        mockMvc.perform(get("/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.totalCustomers").value(2))
                .andExpect(jsonPath("$.summary.totalAssetsManaged").value(closeTo(2000.0, 0.01)))
                .andExpect(jsonPath("$.summary.portfolioValue").value(closeTo(2050.0, 0.01)))
                .andExpect(jsonPath("$.summary.overallProfitLoss").value(closeTo(50.0, 0.01)))
                .andExpect(jsonPath("$.summary.returnPercentage").value(closeTo(2.5, 0.001)))
                .andExpect(jsonPath("$.allocation[0].assetType").value("Stock"))
                .andExpect(jsonPath("$.allocation[0].value").value(closeTo(1100.0, 0.01)))
                .andExpect(jsonPath("$.allocation[1].assetType").value("Bond"))
                .andExpect(jsonPath("$.performanceTrend.length()").value(6));
    }

    @Test
    void dashboardAllocation_returnsSortedAllocation() throws Exception {
        seedDashboardData();

        mockMvc.perform(get("/dashboard/allocation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].assetType").value("Stock"))
                .andExpect(jsonPath("$[0].percentage").value(closeTo(53.66, 0.01)))
                .andExpect(jsonPath("$[1].assetType").value("Bond"))
                .andExpect(jsonPath("$[1].percentage").value(closeTo(46.34, 0.01)));
    }

    @Test
    void dashboardSummary_withNoInvestments_returnsZeroValues() throws Exception {
        mockMvc.perform(get("/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.totalCustomers").value(0))
                .andExpect(jsonPath("$.summary.totalAssetsManaged").value(0))
                .andExpect(jsonPath("$.summary.portfolioValue").value(0))
                .andExpect(jsonPath("$.summary.overallProfitLoss").value(0))
                .andExpect(jsonPath("$.summary.returnPercentage").value(0.0))
                .andExpect(jsonPath("$.allocation.length()").value(0))
                .andExpect(jsonPath("$.performanceTrend.length()").value(6));

        mockMvc.perform(get("/dashboard/allocation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private void seedDashboardData() {
        Customer customerOne = new Customer();
        customerOne.setName("Customer One");
        customerOne.setEmail("customer.one@example.com");
        customerOne.setRiskProfile("Moderate");
        customerOne.setInvestmentGoal("Growth");
        customerOne = customerRepository.save(customerOne);

        Customer customerTwo = new Customer();
        customerTwo.setName("Customer Two");
        customerTwo.setEmail("customer.two@example.com");
        customerTwo.setRiskProfile("Conservative");
        customerTwo.setInvestmentGoal("Income");
        customerTwo = customerRepository.save(customerTwo);

        Portfolio portfolioOne = new Portfolio();
        portfolioOne.setCustomer(customerOne);
        portfolioOne = portfolioRepository.save(portfolioOne);

        Portfolio portfolioTwo = new Portfolio();
        portfolioTwo.setCustomer(customerTwo);
        portfolioTwo = portfolioRepository.save(portfolioTwo);

        Investment stockInvestment = new Investment();
        stockInvestment.setPortfolio(portfolioOne);
        stockInvestment.setAssetName("Stock A");
        stockInvestment.setAssetType("Stock");
        stockInvestment.setTicker("STA");
        stockInvestment.setQuantity(new BigDecimal("10.00"));
        stockInvestment.setPurchasePrice(new BigDecimal("100.00"));
        stockInvestment.setCurrentPrice(new BigDecimal("110.00"));
        stockInvestment.setPurchaseDate(LocalDate.now().minusMonths(4));
        investmentRepository.save(stockInvestment);

        Investment bondInvestment = new Investment();
        bondInvestment.setPortfolio(portfolioTwo);
        bondInvestment.setAssetName("Bond B");
        bondInvestment.setAssetType("Bond");
        bondInvestment.setTicker("BDB");
        bondInvestment.setQuantity(new BigDecimal("5.00"));
        bondInvestment.setPurchasePrice(new BigDecimal("200.00"));
        bondInvestment.setCurrentPrice(new BigDecimal("190.00"));
        bondInvestment.setPurchaseDate(LocalDate.now().minusMonths(2));
        investmentRepository.save(bondInvestment);
    }
}

