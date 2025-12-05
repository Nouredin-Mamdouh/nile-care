package com.nilecare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryStatsDTO {
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("count")
    private Integer count;
    
    @JsonProperty("avgRating")
    private Double avgRating;

    public CategoryStatsDTO() {
    }

    public CategoryStatsDTO(String category, Integer count, Double avgRating) {
        this.category = category;
        this.count = count;
        this.avgRating = avgRating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}
