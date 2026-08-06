package com.backend.controller;

import com.backend.dto.CustomerRequestDTO;
import com.backend.dto.CustomerResponseDTO;
import com.backend.dto.PortfolioDTO;
import com.backend.dto.PortfolioPerformanceDTO;
import com.backend.exception.CustomerNotFoundException;
import com.backend.service.CustomerService;
import com.backend.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private PortfolioService portfolioService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerResponseDTO customerResponse;
    private CustomerRequestDTO customerRequest;

    @BeforeEach
    void setUp() {
        customerResponse = new CustomerResponseDTO(
                1L, "Alice", "alice@example.com", "1234567890",
                "Moderate", "Retirement", LocalDateTime.now(), "Active", BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0.0, null, null
        );

        customerRequest = new CustomerRequestDTO();
        customerRequest.setName("Alice");
        customerRequest.setEmail("alice@example.com");
        customerRequest.setPhone("1234567890");
        customerRequest.setRiskProfile("Moderate");
        customerRequest.setInvestmentGoal("Retirement");
    }

    // ── POST /customers ───────────────────────────────────────────────────────

    @Test
    void createCustomer_validJson_returns201WithBody() throws Exception {
        when(customerService.createCustomer(any())).thenReturn(customerResponse);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.status").value("Active"));
    }

    @Test
    void createCustomer_missingName_returns400() throws Exception {
        customerRequest.setName("");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCustomer_invalidEmail_returns400() throws Exception {
        customerRequest.setEmail("not-an-email");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCustomer_textPlainWithJsonBody_returns201() throws Exception {
        when(customerService.createCustomer(any())).thenReturn(customerResponse);

        String jsonBody = objectMapper.writeValueAsString(customerRequest);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void createCustomer_duplicateEmail_returns400() throws Exception {
        when(customerService.createCustomer(any()))
                .thenThrow(new IllegalArgumentException("Email is already in use: alice@example.com"));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("alice@example.com")));
    }

    @Test
    void createCustomer_textPlainInvalidJson_returns400() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                        .content("this is not json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCustomer_unsupportedMediaType_returns415() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(org.springframework.http.MediaType.APPLICATION_XML)
                        .content("<customer/>"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // ── GET /customers ────────────────────────────────────────────────────────

    @Test
    void getCustomers_returns200WithList() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of(customerResponse));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    void getCustomers_noCustomers_returns200WithEmptyList() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /customers/{id} ───────────────────────────────────────────────────

    @Test
    void getCustomerById_existingId_returns200() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(customerResponse);

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void getCustomerById_nonExistingId_returns404() throws Exception {
        when(customerService.getCustomerById(99L))
                .thenThrow(new CustomerNotFoundException(99L));

        mockMvc.perform(get("/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ── PUT /customers/{id} ───────────────────────────────────────────────────

    @Test
    void updateCustomer_validJson_returns200() throws Exception {
        customerResponse = new CustomerResponseDTO(
                1L, "Alice Updated", "alice@example.com", "1234567890",
                "Moderate", "Retirement", LocalDateTime.now(), "Active", BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0.0, null, null
        );
        when(customerService.updateCustomer(eq(1L), any())).thenReturn(customerResponse);

        customerRequest.setName("Alice Updated");

        mockMvc.perform(put("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"));
    }

    @Test
    void updateCustomer_nonExistingId_returns404() throws Exception {
        when(customerService.updateCustomer(eq(99L), any()))
                .thenThrow(new CustomerNotFoundException(99L));

        mockMvc.perform(put("/customers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /customers/{id} ────────────────────────────────────────────────

    @Test
    void deleteCustomer_existingId_returns204() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer(1L);
    }

    @Test
    void deleteCustomer_nonExistingId_returns404() throws Exception {
        doThrow(new CustomerNotFoundException(99L)).when(customerService).deleteCustomer(99L);

        mockMvc.perform(delete("/customers/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /customers/{id}/portfolio ─────────────────────────────────────────

    @Test
    void getPortfolio_existingCustomer_returns200() throws Exception {
        PortfolioDTO portfolio = new PortfolioDTO(
                10L, 1L, LocalDateTime.now(), List.of(),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0.0
        );
        when(portfolioService.getPortfolioByCustomer(1L)).thenReturn(portfolio);

        mockMvc.perform(get("/customers/1/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.customerId").value(1));
    }

    // ── GET /customers/{id}/performance ──────────────────────────────────────

    @Test
    void getPerformance_existingCustomer_returns200() throws Exception {
        PortfolioPerformanceDTO perf = new PortfolioPerformanceDTO(
                1L, "Alice", new BigDecimal("2000.00"), new BigDecimal("2100.00"),
                new BigDecimal("100.00"), 5.0, List.of()
        );
        when(portfolioService.getPerformance(eq(1L), anyString())).thenReturn(perf);

        mockMvc.perform(get("/customers/1/performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Alice"))
                .andExpect(jsonPath("$.returnPercentage").value(5.0));
    }
}




