package com.journal.life5to9.ui.fragments;

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

import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.ui.adapters.CategorySummaryAdapter;
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

public class SummaryFragment extends Fragment {
    
    private MainViewModel viewModel;
    private TextView textViewWeeklyTotal;
    private TextView textViewMonthlyTotal;
    private TextView textViewWeeklyPeriod;
    private TextView textViewMonthlyPeriod;
    private RecyclerView recyclerViewWeeklyCategoryBreakdown;
    private RecyclerView recyclerViewMonthlyCategoryBreakdown;
    private CategorySummaryAdapter weeklyAdapter;
    private CategorySummaryAdapter monthlyAdapter;
    private List<Category> categories;
    
    // Dropdown functionality
    private com.google.android.material.button.MaterialButton buttonWeeklyDropdown;
    private com.google.android.material.button.MaterialButton buttonMonthlyDropdown;
    private android.widget.LinearLayout layoutWeeklyBreakdown;
    private android.widget.LinearLayout layoutMonthlyBreakdown;
    private boolean isWeeklyExpanded = false;
    private boolean isMonthlyExpanded = false;
    
    /** Shared All / Weekday / Weekend tab values (weekly + monthly breakdown). */
    private static final int TAB_WEEKDAY = 0;
    private static final int TAB_WEEKEND = 1;
    private static final int TAB_ALL = 2;
    private int currentWeeklyTabMode = TAB_ALL;
    private int currentMonthlyTabMode = TAB_ALL;
    
    private List<Activity> currentWeeklyActivities = new ArrayList<>();
    private List<Activity> currentMonthlyActivities = new ArrayList<>();
    
    // Navigation functionality
    private com.google.android.material.button.MaterialButton buttonWeeklyPrevious;
    private com.google.android.material.button.MaterialButton buttonWeeklyNext;
    private com.google.android.material.button.MaterialButton buttonMonthlyPrevious;
    private com.google.android.material.button.MaterialButton buttonMonthlyNext;
    private com.google.android.material.button.MaterialButton buttonMonthlyWeekday;
    private com.google.android.material.button.MaterialButton buttonMonthlyWeekend;
    private com.google.android.material.button.MaterialButton buttonMonthlyAll;
    private com.google.android.material.button.MaterialButton buttonWeeklyWeekday;
    private com.google.android.material.button.MaterialButton buttonWeeklyWeekend;
    private com.google.android.material.button.MaterialButton buttonWeeklyAll;
    
    /** Monday 00:00 of the week shown in the weekly card. */
    private Date currentWeekStart;
    private Date currentMonthStart;
    private Date originalWeekStart;
    private Date originalMonthStart;
    
    // Date formatters
    private SimpleDateFormat weekFormatter;
    private SimpleDateFormat weekdayFormatter;
    private SimpleDateFormat weekendFormatter;
    private SimpleDateFormat monthFormatter;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        
        // Initialize date formatters
        weekFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
        weekdayFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
        weekendFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
        monthFormatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        
        textViewWeeklyTotal = view.findViewById(R.id.textViewWeeklyTotal);
        textViewMonthlyTotal = view.findViewById(R.id.textViewMonthlyTotal);
        textViewWeeklyPeriod = view.findViewById(R.id.textViewWeeklyPeriod);
        textViewMonthlyPeriod = view.findViewById(R.id.textViewMonthlyPeriod);
        recyclerViewWeeklyCategoryBreakdown = view.findViewById(R.id.recyclerViewWeeklyCategoryBreakdown);
        recyclerViewMonthlyCategoryBreakdown = view.findViewById(R.id.recyclerViewMonthlyCategoryBreakdown);
        
        buttonWeeklyDropdown = view.findViewById(R.id.buttonWeeklyDropdown);
        buttonMonthlyDropdown = view.findViewById(R.id.buttonMonthlyDropdown);
        layoutWeeklyBreakdown = view.findViewById(R.id.layoutWeeklyBreakdown);
        layoutMonthlyBreakdown = view.findViewById(R.id.layoutMonthlyBreakdown);
        
        buttonWeeklyPrevious = view.findViewById(R.id.buttonWeeklyPrevious);
        buttonWeeklyNext = view.findViewById(R.id.buttonWeeklyNext);
        buttonMonthlyPrevious = view.findViewById(R.id.buttonMonthlyPrevious);
        buttonMonthlyNext = view.findViewById(R.id.buttonMonthlyNext);
        buttonMonthlyWeekday = view.findViewById(R.id.buttonMonthlyWeekday);
        buttonMonthlyWeekend = view.findViewById(R.id.buttonMonthlyWeekend);
        buttonMonthlyAll = view.findViewById(R.id.buttonMonthlyAll);
        buttonWeeklyAll = view.findViewById(R.id.buttonWeeklyAll);
        buttonWeeklyWeekday = view.findViewById(R.id.buttonWeeklyWeekday);
        buttonWeeklyWeekend = view.findViewById(R.id.buttonWeeklyWeekend);
        
