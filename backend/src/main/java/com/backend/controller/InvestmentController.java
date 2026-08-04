package com.backend.controller;

import com.backend.dto.InvestmentRequestDTO;
import com.backend.dto.InvestmentResponseDTO;
import com.backend.service.InvestmentService;

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
public class InvestmentController {

    private final InvestmentService investmentService;
    private final ObjectMapper objectMapper;

    public InvestmentController(InvestmentService investmentService, ObjectMapper objectMapper) {
        this.investmentService = investmentService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/investments")
    public List<InvestmentResponseDTO> getAllInvestments() {
        return investmentService.getAllInvestments();
    }

    @GetMapping("/customers/{customerId}/investments")
    public List<InvestmentResponseDTO> getInvestmentsByCustomer(@PathVariable Long customerId) {
        return investmentService.getInvestmentsByCustomer(customerId);
    }

    @PostMapping(value = "/customers/{customerId}/investments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvestmentResponseDTO> addInvestment(@PathVariable Long customerId,
                                                                @Valid @RequestBody InvestmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(investmentService.addInvestment(customerId, request));
    }

    @PostMapping(value = "/customers/{customerId}/investments", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<InvestmentResponseDTO> addInvestmentFromText(@PathVariable Long customerId,
                                                                        @RequestBody String requestBody) {
        InvestmentRequestDTO request = parseRequest(requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(investmentService.addInvestment(customerId, request));
    }

    @PutMapping(value = "/investments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public InvestmentResponseDTO updateInvestment(@PathVariable Long id,
                                                   @Valid @RequestBody InvestmentRequestDTO request) {
        return investmentService.updateInvestment(id, request);
    }

    @PutMapping(value = "/investments/{id}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public InvestmentResponseDTO updateInvestmentFromText(@PathVariable Long id, @RequestBody String requestBody) {
        InvestmentRequestDTO request = parseRequest(requestBody);
        return investmentService.updateInvestment(id, request);
    }

    @DeleteMapping("/investments/{id}")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }

    private InvestmentRequestDTO parseRequest(String requestBody) {
        try {
            // Accept clients that send JSON payloads with text/plain content type.
            return objectMapper.readValue(requestBody, InvestmentRequestDTO.class);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid request body. Expected JSON payload.");
        }
    }
}
