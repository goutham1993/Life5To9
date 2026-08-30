package com.journal.life5to9.data.remote.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InsightsResponseDeserializerTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(InsightsResponse.class, new InsightsResponseDeserializer())
            .create();

    @Test
    public void parsesCanonicalFields() {
        String json = "{"
                + "\"title\":\"Time check\","
                + "\"summary\":\"Most evenings went to fitness.\","
                + "\"highlights\":[\"Gym 3 times\"],"
                + "\"recommendations\":[\"Keep the streak\"]"
                + "}";

        InsightsResponse response = gson.fromJson(json, InsightsResponse.class);

        assertEquals("Time check", response.getTitle());
        assertEquals("Most evenings went to fitness.", response.getSummary());
        assertEquals(1, response.getHighlights().size());
        assertEquals("Gym 3 times", response.getHighlights().get(0));
        assertEquals("Keep the streak", response.getRecommendations().get(0));
    }

    @Test
    public void parsesAlternateFieldNames() {
        String json = "{"
                + "\"headline\":\"Balance\","
                + "\"insight\":\"Learning is light this week.\","
                + "\"bullets\":[\"Only 1 hour of reading\"],"
                + "\"actions\":[\"Schedule two study blocks\"]"
                + "}";

        InsightsResponse response = gson.fromJson(json, InsightsResponse.class);

        assertEquals("Balance", response.getTitle());
        assertEquals("Learning is light this week.", response.getSummary());
        assertEquals("Only 1 hour of reading", response.getHighlights().get(0));
        assertEquals("Schedule two study blocks", response.getRecommendations().get(0));
        assertTrue(response.hasContent());
    }

    @Test
    public void unwrapsDataObject() {
        String json = "{\"data\":{\"title\":\"Wrapped\",\"summary\":\"From data\"}}";

        InsightsResponse response = gson.fromJson(json, InsightsResponse.class);

        assertEquals("Wrapped", response.getTitle());
        assertEquals("From data", response.getSummary());
    }
}
