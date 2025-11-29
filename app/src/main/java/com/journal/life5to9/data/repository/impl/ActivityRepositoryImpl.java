package com.journal.life5to9.data.repository.impl;

import androidx.lifecycle.LiveData;

import com.journal.life5to9.data.dao.ActivityDao;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.repository.ActivityRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityRepositoryImpl implements ActivityRepository {
    
    private final ActivityDao activityDao;
    private final ExecutorService executor;
    
    public ActivityRepositoryImpl(ActivityDao activityDao) {
        this.activityDao = activityDao;
        this.executor = Executors.newFixedThreadPool(4);
    }
    
    @Override
    public LiveData<List<Activity>> getAllActivities() {
        return activityDao.getAllActivities();
    }
    
    @Override
    public LiveData<Activity> getActivityById(long id) {
        return activityDao.getActivityById(id);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesByCategory(long categoryId) {
        return activityDao.getActivitiesByCategory(categoryId);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesByDateRange(Date startDate, Date endDate) {
        return activityDao.getActivitiesByDateRange(startDate, endDate);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesByDate(Date startOfDay, Date endOfDay) {
        return activityDao.getActivitiesByDate(startOfDay, endOfDay);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesForWeekdays(Date weekdayStart, Date weekdayEnd) {
        return activityDao.getActivitiesForWeekdays(weekdayStart, weekdayEnd);
    }
    
    @Override
    public LiveData<Double> getTotalTimeByCategory(long categoryId, Date startDate, Date endDate) {
        return activityDao.getTotalTimeByCategory(categoryId, startDate, endDate);
    }
    
    @Override
    public LiveData<Double> getTotalTimeByDateRange(Date startDate, Date endDate) {
        return activityDao.getTotalTimeByDateRange(startDate, endDate);
    }
    
    @Override
    public void insertActivity(Activity activity) {
        executor.execute(() -> activityDao.insertActivity(activity));
    }
    
    @Override
    public void updateActivity(Activity activity) {
        executor.execute(() -> {
            activity.setUpdatedAt(new Date());
            activityDao.updateActivity(activity);
        });
    }
    
    @Override
    public void deleteActivity(Activity activity) {
        executor.execute(() -> activityDao.deleteActivity(activity));
    }
    
    @Override
    public void deleteActivityById(long id) {
        executor.execute(() -> activityDao.deleteActivityById(id));
    }
    
    @Override
    public LiveData<List<String>> getDistinctNotesByCategory(long categoryId) {
        return activityDao.getDistinctNotesByCategory(categoryId);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesForPreviousWeek(Date previousWeekStart, Date currentWeekStart) {
        return activityDao.getActivitiesForPreviousWeek(previousWeekStart, currentWeekStart);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesForPreviousWeekend(Date previousWeekendStart, Date currentWeekendStart) {
        return activityDao.getActivitiesForPreviousWeekend(previousWeekendStart, currentWeekendStart);
    }
    
    @Override
    public LiveData<List<Activity>> getActivitiesForPreviousMonth(Date previousMonthStart, Date currentMonthStart) {
        return activityDao.getActivitiesForPreviousMonth(previousMonthStart, currentMonthStart);
    }
    
    @Override
    public LiveData<List<Activity>> getRecentActivities(int limit) {
        return activityDao.getRecentActivities(limit);
    }
}
