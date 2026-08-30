package com.journal.life5to9.data.remote.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Accepts common backend field names so the UI can render a structured insight
 * even if the API uses title/summary/highlights or insight/bullets/actions.
 */
public class InsightsResponseDeserializer implements JsonDeserializer<InsightsResponse> {

    private static final String[] TITLE_KEYS = {"title", "headline", "heading"};
    private static final String[] SUMMARY_KEYS = {
            "summary", "insight", "answer", "text", "message", "body", "content"
    };
    private static final String[] HIGHLIGHT_KEYS = {
            "highlights", "bullets", "points", "keyTakeaways", "takeaways"
    };
    private static final String[] RECOMMENDATION_KEYS = {
            "recommendations", "actions", "suggestions", "tips"
    };

    @Override
    public InsightsResponse deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return new InsightsResponse();
        }

        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            return InsightsResponse.fromSummary(json.getAsString());
        }

        if (!json.isJsonObject()) {
            return new InsightsResponse();
        }

        JsonObject obj = json.getAsJsonObject();
        if (obj.has("data") && obj.get("data").isJsonObject()) {
            obj = mergeObjects(obj.getAsJsonObject("data"), obj);
        }
        if (obj.has("insight") && obj.get("insight").isJsonObject()) {
            obj = mergeObjects(obj.getAsJsonObject("insight"), obj);
        }
        if (obj.has("result") && obj.get("result").isJsonObject()) {
            obj = mergeObjects(obj.getAsJsonObject("result"), obj);
        }

        String title = firstString(obj, TITLE_KEYS);
        String summary = firstString(obj, SUMMARY_KEYS);
        List<String> highlights = firstStringList(obj, HIGHLIGHT_KEYS);
        List<String> recommendations = firstStringList(obj, RECOMMENDATION_KEYS);

        return new InsightsResponse(title, summary, highlights, recommendations);
    }

    private static JsonObject mergeObjects(JsonObject preferred, JsonObject fallback) {
        JsonObject merged = new JsonObject();
        for (String key : fallback.keySet()) {
            merged.add(key, fallback.get(key));
        }
        for (String key : preferred.keySet()) {
            merged.add(key, preferred.get(key));
        }
        return merged;
    }

    private static String firstString(JsonObject obj, String[] keys) {
        for (String key : keys) {
            if (!obj.has(key) || obj.get(key).isJsonNull()) {
                continue;
            }
            JsonElement element = obj.get(key);
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String value = element.getAsString();
                if (value != null && !value.trim().isEmpty()) {
                    return value.trim();
                }
            }
        }
        return null;
    }

    private static List<String> firstStringList(JsonObject obj, String[] keys) {
        for (String key : keys) {
            if (!obj.has(key) || obj.get(key).isJsonNull()) {
                continue;
            }
            List<String> values = readStringList(obj.get(key));
            if (!values.isEmpty()) {
                return values;
            }
        }
        return new ArrayList<>();
    }

    private static List<String> readStringList(JsonElement element) {
        List<String> values = new ArrayList<>();
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement item : array) {
                String value = readListItem(item);
                if (value != null) {
                    values.add(value);
                }
            }
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String value = element.getAsString();
            if (value != null && !value.trim().isEmpty()) {
                values.add(value.trim());
            }
        }
        return values;
    }

    private static String readListItem(JsonElement item) {
        if (item == null || item.isJsonNull()) {
            return null;
        }
        if (item.isJsonPrimitive() && item.getAsJsonPrimitive().isString()) {
            String value = item.getAsString();
            return value != null && !value.trim().isEmpty() ? value.trim() : null;
        }
        if (item.isJsonObject()) {
            JsonObject obj = item.getAsJsonObject();
            String[] itemKeys = {"text", "value", "summary", "title", "point"};
            for (String key : itemKeys) {
                if (obj.has(key) && obj.get(key).isJsonPrimitive()) {
                    String value = obj.get(key).getAsString();
                    if (value != null && !value.trim().isEmpty()) {
                        return value.trim();
                    }
                }
            }
        }
        return null;
    }
}
