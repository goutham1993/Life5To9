package com.journal.life5to9.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.journal.life5to9.data.repository.ActivityRepository;
import com.journal.life5to9.data.repository.CategoryRepository;
import com.journal.life5to9.data.repository.InsightsRepository;
import com.journal.life5to9.service.ActivityService;
import com.journal.life5to9.service.CategoryService;

public class ViewModelFactory implements ViewModelProvider.Factory {
    
    private final ActivityService activityService;
    private final CategoryService categoryService;
    private final ActivityRepository activityRepository;
    private final CategoryRepository categoryRepository;
    private final InsightsRepository insightsRepository;
    
    public ViewModelFactory(ActivityService activityService, CategoryService categoryService,
                            ActivityRepository activityRepository,
                            CategoryRepository categoryRepository,
                            InsightsRepository insightsRepository) {
        this.activityService = activityService;
        this.categoryService = categoryService;
        this.activityRepository = activityRepository;
        this.categoryRepository = categoryRepository;
        this.insightsRepository = insightsRepository;
    }
    
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(activityService, categoryService);
        }
        if (modelClass.isAssignableFrom(InsightsViewModel.class)) {
            return (T) new InsightsViewModel(activityRepository, categoryRepository, insightsRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
