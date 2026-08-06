package com.backend.controller;

import com.backend.dto.MarketPriceDTO;
import com.backend.service.MarketPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@Tag(name = "Market", description = "Live market price APIs (Yahoo Finance)")
public class MarketController {

    private final MarketPriceService marketPriceService;

    public MarketController(MarketPriceService marketPriceService) {
        this.marketPriceService = marketPriceService;
    }

    @GetMapping("/market/{ticker}")
    @Operation(summary = "Get current market price", description = "Fetches the latest market price for a ticker from Yahoo Finance")
    @Parameter(name = "ticker", description = "Asset ticker, e.g. RELIANCE", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Price fetched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MarketPriceDTO.class))),
            @ApiResponse(responseCode = "503", description = "Market data temporarily unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MarketPriceDTO.class)))
    })
    public ResponseEntity<MarketPriceDTO> getPrice(@PathVariable String ticker) {
        Optional<BigDecimal> price = marketPriceService.getLatestPrice(ticker);
        if (price.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new MarketPriceDTO(ticker.toUpperCase(), null, false,
                            "Market data temporarily unavailable for " + ticker));
        }
        return ResponseEntity.ok(new MarketPriceDTO(ticker.toUpperCase(), price.get(), true, null));
    }
}
