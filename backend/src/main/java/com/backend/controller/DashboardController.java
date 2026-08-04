package com.backend.controller;

import com.backend.dto.AllocationDTO;
import com.backend.dto.DashboardResponseDTO;
import com.backend.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Portfolio dashboard and allocation APIs")
public class DashboardController {

    private final PortfolioService portfolioService;

    public DashboardController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary", description = "Returns aggregate portfolio metrics for the dashboard")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard summary fetched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DashboardResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public DashboardResponseDTO getSummary() {
        return portfolioService.getDashboardSummary();
    }

    @GetMapping("/allocation")
    @Operation(summary = "Get asset allocation", description = "Returns current asset allocation distribution")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Allocation fetched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AllocationDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public List<AllocationDTO> getAllocation() {
        return portfolioService.getAllocation();
    }
}
