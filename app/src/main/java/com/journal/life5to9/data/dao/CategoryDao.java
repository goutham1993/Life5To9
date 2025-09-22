package com.journal.life5to9.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.journal.life5to9.data.entity.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    
    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();
    
    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(long id);
    
    @Query("SELECT * FROM categories WHERE isDefault = 1 ORDER BY name ASC")
    LiveData<List<Category>> getDefaultCategories();
    
    @Query("SELECT * FROM categories WHERE isDefault = 0 ORDER BY name ASC")
    LiveData<List<Category>> getUserCategories();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCategory(Category category);
    
    @Update
    void updateCategory(Category category);
    
    @Delete
    void deleteCategory(Category category);
    
    @Query("DELETE FROM categories WHERE id = :id")
    void deleteCategoryById(long id);
    
    @Query("SELECT COUNT(*) FROM categories WHERE name = :name")
    int getCategoryCountByName(String name);
}
