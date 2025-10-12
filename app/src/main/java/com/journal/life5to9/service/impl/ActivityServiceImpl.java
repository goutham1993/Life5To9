package com.journal.life5to9.service.impl;

import androidx.lifecycle.LiveData;

import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.repository.ActivityRepository;
import com.journal.life5to9.service.ActivityService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityServiceImpl implements ActivityService {
    
    private final ActivityRepository activityRepository;
    
    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }
    
    @Override
    public LiveData<List<Activity>> getAllActivities() {
        return activityRepository.getAllActivities();
    }
    
    @Override
    public LiveData<Activity> getActivityById(long id) {
        return activityRepository.getActivityById(id);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesByCategory(long categoryId) {
        return activityRepository.getActivitiesByCategory(categoryId);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesByDateRange(Date startDate, Date endDate) {
        return activityRepository.getActivitiesByDateRange(startDate, endDate);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesByDate(Date startOfDay, Date endOfDay) {
        return activityRepository.getActivitiesByDate(startOfDay, endOfDay);
    }
    
    @Override
    public LiveData<Double> getTotalTimeByCategory(long categoryId, Date startDate, Date endDate) {
        return activityRepository.getTotalTimeByCategory(categoryId, startDate, endDate);
    }
    
    @Override
    public LiveData<Double> getTotalTimeByDateRange(Date startDate, Date endDate) {
        return activityRepository.getTotalTimeByDateRange(startDate, endDate);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesForWeek(Date weekStart) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(weekStart);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        Date startOfWeek = calendar.getTime();
        
        // Add 6 days to get to Sunday (end of week)
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        
        Date endOfWeek = calendar.getTime();
        
        // Debug logging
        android.util.Log.d("ActivityServiceImpl", "Week range: " + startOfWeek + " to " + endOfWeek);
        
        return activityRepository.getActivitiesByDateRange(startOfWeek, endOfWeek);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesForWeekend(Date weekendStart) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(weekendStart);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        Date startOfWeekend = calendar.getTime();
        
        // Add 1 day to get to Sunday (end of weekend)
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        
        Date endOfWeekend = calendar.getTime();
        
        // Debug logging
        android.util.Log.d("ActivityServiceImpl", "Weekend range: " + startOfWeekend + " to " + endOfWeekend);
        
        return activityRepository.getActivitiesByDateRange(startOfWeekend, endOfWeekend);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesForMonth(Date monthStart) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(monthStart);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        Date startOfMonth = calendar.getTime();
        
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        
        Date endOfMonth = calendar.getTime();
        
        return activityRepository.getActivitiesByDateRange(startOfMonth, endOfMonth);
    }
    
    @Override
    public void addActivity(long categoryId, String notes, double timeSpentHours, Date date) {
        Activity activity = new Activity(categoryId, notes, timeSpentHours, date);
        activityRepository.insertActivity(activity);
    }
    
    @Override
    public void updateActivity(Activity activity) {
        activityRepository.updateActivity(activity);
    }
    
    @Override
    public void deleteActivity(Activity activity) {
        activityRepository.deleteActivity(activity);
    }
    
    @Override
    public void deleteActivityById(long id) {
        activityRepository.deleteActivityById(id);
    }
}
