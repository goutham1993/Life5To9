package com.journal.life5to9.data.remote;

import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.data.remote.dto.InsightsRequest;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class InsightsRequestBuilderTest {

    @Test
    public void includesOnlyCategoriesUsedInThePeriod() {
        Category fitness = new Category("Fitness", "#FF5722", "fitness_center", true);
        fitness.setId(1);
        Category learning = new Category("Learning", "#9C27B0", "school", true);
        learning.setId(3);
        Category rest = new Category("Rest", "#607D8B", "bedtime", true);
        rest.setId(7);

        Date date = new Date(1758542400000L);
        Activity gym = new Activity(1, "gym", 1.5, date);
        Activity reading = new Activity(3, "reading", 2.0, date);

        InsightsRequest request = InsightsRequestBuilder.build(
                "Last 7 days",
                "Where is my time going?",
                Arrays.asList(gym, reading),
                Arrays.asList(fitness, learning, rest)
        );

        assertEquals("Last 7 days", request.getPeriodLabel());
        assertEquals("Where is my time going?", request.getQuestion());
        assertEquals(2, request.getCategories().size());
        assertEquals(2, request.getActivities().size());
        assertEquals(1, request.getActivities().get(0).getCategoryId());
        assertEquals("Fitness", request.getActivities().get(0).getCategoryName());
        assertEquals("gym", request.getActivities().get(0).getNotes());
        assertEquals(1.5, request.getActivities().get(0).getTimeSpentHours(), 0.001);
        assertEquals(1758542400000L, request.getActivities().get(0).getDate());
        assertEquals(3, request.getCategories().get(1).getId());
        assertEquals("Learning", request.getCategories().get(1).getName());
    }

    @Test
    public void buildsEmptyPayloadWhenThereAreNoActivities() {
        InsightsRequest request = InsightsRequestBuilder.build(
                "This week",
                "What should I do more of?",
                Collections.emptyList(),
                Collections.emptyList()
        );

        assertEquals(0, request.getCategories().size());
        assertEquals(0, request.getActivities().size());
    }
}
