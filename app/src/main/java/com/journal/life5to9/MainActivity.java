package com.journal.life5to9;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.journal.life5to9.data.database.AppDatabase;
import com.journal.life5to9.data.repository.impl.ActivityRepositoryImpl;
import com.journal.life5to9.data.repository.impl.CategoryRepositoryImpl;
import com.journal.life5to9.service.impl.ActivityServiceImpl;
import com.journal.life5to9.service.impl.CategoryServiceImpl;
import com.journal.life5to9.ui.adapters.ViewPagerAdapter;
import com.journal.life5to9.service.DailyReminderService;
import com.journal.life5to9.ui.dialogs.AddActivityDialog;
import com.journal.life5to9.ui.dialogs.AddCategoryDialog;
import com.journal.life5to9.ui.fragments.ActivityFragment;
import com.journal.life5to9.ui.fragments.CategoriesFragment;
import com.journal.life5to9.utils.NotificationScheduler;
import com.journal.life5to9.viewmodel.MainViewModel;
import com.journal.life5to9.viewmodel.ViewModelFactory;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fab;
    private ViewPagerAdapter viewPagerAdapter;
    private MainViewModel viewModel;
    private ActivityFragment activityFragment;
    private boolean isAddActivityDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupToolbar();
        setupViewPager();
        setupFAB();
        initializeViewModel();
        initializeNotifications();
        handleNotificationIntent();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fab = findViewById(R.id.fab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Life 5To9");
        }
    }

    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        
        // Add page change callback to handle FAB visibility
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Hide FAB on Summary (2), Calendar (3), and Insights (4) tabs
                if (position == 2 || position == 3 || position == 4) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Activities");
                    break;
                case 1:
                    tab.setText("Categories");
                    break;
                case 2:
                    tab.setText("Summary");
                    break;
                case 3:
                    tab.setText("Calendar");
                    break;
                case 4:
                    tab.setText("Insights");
                    break;
            }
        }).attach();
    }

    private void setupFAB() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show different dialogs based on current tab
                int currentTab = viewPager.getCurrentItem();
                switch (currentTab) {
                    case 0: // Activities tab
                        showAddActivityDialog();
                        break;
                    case 1: // Categories tab
                        showAddCategoryDialog();
                        break;
                    case 2: // Summary tab
                        // Summary tab doesn't need FAB functionality
                        break;
                    case 3: // Calendar tab
                        // Calendar tab doesn't need FAB functionality
                        break;
                    case 4: // Insights tab
                        // Insights tab doesn't need FAB functionality
                        break;
                }
            }
        });
    }

    private void initializeViewModel() {
        // Initialize database and repositories
        AppDatabase database = AppDatabase.getDatabase(this);
        
        ActivityRepositoryImpl activityRepository = new ActivityRepositoryImpl(database.activityDao());
        CategoryRepositoryImpl categoryRepository = new CategoryRepositoryImpl(database.categoryDao());
        
        ActivityServiceImpl activityService = new ActivityServiceImpl(activityRepository);
        CategoryServiceImpl categoryService = new CategoryServiceImpl(categoryRepository);
        
        ViewModelFactory factory = new ViewModelFactory(activityService, categoryService);
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
    }

    private void showAddActivityDialog() {
        // Check if dialog is already showing
        if (isAddActivityDialogShowing) {
            return; // Dialog is already showing, don't show another one
        }
        
        // Dismiss any existing dialog first
        DialogFragment existingDialog = (DialogFragment) getSupportFragmentManager().findFragmentByTag("AddActivityDialog");
        if (existingDialog != null) {
            existingDialog.dismiss();
        }
        
        isAddActivityDialogShowing = true;
        AddActivityDialog dialog = AddActivityDialog.newInstance();
        
        // Get the selected date from the ActivityFragment
        Date selectedDate = getSelectedDateFromActivityFragment();
        android.util.Log.d("MainActivity", "Selected date for AddActivityDialog: " + selectedDate);
        
        // Set up the listener
        dialog.setOnActivityAddedListener((categoryId, notes, timeSpent, date) -> {
            viewModel.addActivity(categoryId, notes, timeSpent, date);
            // Add some debugging
            android.util.Log.d("MainActivity", "Activity added: categoryId=" + categoryId + ", notes=" + notes + ", timeSpent=" + timeSpent + ", date=" + date);
            
            // Show success toast
            Toast.makeText(this, "Activity saved successfully!", Toast.LENGTH_SHORT).show();
            
            // Force refresh of activities list
            refreshActivitiesList();
            
            // Reset the flag when dialog is dismissed
            isAddActivityDialogShowing = false;
        });
        
        // Add dismiss listener to reset flag
        dialog.setOnDismissListener(() -> {
            isAddActivityDialogShowing = false;
        });
        
        // Load categories first, then show dialog
        viewModel.getAllCategories().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                dialog.setCategories(categories);
                dialog.setSelectedDate(selectedDate);
                dialog.show(getSupportFragmentManager(), "AddActivityDialog");
            } else {
                // Show dialog even if no categories (for debugging)
                dialog.setSelectedDate(selectedDate);
                dialog.show(getSupportFragmentManager(), "AddActivityDialog");
            }
        });
    }
    
    public void loadNotesForCategory(long categoryId, AddActivityDialog dialog) {
        viewModel.getDistinctNotesByCategory(categoryId).observe(this, notes -> {
            if (notes != null && dialog != null) {
                dialog.setNotesSuggestions(notes);
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
    
    private void refreshActivitiesList() {
        // Force refresh by observing all activities and then filtering by date
        viewModel.getAllActivities().observe(this, activities -> {
            android.util.Log.d("MainActivity", "All activities count: " + (activities != null ? activities.size() : 0));
            // This will trigger the ActivityFragment to refresh
        });
    }
    
    private Date getSelectedDateFromActivityFragment() {
        // Get the ActivityFragment from the ViewPager adapter
        if (viewPagerAdapter != null) {
            ActivityFragment fragment = viewPagerAdapter.getActivityFragment();
            if (fragment != null && fragment.isAdded()) {
                return fragment.getSelectedDate();
            }
        }
        
        // Fallback to current date if fragment is not available
        return new Date();
    }
    
    private void initializeNotifications() {
        // Create notification channel
        DailyReminderService.createNotificationChannel(this);
        
        // Schedule daily reminder if enabled
        android.content.SharedPreferences prefs = getSharedPreferences("life5to9_settings", MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("notification_enabled", true);
        if (notificationsEnabled) {
            int hour = prefs.getInt("notification_hour", 21);
            int minute = prefs.getInt("notification_minute", 0);
            NotificationScheduler.scheduleDailyReminder(this, hour, minute);
        }
    }
    
    private void handleNotificationIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("open_add_activity", false)) {
            // Switch to Activities tab and open AddActivityDialog
            viewPager.setCurrentItem(0, false);
            
            // Delay to ensure fragment is ready
            viewPager.post(() -> {
                showAddActivityDialog();
            });
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_manage_categories) {
            Intent manageCategoriesIntent = new Intent(this, ManageCategoriesActivity.class);
            startActivity(manageCategoriesIntent);
            return true;
        }
        
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    public MainViewModel getViewModel() {
        return viewModel;
    }
}