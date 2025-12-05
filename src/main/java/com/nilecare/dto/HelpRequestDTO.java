package com.nilecare.dto;

public class HelpRequestDTO {
    
    private Long requestId;
    private String category;
    private String subject;
    private String message;
    private String status;
    private String response;
    private String createdAt;
    private String updatedAt;

    public HelpRequestDTO() {}

    public HelpRequestDTO(Long requestId, String category, String subject, String message, 
                          String status, String response, String createdAt, String updatedAt) {
        this.requestId = requestId;
        this.category = category;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.response = response;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
