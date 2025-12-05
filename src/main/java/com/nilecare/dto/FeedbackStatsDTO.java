package com.nilecare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedbackStatsDTO {
    @JsonProperty("totalFeedback")
    private Integer totalFeedback;
    
    @JsonProperty("averageRating")
    private Double averageRating;
    
    @JsonProperty("categoryBreakdown")
    private CategoryStatsDTO[] categoryBreakdown;

    public FeedbackStatsDTO() {
    }

    public FeedbackStatsDTO(Integer totalFeedback, Double averageRating, CategoryStatsDTO[] categoryBreakdown) {
        this.totalFeedback = totalFeedback;
        this.averageRating = averageRating;
        this.categoryBreakdown = categoryBreakdown;
    }

    public Integer getTotalFeedback() {
        return totalFeedback;
    }

    public void setTotalFeedback(Integer totalFeedback) {
        this.totalFeedback = totalFeedback;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public CategoryStatsDTO[] getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(CategoryStatsDTO[] categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }
}
