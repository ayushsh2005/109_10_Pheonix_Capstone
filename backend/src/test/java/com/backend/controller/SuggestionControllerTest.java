package com.backend.controller;

import com.backend.dto.SuggestionDTO;
import com.backend.exception.CustomerNotFoundException;
import com.backend.service.SuggestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SuggestionController.class)
class SuggestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SuggestionService suggestionService;

    // ── GET /suggestions ─────────────────────────────────────────────────────

    @Test
    void getAllSuggestions_returns200WithList() throws Exception {
        SuggestionDTO dto = new SuggestionDTO(
                "SUG-1-1", 1L, "Diversification", "High",
                "Portfolio has high exposure to Stock (80%). Consider diversification."
        );

        when(suggestionService.getAllSuggestions()).thenReturn(List.of(dto));

        mockMvc.perform(get("/suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("SUG-1-1"))
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].type").value("Diversification"))
                .andExpect(jsonPath("$[0].severity").value("High"));
    }

    @Test
    void getAllSuggestions_noSuggestions_returns200WithEmptyList() throws Exception {
        when(suggestionService.getAllSuggestions()).thenReturn(List.of());

        mockMvc.perform(get("/suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /customers/{id}/suggestions ──────────────────────────────────────

    @Test
    void getSuggestionsByCustomer_returns200WithList() throws Exception {
        SuggestionDTO risk = new SuggestionDTO(
                "SUG-1-1", 1L, "Risk", "High",
                "Current stock allocation (85%) exceeds the recommended level."
        );
        SuggestionDTO opp = new SuggestionDTO(
                "SUG-1-2", 1L, "Opportunity", "Low",
                "Apple Inc is performing strongly (+25%). Review concentration risk."
        );

        when(suggestionService.getSuggestionsByCustomer(1L)).thenReturn(List.of(risk, opp));

        mockMvc.perform(get("/customers/1/suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("Risk"))
                .andExpect(jsonPath("$[1].type").value("Opportunity"))
                .andExpect(jsonPath("$[1].message").value(org.hamcrest.Matchers.containsString("Apple Inc")));
    }

    @Test
    void getSuggestionsByCustomer_noSuggestions_returns200WithEmptyList() throws Exception {
        when(suggestionService.getSuggestionsByCustomer(1L)).thenReturn(List.of());

        mockMvc.perform(get("/customers/1/suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getSuggestionsByCustomer_notFound_returns404() throws Exception {
        when(suggestionService.getSuggestionsByCustomer(99L))
                .thenThrow(new CustomerNotFoundException(99L));

        mockMvc.perform(get("/customers/99/suggestions"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}

