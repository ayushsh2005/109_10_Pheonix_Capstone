package com.backend.dto;

public class SuggestionDTO {

    private String id;
    private Long customerId;
    private String type;
    private String severity;
    private String message;

    public SuggestionDTO() {
    }

    public SuggestionDTO(String id, Long customerId, String type, String severity, String message) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.severity = severity;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
