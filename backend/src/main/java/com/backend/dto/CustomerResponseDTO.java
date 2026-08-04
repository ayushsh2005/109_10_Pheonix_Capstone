package com.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String riskProfile;
    private String investmentGoal;
    private LocalDateTime joinedDate;
    private String status;
    private BigDecimal portfolioValue;

    public CustomerResponseDTO() {
    }

    public CustomerResponseDTO(Long id, String name, String email, String phone, String riskProfile,
                                String investmentGoal, LocalDateTime joinedDate, String status,
                                BigDecimal portfolioValue) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.riskProfile = riskProfile;
        this.investmentGoal = investmentGoal;
        this.joinedDate = joinedDate;
        this.status = status;
        this.portfolioValue = portfolioValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(String riskProfile) {
        this.riskProfile = riskProfile;
    }

    public String getInvestmentGoal() {
        return investmentGoal;
    }

    public void setInvestmentGoal(String investmentGoal) {
        this.investmentGoal = investmentGoal;
    }

    public LocalDateTime getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDateTime joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    public void setPortfolioValue(BigDecimal portfolioValue) {
        this.portfolioValue = portfolioValue;
    }
}
