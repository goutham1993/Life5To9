package com.journal.life5to9.data.remote;

import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.data.remote.dto.InsightsActivityDto;
import com.journal.life5to9.data.remote.dto.InsightsCategoryDto;
import com.journal.life5to9.data.remote.dto.InsightsRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InsightsRequestBuilder {

    private InsightsRequestBuilder() {
    }

    public static InsightsRequest build(String periodLabel, String question,
                                        List<Activity> activities, List<Category> categories) {
        Map<Long, Category> categoriesById = new HashMap<>();
        if (categories != null) {
            for (Category category : categories) {
                categoriesById.put(category.getId(), category);
            }
        }

        List<InsightsActivityDto> activityDtos = new ArrayList<>();
        Set<Long> usedCategoryIds = new HashSet<>();

        if (activities != null) {
            for (Activity activity : activities) {
                Category category = categoriesById.get(activity.getCategoryId());
                String categoryName = category != null ? category.getName() : "Unknown";
                long dateMillis = activity.getDate() != null ? activity.getDate().getTime() : 0L;
                activityDtos.add(new InsightsActivityDto(
                        activity.getCategoryId(),
                        categoryName,
                        activity.getNotes(),
                        activity.getTimeSpentHours(),
                        dateMillis
                ));
                usedCategoryIds.add(activity.getCategoryId());
            }
        }

        List<InsightsCategoryDto> categoryDtos = new ArrayList<>();
        if (categories != null) {
            for (Category category : categories) {
                if (usedCategoryIds.contains(category.getId())) {
                    categoryDtos.add(new InsightsCategoryDto(category.getId(), category.getName()));
                }
            }
        }

        return new InsightsRequest(periodLabel, question, categoryDtos, activityDtos);
    }
}
