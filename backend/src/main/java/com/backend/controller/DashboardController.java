package com.backend.controller;

import com.backend.dto.AllocationDTO;
import com.backend.dto.DashboardResponseDTO;
import com.backend.service.PortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final PortfolioService portfolioService;

    public DashboardController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/summary")
    public DashboardResponseDTO getSummary() {
        return portfolioService.getDashboardSummary();
    }

    @GetMapping("/allocation")
    public List<AllocationDTO> getAllocation() {
        return portfolioService.getAllocation();
    }
}
