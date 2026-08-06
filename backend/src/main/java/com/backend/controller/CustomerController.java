package com.backend.controller;

import com.backend.dto.CustomerRequestDTO;
import com.backend.dto.CustomerResponseDTO;
import com.backend.dto.PortfolioDTO;
import com.backend.dto.PortfolioPerformanceDTO;
import com.backend.service.CustomerService;
import com.backend.service.PortfolioService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "Customer and portfolio management APIs")
public class CustomerController {

    private final CustomerService customerService;
    private final PortfolioService portfolioService;
    private final ObjectMapper objectMapper;

    public CustomerController(CustomerService customerService, PortfolioService portfolioService,
                              ObjectMapper objectMapper) {
        this.customerService = customerService;
        this.portfolioService = portfolioService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Create customer", description = "Creates a new customer using an application/json payload")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Customer details",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CustomerRequestDTO.class))
        )
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "409", description = "Customer email already exists", content = @Content)
        })
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
        @Operation(summary = "Create customer (text/plain)",
            description = "Creates a new customer when JSON is sent with text/plain content type")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Raw JSON payload as text/plain",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(type = "string"))
        )
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "409", description = "Customer email already exists", content = @Content)
        })
    public ResponseEntity<CustomerResponseDTO> createCustomerFromText(@RequestBody String requestBody) {
        CustomerRequestDTO request = parseRequest(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }

    @GetMapping
        @Operation(summary = "List customers", description = "Returns all customers")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customers fetched",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = @ArraySchema(schema = @Schema(implementation = CustomerResponseDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
    public List<CustomerResponseDTO> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
        @Operation(summary = "Get customer", description = "Returns a single customer by ID")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer fetched",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
        })
    public CustomerResponseDTO getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Update customer", description = "Updates an existing customer by ID with application/json payload")
        @Parameter(name = "id", description = "Customer ID", required = true)
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Updated customer details",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CustomerRequestDTO.class))
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Customer email already exists", content = @Content)
        })
    public CustomerResponseDTO updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequestDTO request) {
        return customerService.updateCustomer(id, request);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.TEXT_PLAIN_VALUE)
        @Operation(summary = "Update customer (text/plain)",
            description = "Updates a customer when JSON is sent with text/plain content type")
        @Parameter(name = "id", description = "Customer ID", required = true)
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Raw JSON payload as text/plain",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(type = "string"))
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Customer email already exists", content = @Content)
        })
    public CustomerResponseDTO updateCustomerFromText(@PathVariable Long id, @RequestBody String requestBody) {
        CustomerRequestDTO request = parseRequest(requestBody);
        return customerService.updateCustomer(id, request);
    }

    @DeleteMapping("/{id}")
        @Operation(summary = "Delete customer", description = "Deletes a customer and associated portfolio data")
        @Parameter(name = "id", description = "Customer ID", required = true)
        @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
        })
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/portfolio")
        @Operation(summary = "Get customer portfolio", description = "Returns portfolio details for a given customer")
        @Parameter(name = "id", description = "Customer ID", required = true)
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portfolio fetched",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PortfolioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer or portfolio not found", content = @Content)
        })
    public PortfolioDTO getPortfolio(@PathVariable Long id) {
        return portfolioService.getPortfolioByCustomer(id);
    }

    @GetMapping("/{id}/performance")
        @Operation(summary = "Get customer performance", description = "Returns portfolio performance metrics for a customer")
        @Parameter(name = "id", description = "Customer ID", required = true)
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Performance fetched",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PortfolioPerformanceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer or portfolio not found", content = @Content)
        })
    public PortfolioPerformanceDTO getPerformance(@PathVariable Long id) {
        return portfolioService.getPerformance(id);
    }

    private CustomerRequestDTO parseRequest(String requestBody) {
        try {
            // Accept clients that send JSON payloads with text/plain content type.
            return objectMapper.readValue(requestBody, CustomerRequestDTO.class);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid request body. Expected JSON payload.");
        }
    }
}
