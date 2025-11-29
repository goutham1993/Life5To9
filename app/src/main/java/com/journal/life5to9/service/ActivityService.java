package com.journal.life5to9.service;

import androidx.lifecycle.LiveData;

import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;

import java.util.Date;
import java.util.List;

public interface ActivityService {
    LiveData<List<Activity>> getAllActivities();
    LiveData<Activity> getActivityById(long id);
    LiveData<List<Activity>> getActivitiesByCategory(long categoryId);
    LiveData<List<Activity>> getActivitiesByDateRange(Date startDate, Date endDate);
    LiveData<List<Activity>> getActivitiesByDate(Date startOfDay, Date endOfDay);
    LiveData<List<Activity>> getActivitiesForWeekdays(Date weekdayStart, Date weekdayEnd);
    LiveData<Double> getTotalTimeByCategory(long categoryId, Date startDate, Date endDate);
    LiveData<Double> getTotalTimeByDateRange(Date startDate, Date endDate);
    LiveData<List<Activity>> getActivitiesForWeek(Date weekStart);
    LiveData<List<Activity>> getActivitiesForWeekend(Date weekendStart);
    LiveData<List<Activity>> getActivitiesForMonth(Date monthStart);
    LiveData<List<String>> getDistinctNotesByCategory(long categoryId);
    
    // Previous period methods for comparisons
    LiveData<List<Activity>> getActivitiesForPreviousWeek(Date previousWeekStart, Date currentWeekStart);
    LiveData<List<Activity>> getActivitiesForPreviousWeekend(Date previousWeekendStart, Date currentWeekendStart);
    LiveData<List<Activity>> getActivitiesForPreviousMonth(Date previousMonthStart, Date currentMonthStart);
    
    // Recent activities for quick add
    LiveData<List<Activity>> getRecentActivities(int limit);
    
    void addActivity(long categoryId, String notes, double timeSpentHours, Date date);
    void updateActivity(Activity activity);
    void deleteActivity(Activity activity);
    void deleteActivityById(long id);
}
