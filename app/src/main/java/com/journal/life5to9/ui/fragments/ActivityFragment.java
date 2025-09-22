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
import com.journal.life5to9.ui.dialogs.EditActivityDialog;
import com.journal.life5to9.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityFragment extends Fragment {
    
    private MainViewModel viewModel;
    private RecyclerView recyclerViewActivities;
    private TextView textViewEmpty;
    private TextView textViewSelectedDate;
    private TextView textViewSelectedDay;
    private MaterialButton buttonSelectDate;
    private MaterialButton buttonPreviousDay;
    private MaterialButton buttonNextDay;
    private ActivityAdapter adapter;
    private List<Category> categories;
    
    private Date selectedDate;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dayFormat;
    
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
        
        // Set initial date display
        updateSelectedDateDisplay();
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
        
        recyclerViewActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewActivities.setAdapter(adapter);
        
        // Load categories for the adapter
        loadCategories();
    }
    
    
    private void setupClickListeners() {
        buttonSelectDate.setOnClickListener(v -> showDatePickerDialog());
        buttonPreviousDay.setOnClickListener(v -> goToPreviousDay());
        buttonNextDay.setOnClickListener(v -> goToNextDay());
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
        if (textViewSelectedDate != null) {
            textViewSelectedDate.setText(dateFormat.format(selectedDate));
        }
        if (textViewSelectedDay != null) {
            textViewSelectedDay.setText(dayFormat.format(selectedDate));
        }
        android.util.Log.d("ActivityFragment", "Selected date updated to: " + selectedDate);
        android.util.Log.d("ActivityFragment", "Selected day: " + dayFormat.format(selectedDate));
        android.util.Log.d("ActivityFragment", "Selected date timestamp: " + selectedDate.getTime());
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
        // Load activities for the initially selected date (today)
        loadActivitiesForSelectedDate();
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
        cal.add(Calendar.DAY_OF_MONTH, -1);
        // Normalize to start of day
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDate = cal.getTime();
        updateSelectedDateDisplay();
        loadActivitiesForSelectedDate();
    }
    
    private void goToNextDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        // Normalize to start of day
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDate = cal.getTime();
        updateSelectedDateDisplay();
        loadActivitiesForSelectedDate();
    }
    
    // Method to get the currently selected date (for use by MainActivity)
    public Date getSelectedDate() {
        return selectedDate;
    }
    
    // Method to refresh activities (for use by MainActivity)
    public void refreshActivities() {
        loadActivitiesForSelectedDate();
    }
}
