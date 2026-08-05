package com.backend.controller;

import com.backend.dto.AllocationDTO;
import com.backend.dto.DashboardResponseDTO;
import com.backend.dto.DashboardSummaryDTO;
import com.backend.dto.PerformancePointDTO;
import com.backend.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PortfolioService portfolioService;

    // ── GET /dashboard/summary ───────────────────────────────────────────────

    @Test
    void getSummary_returns200WithDashboardData() throws Exception {
        DashboardSummaryDTO summary = new DashboardSummaryDTO(
                3L,
                new BigDecimal("10000.00"),
                new BigDecimal("11500.00"),
                new BigDecimal("1500.00"),
                15.0
        );
        AllocationDTO allocation = new AllocationDTO("Stock", 70.0, new BigDecimal("8050.00"));
        PerformancePointDTO point = new PerformancePointDTO("Jan", new BigDecimal("10000.00"));
        DashboardResponseDTO dashboard = new DashboardResponseDTO(summary, List.of(allocation), List.of(point));

        when(portfolioService.getDashboardSummary()).thenReturn(dashboard);

        mockMvc.perform(get("/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.totalCustomers").value(3))
                .andExpect(jsonPath("$.summary.totalAssetsManaged").value(10000.00))
                .andExpect(jsonPath("$.summary.portfolioValue").value(11500.00))
                .andExpect(jsonPath("$.summary.overallProfitLoss").value(1500.00))
                .andExpect(jsonPath("$.summary.returnPercentage").value(15.0))
                .andExpect(jsonPath("$.allocation[0].assetType").value("Stock"))
                .andExpect(jsonPath("$.allocation[0].percentage").value(70.0))
                .andExpect(jsonPath("$.performanceTrend[0].month").value("Jan"));
    }

    @Test
    void getSummary_noData_returns200WithZeros() throws Exception {
        DashboardSummaryDTO summary = new DashboardSummaryDTO(0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0.0);
        DashboardResponseDTO dashboard = new DashboardResponseDTO(summary, List.of(), List.of());

        when(portfolioService.getDashboardSummary()).thenReturn(dashboard);

        mockMvc.perform(get("/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.totalCustomers").value(0))
                .andExpect(jsonPath("$.allocation.length()").value(0))
                .andExpect(jsonPath("$.performanceTrend.length()").value(0));
    }

    // ── GET /dashboard/allocation ────────────────────────────────────────────

    @Test
    void getAllocation_returns200WithList() throws Exception {
        AllocationDTO stock = new AllocationDTO("Stock", 65.0, new BigDecimal("6500.00"));
        AllocationDTO bond  = new AllocationDTO("Bond",  35.0, new BigDecimal("3500.00"));

        when(portfolioService.getAllocation()).thenReturn(List.of(stock, bond));

        mockMvc.perform(get("/dashboard/allocation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].assetType").value("Stock"))
                .andExpect(jsonPath("$[0].percentage").value(65.0))
                .andExpect(jsonPath("$[1].assetType").value("Bond"))
                .andExpect(jsonPath("$[1].percentage").value(35.0));
    }

    @Test
    void getAllocation_noInvestments_returns200WithEmptyList() throws Exception {
        when(portfolioService.getAllocation()).thenReturn(List.of());

        mockMvc.perform(get("/dashboard/allocation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}

