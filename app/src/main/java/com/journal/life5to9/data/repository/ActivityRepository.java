package com.journal.life5to9.data.repository;

import androidx.lifecycle.LiveData;

import com.journal.life5to9.data.entity.Activity;

import java.util.Date;
import java.util.List;

public interface ActivityRepository {
    LiveData<List<Activity>> getAllActivities();
    LiveData<Activity> getActivityById(long id);
    LiveData<List<Activity>> getActivitiesByCategory(long categoryId);
    LiveData<List<Activity>> getActivitiesByDateRange(Date startDate, Date endDate);
    LiveData<List<Activity>> getActivitiesByDate(Date startOfDay, Date endOfDay);
    LiveData<Double> getTotalTimeByCategory(long categoryId, Date startDate, Date endDate);
    LiveData<Double> getTotalTimeByDateRange(Date startDate, Date endDate);
    void insertActivity(Activity activity);
    void updateActivity(Activity activity);
    void deleteActivity(Activity activity);
    void deleteActivityById(long id);
}
