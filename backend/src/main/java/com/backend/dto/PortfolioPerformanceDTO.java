package com.backend.dto;

import java.math.BigDecimal;

public class PortfolioPerformanceDTO {

    private Long customerId;
    private String customerName;
    private BigDecimal totalInvestment;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private double returnPercentage;

    public PortfolioPerformanceDTO() {
    }

    public PortfolioPerformanceDTO(Long customerId, String customerName, BigDecimal totalInvestment,
                                    BigDecimal currentValue, BigDecimal profitLoss, double returnPercentage) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalInvestment = totalInvestment;
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.returnPercentage = returnPercentage;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTotalInvestment() {
        return totalInvestment;
    }

    public void setTotalInvestment(BigDecimal totalInvestment) {
        this.totalInvestment = totalInvestment;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }

    public double getReturnPercentage() {
        return returnPercentage;
    }

    public void setReturnPercentage(double returnPercentage) {
        this.returnPercentage = returnPercentage;
    }
}
