package com.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PortfolioDTO {

    private Long id;
    private Long customerId;
    private LocalDateTime createdDate;
    private List<InvestmentResponseDTO> investments;
    private BigDecimal totalInvestment;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private double returnPercentage;

    public PortfolioDTO() {
    }

    public PortfolioDTO(Long id, Long customerId, LocalDateTime createdDate, List<InvestmentResponseDTO> investments,
                         BigDecimal totalInvestment, BigDecimal currentValue, BigDecimal profitLoss,
                         double returnPercentage) {
        this.id = id;
        this.customerId = customerId;
        this.createdDate = createdDate;
        this.investments = investments;
        this.totalInvestment = totalInvestment;
        this.currentValue = currentValue;
        this.profitLoss = profitLoss;
        this.returnPercentage = returnPercentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public List<InvestmentResponseDTO> getInvestments() {
        return investments;
    }

    public void setInvestments(List<InvestmentResponseDTO> investments) {
        this.investments = investments;
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
