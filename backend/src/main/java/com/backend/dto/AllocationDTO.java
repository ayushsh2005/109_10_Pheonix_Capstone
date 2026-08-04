package com.backend.dto;

import java.math.BigDecimal;

public class AllocationDTO {

    private String assetType;
    private double percentage;
    private BigDecimal value;

    public AllocationDTO() {
    }

    public AllocationDTO(String assetType, double percentage, BigDecimal value) {
        this.assetType = assetType;
        this.percentage = percentage;
        this.value = value;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
