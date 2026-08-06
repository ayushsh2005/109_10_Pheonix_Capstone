package com.backend;

import com.backend.dto.AllocationDTO;
import com.backend.dto.CustomerResponseDTO;
import com.backend.dto.DashboardResponseDTO;
import com.backend.dto.DashboardSummaryDTO;
import com.backend.dto.InvestmentResponseDTO;
import com.backend.dto.PerformancePointDTO;
import com.backend.dto.PortfolioDTO;
import com.backend.dto.PortfolioPerformanceDTO;
import com.backend.dto.SuggestionDTO;
import com.backend.entity.Customer;
import com.backend.entity.Portfolio;
import com.backend.exception.CustomerNotFoundException;
import com.backend.exception.ErrorResponse;
import com.backend.exception.InvestmentNotFoundException;
import com.backend.exception.PortfolioNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises DTO setters/getters and all exception constructors to raise coverage
 * on classes that are only partially hit during normal flow testing.
 * No Spring context needed — these are plain unit tests.
 */
class DtoAndExceptionCoverageTest {

    // ── AllocationDTO ────────────────────────────────────────────────────────

    @Test
    void allocationDTO_settersAndGetters() {
        AllocationDTO dto = new AllocationDTO();
        dto.setAssetType("Bond");
        dto.setPercentage(35.5);
        dto.setValue(new BigDecimal("3550.00"));

        assertThat(dto.getAssetType()).isEqualTo("Bond");
        assertThat(dto.getPercentage()).isEqualTo(35.5);
        assertThat(dto.getValue()).isEqualByComparingTo(new BigDecimal("3550.00"));
    }

    // ── PerformancePointDTO ──────────────────────────────────────────────────

    @Test
    void performancePointDTO_settersAndGetters() {
        PerformancePointDTO dto = new PerformancePointDTO();
        dto.setMonth("Aug");
        dto.setValue(new BigDecimal("12500.00"));

        assertThat(dto.getMonth()).isEqualTo("Aug");
        assertThat(dto.getValue()).isEqualByComparingTo(new BigDecimal("12500.00"));
    }

    @Test
    void performancePointDTO_allArgsConstructor() {
        PerformancePointDTO dto = new PerformancePointDTO("Sep", new BigDecimal("13000.00"));
        assertThat(dto.getMonth()).isEqualTo("Sep");
        assertThat(dto.getValue()).isEqualByComparingTo(new BigDecimal("13000.00"));
    }

    // ── DashboardSummaryDTO ──────────────────────────────────────────────────

