package com.journal.life5to9.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name;
    private String color;
    private String icon;
    private Date createdAt;
    private Date updatedAt;
    private boolean isDefault;

    public Category() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isDefault = false;
    }

    public Category(String name, String color, String icon, boolean isDefault) {
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.isDefault = isDefault;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
