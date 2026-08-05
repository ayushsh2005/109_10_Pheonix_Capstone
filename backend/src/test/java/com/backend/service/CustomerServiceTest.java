package com.backend.service;

import com.backend.dto.CustomerRequestDTO;
import com.backend.dto.CustomerResponseDTO;
import com.backend.entity.Customer;
import com.backend.entity.Investment;
import com.backend.entity.Portfolio;
import com.backend.exception.CustomerNotFoundException;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerRequestDTO request;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");
        customer.setEmail("alice@example.com");
        customer.setPhone("1234567890");
        customer.setRiskProfile("Moderate");
        customer.setInvestmentGoal("Retirement");

        request = new CustomerRequestDTO();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPhone("1234567890");
        request.setRiskProfile("Moderate");
        request.setInvestmentGoal("Retirement");
    }

    // ── GET ALL ──────────────────────────────────────────────────────────────

    @Test
    void getAllCustomers_returnsListOfCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of());

        List<CustomerResponseDTO> result = customerService.getAllCustomers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Alice");
        assertThat(result.get(0).getEmail()).isEqualTo("alice@example.com");
        assertThat(result.get(0).getPortfolioValue()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getAllCustomers_emptyDatabase_returnsEmptyList() {
        when(customerRepository.findAll()).thenReturn(List.of());

        List<CustomerResponseDTO> result = customerService.getAllCustomers();

        assertThat(result).isEmpty();
    }

    // ── GET BY ID ────────────────────────────────────────────────────────────

    @Test
    void getCustomerById_existingId_returnsCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of());

        CustomerResponseDTO result = customerService.getCustomerById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice");
        assertThat(result.getStatus()).isEqualTo("Active");
    }

    @Test
    void getCustomerById_nonExistingId_throwsCustomerNotFoundException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(99L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    @Test
    void createCustomer_validRequest_createsAndReturnsCustomer() {
        when(customerRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of());

        CustomerResponseDTO result = customerService.createCustomer(request);

        assertThat(result.getName()).isEqualTo("Alice");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    void createCustomer_duplicateEmail_throwsIllegalArgumentException() {
        when(customerRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("alice@example.com");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_withInvestments_calculatesPortfolioValue() {
        Investment inv = buildInvestment(BigDecimal.TEN, new BigDecimal("150.00"));

        when(customerRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of(inv));

        CustomerResponseDTO result = customerService.createCustomer(request);

        assertThat(result.getPortfolioValue()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @Test
    void updateCustomer_validRequest_updatesAndReturnsCustomer() {
        request.setName("Alice Updated");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of());

        CustomerResponseDTO result = customerService.updateCustomer(1L, request);

        assertThat(result.getName()).isEqualTo("Alice Updated");
    }

    @Test
    void updateCustomer_nonExistingId_throwsCustomerNotFoundException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(99L, request))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void updateCustomer_emailTakenByOtherCustomer_throwsIllegalArgumentException() {
        customer.setEmail("alice@example.com");
        request.setEmail("taken@example.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.updateCustomer(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("taken@example.com");
    }

    @Test
    void updateCustomer_sameEmail_doesNotCheckForDuplicate() {
        request.setEmail("alice@example.com"); // same email
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
        when(investmentRepository.findByPortfolioCustomerId(1L)).thenReturn(List.of());

        assertThatCode(() -> customerService.updateCustomer(1L, request))
                .doesNotThrowAnyException();

        verify(customerRepository, never()).existsByEmail(anyString());
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Test
    void deleteCustomer_existingId_deletesCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(1L);

        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomer_nonExistingId_throwsCustomerNotFoundException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.deleteCustomer(99L))
                .isInstanceOf(CustomerNotFoundException.class);

        verify(customerRepository, never()).delete(any());
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private Investment buildInvestment(BigDecimal quantity, BigDecimal currentPrice) {
        Investment inv = new Investment();
        inv.setQuantity(quantity);
        inv.setPurchasePrice(new BigDecimal("100.00"));
        inv.setCurrentPrice(currentPrice);
        inv.setPurchaseDate(LocalDate.of(2026, 1, 1));
        return inv;
    }
}

