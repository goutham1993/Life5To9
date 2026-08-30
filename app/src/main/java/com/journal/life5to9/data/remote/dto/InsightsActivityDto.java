package com.journal.life5to9.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class InsightsActivityDto {
    @SerializedName("categoryId")
    private long categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("notes")
    private String notes;

    @SerializedName("timeSpentHours")
    private double timeSpentHours;

    @SerializedName("date")
    private long date;

    public InsightsActivityDto() {
    }

    public InsightsActivityDto(long categoryId, String categoryName, String notes,
                               double timeSpentHours, long date) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.notes = notes;
        this.timeSpentHours = timeSpentHours;
        this.date = date;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getNotes() {
        return notes;
    }

    public double getTimeSpentHours() {
        return timeSpentHours;
    }

    public long getDate() {
        return date;
    }
}
