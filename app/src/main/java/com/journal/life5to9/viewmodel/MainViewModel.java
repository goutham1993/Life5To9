package com.journal.life5to9.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.service.ActivityService;
import com.journal.life5to9.service.CategoryService;

import java.util.Date;
import java.util.List;

public class MainViewModel extends ViewModel {
    
    private final ActivityService activityService;
    private final CategoryService categoryService;
    
    public MainViewModel(ActivityService activityService, CategoryService categoryService) {
        this.activityService = activityService;
        this.categoryService = categoryService;
    }
    
    // Activity related methods
    public LiveData<List<Activity>> getAllActivities() {
        return activityService.getAllActivities();
    }
    
    public LiveData<List<Activity>> getActivitiesByCategory(long categoryId) {
        return activityService.getActivitiesByCategory(categoryId);
    }
    
    public LiveData<List<Activity>> getActivitiesForWeek(Date weekStart) {
        return activityService.getActivitiesForWeek(weekStart);
    }
    
    public LiveData<List<Activity>> getActivitiesForWeekend(Date weekendStart) {
        return activityService.getActivitiesForWeekend(weekendStart);
    }
    
    public LiveData<List<Activity>> getActivitiesForMonth(Date monthStart) {
        return activityService.getActivitiesForMonth(monthStart);
    }
    
    public LiveData<List<Activity>> getActivitiesByDate(Date startOfDay, Date endOfDay) {
        return activityService.getActivitiesByDate(startOfDay, endOfDay);
    }
    
    public LiveData<List<Activity>> getActivitiesForDateRange(Date startDate, Date endDate) {
        return activityService.getActivitiesByDate(startDate, endDate);
    }
    
    public LiveData<List<Activity>> getActivitiesForWeekdays(Date weekdayStart, Date weekdayEnd) {
        return activityService.getActivitiesForWeekdays(weekdayStart, weekdayEnd);
    }
    
    public LiveData<Double> getTotalTimeByCategory(long categoryId, Date startDate, Date endDate) {
        return activityService.getTotalTimeByCategory(categoryId, startDate, endDate);
    }
    
    public LiveData<Double> getTotalTimeByDateRange(Date startDate, Date endDate) {
        return activityService.getTotalTimeByDateRange(startDate, endDate);
    }
    
    public void addActivity(long categoryId, String notes, double timeSpentHours, Date date) {
        activityService.addActivity(categoryId, notes, timeSpentHours, date);
    }
    
    public void updateActivity(Activity activity) {
        activityService.updateActivity(activity);
    }
    
    public void deleteActivity(Activity activity) {
        activityService.deleteActivity(activity);
    }
    
    public LiveData<List<String>> getDistinctNotesByCategory(long categoryId) {
        return activityService.getDistinctNotesByCategory(categoryId);
    }
    
    // Previous period methods for comparisons
    public LiveData<List<Activity>> getActivitiesForPreviousWeek(Date previousWeekStart, Date currentWeekStart) {
        return activityService.getActivitiesForPreviousWeek(previousWeekStart, currentWeekStart);
    }
    
    public LiveData<List<Activity>> getActivitiesForPreviousWeekend(Date previousWeekendStart, Date currentWeekendStart) {
        return activityService.getActivitiesForPreviousWeekend(previousWeekendStart, currentWeekendStart);
    }
    
    public LiveData<List<Activity>> getActivitiesForPreviousMonth(Date previousMonthStart, Date currentMonthStart) {
        return activityService.getActivitiesForPreviousMonth(previousMonthStart, currentMonthStart);
    }
    
    // Category related methods
    public LiveData<List<Category>> getAllCategories() {
        return categoryService.getAllCategories();
    }
    
    public LiveData<List<Category>> getDefaultCategories() {
        return categoryService.getDefaultCategories();
    }
    
    public LiveData<List<Category>> getUserCategories() {
        return categoryService.getUserCategories();
    }
    
    public void addCategory(String name, String color, String icon) {
        categoryService.addCategory(name, color, icon);
    }
    
    public void updateCategory(Category category) {
        categoryService.updateCategory(category);
    }
    
    public void deleteCategory(Category category) {
        categoryService.deleteCategory(category);
    }
}
