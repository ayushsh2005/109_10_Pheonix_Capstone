package com.backend.dto;

import java.math.BigDecimal;

public class PerformancePointDTO {

    private String month;
    private BigDecimal value;

    public PerformancePointDTO() {
    }

    public PerformancePointDTO(String month, BigDecimal value) {
        this.month = month;
        this.value = value;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
