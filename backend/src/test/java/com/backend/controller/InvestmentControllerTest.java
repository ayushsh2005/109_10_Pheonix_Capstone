package com.backend.controller;

import com.backend.dto.InvestmentRequestDTO;
import com.backend.dto.InvestmentResponseDTO;
import com.backend.exception.InvestmentNotFoundException;
import com.backend.exception.PortfolioNotFoundException;
import com.backend.service.InvestmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvestmentController.class)
class InvestmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvestmentService investmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private InvestmentResponseDTO investmentResponse;
    private InvestmentRequestDTO investmentRequest;

    @BeforeEach
    void setUp() {
        investmentResponse = new InvestmentResponseDTO(
                100L, 10L, 1L,
                "Apple Inc", "Stock", "AAPL",
                new BigDecimal("10"), new BigDecimal("170.00"),
                new BigDecimal("185.00"), LocalDate.of(2026, 1, 1)
        );

        investmentRequest = new InvestmentRequestDTO();
        investmentRequest.setAssetName("Apple Inc");
        investmentRequest.setAssetType("Stock");
        investmentRequest.setTicker("AAPL");
        investmentRequest.setQuantity(new BigDecimal("10"));
        investmentRequest.setPurchasePrice(new BigDecimal("170.00"));
        investmentRequest.setCurrentPrice(new BigDecimal("185.00"));
        investmentRequest.setPurchaseDate(LocalDate.of(2026, 1, 1));
    }

    // ── POST /customers/{id}/investments ─────────────────────────────────────

    @Test
    void addInvestment_validJson_returns201() throws Exception {
        when(investmentService.addInvestment(eq(1L), any())).thenReturn(investmentResponse);

        mockMvc.perform(post("/customers/1/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.assetName").value("Apple Inc"))
                .andExpect(jsonPath("$.ticker").value("AAPL"))
                .andExpect(jsonPath("$.customerId").value(1));
    }

    @Test
    void addInvestment_missingAssetName_returns400() throws Exception {
        investmentRequest.setAssetName("");

        mockMvc.perform(post("/customers/1/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investmentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addInvestment_missingAssetType_returns400() throws Exception {
        investmentRequest.setAssetType("");

        mockMvc.perform(post("/customers/1/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investmentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addInvestment_zeroQuantity_returns400() throws Exception {
        investmentRequest.setQuantity(BigDecimal.ZERO);

        mockMvc.perform(post("/customers/1/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investmentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addInvestment_portfolioNotFound_returns404() throws Exception {
        when(investmentService.addInvestment(eq(99L), any()))
                .thenThrow(new PortfolioNotFoundException("Portfolio not found for customer id: 99"));

        mockMvc.perform(post("/customers/99/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investmentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void addInvestment_textPlainWithJsonBody_returns201() throws Exception {
        when(investmentService.addInvestment(eq(1L), any())).thenReturn(investmentResponse);

        String jsonBody = objectMapper.writeValueAsString(investmentRequest);

        mockMvc.perform(post("/customers/1/investments")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assetName").value("Apple Inc"));
    }

    @Test
    void addInvestment_textPlainInvalidJson_returns400() throws Exception {
        mockMvc.perform(post("/customers/1/investments")
                        .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                        .content("this is not json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addInvestment_unsupportedMediaType_returns415() throws Exception {
        mockMvc.perform(post("/customers/1/investments")
                        .contentType(org.springframework.http.MediaType.APPLICATION_XML)
                        .content("<investment/>"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // ── GET /customers/{id}/investments ──────────────────────────────────────

    @Test
    void getInvestmentsByCustomer_returns200WithList() throws Exception {
        when(investmentService.getInvestmentsByCustomer(1L)).thenReturn(List.of(investmentResponse));

        mockMvc.perform(get("/customers/1/investments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].assetName").value("Apple Inc"));
    }

    @Test
    void getInvestmentsByCustomer_noInvestments_returns200WithEmptyList() throws Exception {
        when(investmentService.getInvestmentsByCustomer(1L)).thenReturn(List.of());

        mockMvc.perform(get("/customers/1/investments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /investments ──────────────────────────────────────────────────────

    @Test
    void getAllInvestments_returns200WithList() throws Exception {
        when(investmentService.getAllInvestments()).thenReturn(List.of(investmentResponse));

        mockMvc.perform(get("/investments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ── PUT /investments/{id} ─────────────────────────────────────────────────

    @Test
    void updateInvestment_validJson_returns200() throws Exception {
        investmentResponse = new InvestmentResponseDTO(
                100L, 10L, 1L,
                "Microsoft", "Stock", "MSFT",
                new BigDecimal("10"), new BigDecimal("300.00"),
                new BigDecimal("320.00"), LocalDate.of(2026, 1, 1)
        );
        investmentRequest.setAssetName("Microsoft");
        investmentRequest.setTicker("MSFT");
        investmentRequest.setPurchasePrice(new BigDecimal("300.00"));
        investmentRequest.setCurrentPrice(new BigDecimal("320.00"));

        when(investmentService.updateInvestment(eq(100L), any())).thenReturn(investmentResponse);

        mockMvc.perform(put("/investments/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetName").value("Microsoft"))
                .andExpect(jsonPath("$.ticker").value("MSFT"));
    }

    @Test
    void updateInvestment_nonExistingId_returns404() throws Exception {
        when(investmentService.updateInvestment(eq(999L), any()))
                .thenThrow(new InvestmentNotFoundException(999L));

        mockMvc.perform(put("/investments/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investmentRequest)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /investments/{id} ──────────────────────────────────────────────

    @Test
    void deleteInvestment_existingId_returns204() throws Exception {
        doNothing().when(investmentService).deleteInvestment(100L);

        mockMvc.perform(delete("/investments/100"))
                .andExpect(status().isNoContent());

        verify(investmentService).deleteInvestment(100L);
    }

    @Test
    void deleteInvestment_nonExistingId_returns404() throws Exception {
        doThrow(new InvestmentNotFoundException(999L)).when(investmentService).deleteInvestment(999L);

        mockMvc.perform(delete("/investments/999"))
                .andExpect(status().isNotFound());
    }
}




