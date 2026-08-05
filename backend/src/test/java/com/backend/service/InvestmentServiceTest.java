package com.backend.service;

import com.backend.dto.InvestmentRequestDTO;
import com.backend.dto.InvestmentResponseDTO;
import com.backend.entity.Customer;
import com.backend.entity.Investment;
import com.backend.entity.Portfolio;
import com.backend.exception.InvestmentNotFoundException;
import com.backend.exception.PortfolioNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private InvestmentService investmentService;

    private Investment investment;
    private Portfolio portfolio;
    private Customer customer;
    private InvestmentRequestDTO request;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");

        portfolio = new Portfolio();
        portfolio.setId(10L);
        portfolio.setCustomer(customer);

        investment = new Investment();
        investment.setId(100L);
        investment.setPortfolio(portfolio);
        investment.setAssetName("Apple Inc");
        investment.setAssetType("Stock");
        investment.setTicker("AAPL");
        investment.setQuantity(new BigDecimal("10"));
        investment.setPurchasePrice(new BigDecimal("170.00"));
        investment.setCurrentPrice(new BigDecimal("185.00"));
        investment.setPurchaseDate(LocalDate.of(2026, 1, 1));

        request = new InvestmentRequestDTO();
        request.setAssetName("Apple Inc");
        request.setAssetType("Stock");
        request.setTicker("AAPL");
        request.setQuantity(new BigDecimal("10"));
        request.setPurchasePrice(new BigDecimal("170.00"));
        request.setCurrentPrice(new BigDecimal("185.00"));
        request.setPurchaseDate(LocalDate.of(2026, 1, 1));
    }

    // ── GET ALL ──────────────────────────────────────────────────────────────

    @Test
    void getAllInvestments_returnsAllInvestments() {
        when(investmentRepository.findAll()).thenReturn(List.of(investment));

        List<InvestmentResponseDTO> result = investmentService.getAllInvestments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAssetName()).isEqualTo("Apple Inc");
        assertThat(result.get(0).getTicker()).isEqualTo("AAPL");
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
        assertThat(result.get(0).getPortfolioId()).isEqualTo(10L);
    }

    @Test
    void getAllInvestments_emptyDatabase_returnsEmptyList() {
        when(investmentRepository.findAll()).thenReturn(List.of());

        assertThat(investmentService.getAllInvestments()).isEmpty();
    }

    // ── GET BY CUSTOMER ──────────────────────────────────────────────────────

    @Test
    void getInvestmentsByCustomer_returnsInvestmentsForThatCustomer() {
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(investment));

        List<InvestmentResponseDTO> result = investmentService.getInvestmentsByCustomer(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
    }

    @Test
    void getInvestmentsByCustomer_noInvestments_returnsEmptyList() {
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of());

        assertThat(investmentService.getInvestmentsByCustomer(1L)).isEmpty();
    }

    // ── ADD ──────────────────────────────────────────────────────────────────

    @Test
    void addInvestment_validRequest_returnsCreatedInvestment() {
        when(portfolioRepository.findByCustomerId(1L)).thenReturn(Optional.of(portfolio));
        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);

        InvestmentResponseDTO result = investmentService.addInvestment(1L, request);

        assertThat(result.getAssetName()).isEqualTo("Apple Inc");
        assertThat(result.getAssetType()).isEqualTo("Stock");
        assertThat(result.getQuantity()).isEqualByComparingTo(new BigDecimal("10"));
        verify(investmentRepository).save(any(Investment.class));
    }

    @Test
    void addInvestment_portfolioNotFound_throwsPortfolioNotFoundException() {
        when(portfolioRepository.findByCustomerId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> investmentService.addInvestment(99L, request))
                .isInstanceOf(PortfolioNotFoundException.class)
                .hasMessageContaining("99");

        verify(investmentRepository, never()).save(any());
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @Test
    void updateInvestment_validRequest_returnsUpdatedInvestment() {
        request.setAssetName("Microsoft");
        request.setTicker("MSFT");

        Investment updated = new Investment();
        updated.setId(100L);
        updated.setPortfolio(portfolio);
        updated.setAssetName("Microsoft");
        updated.setAssetType("Stock");
        updated.setTicker("MSFT");
        updated.setQuantity(new BigDecimal("10"));
        updated.setPurchasePrice(new BigDecimal("170.00"));
        updated.setCurrentPrice(new BigDecimal("185.00"));
        updated.setPurchaseDate(LocalDate.of(2026, 1, 1));

        when(investmentRepository.findById(100L)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any(Investment.class))).thenReturn(updated);

        InvestmentResponseDTO result = investmentService.updateInvestment(100L, request);

        assertThat(result.getAssetName()).isEqualTo("Microsoft");
        assertThat(result.getTicker()).isEqualTo("MSFT");
    }

    @Test
    void updateInvestment_nonExistingId_throwsInvestmentNotFoundException() {
        when(investmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> investmentService.updateInvestment(999L, request))
                .isInstanceOf(InvestmentNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Test
    void deleteInvestment_existingId_deletesInvestment() {
        when(investmentRepository.findById(100L)).thenReturn(Optional.of(investment));

        investmentService.deleteInvestment(100L);

        verify(investmentRepository).delete(investment);
    }

    @Test
    void deleteInvestment_nonExistingId_throwsInvestmentNotFoundException() {
        when(investmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> investmentService.deleteInvestment(999L))
                .isInstanceOf(InvestmentNotFoundException.class);

        verify(investmentRepository, never()).delete(any());
    }
}

