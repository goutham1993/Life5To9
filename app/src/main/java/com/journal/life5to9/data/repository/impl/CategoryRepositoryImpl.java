package com.journal.life5to9.data.repository.impl;

import androidx.lifecycle.LiveData;

import com.journal.life5to9.data.dao.CategoryDao;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.data.repository.CategoryRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryRepositoryImpl implements CategoryRepository {
    
    private final CategoryDao categoryDao;
    private final ExecutorService executor;
    
    public CategoryRepositoryImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
        this.executor = Executors.newFixedThreadPool(4);
    }
    
    @Override
    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }
    
    @Override
    public LiveData<Category> getCategoryById(long id) {
        return categoryDao.getCategoryById(id);
    }
    
    @Override
    public LiveData<List<Category>> getDefaultCategories() {
        return categoryDao.getDefaultCategories();
    }
    
    @Override
    public LiveData<List<Category>> getUserCategories() {
        return categoryDao.getUserCategories();
    }
    
    @Override
    public void insertCategory(Category category) {
        executor.execute(() -> categoryDao.insertCategory(category));
    }
    
    @Override
    public void updateCategory(Category category) {
        executor.execute(() -> {
            category.setUpdatedAt(new Date());
            categoryDao.updateCategory(category);
        });
    }
    
    @Override
    public void deleteCategory(Category category) {
        executor.execute(() -> categoryDao.deleteCategory(category));
    }
    
    @Override
    public void deleteCategoryById(long id) {
        executor.execute(() -> categoryDao.deleteCategoryById(id));
    }
    
    @Override
    public boolean isCategoryNameExists(String name) {
        try {
            return executor.submit(() -> categoryDao.getCategoryCountByName(name) > 0).get();
        } catch (Exception e) {
            return false;
        }
    }
}
