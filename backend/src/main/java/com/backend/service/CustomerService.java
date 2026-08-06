package com.backend.service;

import com.backend.dto.CustomerRequestDTO;
import com.backend.dto.CustomerResponseDTO;
import com.backend.entity.Customer;
import com.backend.entity.Investment;
import com.backend.entity.Portfolio;
import com.backend.exception.CustomerNotFoundException;
import com.backend.repository.CustomerRepository;
import com.backend.repository.InvestmentRepository;
import com.backend.repository.PortfolioRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PortfolioRepository portfolioRepository;
    private final InvestmentRepository investmentRepository;
    private final ObjectMapper objectMapper;

    public CustomerService(CustomerRepository customerRepository, PortfolioRepository portfolioRepository,
                            InvestmentRepository investmentRepository, ObjectMapper objectMapper) {
        this.customerRepository = customerRepository;
        this.portfolioRepository = portfolioRepository;
        this.investmentRepository = investmentRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return toResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use: " + request.getEmail());
        }

        Customer customer = new Customer();
        applyRequest(customer, request);
        customer.setStatus("Active");

        Customer saved = customerRepository.save(customer);

        Portfolio portfolio = new Portfolio();
        portfolio.setCustomer(saved);
        portfolioRepository.save(portfolio);

        return toResponseDTO(saved);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if (!customer.getEmail().equalsIgnoreCase(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use: " + request.getEmail());
        }

        applyRequest(customer, request);
        return toResponseDTO(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponseDTO archiveCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.updateStatus(id, "Archived");
        customer.setStatus("Archived");
        return toResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO restoreCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.updateStatus(id, "Active");
        customer.setStatus("Active");
        return toResponseDTO(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.delete(customer);
    }

    private void applyRequest(Customer customer, CustomerRequestDTO request) {
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setRiskProfile(request.getRiskProfile());
        customer.setInvestmentGoal(request.getInvestmentGoal());
        customer.setNotes(request.getNotes());
        customer.setTargetAllocation(serialiseAllocation(request.getTargetAllocation()));
    }

    private String serialiseAllocation(Map<String, Object> map) {
        if (map == null || map.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> deserialiseAllocation(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    CustomerResponseDTO toResponseDTO(Customer customer) {
        List<Investment> investments = investmentRepository.findByPortfolioCustomerId(customer.getId());

        BigDecimal totalInvestment = investments.stream()
                .map(i -> i.getQuantity().multiply(i.getPurchasePrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentValue = investments.stream()
                .map(i -> i.getQuantity().multiply(i.getCurrentPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profitLoss = currentValue.subtract(totalInvestment);

        double returnPercentage = totalInvestment.compareTo(BigDecimal.ZERO) > 0
                ? profitLoss.multiply(BigDecimal.valueOf(100))
                        .divide(totalInvestment, 4, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        String status = customer.getStatus() != null ? customer.getStatus() : "Active";

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getRiskProfile(),
                customer.getInvestmentGoal(),
                customer.getCreatedDate(),
                status,
                currentValue,
                totalInvestment,
                currentValue,
                profitLoss,
                returnPercentage,
                customer.getNotes(),
                deserialiseAllocation(customer.getTargetAllocation())
        );
    }
}