        setupRecyclerView();
        setupCardBackgrounds(view);
        setupDropdownListeners();
        setupNavigationListeners();
        observeData();
        
        return view;
    }
    
    private void setupCardBackgrounds(View view) {
        // Check if dark theme is enabled
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        
        int backgroundColor;
        if (isDarkTheme) {
            backgroundColor = getContext().getColor(android.R.color.background_dark);
        } else {
            backgroundColor = getContext().getColor(android.R.color.background_light);
        }
        
        // Find all MaterialCardView elements and set their background color
        com.google.android.material.card.MaterialCardView weeklyCard = view.findViewById(R.id.cardWeekly);
        com.google.android.material.card.MaterialCardView monthlyCard = view.findViewById(R.id.cardMonthly);
        
        if (weeklyCard != null) {
            weeklyCard.setCardBackgroundColor(backgroundColor);
        }
        if (monthlyCard != null) {
            monthlyCard.setCardBackgroundColor(backgroundColor);
        }
    }
    
    private void setupRecyclerView() {
        weeklyAdapter = new CategorySummaryAdapter();
        recyclerViewWeeklyCategoryBreakdown.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewWeeklyCategoryBreakdown.setAdapter(weeklyAdapter);
        
        // Setup monthly breakdown
        monthlyAdapter = new CategorySummaryAdapter();
        recyclerViewMonthlyCategoryBreakdown.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMonthlyCategoryBreakdown.setAdapter(monthlyAdapter);
    }
    
    private void setupDropdownListeners() {
        buttonWeeklyDropdown.setOnClickListener(v -> toggleWeeklyBreakdown());
        buttonMonthlyDropdown.setOnClickListener(v -> toggleMonthlyBreakdown());
        
        buttonWeeklyWeekday.setOnClickListener(v -> setWeeklyTabMode(TAB_WEEKDAY));
        buttonWeeklyWeekend.setOnClickListener(v -> setWeeklyTabMode(TAB_WEEKEND));
        buttonWeeklyAll.setOnClickListener(v -> setWeeklyTabMode(TAB_ALL));
        
        buttonMonthlyWeekday.setOnClickListener(v -> setMonthlyTabMode(TAB_WEEKDAY));
        buttonMonthlyWeekend.setOnClickListener(v -> setMonthlyTabMode(TAB_WEEKEND));
        buttonMonthlyAll.setOnClickListener(v -> setMonthlyTabMode(TAB_ALL));
    }
    
    private void setupNavigationListeners() {
        buttonWeeklyPrevious.setOnClickListener(v -> navigateToPreviousWeek());
        buttonWeeklyNext.setOnClickListener(v -> navigateToNextWeek());
        buttonMonthlyPrevious.setOnClickListener(v -> navigateToPreviousMonth());
        buttonMonthlyNext.setOnClickListener(v -> navigateToNextMonth());
    }
    
    private void toggleWeeklyBreakdown() {
        isWeeklyExpanded = !isWeeklyExpanded;
        if (isWeeklyExpanded) {
            layoutWeeklyBreakdown.setVisibility(android.view.View.VISIBLE);
            buttonWeeklyDropdown.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
            buttonWeeklyDropdown.setIconSize(32);
            updateWeeklyTabSelector();
        } else {
            layoutWeeklyBreakdown.setVisibility(android.view.View.GONE);
            buttonWeeklyDropdown.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_more));
            buttonWeeklyDropdown.setIconSize(32);
        }
    }
    
    private void toggleMonthlyBreakdown() {
        isMonthlyExpanded = !isMonthlyExpanded;
        if (isMonthlyExpanded) {
            layoutMonthlyBreakdown.setVisibility(android.view.View.VISIBLE);
            buttonMonthlyDropdown.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
            buttonMonthlyDropdown.setIconSize(32);
            updateMonthlyTabSelector();
        } else {
            layoutMonthlyBreakdown.setVisibility(android.view.View.GONE);
            buttonMonthlyDropdown.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_more));
            buttonMonthlyDropdown.setIconSize(32);
        }
    }
    
    private void setMonthlyTabMode(int tabMode) {
        currentMonthlyTabMode = tabMode;
        updateMonthlyTabSelector();
        // Refresh monthly breakdown with current activities and new tab mode
        if (currentMonthlyActivities != null && !currentMonthlyActivities.isEmpty()) {
            updateMonthlyCategoryBreakdown(currentMonthlyActivities);
        }
    }
    
    private void updateMonthlyTabSelector() {
        // Check if dark theme is enabled
        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        
        // Reset all buttons to unselected state
        buttonMonthlyWeekday.setSelected(false);
        buttonMonthlyWeekend.setSelected(false);
        buttonMonthlyAll.setSelected(false);
        
        // Reset button colors to default
        buttonMonthlyWeekday.setBackgroundTintList(null);
        buttonMonthlyWeekday.setTextColor(getResources().getColorStateList(R.color.primary, null));
        buttonMonthlyWeekend.setBackgroundTintList(null);
        buttonMonthlyWeekend.setTextColor(getResources().getColorStateList(R.color.primary, null));
        buttonMonthlyAll.setBackgroundTintList(null);
        buttonMonthlyAll.setTextColor(getResources().getColorStateList(R.color.primary, null));
        
        // Set selected button with orange highlighting and unselected buttons with theme-aware background
        switch (currentMonthlyTabMode) {
            case TAB_WEEKDAY:
                buttonMonthlyWeekday.setSelected(true);
                buttonMonthlyWeekday.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
                buttonMonthlyWeekday.setTextColor(getResources().getColorStateList(android.R.color.white, null));
                
                // Set other buttons to unselected state with theme-aware background
                buttonMonthlyWeekend.setSelected(false);
                if (isDarkTheme) {
                    buttonMonthlyWeekend.setBackgroundTintList(getResources().getColorStateList(R.color.surface_variant, null));
                    buttonMonthlyWeekend.setTextColor(getResources().getColorStateList(R.color.on_surface_variant, null));
                } else {
                    buttonMonthlyWeekend.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
                    buttonMonthlyWeekend.setTextColor(getResources().getColorStateList(R.color.primary, null));
                }
                
                buttonMonthlyAll.setSelected(false);
                if (isDarkTheme) {
                    buttonMonthlyAll.setBackgroundTintList(getResources().getColorStateList(R.color.surface_variant, null));
                    buttonMonthlyAll.setTextColor(getResources().getColorStateList(R.color.on_surface_variant, null));
                } else {
                    buttonMonthlyAll.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
                    buttonMonthlyAll.setTextColor(getResources().getColorStateList(R.color.primary, null));
                }
                break;
                
            case TAB_WEEKEND:
                buttonMonthlyWeekend.setSelected(true);
                buttonMonthlyWeekend.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
                buttonMonthlyWeekend.setTextColor(getResources().getColorStateList(android.R.color.white, null));
                
                // Set other buttons to unselected state with theme-aware background
                buttonMonthlyWeekday.setSelected(false);
                if (isDarkTheme) {
                    buttonMonthlyWeekday.setBackgroundTintList(getResources().getColorStateList(R.color.surface_variant, null));
                    buttonMonthlyWeekday.setTextColor(getResources().getColorStateList(R.color.on_surface_variant, null));
                } else {
                    buttonMonthlyWeekday.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
                    buttonMonthlyWeekday.setTextColor(getResources().getColorStateList(R.color.primary, null));
                }
                
                buttonMonthlyAll.setSelected(false);
                if (isDarkTheme) {
                    buttonMonthlyAll.setBackgroundTintList(getResources().getColorStateList(R.color.surface_variant, null));
                    buttonMonthlyAll.setTextColor(getResources().getColorStateList(R.color.on_surface_variant, null));
                } else {
                    buttonMonthlyAll.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
                    buttonMonthlyAll.setTextColor(getResources().getColorStateList(R.color.primary, null));
                }
                break;
                
            case TAB_ALL:
                buttonMonthlyAll.setSelected(true);
                buttonMonthlyAll.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
                buttonMonthlyAll.setTextColor(getResources().getColorStateList(android.R.color.white, null));
                
                // Set other buttons to unselected state with theme-aware background
                buttonMonthlyWeekday.setSelected(false);
                if (isDarkTheme) {
                    buttonMonthlyWeekday.setBackgroundTintList(getResources().getColorStateList(R.color.surface_variant, null));
                    buttonMonthlyWeekday.setTextColor(getResources().getColorStateList(R.color.on_surface_variant, null));
                } else {
                    buttonMonthlyWeekday.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
                    buttonMonthlyWeekday.setTextColor(getResources().getColorStateList(R.color.primary, null));
                }
                
                buttonMonthlyWeekend.setSelected(false);
                if (isDarkTheme) {
                    buttonMonthlyWeekend.setBackgroundTintList(getResources().getColorStateList(R.color.surface_variant, null));
                    buttonMonthlyWeekend.setTextColor(getResources().getColorStateList(R.color.on_surface_variant, null));
                } else {
                    buttonMonthlyWeekend.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
                    buttonMonthlyWeekend.setTextColor(getResources().getColorStateList(R.color.primary, null));
                }
                break;
        }
    }
    
    private void setWeeklyTabMode(int tabMode) {
        currentWeeklyTabMode = tabMode;
        updateWeeklyTabSelector();
        if (currentWeeklyActivities != null && !currentWeeklyActivities.isEmpty()) {
            List<Activity> filtered = filterActivitiesByTab(currentWeeklyActivities, currentWeeklyTabMode);
            textViewWeeklyTotal.setText(String.format(Locale.getDefault(), "%.1f hours", calculateTotalTime(filtered)));
            updateWeeklyCategoryBreakdown(currentWeeklyActivities);
        }
        updatePeriodLabels();
    }
    
    private void updateWeeklyTabSelector() {
        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        
        buttonWeeklyWeekday.setSelected(false);
        buttonWeeklyWeekend.setSelected(false);
        buttonWeeklyAll.setSelected(false);
        
        buttonWeeklyWeekday.setBackgroundTintList(null);
        buttonWeeklyWeekday.setTextColor(getResources().getColorStateList(R.color.primary, null));
        buttonWeeklyWeekend.setBackgroundTintList(null);
        buttonWeeklyWeekend.setTextColor(getResources().getColorStateList(R.color.primary, null));
        buttonWeeklyAll.setBackgroundTintList(null);
        buttonWeeklyAll.setTextColor(getResources().getColorStateList(R.color.primary, null));
        
        switch (currentWeeklyTabMode) {
            case TAB_WEEKDAY:
                buttonWeeklyWeekday.setSelected(true);
                buttonWeeklyWeekday.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
                buttonWeeklyWeekday.setTextColor(getResources().getColorStateList(android.R.color.white, null));
                styleWeeklyTabUnselected(buttonWeeklyWeekend, isDarkTheme);
                styleWeeklyTabUnselected(buttonWeeklyAll, isDarkTheme);
                break;
            case TAB_WEEKEND:
                buttonWeeklyWeekend.setSelected(true);
                buttonWeeklyWeekend.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
                buttonWeeklyWeekend.setTextColor(getResources().getColorStateList(android.R.color.white, null));
                styleWeeklyTabUnselected(buttonWeeklyWeekday, isDarkTheme);
                styleWeeklyTabUnselected(buttonWeeklyAll, isDarkTheme);
                break;
            case TAB_ALL:
                buttonWeeklyAll.setSelected(true);
                buttonWeeklyAll.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
                buttonWeeklyAll.setTextColor(getResources().getColorStateList(android.R.color.white, null));
                styleWeeklyTabUnselected(buttonWeeklyWeekday, isDarkTheme);
                styleWeeklyTabUnselected(buttonWeeklyWeekend, isDarkTheme);
                break;
        }
    }
    
    private void styleWeeklyTabUnselected(com.google.android.material.button.MaterialButton button, boolean isDarkTheme) {
        button.setSelected(false);
        if (isDarkTheme) {
            button.setBackgroundTintList(getResources().getColorStateList(R.color.surface_variant, null));
            button.setTextColor(getResources().getColorStateList(R.color.on_surface_variant, null));
        } else {
            button.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
            button.setTextColor(getResources().getColorStateList(R.color.primary, null));
        }
    }
    
    private void observeData() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        
        calendar.setTime(now);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date mondayStart = calendar.getTime();
        
        calendar.setTime(now);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date monthStart = calendar.getTime();
        
        android.util.Log.d("SummaryFragment", "Week start (Monday): " + mondayStart);
        
        this.currentWeekStart = mondayStart;
        this.currentMonthStart = monthStart;
        this.originalWeekStart = mondayStart;
        this.originalMonthStart = monthStart;
        
        // Observe categories first, then activities
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            this.categories = categories;
            android.util.Log.d("SummaryFragment", "Categories loaded: " + (categories != null ? categories.size() : 0));
            if (categories != null) {
                for (Category category : categories) {
                    android.util.Log.d("SummaryFragment", "Category: ID=" + category.getId() + ", Name=" + category.getName() + ", Color=" + category.getColor());
                }
            }
            
            // Now that categories are loaded, observe activities
            observeActivities();
        });
    }
    
    private void observeActivities() {
        viewModel.getActivitiesForWeek(currentWeekStart).observe(getViewLifecycleOwner(), activities -> {
            currentWeeklyActivities = activities != null ? activities : new ArrayList<>();
            List<Activity> filtered = filterActivitiesByTab(currentWeeklyActivities, currentWeeklyTabMode);
            double weeklyTotal = calculateTotalTime(filtered);
            android.util.Log.d("SummaryFragment", "Week activities count: " + currentWeeklyActivities.size() + ", filtered: " + filtered.size());
            android.util.Log.d("SummaryFragment", "Weekly total (tab): " + weeklyTotal + " hours");
            textViewWeeklyTotal.setText(String.format(Locale.getDefault(), "%.1f hours", weeklyTotal));
            updateWeeklyCategoryBreakdown(currentWeeklyActivities);
        });
        
        // Observe monthly activities
        viewModel.getActivitiesForMonth(currentMonthStart).observe(getViewLifecycleOwner(), activities -> {
            // Store current activities for tab filtering
            currentMonthlyActivities = activities != null ? activities : new ArrayList<>();
            
            double monthlyTotal = calculateTotalTime(activities);
            android.util.Log.d("SummaryFragment", "Monthly activities count: " + (activities != null ? activities.size() : 0));
            android.util.Log.d("SummaryFragment", "Monthly total: " + monthlyTotal + " hours");
            textViewMonthlyTotal.setText(String.format(Locale.getDefault(), "%.1f hours", monthlyTotal));
            updateMonthlyCategoryBreakdown(activities);
        });
        
        // Update period labels
        updatePeriodLabels();
    }
    
    private double calculateTotalTime(List<Activity> activities) {
        if (activities == null) return 0.0;
        
        double total = 0.0;
        for (Activity activity : activities) {
            total += activity.getTimeSpentHours();
        }
        return total;
    }
    
    private void updateWeeklyCategoryBreakdown(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            layoutWeeklyBreakdown.setVisibility(android.view.View.GONE);
            return;
        }
        
        if (isWeeklyExpanded) {
            layoutWeeklyBreakdown.setVisibility(android.view.View.VISIBLE);
        }
        
        List<Activity> filteredCurrent = filterActivitiesByTab(activities, currentWeeklyTabMode);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekStart);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        Date previousWeekStart = calendar.getTime();
        
        viewModel.getActivitiesForPreviousWeek(previousWeekStart, currentWeekStart).observe(getViewLifecycleOwner(), previousActivities -> {
            List<Activity> filteredPrevious = filterActivitiesByTab(previousActivities, currentWeeklyTabMode);
            updateWeeklyCategoryBreakdownWithComparison(filteredCurrent, filteredPrevious);
        });
    }
    
    private void updateWeeklyCategoryBreakdownWithComparison(List<Activity> activities, List<Activity> previousActivities) {
        // Group activities by category
        Map<Long, List<Activity>> categoryActivitiesMap = new HashMap<>();
        Map<Long, Double> categoryTimeMap = new HashMap<>();
        for (Activity activity : activities) {
            long categoryId = activity.getCategoryId();
            double timeSpent = activity.getTimeSpentHours();
            android.util.Log.d("SummaryFragment", "Weekly Activity: ID=" + activity.getId() + ", CategoryID=" + categoryId + ", Time=" + timeSpent + "h, Notes=" + activity.getNotes());
            
            // Add to activities list for this category
            if (!categoryActivitiesMap.containsKey(categoryId)) {
                categoryActivitiesMap.put(categoryId, new ArrayList<>());
            }
            categoryActivitiesMap.get(categoryId).add(activity);
            
            // Add to time total
            categoryTimeMap.put(categoryId, categoryTimeMap.getOrDefault(categoryId, 0.0) + timeSpent);
        }
        
        // Group previous week activities by category for comparison
        Map<Long, Double> previousCategoryTimeMap = new HashMap<>();
        if (previousActivities != null) {
            for (Activity activity : previousActivities) {
                long categoryId = activity.getCategoryId();
                double timeSpent = activity.getTimeSpentHours();
                previousCategoryTimeMap.put(categoryId, previousCategoryTimeMap.getOrDefault(categoryId, 0.0) + timeSpent);
            }
        }
        
        // Create summary items
        List<CategorySummaryAdapter.CategorySummaryItem> summaryItems = new ArrayList<>();
        double totalTime = calculateTotalTime(activities);
        
        for (Map.Entry<Long, Double> entry : categoryTimeMap.entrySet()) {
            long categoryId = entry.getKey();
            double timeSpent = entry.getValue();
            List<Activity> categoryActivities = categoryActivitiesMap.get(categoryId);
            
            android.util.Log.d("SummaryFragment", "Looking up category ID: " + categoryId + " with time: " + timeSpent);
            
            // Look up actual category name and color
            String categoryName = "Unknown Category";
            String categoryColor = "#FF2E7D32"; // Default primary color
            
            if (categories != null) {
                android.util.Log.d("SummaryFragment", "Categories list size: " + categories.size());
                boolean found = false;
                for (Category category : categories) {
                    android.util.Log.d("SummaryFragment", "Checking category: ID=" + category.getId() + ", Name=" + category.getName());
                    if (category.getId() == categoryId) {
                        // Get emoji for the category
                        String emoji = CategoryEmojiMapper.getEmojiForCategory(category.getName());
                        categoryName = emoji + " " + category.getName();
                        categoryColor = category.getColor();
                        android.util.Log.d("SummaryFragment", "Weekly Category FOUND: " + category.getName() + " -> Emoji: " + emoji);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    android.util.Log.w("SummaryFragment", "Category ID " + categoryId + " NOT FOUND in categories list!");
                }
            } else {
                android.util.Log.w("SummaryFragment", "Categories list is null!");
            }
            
            // Get previous week time for this category
            double previousTimeSpent = previousCategoryTimeMap.getOrDefault(categoryId, 0.0);
            
            summaryItems.add(new CategorySummaryAdapter.CategorySummaryItem(
                categoryName, categoryColor, timeSpent, totalTime, categoryActivities, previousTimeSpent
            ));
        }
        
        // Sort by time spent (highest first)
        summaryItems.sort((a, b) -> Double.compare(b.getTimeSpent(), a.getTimeSpent()));
        
        // Debug: Log sorted order
        android.util.Log.d("SummaryFragment", "Weekly breakdown sorted order:");
        for (int i = 0; i < summaryItems.size(); i++) {
            CategorySummaryAdapter.CategorySummaryItem item = summaryItems.get(i);
            android.util.Log.d("SummaryFragment", (i + 1) + ". " + item.getCategoryName() + ": " + item.getTimeSpent() + "h");
        }
        
        weeklyAdapter.setSummaryItems(summaryItems, null);
    }
    
    private void updateMonthlyCategoryBreakdown(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            layoutMonthlyBreakdown.setVisibility(android.view.View.GONE);
            return;
        }
        
        // Only show if expanded
        if (isMonthlyExpanded) {
            layoutMonthlyBreakdown.setVisibility(android.view.View.VISIBLE);
        }
        
        List<Activity> filteredActivities = filterActivitiesByTab(activities, currentMonthlyTabMode);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentMonthStart);
        calendar.add(Calendar.MONTH, -1);
        Date previousMonthStart = calendar.getTime();
        
        viewModel.getActivitiesForPreviousMonth(previousMonthStart, currentMonthStart).observe(getViewLifecycleOwner(), previousActivities -> {
            List<Activity> filteredPreviousActivities = filterActivitiesByTab(previousActivities, currentMonthlyTabMode);
            updateMonthlyCategoryBreakdownWithComparison(filteredActivities, filteredPreviousActivities);
        });
    }
    
    private List<Activity> filterActivitiesByTab(List<Activity> activities, int tabMode) {
        if (activities == null) return new ArrayList<>();
        
        if (tabMode == TAB_ALL) {
            return new ArrayList<>(activities);
        }
        
        List<Activity> filteredActivities = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        
        for (Activity activity : activities) {
            calendar.setTime(activity.getDate());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            
            if (tabMode == TAB_WEEKDAY) {
                if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
                    filteredActivities.add(activity);
                }
            } else if (tabMode == TAB_WEEKEND) {
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    filteredActivities.add(activity);
                }
            }
        }
        
        return filteredActivities;
    }
    
    private void updateMonthlyCategoryBreakdownWithComparison(List<Activity> activities, List<Activity> previousActivities) {
        // Group activities by category
        Map<Long, List<Activity>> categoryActivitiesMap = new HashMap<>();
        Map<Long, Double> categoryTimeMap = new HashMap<>();
        for (Activity activity : activities) {
            long categoryId = activity.getCategoryId();
            double timeSpent = activity.getTimeSpentHours();
            android.util.Log.d("SummaryFragment", "Monthly Activity: ID=" + activity.getId() + ", CategoryID=" + categoryId + ", Time=" + timeSpent + "h, Notes=" + activity.getNotes());
            
            // Add to activities list for this category
            if (!categoryActivitiesMap.containsKey(categoryId)) {
                categoryActivitiesMap.put(categoryId, new ArrayList<>());
            }
            categoryActivitiesMap.get(categoryId).add(activity);
            
            // Add to time total
            categoryTimeMap.put(categoryId, categoryTimeMap.getOrDefault(categoryId, 0.0) + timeSpent);
        }
        
        // Group previous month activities by category for comparison
        Map<Long, Double> previousCategoryTimeMap = new HashMap<>();
        if (previousActivities != null) {
            for (Activity activity : previousActivities) {
                long categoryId = activity.getCategoryId();
                double timeSpent = activity.getTimeSpentHours();
                previousCategoryTimeMap.put(categoryId, previousCategoryTimeMap.getOrDefault(categoryId, 0.0) + timeSpent);
            }
        }
        
        // Create summary items
        List<CategorySummaryAdapter.CategorySummaryItem> summaryItems = new ArrayList<>();
        double totalTime = calculateTotalTime(activities);
        
        for (Map.Entry<Long, Double> entry : categoryTimeMap.entrySet()) {
            long categoryId = entry.getKey();
            double timeSpent = entry.getValue();
            List<Activity> categoryActivities = categoryActivitiesMap.get(categoryId);
            
            android.util.Log.d("SummaryFragment", "Looking up category ID: " + categoryId + " with time: " + timeSpent);
            
            // Look up actual category name and color
            String categoryName = "Unknown Category";
            String categoryColor = "#FF2E7D32"; // Default primary color
            
            if (categories != null) {
                android.util.Log.d("SummaryFragment", "Categories list size: " + categories.size());
                boolean found = false;
                for (Category category : categories) {
                    android.util.Log.d("SummaryFragment", "Checking category: ID=" + category.getId() + ", Name=" + category.getName());
                    if (category.getId() == categoryId) {
                        // Get emoji for the category
                        String emoji = CategoryEmojiMapper.getEmojiForCategory(category.getName());
                        categoryName = emoji + " " + category.getName();
                        categoryColor = category.getColor();
                        android.util.Log.d("SummaryFragment", "Monthly Category FOUND: " + category.getName() + " -> Emoji: " + emoji);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    android.util.Log.w("SummaryFragment", "Category ID " + categoryId + " NOT FOUND in categories list!");
                }
            } else {
                android.util.Log.w("SummaryFragment", "Categories list is null!");
            }
            
            // Get previous month time for this category
            double previousTimeSpent = previousCategoryTimeMap.getOrDefault(categoryId, 0.0);
            
            summaryItems.add(new CategorySummaryAdapter.CategorySummaryItem(
                categoryName, categoryColor, timeSpent, totalTime, categoryActivities, previousTimeSpent
            ));
        }
        
        // Sort by time spent (highest first)
        summaryItems.sort((a, b) -> Double.compare(b.getTimeSpent(), a.getTimeSpent()));
        
        // Debug: Log sorted order
        android.util.Log.d("SummaryFragment", "Monthly breakdown sorted order:");
        for (int i = 0; i < summaryItems.size(); i++) {
            CategorySummaryAdapter.CategorySummaryItem item = summaryItems.get(i);
            android.util.Log.d("SummaryFragment", (i + 1) + ". " + item.getCategoryName() + ": " + item.getTimeSpent() + "h");
        }
        
        Integer monthlyDayDenominator;
        if (currentMonthlyTabMode == TAB_ALL) {
            monthlyDayDenominator = getMonthlyAllTabDayDenominator(currentMonthStart);
        } else if (currentMonthlyTabMode == TAB_WEEKDAY) {
            monthlyDayDenominator = getMonthlyWeekdayTabDayDenominator(currentMonthStart);
        } else if (currentMonthlyTabMode == TAB_WEEKEND) {
            monthlyDayDenominator = getMonthlyWeekendTabDayDenominator(currentMonthStart);
        } else {
            monthlyDayDenominator = null;
        }
        monthlyAdapter.setSummaryItems(summaryItems, monthlyDayDenominator);
        
        // Set dynamic height for monthly RecyclerView based on number of categories
        setMonthlyRecyclerViewHeight(summaryItems.size());
    }
    
    private void setMonthlyRecyclerViewHeight(int categoryCount) {
        if (recyclerViewMonthlyCategoryBreakdown != null) {
            // Calculate height based on number of categories
            // Each category item is approximately 60dp in height
            int itemHeight = 60; // dp
            int minHeight = 200; // minimum height
            int maxHeight = 800; // maximum height to prevent excessive height
            
            int calculatedHeight = Math.max(minHeight, categoryCount * itemHeight);
            int finalHeight = Math.min(maxHeight, calculatedHeight);
            
            // Convert dp to pixels
            float density = getResources().getDisplayMetrics().density;
            int heightInPixels = (int) (finalHeight * density);
            
            // Set the height
            ViewGroup.LayoutParams params = recyclerViewMonthlyCategoryBreakdown.getLayoutParams();
            params.height = heightInPixels;
            recyclerViewMonthlyCategoryBreakdown.setLayoutParams(params);
            
            android.util.Log.d("SummaryFragment", "Set monthly RecyclerView height to " + finalHeight + "dp for " + categoryCount + " categories");
        }
    }
    
    // Navigation methods
    private void navigateToPreviousWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekStart);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        currentWeekStart = calendar.getTime();
        observeActivities();
    }
    
    private void navigateToNextWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekStart);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        currentWeekStart = calendar.getTime();
        observeActivities();
    }
    
    private void navigateToPreviousMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentMonthStart);
        calendar.add(Calendar.MONTH, -1);
        currentMonthStart = calendar.getTime();
        
        // Re-observe activities with new date
        observeActivities();
    }
    
    private void navigateToNextMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentMonthStart);
        calendar.add(Calendar.MONTH, 1);
        currentMonthStart = calendar.getTime();
        
        // Re-observe activities with new date
        observeActivities();
    }
    
    private void updatePeriodLabels() {
        boolean currentWeek = isCurrentWeek();
        if (currentWeeklyTabMode == TAB_ALL) {
            textViewWeeklyPeriod.setText(currentWeek ? "This Week" : formatWeekRange(currentWeekStart));
        } else if (currentWeeklyTabMode == TAB_WEEKDAY) {
            textViewWeeklyPeriod.setText(currentWeek ? "This Weekday" : formatWeekdayRange(currentWeekStart));
        } else {
            textViewWeeklyPeriod.setText(currentWeek ? "This Weekend" : formatWeekendRangeFromMonday(currentWeekStart));
        }
        
        // Update monthly period label
        if (isCurrentMonth()) {
            textViewMonthlyPeriod.setText("This Month");
        } else {
            String monthLabel = formatMonthRange(currentMonthStart);
            textViewMonthlyPeriod.setText(monthLabel);
        }
    }
    
    private boolean isCurrentWeek() {
        Calendar current = Calendar.getInstance();
        Calendar week = Calendar.getInstance();
        week.setTime(currentWeekStart);
        
        return current.get(Calendar.YEAR) == week.get(Calendar.YEAR) &&
               current.get(Calendar.WEEK_OF_YEAR) == week.get(Calendar.WEEK_OF_YEAR);
    }
    
    private boolean isCurrentMonth() {
        Calendar current = Calendar.getInstance();
        Calendar month = Calendar.getInstance();
        month.setTime(currentMonthStart);
        
        return current.get(Calendar.YEAR) == month.get(Calendar.YEAR) &&
               current.get(Calendar.MONTH) == month.get(Calendar.MONTH);
    }
    
    /**
     * Last calendar day (1-based) to include when building monthly denominators: through today for the
     * current month, otherwise the full length of that month.
     */
    private int getInclusiveEndDayOfMonthForMonthlyDenominator(Date monthStart) {
        Calendar monthCal = Calendar.getInstance();
        monthCal.setTime(monthStart);
        Calendar now = Calendar.getInstance();
        int my = monthCal.get(Calendar.YEAR);
        int mm = monthCal.get(Calendar.MONTH);
        int ny = now.get(Calendar.YEAR);
        int nm = now.get(Calendar.MONTH);
        if (my == ny && mm == nm) {
            return now.get(Calendar.DAY_OF_MONTH);
        }
        return monthCal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    
    /** All tab: calendar days from the 1st through the inclusive end day. */
    private int getMonthlyAllTabDayDenominator(Date monthStart) {
        return getInclusiveEndDayOfMonthForMonthlyDenominator(monthStart);
    }
    
    /** Weekday tab: Mon–Fri days from the 1st through the inclusive end day. */
    private int getMonthlyWeekdayTabDayDenominator(Date monthStart) {
        Calendar c = Calendar.getInstance();
        c.setTime(monthStart);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int endDay = getInclusiveEndDayOfMonthForMonthlyDenominator(monthStart);
        int count = 0;
        for (int day = 1; day <= endDay; day++) {
            c.set(year, month, day);
            int dow = c.get(Calendar.DAY_OF_WEEK);
            if (dow >= Calendar.MONDAY && dow <= Calendar.FRIDAY) {
                count++;
            }
        }
        return count;
    }
    
    /** Weekend tab: Sat–Sun days from the 1st through the inclusive end day. */
    private int getMonthlyWeekendTabDayDenominator(Date monthStart) {
        Calendar c = Calendar.getInstance();
        c.setTime(monthStart);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int endDay = getInclusiveEndDayOfMonthForMonthlyDenominator(monthStart);
        int count = 0;
        for (int day = 1; day <= endDay; day++) {
            c.set(year, month, day);
            int dow = c.get(Calendar.DAY_OF_WEEK);
            if (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) {
                count++;
            }
        }
        return count;
    }
    
    private String formatWeekdayRange(Date weekdayStart) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(weekdayStart);
        
        // Format Monday to Friday range
        String startDate = weekdayFormatter.format(weekdayStart);
        
        // Calculate Friday
        calendar.add(Calendar.DAY_OF_MONTH, 4);
        Date friday = calendar.getTime();
        String endDate = weekdayFormatter.format(friday);
        
        return startDate + " - " + endDate;
    }
    
    private String formatWeekRange(Date weekStart) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(weekStart);
        
        // Get start of week (Monday)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday);
        Date start = calendar.getTime();
        
        // Get end of week (Sunday)
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        Date end = calendar.getTime();
        
        return weekFormatter.format(start) + " - " + weekFormatter.format(end);
    }
    
    /** Sat–Sun of the week that starts on Monday {@code monday}. */
    private String formatWeekendRangeFromMonday(Date monday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(monday);
        calendar.add(Calendar.DAY_OF_MONTH, 5);
        Date saturday = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date sunday = calendar.getTime();
        return weekendFormatter.format(saturday) + " - " + weekendFormatter.format(sunday);
    }
    
    private String formatMonthRange(Date monthStart) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(monthStart);
        return monthFormatter.format(calendar.getTime());
    }
}
