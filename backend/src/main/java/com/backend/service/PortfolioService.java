package com.backend.service;

import com.backend.dto.AllocationDTO;
import com.backend.dto.DashboardResponseDTO;
import com.backend.dto.DashboardSummaryDTO;
import com.backend.dto.InvestmentResponseDTO;
import com.backend.dto.PerformancePointDTO;
import com.backend.dto.PortfolioDTO;
import com.backend.dto.PortfolioPerformanceDTO;
import com.backend.entity.Customer;
import com.backend.entity.Investment;
import com.backend.entity.Portfolio;
import com.backend.exception.CustomerNotFoundException;
import com.backend.exception.PortfolioNotFoundException;
import com.backend.repository.CustomerRepository;
import com.backend.repository.InvestmentRepository;
import com.backend.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final CustomerRepository customerRepository;
    private final PortfolioRepository portfolioRepository;
    private final InvestmentRepository investmentRepository;

    public PortfolioService(CustomerRepository customerRepository, PortfolioRepository portfolioRepository,
                             InvestmentRepository investmentRepository) {
        this.customerRepository = customerRepository;
        this.portfolioRepository = portfolioRepository;
        this.investmentRepository = investmentRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioDTO getPortfolioByCustomer(Long customerId) {
        Portfolio portfolio = portfolioRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found for customer id: " + customerId));

        List<Investment> investments = investmentRepository.findByPortfolioId(portfolio.getId());
        BigDecimal totalInvestment = totalInvestment(investments);
        BigDecimal currentValue = currentValue(investments);
        BigDecimal profitLoss = currentValue.subtract(totalInvestment);
        double returnPercentage = returnPercentage(totalInvestment, profitLoss);

        List<InvestmentResponseDTO> investmentDTOs = investments.stream()
                .map(i -> new InvestmentResponseDTO(i.getId(), portfolio.getId(), customerId, i.getAssetName(),
                        i.getAssetType(), i.getTicker(), i.getQuantity(), i.getPurchasePrice(), i.getCurrentPrice(),
                        i.getPurchaseDate()))
                .collect(Collectors.toList());

        return new PortfolioDTO(portfolio.getId(), customerId, portfolio.getCreatedDate(), investmentDTOs,
                totalInvestment, currentValue, profitLoss, returnPercentage);
    }

    @Transactional(readOnly = true)
    public PortfolioPerformanceDTO getPerformance(Long customerId, String range) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        List<Investment> investments = investmentRepository.findByPortfolioCustomerId(customerId);
        BigDecimal totalInvested = totalInvestment(investments);
        BigDecimal currentValue = currentValue(investments);
        BigDecimal profitLoss = currentValue.subtract(totalInvested);
        double returnPercentage = returnPercentage(totalInvested, profitLoss);

        List<PerformancePointDTO> series = buildCustomerPerformanceSeries(investments, range);

        return new PortfolioPerformanceDTO(customerId, customer.getName(), totalInvested, currentValue,
                profitLoss, returnPercentage, series);
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO getDashboardSummary() {
        List<Investment> allInvestments = investmentRepository.findAll();

        BigDecimal totalAssetsManaged = totalInvestment(allInvestments);
        BigDecimal portfolioValue = currentValue(allInvestments);
        BigDecimal overallProfitLoss = portfolioValue.subtract(totalAssetsManaged);
        double returnPercentage = returnPercentage(totalAssetsManaged, overallProfitLoss);

        DashboardSummaryDTO summary = new DashboardSummaryDTO(customerRepository.count(), totalAssetsManaged,
                portfolioValue, overallProfitLoss, returnPercentage);

        return new DashboardResponseDTO(summary, buildAllocation(allInvestments), buildPerformanceTrend(allInvestments));
    }

    @Transactional(readOnly = true)
    public List<AllocationDTO> getAllocation() {
        return buildAllocation(investmentRepository.findAll());
    }

    private List<AllocationDTO> buildAllocation(List<Investment> investments) {
        BigDecimal totalValue = currentValue(investments);

        Map<String, BigDecimal> valueByType = investments.stream()
                .collect(Collectors.groupingBy(Investment::getAssetType, LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO,
                                i -> i.getQuantity().multiply(i.getCurrentPrice()), BigDecimal::add)));

        List<AllocationDTO> allocation = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : valueByType.entrySet()) {
            double percentage = totalValue.compareTo(BigDecimal.ZERO) > 0
                    ? entry.getValue().multiply(BigDecimal.valueOf(100))
                            .divide(totalValue, 2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;
            allocation.add(new AllocationDTO(entry.getKey(), percentage, entry.getValue()));
        }

        allocation.sort(Comparator.comparing(AllocationDTO::getValue).reversed());
        return allocation;
    }

    private List<PerformancePointDTO> buildCustomerPerformanceSeries(List<Investment> investments, String range) {
        int months;
        switch (range == null ? "6M" : range.toUpperCase()) {
            case "1M":  months = 1;  break;
            case "3M":  months = 3;  break;
            case "1Y":  months = 12; break;
            case "ALL": months = 24; break;
            default:    months = 6;  break;
        }

        List<PerformancePointDTO> series = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = months - 1; i >= 0; i--) {
            LocalDate monthEnd = today.minusMonths(i);
            BigDecimal value = investments.stream()
                    .filter(inv -> inv.getPurchaseDate() != null && !inv.getPurchaseDate().isAfter(monthEnd))
                    .map(inv -> inv.getQuantity().multiply(inv.getCurrentPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String label = monthEnd.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            series.add(new PerformancePointDTO(label, value));
        }

        return series;
    }

    private List<PerformancePointDTO> buildPerformanceTrend(List<Investment> investments) {
        List<PerformancePointDTO> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            LocalDate monthEnd = today.minusMonths(i);
            BigDecimal value = investments.stream()
                    .filter(inv -> inv.getPurchaseDate() != null && !inv.getPurchaseDate().isAfter(monthEnd))
                    .map(inv -> inv.getQuantity().multiply(inv.getCurrentPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String label = monthEnd.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            trend.add(new PerformancePointDTO(label, value));
        }

        return trend;
    }

    private BigDecimal totalInvestment(List<Investment> investments) {
        return investments.stream()
                .map(i -> i.getQuantity().multiply(i.getPurchasePrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal currentValue(List<Investment> investments) {
        return investments.stream()
                .map(i -> i.getQuantity().multiply(i.getCurrentPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private double returnPercentage(BigDecimal totalInvestment, BigDecimal profitLoss) {
                if (totalInvestment == null || totalInvestment.compareTo(BigDecimal.ZERO) <= 0) {
                        return 0.0;
                }
                if (profitLoss == null) {
            return 0.0;
        }
        return profitLoss.multiply(BigDecimal.valueOf(100))
                .divide(totalInvestment, 4, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
