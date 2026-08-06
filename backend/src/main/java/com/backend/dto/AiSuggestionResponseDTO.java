package com.backend.dto;

import java.util.List;

/**
 * Response payload for GET /customers/{id}/ai-suggestions.
 * {@code source} is "AI" when Gemini produced the content, or "RULE_BASED"
 * when Gemini was unavailable/unconfigured and the rule-based
 * {@link com.backend.service.SuggestionService} fallback was used instead.
 */
public class AiSuggestionResponseDTO {

    private Long customerId;
    private String summary;
    private List<String> suggestions;
    private String riskLevel;
    private String source;

    public AiSuggestionResponseDTO() {
    }

    public AiSuggestionResponseDTO(Long customerId, String summary, List<String> suggestions, String riskLevel,
                                    String source) {
        this.customerId = customerId;
        this.summary = summary;
        this.suggestions = suggestions;
        this.riskLevel = riskLevel;
        this.source = source;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
