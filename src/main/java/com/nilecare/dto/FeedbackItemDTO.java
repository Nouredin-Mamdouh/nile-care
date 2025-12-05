package com.nilecare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedbackItemDTO {
    @JsonProperty("id")
    private Long feedbackId;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("rating")
    private Integer rating;
    
    @JsonProperty("subject")
    private String subject;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("date")
    private String date;
    
    @JsonProperty("response")
    private String response;

    public FeedbackItemDTO() {
    }

    public FeedbackItemDTO(Long feedbackId, String category, Integer rating, String subject, 
                          String message, String status, String date, String response) {
        this.feedbackId = feedbackId;
        this.category = category;
        this.rating = rating;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.date = date;
        this.response = response;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
