package com.backend.dto;

import java.util.List;

public class DashboardResponseDTO {

    private DashboardSummaryDTO summary;
    private List<AllocationDTO> allocation;
    private List<PerformancePointDTO> performanceTrend;

    public DashboardResponseDTO() {
    }

    public DashboardResponseDTO(DashboardSummaryDTO summary, List<AllocationDTO> allocation,
                                 List<PerformancePointDTO> performanceTrend) {
        this.summary = summary;
        this.allocation = allocation;
        this.performanceTrend = performanceTrend;
    }

    public DashboardSummaryDTO getSummary() {
        return summary;
    }

    public void setSummary(DashboardSummaryDTO summary) {
        this.summary = summary;
    }

    public List<AllocationDTO> getAllocation() {
        return allocation;
    }

    public void setAllocation(List<AllocationDTO> allocation) {
        this.allocation = allocation;
    }

    public List<PerformancePointDTO> getPerformanceTrend() {
        return performanceTrend;
    }

    public void setPerformanceTrend(List<PerformancePointDTO> performanceTrend) {
        this.performanceTrend = performanceTrend;
    }
}
