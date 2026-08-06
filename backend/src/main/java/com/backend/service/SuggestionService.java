package com.backend.service;

import com.backend.dto.SuggestionDTO;
import com.backend.entity.Customer;
import com.backend.entity.Investment;
import com.backend.exception.CustomerNotFoundException;
import com.backend.repository.CustomerRepository;
import com.backend.repository.InvestmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    @Value("${suggestions.risk-thresholds.conservative:40.0}")
    private double conservativeThreshold = 40.0;

    @Value("${suggestions.risk-thresholds.moderate:70.0}")
    private double moderateThreshold = 70.0;

    @Value("${suggestions.risk-thresholds.aggressive:100.0}")
    private double aggressiveThreshold = 100.0;

    private final CustomerRepository customerRepository;
    private final InvestmentRepository investmentRepository;

    public SuggestionService(CustomerRepository customerRepository, InvestmentRepository investmentRepository) {
        this.customerRepository = customerRepository;
        this.investmentRepository = investmentRepository;
    }

    @Transactional(readOnly = true)
    public List<SuggestionDTO> getAllSuggestions() {
        List<SuggestionDTO> suggestions = new ArrayList<>();
        for (Customer customer : customerRepository.findAll()) {
            suggestions.addAll(buildSuggestions(customer));
        }
        return suggestions;
    }

    @Transactional(readOnly = true)
    public List<SuggestionDTO> getSuggestionsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
        return buildSuggestions(customer);
    }

    private List<SuggestionDTO> buildSuggestions(Customer customer) {
        List<Investment> investments = investmentRepository.findByPortfolioCustomerId(customer.getId());
        List<SuggestionDTO> suggestions = new ArrayList<>();
        if (investments.isEmpty()) {
            return suggestions;
        }

        BigDecimal totalValue = investments.stream()
                .map(i -> i.getQuantity().multiply(i.getCurrentPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalValue.compareTo(BigDecimal.ZERO) <= 0) {
            return suggestions;
        }

        Map<String, BigDecimal> valueByType = investments.stream()
                .collect(Collectors.groupingBy(Investment::getAssetType, LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO,
                                i -> i.getQuantity().multiply(i.getCurrentPrice()), BigDecimal::add)));

        int counter = 1;

        for (Map.Entry<String, BigDecimal> entry : valueByType.entrySet()) {
            double percentage = entry.getValue().multiply(BigDecimal.valueOf(100))
                    .divide(totalValue, 2, RoundingMode.HALF_UP).doubleValue();

            if (percentage >= 50) {
                String severity = percentage >= 70 ? "High" : "Medium";
                suggestions.add(new SuggestionDTO("SUG-" + customer.getId() + "-" + counter++, customer.getId(),
                        "Diversification", severity,
                        "Portfolio has high exposure to " + entry.getKey() + " (" + Math.round(percentage)
                                + "%). Consider diversification."));
            }
        }

        double stockPercentage = valueByType.getOrDefault("Stock", BigDecimal.ZERO)
                .multiply(BigDecimal.valueOf(100))
                .divide(totalValue, 2, RoundingMode.HALF_UP).doubleValue();

        double riskThreshold = getRiskThreshold(customer.getRiskProfile());
        if (stockPercentage > riskThreshold) {
            suggestions.add(new SuggestionDTO("SUG-" + customer.getId() + "-" + counter++, customer.getId(),
                    "Risk", "High",
                    "Current stock allocation (" + Math.round(stockPercentage)
                            + "%) exceeds the recommended level for a " + customer.getRiskProfile()
                            + " risk profile. Consider reducing high-risk assets."));
        }

        for (Investment investment : investments) {
            BigDecimal cost = investment.getQuantity().multiply(investment.getPurchasePrice());
            if (cost.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal value = investment.getQuantity().multiply(investment.getCurrentPrice());
            double returnPct = value.subtract(cost).multiply(BigDecimal.valueOf(100))
                    .divide(cost, 2, RoundingMode.HALF_UP).doubleValue();

            if (returnPct >= 20) {
                suggestions.add(new SuggestionDTO("SUG-" + customer.getId() + "-" + counter++, customer.getId(),
                        "Opportunity", "Low",
                        investment.getAssetName() + " is performing strongly (+" + Math.round(returnPct)
                                + "%). Review concentration risk before increasing allocation."));
            }
        }

        return suggestions;
    }

    private double getRiskThreshold(String riskProfile) {
        return switch (riskProfile) {
            case "Conservative" -> conservativeThreshold;
            case "Moderate" -> moderateThreshold;
            case "Aggressive" -> aggressiveThreshold;
            default -> 100.0;
        };
    }
}
