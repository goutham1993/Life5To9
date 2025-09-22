package com.journal.life5to9;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.journal.life5to9.data.database.AppDatabase;
import com.journal.life5to9.data.repository.impl.ActivityRepositoryImpl;
import com.journal.life5to9.data.repository.impl.CategoryRepositoryImpl;
import com.journal.life5to9.service.impl.ActivityServiceImpl;
import com.journal.life5to9.service.impl.CategoryServiceImpl;
import com.journal.life5to9.viewmodel.MainViewModel;
import com.journal.life5to9.viewmodel.ViewModelFactory;

import java.util.Date;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Test database initialization
        testDatabase();
    }
    
    private void testDatabase() {
        try {
            // Initialize database and repositories
            AppDatabase database = AppDatabase.getDatabase(this);
            
            ActivityRepositoryImpl activityRepository = new ActivityRepositoryImpl(database.activityDao());
            CategoryRepositoryImpl categoryRepository = new CategoryRepositoryImpl(database.categoryDao());
            
            ActivityServiceImpl activityService = new ActivityServiceImpl(activityRepository);
            CategoryServiceImpl categoryService = new CategoryServiceImpl(categoryRepository);
            
            ViewModelFactory factory = new ViewModelFactory(activityService, categoryService);
            MainViewModel viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
            
            // Test adding a sample activity
            viewModel.addActivity(1, "Test activity", 2.5, new Date());
            
            Toast.makeText(this, "Database test successful!", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Toast.makeText(this, "Database test failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
