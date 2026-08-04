package com.backend.dto;

import java.math.BigDecimal;

public class DashboardSummaryDTO {

    private long totalCustomers;
    private BigDecimal totalAssetsManaged;
    private BigDecimal portfolioValue;
    private BigDecimal overallProfitLoss;
    private double returnPercentage;

    public DashboardSummaryDTO() {
    }

    public DashboardSummaryDTO(long totalCustomers, BigDecimal totalAssetsManaged, BigDecimal portfolioValue,
                                BigDecimal overallProfitLoss, double returnPercentage) {
        this.totalCustomers = totalCustomers;
        this.totalAssetsManaged = totalAssetsManaged;
        this.portfolioValue = portfolioValue;
        this.overallProfitLoss = overallProfitLoss;
        this.returnPercentage = returnPercentage;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public BigDecimal getTotalAssetsManaged() {
        return totalAssetsManaged;
    }

    public void setTotalAssetsManaged(BigDecimal totalAssetsManaged) {
        this.totalAssetsManaged = totalAssetsManaged;
    }

    public BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    public void setPortfolioValue(BigDecimal portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public BigDecimal getOverallProfitLoss() {
        return overallProfitLoss;
    }

    public void setOverallProfitLoss(BigDecimal overallProfitLoss) {
        this.overallProfitLoss = overallProfitLoss;
    }

    public double getReturnPercentage() {
        return returnPercentage;
    }

    public void setReturnPercentage(double returnPercentage) {
        this.returnPercentage = returnPercentage;
    }
}
