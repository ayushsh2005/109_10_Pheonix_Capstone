package com.backend.controller;

import com.backend.dto.InvestmentRequestDTO;
import com.backend.dto.InvestmentResponseDTO;
import com.backend.service.InvestmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import tools.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Investments", description = "Investment management APIs")
public class InvestmentController {

    private final InvestmentService investmentService;
    private final ObjectMapper objectMapper;

    public InvestmentController(InvestmentService investmentService, ObjectMapper objectMapper) {
        this.investmentService = investmentService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/investments")
        @Operation(summary = "List all investments", description = "Returns investments across all customers")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Investments fetched",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = InvestmentResponseDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
    public List<InvestmentResponseDTO> getAllInvestments() {
        return investmentService.getAllInvestments();
    }

    @GetMapping("/customers/{customerId}/investments")
        @Operation(summary = "List customer investments", description = "Returns all investments for a specific customer")
        @Parameter(name = "customerId", description = "Customer ID", required = true)
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Investments fetched",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = InvestmentResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
        })
    public List<InvestmentResponseDTO> getInvestmentsByCustomer(@PathVariable Long customerId) {
        return investmentService.getInvestmentsByCustomer(customerId);
    }

    @PostMapping(value = "/customers/{customerId}/investments", consumes = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Add investment", description = "Adds an investment to a customer portfolio using application/json payload")
        @Parameter(name = "customerId", description = "Customer ID", required = true)
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Investment details",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InvestmentRequestDTO.class))
        )
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Investment created",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvestmentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
        })
    public ResponseEntity<InvestmentResponseDTO> addInvestment(@PathVariable Long customerId,
                                                                @Valid @RequestBody InvestmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(investmentService.addInvestment(customerId, request));
    }

    @PostMapping(value = "/customers/{customerId}/investments", consumes = MediaType.TEXT_PLAIN_VALUE)
        @Operation(summary = "Add investment (text/plain)",
            description = "Adds an investment when JSON is sent with text/plain content type")
        @Parameter(name = "customerId", description = "Customer ID", required = true)
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Raw JSON payload as text/plain",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(type = "string"))
        )
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Investment created",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvestmentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
        })
    public ResponseEntity<InvestmentResponseDTO> addInvestmentFromText(@PathVariable Long customerId,
                                                                        @RequestBody String requestBody) {
        InvestmentRequestDTO request = parseRequest(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(investmentService.addInvestment(customerId, request));
    }

    @PutMapping(value = "/investments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Update investment", description = "Updates an existing investment by ID using application/json payload")
        @Parameter(name = "id", description = "Investment ID", required = true)
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Updated investment details",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = InvestmentRequestDTO.class))
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Investment updated",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvestmentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "Investment not found", content = @Content)
        })
    public InvestmentResponseDTO updateInvestment(@PathVariable Long id,
                                                   @Valid @RequestBody InvestmentRequestDTO request) {
        return investmentService.updateInvestment(id, request);
    }

    @PutMapping(value = "/investments/{id}", consumes = MediaType.TEXT_PLAIN_VALUE)
        @Operation(summary = "Update investment (text/plain)",
            description = "Updates an investment when JSON is sent with text/plain content type")
        @Parameter(name = "id", description = "Investment ID", required = true)
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Raw JSON payload as text/plain",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(type = "string"))
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Investment updated",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvestmentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "Investment not found", content = @Content)
        })
    public InvestmentResponseDTO updateInvestmentFromText(@PathVariable Long id, @RequestBody String requestBody) {
        InvestmentRequestDTO request = parseRequest(requestBody);
        return investmentService.updateInvestment(id, request);
    }

    @DeleteMapping("/investments/{id}")
        @Operation(summary = "Delete investment", description = "Deletes an investment by ID")
        @Parameter(name = "id", description = "Investment ID", required = true)
        @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Investment deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Investment not found", content = @Content)
        })
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }

    private InvestmentRequestDTO parseRequest(String requestBody) {
        try {
            // Accept clients that send JSON payloads with text/plain content type.
            return objectMapper.readValue(requestBody, InvestmentRequestDTO.class);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid request body. Expected JSON payload.");
        }
    }
}
