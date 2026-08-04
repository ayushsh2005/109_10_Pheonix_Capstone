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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PortfolioRepository portfolioRepository;
    private final InvestmentRepository investmentRepository;

    public CustomerService(CustomerRepository customerRepository, PortfolioRepository portfolioRepository,
                            InvestmentRepository investmentRepository) {
        this.customerRepository = customerRepository;
        this.portfolioRepository = portfolioRepository;
        this.investmentRepository = investmentRepository;
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
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setRiskProfile(request.getRiskProfile());
        customer.setInvestmentGoal(request.getInvestmentGoal());

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

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setRiskProfile(request.getRiskProfile());
        customer.setInvestmentGoal(request.getInvestmentGoal());

        return toResponseDTO(customerRepository.save(customer));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.delete(customer);
    }

    private CustomerResponseDTO toResponseDTO(Customer customer) {
        List<Investment> investments = investmentRepository.findByPortfolioCustomerId(customer.getId());
        BigDecimal portfolioValue = investments.stream()
                .map(i -> i.getQuantity().multiply(i.getCurrentPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getRiskProfile(),
                customer.getInvestmentGoal(),
                customer.getCreatedDate(),
                "Active",
                portfolioValue
        );
    }
}
