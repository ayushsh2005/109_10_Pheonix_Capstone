package com.backend.service;

import com.backend.dto.SuggestionDTO;
import com.backend.entity.Customer;
import com.backend.entity.Investment;
import com.backend.entity.Portfolio;
import com.backend.repository.CustomerRepository;
import com.backend.repository.InvestmentRepository;
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
class SuggestionServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private InvestmentRepository investmentRepository;
    @InjectMocks private SuggestionService suggestionService;

    private Customer customer;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");
        customer.setRiskProfile("Moderate");

        portfolio = new Portfolio();
        portfolio.setId(10L);
        portfolio.setCustomer(customer);
    }

    // ── getAllSuggestions ────────────────────────────────────────────────────

    @Test
    void getAllSuggestions_noCustomers_returnsEmptyList() {
        when(customerRepository.findAll()).thenReturn(List.of());

        assertThat(suggestionService.getAllSuggestions()).isEmpty();
    }

    @Test
    void getAllSuggestions_customerWithNoInvestments_returnsEmptyList() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of());

        assertThat(suggestionService.getAllSuggestions()).isEmpty();
    }

    @Test
    void getAllSuggestions_customerWithInvestments_returnsSuggestions() {
        // 80% stock exposure → Diversification High + Risk High for Moderate profile
        Investment inv = buildInvestment("Stock", new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("100"));
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(inv));

        List<SuggestionDTO> result = suggestionService.getAllSuggestions();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
    }

    // ── getSuggestionsByCustomer ─────────────────────────────────────────────

    @Test
    void getSuggestionsByCustomer_nonExistingCustomer_returnsEmptyList() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(suggestionService.getSuggestionsByCustomer(99L)).isEmpty();
    }

    @Test
    void getSuggestionsByCustomer_customerWithNoInvestments_returnsEmptyList() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of());

        assertThat(suggestionService.getSuggestionsByCustomer(1L)).isEmpty();
    }

    // ── buildSuggestions branch coverage ────────────────────────────────────

    @Test
    void buildSuggestions_totalValueZero_returnsEmptyList() {
        // quantity * currentPrice = 0
        Investment inv = buildInvestment("Stock", BigDecimal.ZERO, new BigDecimal("100"), new BigDecimal("0"));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(inv));

        assertThat(suggestionService.getSuggestionsByCustomer(1L)).isEmpty();
    }

    @Test
    void buildSuggestions_stockOver70Percent_returnsDiversificationHighSuggestion() {
        // 75% stock (>= 70) → "High" severity diversification
        Investment stock = buildInvestment("Stock", new BigDecimal("75"), new BigDecimal("100"), new BigDecimal("100"));
        Investment bond  = buildInvestment("Bond",  new BigDecimal("25"), new BigDecimal("100"), new BigDecimal("100"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(stock, bond));

        List<SuggestionDTO> result = suggestionService.getSuggestionsByCustomer(1L);

        SuggestionDTO diversification = result.stream()
                .filter(s -> "Diversification".equals(s.getType())).findFirst().orElseThrow();
        assertThat(diversification.getSeverity()).isEqualTo("High");
        assertThat(diversification.getMessage()).contains("Stock");
        assertThat(diversification.getId()).startsWith("SUG-1-");
    }

    @Test
    void buildSuggestions_stockBetween50And70_returnsDiversificationMediumSuggestion() {
        // 60% stock (>= 50 but < 70) → "Medium" severity
        Investment stock = buildInvestment("Stock", new BigDecimal("60"), new BigDecimal("100"), new BigDecimal("100"));
        Investment bond  = buildInvestment("Bond",  new BigDecimal("40"), new BigDecimal("100"), new BigDecimal("100"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(stock, bond));

        List<SuggestionDTO> result = suggestionService.getSuggestionsByCustomer(1L);

        SuggestionDTO diversification = result.stream()
                .filter(s -> "Diversification".equals(s.getType())).findFirst().orElseThrow();
        assertThat(diversification.getSeverity()).isEqualTo("Medium");
    }

    @Test
    void buildSuggestions_stockExceedsConservativeThreshold_returnsRiskSuggestion() {
        // Conservative threshold = 40%; stock at 80% → Risk suggestion
        customer.setRiskProfile("Conservative");
        Investment stock = buildInvestment("Stock", new BigDecimal("80"), new BigDecimal("100"), new BigDecimal("100"));
        Investment bond  = buildInvestment("Bond",  new BigDecimal("20"), new BigDecimal("100"), new BigDecimal("100"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(stock, bond));

        List<SuggestionDTO> result = suggestionService.getSuggestionsByCustomer(1L);

        assertThat(result.stream().anyMatch(s -> "Risk".equals(s.getType()))).isTrue();
        SuggestionDTO risk = result.stream().filter(s -> "Risk".equals(s.getType())).findFirst().orElseThrow();
        assertThat(risk.getSeverity()).isEqualTo("High");
        assertThat(risk.getMessage()).contains("Conservative");
    }

    @Test
    void buildSuggestions_moderateProfileStockAtThreshold_noRiskSuggestion() {
        // Moderate threshold = 70%; stock at 60% → no Risk suggestion
        Investment stock = buildInvestment("Stock", new BigDecimal("60"), new BigDecimal("100"), new BigDecimal("100"));
        Investment bond  = buildInvestment("Bond",  new BigDecimal("40"), new BigDecimal("100"), new BigDecimal("100"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(stock, bond));

        List<SuggestionDTO> result = suggestionService.getSuggestionsByCustomer(1L);

        assertThat(result.stream().anyMatch(s -> "Risk".equals(s.getType()))).isFalse();
    }

    @Test
    void buildSuggestions_investmentReturn20Percent_returnsOpportunitySuggestion() {
        // buyAt=100, currentPrice=125 → return = 25% >= 20 → Opportunity
        Investment inv = new Investment();
        inv.setAssetName("Apple Inc");
        inv.setAssetType("Stock");
        inv.setTicker("AAPL");
        inv.setQuantity(new BigDecimal("10"));
        inv.setPurchasePrice(new BigDecimal("100"));
        inv.setCurrentPrice(new BigDecimal("125"));
        inv.setPurchaseDate(LocalDate.of(2026, 1, 1));
        inv.setPortfolio(portfolio);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(inv));

        List<SuggestionDTO> result = suggestionService.getSuggestionsByCustomer(1L);

        assertThat(result.stream().anyMatch(s -> "Opportunity".equals(s.getType()))).isTrue();
        SuggestionDTO opp = result.stream().filter(s -> "Opportunity".equals(s.getType())).findFirst().orElseThrow();
        assertThat(opp.getSeverity()).isEqualTo("Low");
        assertThat(opp.getMessage()).contains("Apple Inc");
    }

    @Test
    void buildSuggestions_investmentReturnBelow20Percent_noOpportunitySuggestion() {
        // return = 10% < 20 → no Opportunity
        Investment inv = buildInvestment("Stock", new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("110"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(inv));

        List<SuggestionDTO> result = suggestionService.getSuggestionsByCustomer(1L);

        assertThat(result.stream().anyMatch(s -> "Opportunity".equals(s.getType()))).isFalse();
    }

    @Test
    void buildSuggestions_aggressiveProfile_noRiskSuggestionForHighStock() {
        // Aggressive threshold = 100%, stock at 80% → no Risk suggestion
        customer.setRiskProfile("Aggressive");
        Investment stock = buildInvestment("Stock", new BigDecimal("80"), new BigDecimal("100"), new BigDecimal("100"));
        Investment bond  = buildInvestment("Bond",  new BigDecimal("20"), new BigDecimal("100"), new BigDecimal("100"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(stock, bond));

        List<SuggestionDTO> result = suggestionService.getSuggestionsByCustomer(1L);

        assertThat(result.stream().anyMatch(s -> "Risk".equals(s.getType()))).isFalse();
    }

    @Test
    void buildSuggestions_zeroCostInvestment_skipsOpportunityCheck() {
        // quantity = 0 → cost = 0 → skip opportunity
        Investment inv = buildInvestment("Stock", BigDecimal.ZERO, new BigDecimal("100"), new BigDecimal("150"));
        // Add a normal investment to make totalValue > 0
        Investment inv2 = buildInvestment("Bond", new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("100"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(inv, inv2));

        // Should not throw, and no Opportunity from zero-cost investment
        List<SuggestionDTO> result = suggestionService.getSuggestionsByCustomer(1L);
        assertThat(result.stream().anyMatch(s -> "Opportunity".equals(s.getType()))).isFalse();
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Investment buildInvestment(String assetType, BigDecimal quantity,
                                       BigDecimal purchasePrice, BigDecimal currentPrice) {
        Investment inv = new Investment();
        inv.setAssetName(assetType + " Asset");
        inv.setAssetType(assetType);
        inv.setTicker(assetType.substring(0, 2).toUpperCase());
        inv.setQuantity(quantity);
        inv.setPurchasePrice(purchasePrice);
        inv.setCurrentPrice(currentPrice);
        inv.setPurchaseDate(LocalDate.of(2026, 1, 1));
        inv.setPortfolio(portfolio);
        return inv;
    }
}

