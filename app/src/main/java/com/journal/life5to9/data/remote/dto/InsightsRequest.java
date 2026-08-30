package com.journal.life5to9.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class InsightsRequest {
    @SerializedName("periodLabel")
    private String periodLabel;

    @SerializedName("question")
    private String question;

    @SerializedName("categories")
    private List<InsightsCategoryDto> categories;

    @SerializedName("activities")
    private List<InsightsActivityDto> activities;

    public InsightsRequest() {
        this.categories = new ArrayList<>();
        this.activities = new ArrayList<>();
    }

    public InsightsRequest(String periodLabel, String question,
                           List<InsightsCategoryDto> categories,
                           List<InsightsActivityDto> activities) {
        this.periodLabel = periodLabel;
        this.question = question;
        this.categories = categories != null ? categories : new ArrayList<>();
        this.activities = activities != null ? activities : new ArrayList<>();
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public String getQuestion() {
        return question;
    }

    public List<InsightsCategoryDto> getCategories() {
        return categories;
    }

    public List<InsightsActivityDto> getActivities() {
        return activities;
    }
}
