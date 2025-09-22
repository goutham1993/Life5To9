package com.journal.life5to9.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;

@Entity(
    tableName = "activities",
    foreignKeys = @ForeignKey(
        entity = Category.class,
        parentColumns = "id",
        childColumns = "categoryId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("categoryId"), @Index("date")}
)
public class Activity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private long categoryId;
    private String notes;
    private double timeSpentHours;
    private Date date;
    private Date createdAt;
    private Date updatedAt;

    public Activity() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Activity(long categoryId, String notes, double timeSpentHours, Date date) {
        this.categoryId = categoryId;
        this.notes = notes;
        this.timeSpentHours = timeSpentHours;
        this.date = date;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getTimeSpentHours() {
        return timeSpentHours;
    }

    public void setTimeSpentHours(double timeSpentHours) {
        this.timeSpentHours = timeSpentHours;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
