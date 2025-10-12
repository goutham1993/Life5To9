package com.journal.life5to9.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.journal.life5to9.data.entity.Activity;

import java.util.Date;
import java.util.List;

@Dao
public interface ActivityDao {
    
    @Query("SELECT * FROM activities ORDER BY date DESC, createdAt DESC")
    LiveData<List<Activity>> getAllActivities();
    
    @Query("SELECT * FROM activities WHERE id = :id")
    LiveData<Activity> getActivityById(long id);
    
    @Query("SELECT * FROM activities WHERE categoryId = :categoryId ORDER BY date DESC")
    LiveData<List<Activity>> getActivitiesByCategory(long categoryId);
    
    @Query("SELECT * FROM activities WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    LiveData<List<Activity>> getActivitiesByDateRange(Date startDate, Date endDate);
    
    @Query("SELECT * FROM activities WHERE date >= :startOfDay AND date < :endOfDay ORDER BY createdAt DESC")
    LiveData<List<Activity>> getActivitiesByDate(Date startOfDay, Date endOfDay);
    
    @Query("SELECT SUM(timeSpentHours) FROM activities WHERE categoryId = :categoryId AND date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalTimeByCategory(long categoryId, Date startDate, Date endDate);
    
    @Query("SELECT SUM(timeSpentHours) FROM activities WHERE date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalTimeByDateRange(Date startDate, Date endDate);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertActivity(Activity activity);
    
    @Update
    void updateActivity(Activity activity);
    
    @Delete
    void deleteActivity(Activity activity);
    
    @Query("DELETE FROM activities WHERE id = :id")
    void deleteActivityById(long id);
    
    @Query("SELECT DISTINCT notes FROM activities WHERE categoryId = :categoryId AND notes IS NOT NULL AND notes != '' ORDER BY notes ASC")
    LiveData<List<String>> getDistinctNotesByCategory(long categoryId);
}
