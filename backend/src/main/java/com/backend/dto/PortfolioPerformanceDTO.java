package com.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioPerformanceDTO {

    private Long customerId;
    private String customerName;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private double returnPercentage;
    private List<PerformancePointDTO> performanceSeries;

    public PortfolioPerformanceDTO() {}

    public PortfolioPerformanceDTO(Long customerId, String customerName, BigDecimal totalInvested,
                                    BigDecimal currentValue, BigDecimal profitLoss, double returnPercentage,
                                    List<PerformancePointDTO> performanceSeries) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalInvested = totalInvested;
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.returnPercentage = returnPercentage;
        this.performanceSeries = performanceSeries;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public BigDecimal getTotalInvested() { return totalInvested; }
    public void setTotalInvested(BigDecimal totalInvested) { this.totalInvested = totalInvested; }

    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }

    public BigDecimal getProfitLoss() { return profitLoss; }
    public void setProfitLoss(BigDecimal profitLoss) { this.profitLoss = profitLoss; }

    public double getReturnPercentage() { return returnPercentage; }
    public void setReturnPercentage(double returnPercentage) { this.returnPercentage = returnPercentage; }

    public List<PerformancePointDTO> getPerformanceSeries() { return performanceSeries; }
    public void setPerformanceSeries(List<PerformancePointDTO> performanceSeries) { this.performanceSeries = performanceSeries; }
}

