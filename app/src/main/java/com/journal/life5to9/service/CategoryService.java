package com.journal.life5to9.service;

import androidx.lifecycle.LiveData;

import com.journal.life5to9.data.entity.Category;

import java.util.List;

public interface CategoryService {
    LiveData<List<Category>> getAllCategories();
    LiveData<Category> getCategoryById(long id);
    LiveData<List<Category>> getDefaultCategories();
    LiveData<List<Category>> getUserCategories();
    void addCategory(String name, String color, String icon);
    void updateCategory(Category category);
    void deleteCategory(Category category);
    void deleteCategoryById(long id);
    boolean isCategoryNameExists(String name);
}
