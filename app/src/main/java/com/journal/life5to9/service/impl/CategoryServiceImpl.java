package com.journal.life5to9.service.impl;

import androidx.lifecycle.LiveData;

import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.data.repository.CategoryRepository;
import com.journal.life5to9.service.CategoryService;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public LiveData<List<Category>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }
    
    @Override
    public LiveData<Category> getCategoryById(long id) {
        return categoryRepository.getCategoryById(id);
    }
    
    @Override
    public LiveData<List<Category>> getDefaultCategories() {
        return categoryRepository.getDefaultCategories();
    }
    
    @Override
    public LiveData<List<Category>> getUserCategories() {
        return categoryRepository.getUserCategories();
    }
    
    @Override
    public void addCategory(String name, String color, String icon) {
        // Check if category name already exists
        if (categoryRepository.isCategoryNameExists(name)) {
            // Category already exists, don't add it
            return;
        }
        
        Category category = new Category(name, color, icon, false);
        categoryRepository.insertCategory(category);
    }
    
    @Override
    public void updateCategory(Category category) {
        categoryRepository.updateCategory(category);
    }
    
    @Override
    public void deleteCategory(Category category) {
        categoryRepository.deleteCategory(category);
    }
    
    @Override
    public void deleteCategoryById(long id) {
        categoryRepository.deleteCategoryById(id);
    }
    
    @Override
    public boolean isCategoryNameExists(String name) {
        return categoryRepository.isCategoryNameExists(name);
    }
}
