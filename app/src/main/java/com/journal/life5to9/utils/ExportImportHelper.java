package com.journal.life5to9.utils;

import android.content.Context;
import android.util.Log;

import com.journal.life5to9.data.database.AppDatabase;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    
    public boolean importFromJson(String filePath) {
        try {
            // TODO: Implement JSON import
            // This would involve reading the JSON file, parsing it, and inserting data into database
            Log.d(TAG, "JSON import not yet implemented");
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "JSON import failed", e);
            return false;
        }
    }
    
    public boolean importFromCsv(String filePath) {
        try {
            // TODO: Implement CSV import
            // This would involve reading the CSV file, parsing it, and inserting data into database
            Log.d(TAG, "CSV import not yet implemented");
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "CSV import failed", e);
            return false;
        }
    }
}
