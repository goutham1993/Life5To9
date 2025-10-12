package com.journal.life5to9.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.ui.adapters.ActivityAdapter;
import com.journal.life5to9.ui.adapters.DayActivitiesAdapter;
import com.journal.life5to9.ui.dialogs.EditActivityDialog;
import com.journal.life5to9.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityFragment extends Fragment {
    
    private MainViewModel viewModel;
    private RecyclerView recyclerViewActivities;
    private TextView textViewEmpty;
    private TextView textViewSelectedDate;
    private TextView textViewSelectedDay;
    private MaterialButton buttonSelectDate;
    private MaterialButton buttonPreviousDay;
    private MaterialButton buttonNextDay;
    private MaterialButton buttonDailyView;
    private MaterialButton buttonWeeklyView;
    private ActivityAdapter adapter;
    private DayActivitiesAdapter dayAdapter;
    private List<Category> categories;
    
    private Date selectedDate;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dayFormat;
    
    // View mode constants
    private static final int VIEW_DAILY = 0;
    private static final int VIEW_WEEKLY = 1;
    private int currentViewMode = VIEW_DAILY;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        
        // Initialize selected date to today (normalized to start of day)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDate = cal.getTime();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupClickListeners();
        observeData();
        
        return view;
    }
    
    private void initializeViews(View view) {
        recyclerViewActivities = view.findViewById(R.id.recyclerViewActivities);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        textViewSelectedDate = view.findViewById(R.id.textViewSelectedDate);
        textViewSelectedDay = view.findViewById(R.id.textViewSelectedDay);
        buttonSelectDate = view.findViewById(R.id.buttonSelectDate);
        buttonPreviousDay = view.findViewById(R.id.buttonPreviousDay);
        buttonNextDay = view.findViewById(R.id.buttonNextDay);
        buttonDailyView = view.findViewById(R.id.buttonDailyView);
        buttonWeeklyView = view.findViewById(R.id.buttonWeeklyView);
        
        // Set initial date display
        updateSelectedDateDisplay();
        updateViewSelector();
    }
    
    private void setupRecyclerView() {
        adapter = new ActivityAdapter();
        adapter.setOnActivityClickListener(new ActivityAdapter.OnActivityClickListener() {
            @Override
            public void onActivityClick(Activity activity) {
                showEditActivityDialog(activity);
            }
            
            @Override
            public void onActivityLongClick(Activity activity) {
                // This is now handled by the delete listener
            }
        });
        
        adapter.setOnActivityDeleteListener(activity -> {
            showDeleteConfirmationDialog(activity);
        });
        
        // Initialize day adapter for weekly view
        dayAdapter = new DayActivitiesAdapter(getContext());
        
        recyclerViewActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewActivities.setAdapter(adapter);
        
        // Load categories for the adapters
        loadCategories();
    }
    
    
    private void setupClickListeners() {
        buttonSelectDate.setOnClickListener(v -> showDatePickerDialog());
        buttonPreviousDay.setOnClickListener(v -> goToPreviousDay());
        buttonNextDay.setOnClickListener(v -> goToNextDay());
        
        // View selector buttons
        buttonDailyView.setOnClickListener(v -> {
            android.util.Log.d("ActivityFragment", "Daily button clicked");
            setViewMode(VIEW_DAILY);
        });
        buttonWeeklyView.setOnClickListener(v -> {
            android.util.Log.d("ActivityFragment", "Weekly button clicked");
            setViewMode(VIEW_WEEKLY);
        });
    }
    
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                // Normalize to start of day
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
                selectedCalendar.set(Calendar.MINUTE, 0);
                selectedCalendar.set(Calendar.SECOND, 0);
                selectedCalendar.set(Calendar.MILLISECOND, 0);
                selectedDate = selectedCalendar.getTime();
                updateSelectedDateDisplay();
                loadActivitiesForSelectedDate();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }
    
    private void updateSelectedDateDisplay() {
        if (currentViewMode == VIEW_WEEKLY) {
            // Show week range for weekly view
            String weekRange = formatWeekRange(selectedDate);
            if (textViewSelectedDate != null) {
                textViewSelectedDate.setText(weekRange);
            }
            if (textViewSelectedDay != null) {
                textViewSelectedDay.setText("Week");
            }
        } else {
            // Show individual date for daily view
            if (textViewSelectedDate != null) {
                textViewSelectedDate.setText(dateFormat.format(selectedDate));
            }
            if (textViewSelectedDay != null) {
                textViewSelectedDay.setText(dayFormat.format(selectedDate));
            }
        }
        android.util.Log.d("ActivityFragment", "Selected date updated to: " + selectedDate);
        android.util.Log.d("ActivityFragment", "View mode: " + currentViewMode);
    }
    
    private void loadActivitiesForSelectedDate() {
        android.util.Log.d("ActivityFragment", "Loading activities for date: " + selectedDate);
        
        // Calculate start and end of day
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(selectedDate);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        Date startOfDay = startCal.getTime();
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(selectedDate);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        Date endOfDay = endCal.getTime();
        
        android.util.Log.d("ActivityFragment", "Querying activities from " + startOfDay + " to " + endOfDay);
        android.util.Log.d("ActivityFragment", "Start timestamp: " + startOfDay.getTime() + ", End timestamp: " + endOfDay.getTime());
        
        viewModel.getActivitiesByDate(startOfDay, endOfDay).observe(getViewLifecycleOwner(), activities -> {
            android.util.Log.d("ActivityFragment", "Received activities: " + (activities != null ? activities.size() : 0));
            if (activities != null && !activities.isEmpty()) {
                adapter.setActivities(activities);
                textViewEmpty.setVisibility(View.GONE);
                recyclerViewActivities.setVisibility(View.VISIBLE);
            } else {
                textViewEmpty.setVisibility(View.VISIBLE);
                recyclerViewActivities.setVisibility(View.GONE);
            }
        });
    }
    
    private void observeData() {
        // Load activities for the initially selected view mode
        loadActivitiesForCurrentView();
    }
    
    private void loadCategories() {
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && adapter != null) {
                this.categories = categories;
                adapter.setCategories(categories);
            }
        });
    }
    
    private void showEditActivityDialog(Activity activity) {
        EditActivityDialog dialog = EditActivityDialog.newInstance(activity);
        
        dialog.setCategories(categories);
        dialog.setOnActivityUpdatedListener(updatedActivity -> {
            viewModel.updateActivity(updatedActivity);
            // Refresh the activities list
            loadActivitiesForSelectedDate();
        });
        
        dialog.show(getParentFragmentManager(), "EditActivityDialog");
    }
    
    private void showDeleteConfirmationDialog(Activity activity) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Activity")
            .setMessage("Are you sure you want to delete this activity? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> {
                viewModel.deleteActivity(activity);
                // Refresh the activities list
                loadActivitiesForSelectedDate();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void goToPreviousDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        
        if (currentViewMode == VIEW_DAILY) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        } else if (currentViewMode == VIEW_WEEKLY) {
            cal.add(Calendar.WEEK_OF_YEAR, -1);
        }
        
        // Normalize to start of day
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDate = cal.getTime();
        updateSelectedDateDisplay();
        loadActivitiesForCurrentView();
    }
    
    private void goToNextDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        
        if (currentViewMode == VIEW_DAILY) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        } else if (currentViewMode == VIEW_WEEKLY) {
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        }
        
        // Normalize to start of day
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDate = cal.getTime();
        updateSelectedDateDisplay();
        loadActivitiesForCurrentView();
    }
    
    // Method to get the currently selected date (for use by MainActivity)
    public Date getSelectedDate() {
        return selectedDate;
    }
    
    // Method to refresh activities (for use by MainActivity)
    public void refreshActivities() {
        loadActivitiesForSelectedDate();
    }
    
    private void setViewMode(int viewMode) {
        android.util.Log.d("ActivityFragment", "setViewMode called with: " + viewMode);
        currentViewMode = viewMode;
        updateViewSelector();
        updateDateHeaderVisibility();
        updateSelectedDateDisplay(); // Add this to update the display when switching views
        loadActivitiesForCurrentView();
    }
    
    private void updateViewSelector() {
        // Reset all buttons to unselected state
        buttonDailyView.setSelected(false);
        buttonWeeklyView.setSelected(false);
        
        // Reset button colors to default
        buttonDailyView.setBackgroundTintList(null);
        buttonDailyView.setTextColor(getResources().getColorStateList(R.color.primary, null));
        buttonWeeklyView.setBackgroundTintList(null);
        buttonWeeklyView.setTextColor(getResources().getColorStateList(R.color.primary, null));
        
        // Set selected button with orange highlighting
        switch (currentViewMode) {
            case VIEW_DAILY:
                buttonDailyView.setSelected(true);
                buttonDailyView.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
                buttonDailyView.setTextColor(getResources().getColorStateList(android.R.color.white, null));
                break;
            case VIEW_WEEKLY:
                buttonWeeklyView.setSelected(true);
                buttonWeeklyView.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
                buttonWeeklyView.setTextColor(getResources().getColorStateList(android.R.color.white, null));
                break;
        }
    }
    
    private void updateDateHeaderVisibility() {
        // Show date navigation for both Daily and Weekly views
        textViewSelectedDate.setVisibility(View.VISIBLE);
        textViewSelectedDay.setVisibility(View.VISIBLE);
        buttonSelectDate.setVisibility(View.VISIBLE);
        buttonPreviousDay.setVisibility(View.VISIBLE);
        buttonNextDay.setVisibility(View.VISIBLE);
    }
    
    private void loadActivitiesForCurrentView() {
        switch (currentViewMode) {
            case VIEW_DAILY:
                loadDailyActivities();
                break;
            case VIEW_WEEKLY:
                loadWeeklyActivities();
                break;
        }
    }
    
    private void loadDailyActivities() {
        // Switch back to regular adapter for daily view
        recyclerViewActivities.setAdapter(adapter);
        // Load activities for the selected date (same as current implementation)
        loadActivitiesForSelectedDate();
    }
    
    private void loadWeeklyActivities() {
        android.util.Log.d("ActivityFragment", "loadWeeklyActivities called");
        android.util.Log.d("ActivityFragment", "Selected date: " + selectedDate);
        
        // Calculate week start and end dates
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        
        // Get the day of week (1=Sunday, 2=Monday, etc.)
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        
        // Calculate days to subtract to get to Monday
        // If it's Sunday (1), subtract 6 days to get to Monday
        // If it's Monday (2), subtract 0 days
        // If it's Tuesday (3), subtract 1 day, etc.
        int daysToSubtract = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        cal.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
        
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date weekStart = cal.getTime();
        
        cal.add(Calendar.DAY_OF_WEEK, 6);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date weekEnd = cal.getTime();
        
        android.util.Log.d("ActivityFragment", "Week range: " + weekStart + " to " + weekEnd);
        
        // Load activities for the week
        viewModel.getActivitiesByDate(weekStart, weekEnd).observe(getViewLifecycleOwner(), activities -> {
            android.util.Log.d("ActivityFragment", "Weekly activities received: " + (activities != null ? activities.size() : 0));
            if (activities != null && !activities.isEmpty()) {
                // Group activities by day
                List<DayActivitiesAdapter.DayActivities> dayActivitiesList = groupActivitiesByDay(activities, weekStart);
                
                // Switch to day adapter for weekly view
                recyclerViewActivities.setAdapter(dayAdapter);
                dayAdapter.setDayActivities(dayActivitiesList);
                dayAdapter.setCategories(categories);
                
                textViewEmpty.setVisibility(View.GONE);
                recyclerViewActivities.setVisibility(View.VISIBLE);
            } else {
                textViewEmpty.setVisibility(View.VISIBLE);
                recyclerViewActivities.setVisibility(View.GONE);
            }
        });
    }
    
    
    private List<DayActivitiesAdapter.DayActivities> groupActivitiesByDay(List<Activity> activities, Date weekStart) {
        Map<String, List<Activity>> dayMap = new HashMap<>();
        
        // Initialize all days of the week
        Calendar cal = Calendar.getInstance();
        cal.setTime(weekStart);
        
        for (int i = 0; i < 7; i++) {
            String dayKey = String.format("%04d-%02d-%02d", 
                cal.get(Calendar.YEAR), 
                cal.get(Calendar.MONTH) + 1, 
                cal.get(Calendar.DAY_OF_MONTH));
            dayMap.put(dayKey, new ArrayList<>());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        // Group activities by day
        for (Activity activity : activities) {
            Calendar activityCal = Calendar.getInstance();
            activityCal.setTime(activity.getDate());
            String dayKey = String.format("%04d-%02d-%02d", 
                activityCal.get(Calendar.YEAR), 
                activityCal.get(Calendar.MONTH) + 1, 
                activityCal.get(Calendar.DAY_OF_MONTH));
            
            if (dayMap.containsKey(dayKey)) {
                dayMap.get(dayKey).add(activity);
            }
        }
        
        // Create DayActivities objects
        List<DayActivitiesAdapter.DayActivities> dayActivitiesList = new ArrayList<>();
        cal.setTime(weekStart);
        
        for (int i = 0; i < 7; i++) {
            String dayKey = String.format("%04d-%02d-%02d", 
                cal.get(Calendar.YEAR), 
                cal.get(Calendar.MONTH) + 1, 
                cal.get(Calendar.DAY_OF_MONTH));
            
            List<Activity> dayActivities = dayMap.get(dayKey);
            if (dayActivities != null) {
                dayActivitiesList.add(new DayActivitiesAdapter.DayActivities(cal.getTime(), dayActivities));
            }
            
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return dayActivitiesList;
    }
    
    private String formatWeekRange(Date date) {
        android.util.Log.d("ActivityFragment", "formatWeekRange called with date: " + date);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        // Get the day of week (1=Sunday, 2=Monday, etc.)
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        android.util.Log.d("ActivityFragment", "Day of week: " + dayOfWeek);
        
        // Calculate days to subtract to get to Monday
        int daysToSubtract = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        android.util.Log.d("ActivityFragment", "Days to subtract: " + daysToSubtract);
        cal.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
        
        // Get Monday date
        Date monday = cal.getTime();
        android.util.Log.d("ActivityFragment", "Monday date: " + monday);
        
        // Get Sunday date (6 days later)
        cal.add(Calendar.DAY_OF_MONTH, 6);
        Date sunday = cal.getTime();
        android.util.Log.d("ActivityFragment", "Sunday date: " + sunday);
        
        // Format the range
        SimpleDateFormat dayFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String weekRange = dayFormat.format(monday) + " - " + dayFormat.format(sunday);
        android.util.Log.d("ActivityFragment", "Formatted week range: " + weekRange);
        
        return weekRange;
    }
}
