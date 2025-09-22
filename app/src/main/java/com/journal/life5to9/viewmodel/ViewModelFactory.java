package com.journal.life5to9.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.journal.life5to9.service.ActivityService;
import com.journal.life5to9.service.CategoryService;

public class ViewModelFactory implements ViewModelProvider.Factory {
    
    private final ActivityService activityService;
    private final CategoryService categoryService;
    
    public ViewModelFactory(ActivityService activityService, CategoryService categoryService) {
        this.activityService = activityService;
        this.categoryService = categoryService;
    }
    
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(activityService, categoryService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
