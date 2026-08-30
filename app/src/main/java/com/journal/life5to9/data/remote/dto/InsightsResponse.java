package com.journal.life5to9.data.remote.dto;

import java.util.ArrayList;
import java.util.List;

public class InsightsResponse {
    private String title;
    private String summary;
    private List<String> highlights;
    private List<String> recommendations;

    public InsightsResponse() {
        this.highlights = new ArrayList<>();
        this.recommendations = new ArrayList<>();
    }

    public InsightsResponse(String title, String summary, List<String> highlights,
                            List<String> recommendations) {
        this.title = title;
        this.summary = summary;
        this.highlights = highlights != null ? highlights : new ArrayList<>();
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
    }

    public static InsightsResponse fromSummary(String summary) {
        return new InsightsResponse(null, summary, null, null);
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getHighlights() {
        return highlights != null ? highlights : new ArrayList<>();
    }

    public List<String> getRecommendations() {
        return recommendations != null ? recommendations : new ArrayList<>();
    }

    public boolean hasContent() {
        return (title != null && !title.trim().isEmpty())
                || (summary != null && !summary.trim().isEmpty())
                || !getHighlights().isEmpty()
                || !getRecommendations().isEmpty();
    }
}
