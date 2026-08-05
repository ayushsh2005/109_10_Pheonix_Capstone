package com.backend.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoCoverageTest {

    @Test
    void customerRequestDto_gettersAndSetters() {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setName("Alice");
        dto.setEmail("alice@example.com");
        dto.setPhone("1234567890");
        dto.setRiskProfile("Moderate");
        dto.setInvestmentGoal("Retirement");

        assertThat(dto.getName()).isEqualTo("Alice");
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
        assertThat(dto.getPhone()).isEqualTo("1234567890");
        assertThat(dto.getRiskProfile()).isEqualTo("Moderate");
        assertThat(dto.getInvestmentGoal()).isEqualTo("Retirement");
    }

    @Test
    void investmentRequestDto_gettersAndSetters() {
        InvestmentRequestDTO dto = new InvestmentRequestDTO();
        dto.setAssetName("Apple");
        dto.setAssetType("Stock");
        dto.setTicker("AAPL");
        dto.setQuantity(new BigDecimal("2.50"));
        dto.setPurchasePrice(new BigDecimal("100.00"));
        dto.setCurrentPrice(new BigDecimal("120.00"));
        dto.setPurchaseDate(LocalDate.of(2025, 1, 10));

        assertThat(dto.getAssetName()).isEqualTo("Apple");
        assertThat(dto.getAssetType()).isEqualTo("Stock");
        assertThat(dto.getTicker()).isEqualTo("AAPL");
        assertThat(dto.getQuantity()).isEqualByComparingTo("2.50");
        assertThat(dto.getPurchasePrice()).isEqualByComparingTo("100.00");
        assertThat(dto.getCurrentPrice()).isEqualByComparingTo("120.00");
        assertThat(dto.getPurchaseDate()).isEqualTo(LocalDate.of(2025, 1, 10));
    }

    @Test
    void customerResponseDto_constructorsAndAccessors() {
        CustomerResponseDTO empty = new CustomerResponseDTO();
        assertThat(empty).isNotNull();

        LocalDateTime joined = LocalDateTime.of(2026, 1, 1, 10, 0);
        CustomerResponseDTO dto = new CustomerResponseDTO(1L, "Bob", "bob@example.com", "9999999999",
                "Conservative", "Income", joined, "Active", new BigDecimal("2500.00"));

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Bob");
        assertThat(dto.getEmail()).isEqualTo("bob@example.com");
        assertThat(dto.getPhone()).isEqualTo("9999999999");
        assertThat(dto.getRiskProfile()).isEqualTo("Conservative");
        assertThat(dto.getInvestmentGoal()).isEqualTo("Income");
        assertThat(dto.getJoinedDate()).isEqualTo(joined);
        assertThat(dto.getStatus()).isEqualTo("Active");
        assertThat(dto.getPortfolioValue()).isEqualByComparingTo("2500.00");

        dto.setId(2L);
        dto.setName("Bobby");
        dto.setEmail("bobby@example.com");
        dto.setPhone("8888888888");
        dto.setRiskProfile("Aggressive");
        dto.setInvestmentGoal("Growth");
        dto.setJoinedDate(LocalDateTime.of(2026, 3, 1, 9, 30));
        dto.setStatus("Paused");
        dto.setPortfolioValue(new BigDecimal("3200.00"));

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Bobby");
        assertThat(dto.getEmail()).isEqualTo("bobby@example.com");
        assertThat(dto.getPhone()).isEqualTo("8888888888");
        assertThat(dto.getRiskProfile()).isEqualTo("Aggressive");
        assertThat(dto.getInvestmentGoal()).isEqualTo("Growth");
        assertThat(dto.getJoinedDate()).isEqualTo(LocalDateTime.of(2026, 3, 1, 9, 30));
        assertThat(dto.getStatus()).isEqualTo("Paused");
        assertThat(dto.getPortfolioValue()).isEqualByComparingTo("3200.00");
    }

    @Test
    void investmentResponseDto_constructorsAndAccessors() {
        InvestmentResponseDTO empty = new InvestmentResponseDTO();
        assertThat(empty).isNotNull();

        InvestmentResponseDTO dto = new InvestmentResponseDTO(10L, 20L, 30L, "ETF A", "ETF", "ETFA",
                new BigDecimal("3.25"), new BigDecimal("95.50"), new BigDecimal("110.00"),
                LocalDate.of(2024, 12, 1));

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getPortfolioId()).isEqualTo(20L);
        assertThat(dto.getCustomerId()).isEqualTo(30L);
        assertThat(dto.getAssetName()).isEqualTo("ETF A");
        assertThat(dto.getAssetType()).isEqualTo("ETF");
        assertThat(dto.getTicker()).isEqualTo("ETFA");
        assertThat(dto.getQuantity()).isEqualByComparingTo("3.25");
        assertThat(dto.getPurchasePrice()).isEqualByComparingTo("95.50");
        assertThat(dto.getCurrentPrice()).isEqualByComparingTo("110.00");
        assertThat(dto.getPurchaseDate()).isEqualTo(LocalDate.of(2024, 12, 1));

        dto.setId(11L);
        dto.setPortfolioId(21L);
        dto.setCustomerId(31L);
        dto.setAssetName("ETF B");
        dto.setAssetType("Mutual Fund");
        dto.setTicker("NEW");
        dto.setQuantity(new BigDecimal("8.00"));
        dto.setPurchasePrice(new BigDecimal("80.00"));
        dto.setCurrentPrice(new BigDecimal("90.00"));
        dto.setPurchaseDate(LocalDate.of(2025, 2, 2));

        assertThat(dto.getId()).isEqualTo(11L);
        assertThat(dto.getPortfolioId()).isEqualTo(21L);
        assertThat(dto.getCustomerId()).isEqualTo(31L);
        assertThat(dto.getAssetName()).isEqualTo("ETF B");
        assertThat(dto.getAssetType()).isEqualTo("Mutual Fund");
        assertThat(dto.getTicker()).isEqualTo("NEW");
        assertThat(dto.getQuantity()).isEqualByComparingTo("8.00");
        assertThat(dto.getPurchasePrice()).isEqualByComparingTo("80.00");
        assertThat(dto.getCurrentPrice()).isEqualByComparingTo("90.00");
        assertThat(dto.getPurchaseDate()).isEqualTo(LocalDate.of(2025, 2, 2));
    }

    @Test
    void portfolioAndPerformanceDtos_constructorsAndAccessors() {
        PortfolioDTO portfolioEmpty = new PortfolioDTO();
        PortfolioPerformanceDTO performanceEmpty = new PortfolioPerformanceDTO();
        assertThat(portfolioEmpty).isNotNull();
        assertThat(performanceEmpty).isNotNull();

        InvestmentResponseDTO investment = new InvestmentResponseDTO(1L, 2L, 3L, "Asset", "Stock", "AST",
                new BigDecimal("1"), new BigDecimal("10"), new BigDecimal("12"), LocalDate.of(2025, 1, 1));

        PortfolioDTO portfolio = new PortfolioDTO(2L, 3L, LocalDateTime.of(2026, 2, 2, 12, 0),
                List.of(investment), new BigDecimal("1000"), new BigDecimal("1200"), new BigDecimal("200"), 20.0);

        assertThat(portfolio.getId()).isEqualTo(2L);
        assertThat(portfolio.getCustomerId()).isEqualTo(3L);
        assertThat(portfolio.getInvestments()).hasSize(1);
        assertThat(portfolio.getTotalInvestment()).isEqualByComparingTo("1000");
        assertThat(portfolio.getCurrentValue()).isEqualByComparingTo("1200");
        assertThat(portfolio.getProfitLoss()).isEqualByComparingTo("200");
        assertThat(portfolio.getReturnPercentage()).isEqualTo(20.0);

        portfolio.setId(12L);
        portfolio.setCustomerId(33L);
        portfolio.setCreatedDate(LocalDateTime.of(2026, 4, 4, 8, 0));
        portfolio.setInvestments(List.of());
        portfolio.setTotalInvestment(new BigDecimal("2500"));
        portfolio.setCurrentValue(new BigDecimal("2800"));
        portfolio.setProfitLoss(new BigDecimal("300"));
        portfolio.setReturnPercentage(12.0);

        assertThat(portfolio.getId()).isEqualTo(12L);
        assertThat(portfolio.getCustomerId()).isEqualTo(33L);
        assertThat(portfolio.getCreatedDate()).isEqualTo(LocalDateTime.of(2026, 4, 4, 8, 0));
        assertThat(portfolio.getInvestments()).isEmpty();
        assertThat(portfolio.getTotalInvestment()).isEqualByComparingTo("2500");
        assertThat(portfolio.getCurrentValue()).isEqualByComparingTo("2800");
        assertThat(portfolio.getProfitLoss()).isEqualByComparingTo("300");
        assertThat(portfolio.getReturnPercentage()).isEqualTo(12.0);

        PortfolioPerformanceDTO performance = new PortfolioPerformanceDTO(3L, "User",
                new BigDecimal("500"), new BigDecimal("650"), new BigDecimal("150"), 30.0);

        assertThat(performance.getCustomerId()).isEqualTo(3L);
        assertThat(performance.getCustomerName()).isEqualTo("User");
        assertThat(performance.getTotalInvestment()).isEqualByComparingTo("500");
        assertThat(performance.getCurrentValue()).isEqualByComparingTo("650");
        assertThat(performance.getProfitLoss()).isEqualByComparingTo("150");
        assertThat(performance.getReturnPercentage()).isEqualTo(30.0);

        performance.setCustomerId(5L);
        performance.setCustomerName("Updated User");
        performance.setTotalInvestment(new BigDecimal("900"));
        performance.setCurrentValue(new BigDecimal("990"));
        performance.setProfitLoss(new BigDecimal("90"));
        performance.setReturnPercentage(10.0);

        assertThat(performance.getCustomerId()).isEqualTo(5L);
        assertThat(performance.getCustomerName()).isEqualTo("Updated User");
        assertThat(performance.getTotalInvestment()).isEqualByComparingTo("900");
        assertThat(performance.getCurrentValue()).isEqualByComparingTo("990");
        assertThat(performance.getProfitLoss()).isEqualByComparingTo("90");
        assertThat(performance.getReturnPercentage()).isEqualTo(10.0);
    }

    @Test
    void dashboardAllocationAndPointDtos_constructorsAndAccessors() {
        DashboardResponseDTO responseEmpty = new DashboardResponseDTO();
        DashboardSummaryDTO summaryEmpty = new DashboardSummaryDTO();
        AllocationDTO allocationEmpty = new AllocationDTO();
        PerformancePointDTO pointEmpty = new PerformancePointDTO();
        assertThat(responseEmpty).isNotNull();
        assertThat(summaryEmpty).isNotNull();
        assertThat(allocationEmpty).isNotNull();
        assertThat(pointEmpty).isNotNull();

        DashboardSummaryDTO summary = new DashboardSummaryDTO(5L, new BigDecimal("10000"),
                new BigDecimal("12000"), new BigDecimal("2000"), 20.0);
        AllocationDTO allocation = new AllocationDTO("Stock", 60.5, new BigDecimal("7260"));
        PerformancePointDTO point = new PerformancePointDTO("Jan", new BigDecimal("12000"));

        DashboardResponseDTO response = new DashboardResponseDTO(summary, List.of(allocation), List.of(point));

        assertThat(response.getSummary().getTotalCustomers()).isEqualTo(5L);
        assertThat(response.getSummary().getReturnPercentage()).isEqualTo(20.0);
        assertThat(response.getAllocation()).hasSize(1);
        assertThat(response.getAllocation().get(0).getAssetType()).isEqualTo("Stock");
        assertThat(response.getPerformanceTrend().get(0).getMonth()).isEqualTo("Jan");
        assertThat(response.getPerformanceTrend().get(0).getValue()).isEqualByComparingTo("12000");

        summary.setTotalCustomers(7L);
        summary.setTotalAssetsManaged(new BigDecimal("14000"));
        summary.setPortfolioValue(new BigDecimal("15000"));
        summary.setOverallProfitLoss(new BigDecimal("1000"));
        summary.setReturnPercentage(7.14);

        allocation.setAssetType("Bond");
        allocation.setPercentage(39.5);
        allocation.setValue(new BigDecimal("5925"));

        point.setMonth("Feb");
        point.setValue(new BigDecimal("12500"));

        response.setSummary(summary);
        response.setAllocation(List.of(allocation));
        response.setPerformanceTrend(List.of(point));

        assertThat(response.getSummary().getTotalCustomers()).isEqualTo(7L);
        assertThat(response.getSummary().getTotalAssetsManaged()).isEqualByComparingTo("14000");
        assertThat(response.getSummary().getPortfolioValue()).isEqualByComparingTo("15000");
        assertThat(response.getSummary().getOverallProfitLoss()).isEqualByComparingTo("1000");
        assertThat(response.getSummary().getReturnPercentage()).isEqualTo(7.14);
        assertThat(response.getAllocation().get(0).getAssetType()).isEqualTo("Bond");
        assertThat(response.getAllocation().get(0).getPercentage()).isEqualTo(39.5);
        assertThat(response.getAllocation().get(0).getValue()).isEqualByComparingTo("5925");
        assertThat(response.getPerformanceTrend().get(0).getMonth()).isEqualTo("Feb");
        assertThat(response.getPerformanceTrend().get(0).getValue()).isEqualByComparingTo("12500");
    }

    @Test
    void suggestionDto_constructorsAndAccessors() {
        SuggestionDTO empty = new SuggestionDTO();
        assertThat(empty).isNotNull();

        SuggestionDTO dto = new SuggestionDTO("SUG-1", 1L, "Risk", "High", "Reduce stock concentration");

        assertThat(dto.getId()).isEqualTo("SUG-1");
        assertThat(dto.getCustomerId()).isEqualTo(1L);
        assertThat(dto.getType()).isEqualTo("Risk");
        assertThat(dto.getSeverity()).isEqualTo("High");
        assertThat(dto.getMessage()).isEqualTo("Reduce stock concentration");

        dto.setId("SUG-2");
        dto.setCustomerId(2L);
        dto.setType("Opportunity");
        dto.setSeverity("Medium");
        dto.setMessage("Rebalance periodically");

        assertThat(dto.getId()).isEqualTo("SUG-2");
        assertThat(dto.getCustomerId()).isEqualTo(2L);
        assertThat(dto.getType()).isEqualTo("Opportunity");
        assertThat(dto.getSeverity()).isEqualTo("Medium");
        assertThat(dto.getMessage()).isEqualTo("Rebalance periodically");
    }
}

