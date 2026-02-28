package com.journal.life5to9.utils;

import android.content.Context;
import android.util.Log;

import com.journal.life5to9.data.database.AppDatabase;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportImportHelper {
    
    private static final String TAG = "ExportImportHelper";
    private Context context;
    private AppDatabase database;
    
    public ExportImportHelper(Context context) {
        this.context = context;
        this.database = AppDatabase.getDatabase(context);
    }
    
    public boolean exportToJson(String filePath) {
        try {
            // Check if file path is valid
            if (filePath == null || filePath.isEmpty()) {
                Log.e(TAG, "Invalid file path");
                return false;
            }
            
            // Get all data from database
            List<Category> categories = database.categoryDao().getAllCategoriesSync();
            List<Activity> activities = database.activityDao().getAllActivitiesSync();
            
            Log.d(TAG, "Exporting " + categories.size() + " categories and " + activities.size() + " activities");
            
            // Create date formatter for readable timestamps
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            
            // Create JSON structure
            JSONObject exportData = new JSONObject();
            exportData.put("exportDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            exportData.put("appVersion", "1.0");
            exportData.put("totalCategories", categories.size());
            exportData.put("totalActivities", activities.size());
            
            // Add categories
            JSONArray categoriesArray = new JSONArray();
            for (Category category : categories) {
                JSONObject categoryJson = new JSONObject();
                categoryJson.put("id", category.getId());
                categoryJson.put("name", category.getName());
                categoryJson.put("color", category.getColor() != null ? category.getColor() : "#607D8B");
                categoryJson.put("icon", category.getIcon() != null ? category.getIcon() : "label");
                categoryJson.put("isDefault", category.isDefault());
                categoryJson.put("createdAt", dateFormat.format(category.getCreatedAt()));
                categoryJson.put("updatedAt", dateFormat.format(category.getUpdatedAt()));
                categoriesArray.put(categoryJson);
            }
            exportData.put("categories", categoriesArray);
            
            // Add activities with category names
            JSONArray activitiesArray = new JSONArray();
            for (Activity activity : activities) {
                // Find category name for this activity
                String categoryName = "Unknown Category";
                for (Category category : categories) {
                    if (category.getId() == activity.getCategoryId()) {
                        categoryName = category.getName();
                        break;
                    }
                }
                
                JSONObject activityJson = new JSONObject();
                activityJson.put("id", activity.getId());
                activityJson.put("categoryId", activity.getCategoryId());
                activityJson.put("categoryName", categoryName);
                activityJson.put("notes", activity.getNotes());
                activityJson.put("timeSpentHours", activity.getTimeSpentHours());
                activityJson.put("date", activity.getDate().getTime());
                activityJson.put("createdAt", dateFormat.format(activity.getCreatedAt()));
                activityJson.put("updatedAt", dateFormat.format(activity.getUpdatedAt()));
                activitiesArray.put(activityJson);
            }
            exportData.put("activities", activitiesArray);
            
            // Write to file
            File file = new File(filePath);
            
            // Create parent directories if they don't exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirsCreated = parentDir.mkdirs();
                Log.d(TAG, "Created directories: " + dirsCreated);
            }
            
            FileWriter writer = new FileWriter(file);
            writer.write(exportData.toString(2)); // Pretty print with 2-space indentation
            writer.close();
            
            Log.d(TAG, "JSON export successful: " + filePath + " (Size: " + file.length() + " bytes)");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "JSON export failed", e);
            return false;
        }
    }
    
    public boolean exportToCsv(String filePath) {
        try {
            // Check if file path is valid
            if (filePath == null || filePath.isEmpty()) {
                Log.e(TAG, "Invalid file path");
                return false;
            }
            
            // Get all data from database
            List<Category> categories = database.categoryDao().getAllCategoriesSync();
            List<Activity> activities = database.activityDao().getAllActivitiesSync();
            
            Log.d(TAG, "Exporting " + categories.size() + " categories and " + activities.size() + " activities to CSV");
            
            // Create CSV content
            StringBuilder csvContent = new StringBuilder();
            
            // Add header
            csvContent.append("Activity ID,Category ID,Category Name,Notes,Time Spent (Hours),Date,Created At,Updated At\n");
            
            // Add activities with category names
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            for (Activity activity : activities) {
                // Find category name
                String categoryName = "Unknown";
                String categoryColor = "#000000";
                for (Category category : categories) {
                    if (category.getId() == activity.getCategoryId()) {
                        categoryName = category.getName();
                        categoryColor = category.getColor();
                        break;
                    }
                }
                
                csvContent.append(activity.getId()).append(",");
                csvContent.append(activity.getCategoryId()).append(",");
                csvContent.append("\"").append(categoryName).append("\",");
                csvContent.append("\"").append(activity.getNotes() != null ? activity.getNotes().replace("\"", "\"\"") : "").append("\",");
                csvContent.append(activity.getTimeSpentHours()).append(",");
                csvContent.append(dateFormat.format(activity.getDate())).append(",");
                csvContent.append(dateFormat.format(activity.getCreatedAt())).append(",");
                csvContent.append(dateFormat.format(activity.getUpdatedAt())).append("\n");
            }
            
            // Write to file
            File file = new File(filePath);
            
            // Create parent directories if they don't exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirsCreated = parentDir.mkdirs();
                Log.d(TAG, "Created directories: " + dirsCreated);
            }
            
            FileWriter writer = new FileWriter(file);
            writer.write(csvContent.toString());
            writer.close();
            
            Log.d(TAG, "CSV export successful: " + filePath + " (Size: " + file.length() + " bytes)");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "CSV export failed", e);
            return false;
        }
    }
    
    public boolean importFromJson(InputStream inputStream) {
        try {
            if (inputStream == null) {
                Log.e(TAG, "Input stream is null");
                return false;
            }

            // Read the entire JSON content from the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();

            JSONObject exportData = new JSONObject(jsonContent.toString());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            // Map of old category ID -> new category ID
            Map<Long, Long> categoryIdMap = new HashMap<>();

            // Import categories
            if (exportData.has("categories")) {
                JSONArray categoriesArray = exportData.getJSONArray("categories");
                for (int i = 0; i < categoriesArray.length(); i++) {
                    JSONObject categoryJson = categoriesArray.getJSONObject(i);
                    long oldId = categoryJson.getLong("id");
                    String name = categoryJson.getString("name");

                    // Check if a category with this name already exists
                    int existingCount = database.categoryDao().getCategoryCountByName(name);
                    if (existingCount > 0) {
                        // Find the existing category to get its ID
                        List<Category> allCategories = database.categoryDao().getAllCategoriesSync();
                        for (Category existing : allCategories) {
                            if (existing.getName().equalsIgnoreCase(name)) {
                                categoryIdMap.put(oldId, existing.getId());
                                break;
                            }
                        }
                        Log.d(TAG, "Category already exists, reusing: " + name);
                    } else {
                        Category category = new Category();
                        category.setName(name);
                        category.setColor(categoryJson.optString("color", "#607D8B"));
                        category.setIcon(categoryJson.optString("icon", "label"));
                        category.setDefault(categoryJson.optBoolean("isDefault", false));
                        if (categoryJson.has("createdAt")) {
                            try {
                                category.setCreatedAt(dateFormat.parse(categoryJson.getString("createdAt")));
                            } catch (Exception e) {
                                category.setCreatedAt(new Date());
                            }
                        }
                        if (categoryJson.has("updatedAt")) {
                            try {
                                category.setUpdatedAt(dateFormat.parse(categoryJson.getString("updatedAt")));
                            } catch (Exception e) {
                                category.setUpdatedAt(new Date());
                            }
                        }
                        long newId = database.categoryDao().insertCategory(category);
                        categoryIdMap.put(oldId, newId);
                        Log.d(TAG, "Imported category: " + name + " (old ID: " + oldId + " -> new ID: " + newId + ")");
                    }
                }
            }

            // Import activities
            int importedCount = 0;
            if (exportData.has("activities")) {
                JSONArray activitiesArray = exportData.getJSONArray("activities");
                for (int i = 0; i < activitiesArray.length(); i++) {
                    JSONObject activityJson = activitiesArray.getJSONObject(i);

                    long oldCategoryId = activityJson.getLong("categoryId");
                    Long newCategoryId = categoryIdMap.get(oldCategoryId);

                    if (newCategoryId == null) {
                        // Category not found in map; try to find by categoryName
                        String categoryName = activityJson.optString("categoryName", "");
                        if (!categoryName.isEmpty()) {
                            List<Category> allCategories = database.categoryDao().getAllCategoriesSync();
                            for (Category cat : allCategories) {
                                if (cat.getName().equalsIgnoreCase(categoryName)) {
                                    newCategoryId = cat.getId();
                                    break;
                                }
                            }
                        }
                        if (newCategoryId == null) {
                            Log.w(TAG, "Skipping activity - no matching category for ID: " + oldCategoryId);
                            continue;
                        }
                    }

                    Activity activity = new Activity();
                    activity.setCategoryId(newCategoryId);
                    activity.setNotes(activityJson.optString("notes", ""));
                    activity.setTimeSpentHours(activityJson.optDouble("timeSpentHours", 0.0));

                    // Parse date - stored as epoch millis in export
                    if (activityJson.has("date")) {
                        activity.setDate(new Date(activityJson.getLong("date")));
                    } else {
                        activity.setDate(new Date());
                    }

                    if (activityJson.has("createdAt")) {
                        try {
                            activity.setCreatedAt(dateFormat.parse(activityJson.getString("createdAt")));
                        } catch (Exception e) {
                            activity.setCreatedAt(new Date());
                        }
                    }
                    if (activityJson.has("updatedAt")) {
                        try {
                            activity.setUpdatedAt(dateFormat.parse(activityJson.getString("updatedAt")));
                        } catch (Exception e) {
                            activity.setUpdatedAt(new Date());
                        }
                    }

                    database.activityDao().insertActivity(activity);
                    importedCount++;
                }
            }

            Log.d(TAG, "JSON import successful: " + categoryIdMap.size() + " categories, " + importedCount + " activities");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "JSON import failed", e);
            return false;
        }
    }

    public boolean importFromCsv(InputStream inputStream) {
        try {
            if (inputStream == null) {
                Log.e(TAG, "Input stream is null");
                return false;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Read header line
            String headerLine = reader.readLine();
            if (headerLine == null) {
                Log.e(TAG, "CSV file is empty");
                reader.close();
                return false;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            // Cache of category name -> category ID to avoid repeated DB lookups
            Map<String, Long> categoryNameToId = new HashMap<>();
            List<Category> existingCategories = database.categoryDao().getAllCategoriesSync();
            for (Category cat : existingCategories) {
                categoryNameToId.put(cat.getName().toLowerCase(), cat.getId());
            }

            int importedCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Parse CSV line (handles quoted fields)
                String[] fields = parseCsvLine(line);
                if (fields.length < 8) {
                    Log.w(TAG, "Skipping malformed CSV line: " + line);
                    continue;
                }

                // CSV format: Activity ID, Category ID, Category Name, Notes, Time Spent (Hours), Date, Created At, Updated At
                String categoryName = fields[2].trim();
                String notes = fields[3].trim();
                double timeSpentHours;
                try {
                    timeSpentHours = Double.parseDouble(fields[4].trim());
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Skipping line with invalid time: " + fields[4]);
                    continue;
                }

                // Find or create category
                Long categoryId = categoryNameToId.get(categoryName.toLowerCase());
                if (categoryId == null) {
                    // Create the category
                    Category newCategory = new Category();
                    newCategory.setName(categoryName);
                    newCategory.setColor("#607D8B");
                    newCategory.setIcon("label");
                    newCategory.setDefault(false);
                    categoryId = database.categoryDao().insertCategory(newCategory);
                    categoryNameToId.put(categoryName.toLowerCase(), categoryId);
                    Log.d(TAG, "Created new category from CSV: " + categoryName);
                }

                Activity activity = new Activity();
                activity.setCategoryId(categoryId);
                activity.setNotes(notes);
                activity.setTimeSpentHours(timeSpentHours);

                // Parse date
                try {
                    activity.setDate(dateFormat.parse(fields[5].trim()));
                } catch (Exception e) {
                    activity.setDate(new Date());
                }

                // Parse created at
                try {
                    activity.setCreatedAt(dateFormat.parse(fields[6].trim()));
                } catch (Exception e) {
                    activity.setCreatedAt(new Date());
                }

                // Parse updated at
                try {
                    activity.setUpdatedAt(dateFormat.parse(fields[7].trim()));
                } catch (Exception e) {
                    activity.setUpdatedAt(new Date());
                }

                database.activityDao().insertActivity(activity);
                importedCount++;
            }
            reader.close();

            Log.d(TAG, "CSV import successful: " + importedCount + " activities imported");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "CSV import failed", e);
            return false;
        }
    }

    /**
     * Parses a CSV line handling quoted fields (fields may contain commas inside double quotes).
     */
    private String[] parseCsvLine(String line) {
        java.util.ArrayList<String> fields = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    // Check for escaped quote ("")
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++; // skip next quote
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current = new StringBuilder();
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString()); // add last field

        return fields.toArray(new String[0]);
    }
}
