package com.journal.life5to9;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journal.life5to9.data.database.AppDatabase;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.data.repository.impl.ActivityRepositoryImpl;
import com.journal.life5to9.data.repository.impl.CategoryRepositoryImpl;
import com.journal.life5to9.service.impl.ActivityServiceImpl;
import com.journal.life5to9.service.impl.CategoryServiceImpl;
import com.journal.life5to9.ui.adapters.CategoryAdapter;
import com.journal.life5to9.ui.dialogs.AddCategoryDialog;
import com.journal.life5to9.viewmodel.MainViewModel;
import com.journal.life5to9.viewmodel.ViewModelFactory;

import java.util.List;

public class ManageCategoriesActivity extends AppCompatActivity {
    
    private MainViewModel viewModel;
    private RecyclerView recyclerViewCategories;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabAddCategory;
    private CategoryAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);
        
        initializeViews();
        initializeViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeData();
    }
    
    private void initializeViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        fabAddCategory = findViewById(R.id.fabAddCategory);
    }
    
    private void initializeViewModel() {
        // Initialize database and repositories using activity context
        // This ensures we use the same database instance as MainActivity
        AppDatabase database = AppDatabase.getDatabase(this);
        ActivityRepositoryImpl activityRepository = new ActivityRepositoryImpl(database.activityDao());
        CategoryRepositoryImpl categoryRepository = new CategoryRepositoryImpl(database.categoryDao());
        ActivityServiceImpl activityService = new ActivityServiceImpl(activityRepository);
        CategoryServiceImpl categoryService = new CategoryServiceImpl(categoryRepository);
        ViewModelFactory factory = new ViewModelFactory(activityService, categoryService);
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
    }
    
    private void setupRecyclerView() {
        adapter = new CategoryAdapter();
        adapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                // Handle category click - could show activities for this category
                // TODO: Implement filter by category functionality
            }
            
            @Override
            public void onCategoryLongClick(Category category) {
                // Handle long click - could show edit/delete options
                // TODO: Implement edit/delete category functionality
            }
        });
        
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategories.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());
        
        // Handle back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_revert);
        }
    }
    
    private void observeData() {
        viewModel.getAllCategories().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                adapter.setCategories(categories);
                layoutEmpty.setVisibility(View.GONE);
                recyclerViewCategories.setVisibility(View.VISIBLE);
            } else {
                layoutEmpty.setVisibility(View.VISIBLE);
                recyclerViewCategories.setVisibility(View.GONE);
            }
        });
    }
    
    private void showAddCategoryDialog() {
        AddCategoryDialog dialog = AddCategoryDialog.newInstance();
        
        dialog.setOnCategoryAddedListener((name, description, color, icon) -> {
            viewModel.addCategory(name, color, icon);
        });
        
        dialog.show(getSupportFragmentManager(), "AddCategoryDialog");
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