    @Test
    void dashboardSummaryDTO_settersAndGetters() {
        DashboardSummaryDTO dto = new DashboardSummaryDTO();
        dto.setTotalCustomers(5L);
        dto.setTotalAssetsManaged(new BigDecimal("50000.00"));
        dto.setPortfolioValue(new BigDecimal("55000.00"));
        dto.setOverallProfitLoss(new BigDecimal("5000.00"));
        dto.setReturnPercentage(10.0);

        assertThat(dto.getTotalCustomers()).isEqualTo(5L);
        assertThat(dto.getTotalAssetsManaged()).isEqualByComparingTo(new BigDecimal("50000.00"));
        assertThat(dto.getPortfolioValue()).isEqualByComparingTo(new BigDecimal("55000.00"));
        assertThat(dto.getOverallProfitLoss()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(dto.getReturnPercentage()).isEqualTo(10.0);
    }

    // ── DashboardResponseDTO ─────────────────────────────────────────────────

    @Test
    void dashboardResponseDTO_settersAndGetters() {
        DashboardResponseDTO dto = new DashboardResponseDTO();
        DashboardSummaryDTO summary = new DashboardSummaryDTO(2L, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO, 0.0);
        AllocationDTO alloc = new AllocationDTO("Stock", 100.0, BigDecimal.TEN);
        PerformancePointDTO point = new PerformancePointDTO("Jan", BigDecimal.TEN);

        dto.setSummary(summary);
        dto.setAllocation(List.of(alloc));
        dto.setPerformanceTrend(List.of(point));

        assertThat(dto.getSummary().getTotalCustomers()).isEqualTo(2L);
        assertThat(dto.getAllocation()).hasSize(1);
        assertThat(dto.getPerformanceTrend()).hasSize(1);
    }

    // ── CustomerResponseDTO ──────────────────────────────────────────────────

    @Test
    void customerResponseDTO_settersAndGetters() {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(1L);
        dto.setName("Bob");
        dto.setEmail("bob@example.com");
        dto.setPhone("555-1234");
        dto.setRiskProfile("Conservative");
        dto.setInvestmentGoal("Growth");
        dto.setJoinedDate(LocalDateTime.of(2026, 1, 1, 0, 0));
        dto.setStatus("Active");
        dto.setPortfolioValue(new BigDecimal("9000.00"));

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Bob");
        assertThat(dto.getEmail()).isEqualTo("bob@example.com");
        assertThat(dto.getPhone()).isEqualTo("555-1234");
        assertThat(dto.getRiskProfile()).isEqualTo("Conservative");
        assertThat(dto.getInvestmentGoal()).isEqualTo("Growth");
        assertThat(dto.getJoinedDate()).isNotNull();
        assertThat(dto.getStatus()).isEqualTo("Active");
        assertThat(dto.getPortfolioValue()).isEqualByComparingTo(new BigDecimal("9000.00"));
    }

    // ── InvestmentResponseDTO ────────────────────────────────────────────────

    @Test
    void investmentResponseDTO_settersAndGetters() {
        InvestmentResponseDTO dto = new InvestmentResponseDTO();
        dto.setId(5L);
        dto.setPortfolioId(10L);
        dto.setCustomerId(1L);
        dto.setAssetName("Tesla");
        dto.setAssetType("Stock");
        dto.setTicker("TSLA");
        dto.setQuantity(new BigDecimal("5"));
        dto.setPurchasePrice(new BigDecimal("200.00"));
        dto.setCurrentPrice(new BigDecimal("250.00"));
        dto.setPurchaseDate(LocalDate.of(2026, 3, 1));

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getPortfolioId()).isEqualTo(10L);
        assertThat(dto.getCustomerId()).isEqualTo(1L);
        assertThat(dto.getAssetName()).isEqualTo("Tesla");
        assertThat(dto.getAssetType()).isEqualTo("Stock");
        assertThat(dto.getTicker()).isEqualTo("TSLA");
        assertThat(dto.getQuantity()).isEqualByComparingTo(new BigDecimal("5"));
        assertThat(dto.getPurchasePrice()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(dto.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(dto.getPurchaseDate()).isEqualTo(LocalDate.of(2026, 3, 1));
    }

    // ── PortfolioDTO ─────────────────────────────────────────────────────────

    @Test
    void portfolioDTO_settersAndGetters() {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setId(10L);
        dto.setCustomerId(1L);
        dto.setCreatedDate(LocalDateTime.of(2026, 1, 1, 0, 0));
        dto.setInvestments(List.of());
        dto.setTotalInvestment(new BigDecimal("5000.00"));
        dto.setCurrentValue(new BigDecimal("5500.00"));
        dto.setProfitLoss(new BigDecimal("500.00"));
        dto.setReturnPercentage(10.0);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getCustomerId()).isEqualTo(1L);
        assertThat(dto.getCreatedDate()).isNotNull();
        assertThat(dto.getInvestments()).isEmpty();
        assertThat(dto.getTotalInvestment()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(dto.getCurrentValue()).isEqualByComparingTo(new BigDecimal("5500.00"));
        assertThat(dto.getProfitLoss()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(dto.getReturnPercentage()).isEqualTo(10.0);
    }

    // ── PortfolioPerformanceDTO ──────────────────────────────────────────────

    @Test
    void portfolioPerformanceDTO_settersAndGetters() {
        PortfolioPerformanceDTO dto = new PortfolioPerformanceDTO();
        dto.setCustomerId(1L);
        dto.setCustomerName("Alice");
        dto.setTotalInvested(new BigDecimal("2000.00"));
        dto.setCurrentValue(new BigDecimal("2200.00"));
        dto.setProfitLoss(new BigDecimal("200.00"));
        dto.setReturnPercentage(10.0);

        assertThat(dto.getCustomerId()).isEqualTo(1L);
        assertThat(dto.getCustomerName()).isEqualTo("Alice");
        assertThat(dto.getTotalInvested()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(dto.getCurrentValue()).isEqualByComparingTo(new BigDecimal("2200.00"));
        assertThat(dto.getProfitLoss()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(dto.getReturnPercentage()).isEqualTo(10.0);
    }

    // ── SuggestionDTO ────────────────────────────────────────────────────────

    @Test
    void suggestionDTO_settersAndGetters() {
        SuggestionDTO dto = new SuggestionDTO();
        dto.setId("SUG-1-1");
        dto.setCustomerId(1L);
        dto.setType("Diversification");
        dto.setSeverity("High");
        dto.setMessage("Reduce stock exposure.");

        assertThat(dto.getId()).isEqualTo("SUG-1-1");
        assertThat(dto.getCustomerId()).isEqualTo(1L);
        assertThat(dto.getType()).isEqualTo("Diversification");
        assertThat(dto.getSeverity()).isEqualTo("High");
        assertThat(dto.getMessage()).isEqualTo("Reduce stock exposure.");
    }

    // ── Exception constructors ───────────────────────────────────────────────

    @Test
    void customerNotFoundException_stringConstructor() {
        CustomerNotFoundException ex = new CustomerNotFoundException("Custom message");
        assertThat(ex.getMessage()).isEqualTo("Custom message");
    }

    @Test
    void customerNotFoundException_longConstructor() {
        CustomerNotFoundException ex = new CustomerNotFoundException(42L);
        assertThat(ex.getMessage()).contains("42");
    }

    @Test
    void investmentNotFoundException_stringConstructor() {
        InvestmentNotFoundException ex = new InvestmentNotFoundException("Investment missing");
        assertThat(ex.getMessage()).isEqualTo("Investment missing");
    }

    @Test
    void investmentNotFoundException_longConstructor() {
        InvestmentNotFoundException ex = new InvestmentNotFoundException(7L);
        assertThat(ex.getMessage()).contains("7");
    }

    @Test
    void portfolioNotFoundException_stringConstructor() {
        PortfolioNotFoundException ex = new PortfolioNotFoundException("Portfolio missing");
        assertThat(ex.getMessage()).isEqualTo("Portfolio missing");
    }

    @Test
    void portfolioNotFoundException_longConstructor() {
        PortfolioNotFoundException ex = new PortfolioNotFoundException(3L);
        assertThat(ex.getMessage()).contains("3");
    }

    // ── ErrorResponse ────────────────────────────────────────────────────────

    @Test
    void errorResponse_settersAndGetters() {
        ErrorResponse err = new ErrorResponse();
        LocalDateTime now = LocalDateTime.now();
        err.setTimestamp(now);
        err.setStatus(400);
        err.setError("Bad Request");
        err.setMessage("Validation failed");
        err.setPath("/customers");
        err.setFieldErrors(Map.of("name", "must not be blank"));

        assertThat(err.getTimestamp()).isEqualTo(now);
        assertThat(err.getStatus()).isEqualTo(400);
        assertThat(err.getError()).isEqualTo("Bad Request");
        assertThat(err.getMessage()).isEqualTo("Validation failed");
        assertThat(err.getPath()).isEqualTo("/customers");
        assertThat(err.getFieldErrors()).containsKey("name");
    }

    // ── Entity setters ───────────────────────────────────────────────────────

    @Test
    void customer_settersAndGetters() {
        Customer c = new Customer();
        c.setId(1L);
        c.setName("Bob");
        c.setEmail("bob@example.com");
        c.setPhone("555-0001");
        c.setRiskProfile("Moderate");
        c.setInvestmentGoal("Growth");
        c.setCreatedDate(LocalDateTime.of(2026, 1, 1, 0, 0));

        Portfolio p = new Portfolio();
        c.setPortfolio(p);

        assertThat(c.getId()).isEqualTo(1L);
        assertThat(c.getName()).isEqualTo("Bob");
        assertThat(c.getEmail()).isEqualTo("bob@example.com");
        assertThat(c.getPhone()).isEqualTo("555-0001");
        assertThat(c.getRiskProfile()).isEqualTo("Moderate");
        assertThat(c.getInvestmentGoal()).isEqualTo("Growth");
        assertThat(c.getCreatedDate()).isNotNull();
        assertThat(c.getPortfolio()).isSameAs(p);
    }

    @Test
    void portfolio_settersAndGetters() {
        Portfolio p = new Portfolio();
        p.setId(10L);
        LocalDateTime now = LocalDateTime.now();
        p.setCreatedDate(now);

        Customer c = new Customer();
        c.setId(1L);
        p.setCustomer(c);
        p.setInvestments(List.of());

        assertThat(p.getId()).isEqualTo(10L);
        assertThat(p.getCreatedDate()).isEqualTo(now);
        assertThat(p.getCustomer()).isSameAs(c);
        assertThat(p.getInvestments()).isEmpty();
    }
}

