package com.backend.service;

import com.backend.dto.AllocationDTO;
import com.backend.dto.DashboardResponseDTO;
import com.backend.dto.PortfolioDTO;
import com.backend.dto.PortfolioPerformanceDTO;
import com.backend.entity.Customer;
import com.backend.entity.Investment;
import com.backend.entity.Portfolio;
import com.backend.exception.CustomerNotFoundException;
import com.backend.exception.PortfolioNotFoundException;
import com.backend.repository.CustomerRepository;
import com.backend.repository.InvestmentRepository;
import com.backend.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private Customer customer;
    private Portfolio portfolio;
    private Investment investmentA;
    private Investment investmentB;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");

        portfolio = new Portfolio();
        portfolio.setId(10L);
        portfolio.setCustomer(customer);

        // 10 units bought at 100, currently worth 120 → value = 1200, cost = 1000
        investmentA = buildInvestment(portfolio, "Stock", "AAPL",
                new BigDecimal("10"), new BigDecimal("100.00"), new BigDecimal("120.00"),
                LocalDate.of(2026, 1, 1));

        // 5 units bought at 200, currently worth 180 → value = 900, cost = 1000
        investmentB = buildInvestment(portfolio, "Bond", "BOND1",
                new BigDecimal("5"), new BigDecimal("200.00"), new BigDecimal("180.00"),
                LocalDate.of(2026, 2, 1));
    }

    // ── GET PORTFOLIO BY CUSTOMER ────────────────────────────────────────────

    @Test
    void getPortfolioByCustomer_returnsCorrectTotals() {
        when(portfolioRepository.findByCustomerId(1L)).thenReturn(Optional.of(portfolio));
        when(investmentRepository.findByPortfolioId(10L)).thenReturn(List.of(investmentA, investmentB));

        PortfolioDTO result = portfolioService.getPortfolioByCustomer(1L);

        // totalInvestment = (10*100) + (5*200) = 2000
        assertThat(result.getTotalInvestment()).isEqualByComparingTo(new BigDecimal("2000.00"));
        // currentValue = (10*120) + (5*180) = 2100
        assertThat(result.getCurrentValue()).isEqualByComparingTo(new BigDecimal("2100.00"));
        // profitLoss = 2100 - 2000 = 100
        assertThat(result.getProfitLoss()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(result.getInvestments()).hasSize(2);
    }

    @Test
    void getPortfolioByCustomer_noInvestments_returnsZeroTotals() {
        when(portfolioRepository.findByCustomerId(1L)).thenReturn(Optional.of(portfolio));
        when(investmentRepository.findByPortfolioId(10L)).thenReturn(List.of());

        PortfolioDTO result = portfolioService.getPortfolioByCustomer(1L);

        assertThat(result.getTotalInvestment()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getCurrentValue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getProfitLoss()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getReturnPercentage()).isEqualTo(0.0);
    }

    @Test
    void getPortfolioByCustomer_portfolioNotFound_throwsPortfolioNotFoundException() {
        when(portfolioRepository.findByCustomerId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> portfolioService.getPortfolioByCustomer(99L))
                .isInstanceOf(PortfolioNotFoundException.class);
    }

    // ── GET PERFORMANCE ──────────────────────────────────────────────────────

    @Test
    void getPerformance_returnsCorrectReturnPercentage() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(investmentA, investmentB));

        PortfolioPerformanceDTO result = portfolioService.getPerformance(1L, "6M");

        // return% = (100 / 2000) * 100 = 5.0%
        assertThat(result.getReturnPercentage()).isEqualTo(5.0);
        assertThat(result.getCustomerName()).isEqualTo("Alice");
        assertThat(result.getProfitLoss()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void getPerformance_customerNotFound_throwsCustomerNotFoundException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> portfolioService.getPerformance(99L, "6M"))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void getPerformance_noInvestments_returnsZeroReturnPercentage() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of());

        PortfolioPerformanceDTO result = portfolioService.getPerformance(1L, "1M");

        assertThat(result.getReturnPercentage()).isEqualTo(0.0);
        assertThat(result.getProfitLoss()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ── GET DASHBOARD SUMMARY ────────────────────────────────────────────────

    @Test
    void getDashboardSummary_returnsAggregatedDataWithAllocation() {
        when(investmentRepository.findAll()).thenReturn(List.of(investmentA, investmentB));
        when(customerRepository.count()).thenReturn(2L);

        DashboardResponseDTO result = portfolioService.getDashboardSummary();

        assertThat(result.getSummary().getTotalCustomers()).isEqualTo(2L);
        assertThat(result.getSummary().getTotalAssetsManaged()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(result.getSummary().getPortfolioValue()).isEqualByComparingTo(new BigDecimal("2100.00"));
        assertThat(result.getAllocation()).isNotEmpty();
        assertThat(result.getPerformanceTrend()).hasSize(6); // 6 months
    }

    @Test
    void getDashboardSummary_noData_returnsZerosAndEmptyAllocation() {
        when(investmentRepository.findAll()).thenReturn(List.of());
        when(customerRepository.count()).thenReturn(0L);

        DashboardResponseDTO result = portfolioService.getDashboardSummary();

        assertThat(result.getSummary().getTotalCustomers()).isEqualTo(0L);
        assertThat(result.getSummary().getTotalAssetsManaged()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getAllocation()).isEmpty();
    }

    // ── GET ALLOCATION ───────────────────────────────────────────────────────

    @Test
    void getAllocation_groupsCorrectlyByAssetType() {
        when(investmentRepository.findAll()).thenReturn(List.of(investmentA, investmentB));

        List<AllocationDTO> result = portfolioService.getAllocation();

        // Stock = 1200, Bond = 900 → sorted descending by value
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAssetType()).isEqualTo("Stock");
        // Stock % = 1200/2100 * 100 ≈ 57.14%
        assertThat(result.get(0).getPercentage()).isGreaterThan(50.0);
    }

    @Test
    void getAllocation_singleAssetType_returns100Percent() {
        when(investmentRepository.findAll()).thenReturn(List.of(investmentA));

        List<AllocationDTO> result = portfolioService.getAllocation();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPercentage()).isEqualTo(100.0);
    }

    // ── HELPER ───────────────────────────────────────────────────────────────

    private Investment buildInvestment(Portfolio portfolio, String assetType, String ticker,
                                       BigDecimal quantity, BigDecimal purchasePrice,
                                       BigDecimal currentPrice, LocalDate purchaseDate) {
        Investment inv = new Investment();
        inv.setPortfolio(portfolio);
        inv.setAssetName(ticker + " Asset");
        inv.setAssetType(assetType);
        inv.setTicker(ticker);
        inv.setQuantity(quantity);
        inv.setPurchasePrice(purchasePrice);
        inv.setCurrentPrice(currentPrice);
        inv.setPurchaseDate(purchaseDate);
        return inv;
    }
}

