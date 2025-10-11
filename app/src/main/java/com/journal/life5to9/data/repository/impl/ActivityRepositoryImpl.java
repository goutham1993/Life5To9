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
    public LiveData<Double> getTotalTimeByCategory(long categoryId, Date startDate, Date endDate) {
        return activityDao.getTotalTimeByCategory(categoryId, startDate, endDate);
    }
    
    @Override
    public LiveData<Double> getTotalTimeByDateRange(Date startDate, Date endDate) {
        return activityDao.getTotalTimeByDateRange(startDate, endDate);
    }
    
    @Override
    public LiveData<List<String>> getAllDistinctNotes() {
        android.util.Log.d("ActivityRepositoryImpl", "Getting all distinct notes from database");
        return activityDao.getAllDistinctNotes();
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
}
