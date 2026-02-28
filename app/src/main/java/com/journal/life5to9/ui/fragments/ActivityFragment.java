package com.journal.life5to9.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.ui.adapters.ActivityAdapter;
import com.journal.life5to9.ui.adapters.DayActivitiesAdapter;
import com.journal.life5to9.ui.dialogs.EditActivityDialog;
import com.journal.life5to9.utils.CategoryEmojiMapper;
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
    private View emptyStateLayout;
    private TextView textViewSelectedDate;
    private TextView textViewSelectedDay;
    private MaterialButton buttonSelectDate;
    private MaterialButton buttonPreviousDay;
    private MaterialButton buttonNextDay;
    private MaterialButton buttonDailyView;
    private MaterialButton buttonWeeklyView;
    private MaterialButtonToggleGroup toggleGroupViewMode;
    private ActivityAdapter adapter;
    private DayActivitiesAdapter dayAdapter;
    private LinearLayout layoutQuickAdd;
    private ChipGroup chipGroupQuickAdd;
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
        emptyStateLayout = view.findViewById(R.id.emptyState);
        textViewSelectedDate = view.findViewById(R.id.textViewSelectedDate);
        textViewSelectedDay = view.findViewById(R.id.textViewSelectedDay);
        buttonSelectDate = view.findViewById(R.id.buttonSelectDate);
        buttonPreviousDay = view.findViewById(R.id.buttonPreviousDay);
        buttonNextDay = view.findViewById(R.id.buttonNextDay);
        buttonDailyView = view.findViewById(R.id.buttonDailyView);
        buttonWeeklyView = view.findViewById(R.id.buttonWeeklyView);
        toggleGroupViewMode = view.findViewById(R.id.toggleGroupViewMode);
        layoutQuickAdd = view.findViewById(R.id.layoutQuickAdd);
        chipGroupQuickAdd = view.findViewById(R.id.chipGroupQuickAdd);

        // Set empty state text
        if (emptyStateLayout != null) {
            TextView emptyTitle = emptyStateLayout.findViewById(R.id.textViewEmptyTitle);
            TextView emptyBody = emptyStateLayout.findViewById(R.id.textViewEmptyBody);
            if (emptyTitle != null) emptyTitle.setText("No activities yet");
            if (emptyBody != null) emptyBody.setText("Tap the + button to log your first activity");
        }

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
                // Handled by delete listener
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

        // MaterialButtonToggleGroup handles selection state automatically
        toggleGroupViewMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.buttonDailyView) {
                    setViewMode(VIEW_DAILY);
                } else if (checkedId == R.id.buttonWeeklyView) {
                    setViewMode(VIEW_WEEKLY);
                }
            }
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
            String weekRange = formatWeekRange(selectedDate);
            if (textViewSelectedDate != null) {
                textViewSelectedDate.setText(weekRange);
            }
            if (textViewSelectedDay != null) {
                textViewSelectedDay.setText("Week");
            }
        } else {
            if (textViewSelectedDate != null) {
                textViewSelectedDate.setText(dateFormat.format(selectedDate));
            }
            if (textViewSelectedDay != null) {
                textViewSelectedDay.setText(dayFormat.format(selectedDate));
            }
        }
    }

    private void loadActivitiesForSelectedDate() {
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

        viewModel.getActivitiesByDate(startOfDay, endOfDay).observe(getViewLifecycleOwner(), activities -> {
            if (activities != null && !activities.isEmpty()) {
                adapter.setActivities(activities);
                showContent();
            } else {
                showEmpty();
            }
        });
    }

    private void showContent() {
        if (emptyStateLayout != null) emptyStateLayout.setVisibility(View.GONE);
        recyclerViewActivities.setVisibility(View.VISIBLE);
    }

    private void showEmpty() {
        if (emptyStateLayout != null) emptyStateLayout.setVisibility(View.VISIBLE);
        recyclerViewActivities.setVisibility(View.GONE);
    }

    private void observeData() {
        loadActivitiesForCurrentView();
        loadRecentActivities();
    }

    private void loadCategories() {
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && adapter != null) {
                this.categories = categories;
                adapter.setCategories(categories);
            }
        });
    }

    private void quickAddActivity(Activity activity) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);

        Calendar now = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date activityDate = cal.getTime();

        viewModel.addActivity(
            activity.getCategoryId(),
            activity.getNotes(),
            activity.getTimeSpentHours(),
            activityDate
        );

        Toast.makeText(getContext(), "Activity added!", Toast.LENGTH_SHORT).show();
        loadActivitiesForSelectedDate();
    }

    private void loadRecentActivities() {
        viewModel.getRecentActivities(5).observe(getViewLifecycleOwner(), activities -> {
            if (activities != null && !activities.isEmpty() && chipGroupQuickAdd != null) {
                populateQuickAddChips(activities);
                if (currentViewMode == VIEW_DAILY) {
                    layoutQuickAdd.setVisibility(View.VISIBLE);
                }
            } else {
                if (layoutQuickAdd != null) {
                    layoutQuickAdd.setVisibility(View.GONE);
                }
            }
        });
    }

    private void populateQuickAddChips(List<Activity> activities) {
        chipGroupQuickAdd.removeAllViews();

        Map<Long, Category> categoryMap = new HashMap<>();
        if (categories != null) {
            for (Category cat : categories) {
                categoryMap.put(cat.getId(), cat);
            }
        }

        for (Activity activity : activities) {
            Chip chip = new Chip(requireContext());
            chip.setChipBackgroundColorResource(R.color.surfaceContainerLow);
            chip.setChipStrokeColorResource(R.color.outlineVariant);
            chip.setChipStrokeWidth(getResources().getDimension(R.dimen.stroke_thin));
            chip.setChipCornerRadius(getResources().getDimension(R.dimen.radius_chip));
            chip.setEnsureMinTouchTargetSize(false);

            Category cat = categoryMap.get(activity.getCategoryId());
            String categoryName = cat != null ? cat.getName() : "Activity";
            String emoji = CategoryEmojiMapper.getEmojiForCategory(categoryName);
            String notes = activity.getNotes() != null && !activity.getNotes().isEmpty()
                    ? activity.getNotes() : categoryName;
            String label = emoji + " " + notes + " (" + String.format(Locale.getDefault(), "%.1fh", activity.getTimeSpentHours()) + ")";
            chip.setText(label);
            chip.setOnClickListener(v -> quickAddActivity(activity));
            chipGroupQuickAdd.addView(chip);
        }
    }

    private void showEditActivityDialog(Activity activity) {
        EditActivityDialog dialog = EditActivityDialog.newInstance(activity);

        dialog.setCategories(categories);
        dialog.setOnActivityUpdatedListener(updatedActivity -> {
            viewModel.updateActivity(updatedActivity);
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

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDate = cal.getTime();
        updateSelectedDateDisplay();
        loadActivitiesForCurrentView();
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void refreshActivities() {
        loadActivitiesForSelectedDate();
    }

    private void setViewMode(int viewMode) {
        currentViewMode = viewMode;
        updateSelectedDateDisplay();

        // Show/hide quick add chips based on view mode
        if (viewMode == VIEW_DAILY) {
            loadRecentActivities();
        } else {
            if (layoutQuickAdd != null) {
                layoutQuickAdd.setVisibility(View.GONE);
            }
        }

        loadActivitiesForCurrentView();
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
        recyclerViewActivities.setAdapter(adapter);
        loadActivitiesForSelectedDate();
        loadRecentActivities();
    }

    private void loadWeeklyActivities() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
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

        viewModel.getActivitiesByDate(weekStart, weekEnd).observe(getViewLifecycleOwner(), activities -> {
            if (activities != null && !activities.isEmpty()) {
                List<DayActivitiesAdapter.DayActivities> dayActivitiesList = groupActivitiesByDay(activities, weekStart);

                recyclerViewActivities.setAdapter(dayAdapter);
                dayAdapter.setDayActivities(dayActivitiesList);
                dayAdapter.setCategories(categories);

                showContent();
            } else {
                showEmpty();
            }
        });
    }

    private List<DayActivitiesAdapter.DayActivities> groupActivitiesByDay(List<Activity> activities, Date weekStart) {
        Map<String, List<Activity>> dayMap = new HashMap<>();

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
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysToSubtract = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        cal.add(Calendar.DAY_OF_MONTH, -daysToSubtract);

        Date monday = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 6);
        Date sunday = cal.getTime();

        SimpleDateFormat dayFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        return dayFormat.format(monday) + " - " + dayFormat.format(sunday);
    }
}
