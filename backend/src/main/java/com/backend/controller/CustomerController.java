package com.backend.controller;

import com.backend.dto.CustomerRequestDTO;
import com.backend.dto.CustomerResponseDTO;
import com.backend.dto.PortfolioDTO;
import com.backend.dto.PortfolioPerformanceDTO;
import com.backend.service.CustomerService;
import com.backend.service.PortfolioService;
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
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<CustomerResponseDTO> createCustomerFromText(@RequestBody String requestBody) {
        CustomerRequestDTO request = parseRequest(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }

    @GetMapping
    public List<CustomerResponseDTO> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CustomerResponseDTO updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequestDTO request) {
        return customerService.updateCustomer(id, request);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public CustomerResponseDTO updateCustomerFromText(@PathVariable Long id, @RequestBody String requestBody) {
        CustomerRequestDTO request = parseRequest(requestBody);
        return customerService.updateCustomer(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/portfolio")
    public PortfolioDTO getPortfolio(@PathVariable Long id) {
        return portfolioService.getPortfolioByCustomer(id);
    }

    @GetMapping("/{id}/performance")
    public PortfolioPerformanceDTO getPerformance(@PathVariable Long id) {
        return portfolioService.getPerformance(id);
    }

    private CustomerRequestDTO parseRequest(String requestBody) {
        try {
            // Accept clients that send JSON payloads with text/plain content type.
            return objectMapper.readValue(requestBody, CustomerRequestDTO.class);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid request body. Expected JSON payload.");
        }
    }
}
