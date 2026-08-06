package com.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

public class CustomerRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;

    @Size(max = 30, message = "Risk profile must be at most 30 characters")
    private String riskProfile;

    @Size(max = 255, message = "Investment goal must be at most 255 characters")
    private String investmentGoal;

    @Size(max = 1000, message = "Notes must be at most 1000 characters")
    private String notes;

    private Map<String, Object> targetAllocation;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRiskProfile() { return riskProfile; }
    public void setRiskProfile(String riskProfile) { this.riskProfile = riskProfile; }

    public String getInvestmentGoal() { return investmentGoal; }
    public void setInvestmentGoal(String investmentGoal) { this.investmentGoal = investmentGoal; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Map<String, Object> getTargetAllocation() { return targetAllocation; }
    public void setTargetAllocation(Map<String, Object> targetAllocation) { this.targetAllocation = targetAllocation; }
}
