package com.backend.controller;

import com.backend.dto.SellRequestDTO;
import com.backend.dto.TradeDTO;
import com.backend.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Trades", description = "Trade history and sell investment APIs")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/customers/{customerId}/trades")
        @Operation(summary = "List customer trades", description = "Returns all trade records for a customer")
        @Parameter(name = "customerId", description = "Customer ID", required = true)
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trades fetched",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = TradeDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
        })
    public List<TradeDTO> getTrades(@PathVariable Long customerId) {
        return tradeService.getTradesByCustomer(customerId);
    }

    @PostMapping(value = "/investments/{investmentId}/sell", consumes = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Sell investment", description = "Records a sell trade and reduces the investment holding")
        @Parameter(name = "investmentId", description = "Investment ID", required = true)
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Sell details: quantity, sellPrice, tradeDate",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SellRequestDTO.class))
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trade recorded",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TradeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient quantity", content = @Content),
            @ApiResponse(responseCode = "404", description = "Investment not found", content = @Content)
        })
    public ResponseEntity<TradeDTO> sellInvestment(@PathVariable Long investmentId,
                                                    @Valid @RequestBody SellRequestDTO request) {
        return ResponseEntity.ok(tradeService.sellInvestment(investmentId, request));
    }
}
