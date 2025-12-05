package com.nilecare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class FeedbackPageDTO {
    @JsonProperty("feedbackList")
    private List<FeedbackItemDTO> feedbackList;
    
    @JsonProperty("feedbackStats")
    private FeedbackStatsDTO feedbackStats;

    public FeedbackPageDTO() {
    }

    public FeedbackPageDTO(List<FeedbackItemDTO> feedbackList, FeedbackStatsDTO feedbackStats) {
        this.feedbackList = feedbackList;
        this.feedbackStats = feedbackStats;
    }

    public List<FeedbackItemDTO> getFeedbackList() {
        return feedbackList;
    }

    public void setFeedbackList(List<FeedbackItemDTO> feedbackList) {
        this.feedbackList = feedbackList;
    }

    public FeedbackStatsDTO getFeedbackStats() {
        return feedbackStats;
    }

    public void setFeedbackStats(FeedbackStatsDTO feedbackStats) {
        this.feedbackStats = feedbackStats;
    }
}
