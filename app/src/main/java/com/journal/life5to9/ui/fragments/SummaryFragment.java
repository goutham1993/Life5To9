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
    private TextView textViewWeekendTotal;
    private TextView textViewMonthlyTotal;
    private TextView textViewWeeklyPeriod;
    private TextView textViewWeekendPeriod;
    private TextView textViewMonthlyPeriod;
    private RecyclerView recyclerViewWeeklyCategoryBreakdown;
    private RecyclerView recyclerViewWeekendCategoryBreakdown;
    private RecyclerView recyclerViewMonthlyCategoryBreakdown;
    private CategorySummaryAdapter weeklyAdapter;
    private CategorySummaryAdapter weekendAdapter;
    private CategorySummaryAdapter monthlyAdapter;
    private List<Category> categories;
    
    // Dropdown functionality
    private com.google.android.material.button.MaterialButton buttonWeeklyDropdown;
    private com.google.android.material.button.MaterialButton buttonWeekendDropdown;
    private com.google.android.material.button.MaterialButton buttonMonthlyDropdown;
    private android.widget.LinearLayout layoutWeeklyBreakdown;
    private android.widget.LinearLayout layoutWeekendBreakdown;
    private android.widget.LinearLayout layoutMonthlyBreakdown;
    private boolean isWeeklyExpanded = false;
    private boolean isWeekendExpanded = false;
    private boolean isMonthlyExpanded = false;
    
    // Monthly tab mode constants
    private static final int MONTHLY_TAB_WEEKDAY = 0;
    private static final int MONTHLY_TAB_WEEKEND = 1;
    private static final int MONTHLY_TAB_ALL = 2;
    private int currentMonthlyTabMode = MONTHLY_TAB_WEEKDAY;
    
    // Store current monthly activities for tab filtering
    private List<Activity> currentMonthlyActivities = new ArrayList<>();
    
    // Navigation functionality
    private com.google.android.material.button.MaterialButton buttonWeeklyPrevious;
    private com.google.android.material.button.MaterialButton buttonWeeklyNext;
    private com.google.android.material.button.MaterialButton buttonWeekendPrevious;
    private com.google.android.material.button.MaterialButton buttonWeekendNext;
    private com.google.android.material.button.MaterialButton buttonMonthlyPrevious;
    private com.google.android.material.button.MaterialButton buttonMonthlyNext;
    private com.google.android.material.button.MaterialButton buttonMonthlyWeekday;
    private com.google.android.material.button.MaterialButton buttonMonthlyWeekend;
    private com.google.android.material.button.MaterialButton buttonMonthlyAll;
    
    // Current navigation state
    private Date currentWeekdayStart;
    private Date currentWeekendStart;
    private Date currentMonthStart;
    private Date originalWeekStart;
    private Date originalWeekendStart;
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
        textViewWeekendTotal = view.findViewById(R.id.textViewWeekendTotal);
        textViewMonthlyTotal = view.findViewById(R.id.textViewMonthlyTotal);
        textViewWeeklyPeriod = view.findViewById(R.id.textViewWeeklyPeriod);
        textViewWeekendPeriod = view.findViewById(R.id.textViewWeekendPeriod);
        textViewMonthlyPeriod = view.findViewById(R.id.textViewMonthlyPeriod);
        recyclerViewWeeklyCategoryBreakdown = view.findViewById(R.id.recyclerViewWeeklyCategoryBreakdown);
        recyclerViewWeekendCategoryBreakdown = view.findViewById(R.id.recyclerViewWeekendCategoryBreakdown);
        recyclerViewMonthlyCategoryBreakdown = view.findViewById(R.id.recyclerViewMonthlyCategoryBreakdown);
        
        // Initialize dropdown elements
        buttonWeeklyDropdown = view.findViewById(R.id.buttonWeeklyDropdown);
        buttonWeekendDropdown = view.findViewById(R.id.buttonWeekendDropdown);
        buttonMonthlyDropdown = view.findViewById(R.id.buttonMonthlyDropdown);
        layoutWeeklyBreakdown = view.findViewById(R.id.layoutWeeklyBreakdown);
        layoutWeekendBreakdown = view.findViewById(R.id.layoutWeekendBreakdown);
        layoutMonthlyBreakdown = view.findViewById(R.id.layoutMonthlyBreakdown);
        
        // Initialize navigation elements
        buttonWeeklyPrevious = view.findViewById(R.id.buttonWeeklyPrevious);
        buttonWeeklyNext = view.findViewById(R.id.buttonWeeklyNext);
        buttonWeekendPrevious = view.findViewById(R.id.buttonWeekendPrevious);
        buttonWeekendNext = view.findViewById(R.id.buttonWeekendNext);
        buttonMonthlyPrevious = view.findViewById(R.id.buttonMonthlyPrevious);
        buttonMonthlyNext = view.findViewById(R.id.buttonMonthlyNext);
        buttonMonthlyWeekday = view.findViewById(R.id.buttonMonthlyWeekday);
        buttonMonthlyWeekend = view.findViewById(R.id.buttonMonthlyWeekend);
        buttonMonthlyAll = view.findViewById(R.id.buttonMonthlyAll);
        
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
        com.google.android.material.card.MaterialCardView weekendCard = view.findViewById(R.id.cardWeekend);
        com.google.android.material.card.MaterialCardView monthlyCard = view.findViewById(R.id.cardMonthly);
        
        if (weeklyCard != null) {
            weeklyCard.setCardBackgroundColor(backgroundColor);
        }
        if (weekendCard != null) {
            weekendCard.setCardBackgroundColor(backgroundColor);
        }
        if (monthlyCard != null) {
            monthlyCard.setCardBackgroundColor(backgroundColor);
        }
    }
    
    private void setupRecyclerView() {
        // Setup weekly breakdown
        weeklyAdapter = new CategorySummaryAdapter();
        recyclerViewWeeklyCategoryBreakdown.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewWeeklyCategoryBreakdown.setAdapter(weeklyAdapter);
        
        // Setup weekend breakdown
        weekendAdapter = new CategorySummaryAdapter();
        recyclerViewWeekendCategoryBreakdown.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewWeekendCategoryBreakdown.setAdapter(weekendAdapter);
        
        // Setup monthly breakdown
        monthlyAdapter = new CategorySummaryAdapter();
        recyclerViewMonthlyCategoryBreakdown.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMonthlyCategoryBreakdown.setAdapter(monthlyAdapter);
    }
    
    private void setupDropdownListeners() {
        buttonWeeklyDropdown.setOnClickListener(v -> toggleWeeklyBreakdown());
        buttonWeekendDropdown.setOnClickListener(v -> toggleWeekendBreakdown());
        buttonMonthlyDropdown.setOnClickListener(v -> toggleMonthlyBreakdown());
        
        // Monthly tab listeners
        buttonMonthlyWeekday.setOnClickListener(v -> setMonthlyTabMode(MONTHLY_TAB_WEEKDAY));
        buttonMonthlyWeekend.setOnClickListener(v -> setMonthlyTabMode(MONTHLY_TAB_WEEKEND));
        buttonMonthlyAll.setOnClickListener(v -> setMonthlyTabMode(MONTHLY_TAB_ALL));
    }
    
    private void setupNavigationListeners() {
        buttonWeeklyPrevious.setOnClickListener(v -> navigateToPreviousWeek());
        buttonWeeklyNext.setOnClickListener(v -> navigateToNextWeek());
        buttonWeekendPrevious.setOnClickListener(v -> navigateToPreviousWeekend());
        buttonWeekendNext.setOnClickListener(v -> navigateToNextWeekend());
        buttonMonthlyPrevious.setOnClickListener(v -> navigateToPreviousMonth());
        buttonMonthlyNext.setOnClickListener(v -> navigateToNextMonth());
    }
    
    private void toggleWeeklyBreakdown() {
        isWeeklyExpanded = !isWeeklyExpanded;
        if (isWeeklyExpanded) {
            layoutWeeklyBreakdown.setVisibility(android.view.View.VISIBLE);
            buttonWeeklyDropdown.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
            buttonWeeklyDropdown.setIconSize(32);
        } else {
            layoutWeeklyBreakdown.setVisibility(android.view.View.GONE);
            buttonWeeklyDropdown.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_more));
            buttonWeeklyDropdown.setIconSize(32);
        }
    }
    
    private void toggleWeekendBreakdown() {
        isWeekendExpanded = !isWeekendExpanded;
        if (isWeekendExpanded) {
            layoutWeekendBreakdown.setVisibility(android.view.View.VISIBLE);
            buttonWeekendDropdown.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
            buttonWeekendDropdown.setIconSize(32);
        } else {
            layoutWeekendBreakdown.setVisibility(android.view.View.GONE);
            buttonWeekendDropdown.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_more));
            buttonWeekendDropdown.setIconSize(32);
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
        
        // Set selected button with orange highlighting
        switch (currentMonthlyTabMode) {
            case MONTHLY_TAB_WEEKDAY:
                buttonMonthlyWeekday.setSelected(true);
                buttonMonthlyWeekday.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
                buttonMonthlyWeekday.setTextColor(getResources().getColorStateList(android.R.color.white, null));
                break;
            case MONTHLY_TAB_WEEKEND:
                buttonMonthlyWeekend.setSelected(true);
                buttonMonthlyWeekend.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
                buttonMonthlyWeekend.setTextColor(getResources().getColorStateList(android.R.color.white, null));
                break;
            case MONTHLY_TAB_ALL:
                buttonMonthlyAll.setSelected(true);
                buttonMonthlyAll.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
                buttonMonthlyAll.setTextColor(getResources().getColorStateList(android.R.color.white, null));
                break;
        }
    }
    
    private void observeData() {
        // Get current week and month dates
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        
        // Get start of current weekday period (Monday)
        calendar.setTime(now);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date weekdayStart = calendar.getTime();
        
        // Calculate weekday end (Friday end of day)
        calendar.add(Calendar.DAY_OF_MONTH, 4); // Add 4 days to get to Friday
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date weekdayEnd = calendar.getTime();
        
        // Get start of current weekend (Saturday)
        calendar.setTime(now);
        int dayOfWeekForWeekend = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromSaturday = (dayOfWeekForWeekend == Calendar.SUNDAY) ? 1 : (dayOfWeekForWeekend == Calendar.SATURDAY) ? 0 : 7 - dayOfWeekForWeekend + Calendar.SATURDAY;
        calendar.add(Calendar.DAY_OF_MONTH, -daysFromSaturday);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date weekendStart = calendar.getTime();
        
        // Get start of month
        calendar.setTime(now);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date monthStart = calendar.getTime();
        
        // Debug: Log the weekday calculation
        android.util.Log.d("SummaryFragment", "Weekday start: " + weekdayStart);
        android.util.Log.d("SummaryFragment", "Current date: " + now);
        
        // Store dates for later use
        this.weekStart = weekdayStart;
        this.weekendStart = weekendStart;
        this.monthStart = monthStart;
        
        // Initialize current navigation state
        this.currentWeekdayStart = weekdayStart;
        this.currentWeekendStart = weekendStart;
        this.currentMonthStart = monthStart;
        this.originalWeekStart = weekdayStart;
        this.originalWeekendStart = weekendStart;
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
    
    private Date weekStart;
    private Date weekendStart;
    private Date monthStart;
    
    private void observeActivities() {
        // Calculate weekday end date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekdayStart);
        calendar.add(Calendar.DAY_OF_MONTH, 4); // Add 4 days to get to Friday
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date weekdayEnd = calendar.getTime();
        
        // Observe weekday activities (Monday-Friday)
        viewModel.getActivitiesForWeekdays(currentWeekdayStart, weekdayEnd).observe(getViewLifecycleOwner(), activities -> {
            double weekdayTotal = calculateTotalTime(activities);
            android.util.Log.d("SummaryFragment", "Weekday activities count: " + (activities != null ? activities.size() : 0));
            android.util.Log.d("SummaryFragment", "Weekday total: " + weekdayTotal + " hours");
            textViewWeeklyTotal.setText(String.format(Locale.getDefault(), "%.1f hours", weekdayTotal));
            updateWeeklyCategoryBreakdown(activities);
        });
        
        // Observe weekend activities
        viewModel.getActivitiesForWeekend(currentWeekendStart).observe(getViewLifecycleOwner(), activities -> {
            double weekendTotal = calculateTotalTime(activities);
            android.util.Log.d("SummaryFragment", "Weekend activities count: " + (activities != null ? activities.size() : 0));
            android.util.Log.d("SummaryFragment", "Weekend total: " + weekendTotal + " hours");
            textViewWeekendTotal.setText(String.format(Locale.getDefault(), "%.1f hours", weekendTotal));
            updateWeekendCategoryBreakdown(activities);
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
        
        // Only show if expanded
        if (isWeeklyExpanded) {
            layoutWeeklyBreakdown.setVisibility(android.view.View.VISIBLE);
        }
        
        // Calculate previous weekday dates for comparison
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekdayStart);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        Date previousWeekdayStart = calendar.getTime();
        
        // Calculate previous weekday end date
        calendar.add(Calendar.DAY_OF_MONTH, 4); // Add 4 days to get to Friday
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date previousWeekdayEnd = calendar.getTime();
        
        // Load previous weekday data for comparison
        viewModel.getActivitiesForWeekdays(previousWeekdayStart, previousWeekdayEnd).observe(getViewLifecycleOwner(), previousActivities -> {
            updateWeeklyCategoryBreakdownWithComparison(activities, previousActivities);
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
        
        weeklyAdapter.setSummaryItems(summaryItems);
    }
    
    private void updateWeekendCategoryBreakdown(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            layoutWeekendBreakdown.setVisibility(android.view.View.GONE);
            return;
        }
        
        // Only show if expanded
        if (isWeekendExpanded) {
            layoutWeekendBreakdown.setVisibility(android.view.View.VISIBLE);
        }
        
        // Calculate previous weekend dates for comparison
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekendStart);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        Date previousWeekendStart = calendar.getTime();
        
        // Load previous weekend data for comparison
        viewModel.getActivitiesForPreviousWeekend(previousWeekendStart, currentWeekendStart).observe(getViewLifecycleOwner(), previousActivities -> {
            updateWeekendCategoryBreakdownWithComparison(activities, previousActivities);
        });
    }
    
    private void updateWeekendCategoryBreakdownWithComparison(List<Activity> activities, List<Activity> previousActivities) {
        // Group activities by category
        Map<Long, List<Activity>> categoryActivitiesMap = new HashMap<>();
        Map<Long, Double> categoryTimeMap = new HashMap<>();
        for (Activity activity : activities) {
            long categoryId = activity.getCategoryId();
            double timeSpent = activity.getTimeSpentHours();
            android.util.Log.d("SummaryFragment", "Weekend Activity: ID=" + activity.getId() + ", CategoryID=" + categoryId + ", Time=" + timeSpent + "h, Notes=" + activity.getNotes());
            
            // Add to activities list for this category
            if (!categoryActivitiesMap.containsKey(categoryId)) {
                categoryActivitiesMap.put(categoryId, new ArrayList<>());
            }
            categoryActivitiesMap.get(categoryId).add(activity);
            
            // Add to time total
            categoryTimeMap.put(categoryId, categoryTimeMap.getOrDefault(categoryId, 0.0) + timeSpent);
        }
        
        // Group previous weekend activities by category for comparison
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
                        android.util.Log.d("SummaryFragment", "Weekend Category FOUND: " + category.getName() + " -> Emoji: " + emoji);
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
            
            // Get previous weekend time for this category
            double previousTimeSpent = previousCategoryTimeMap.getOrDefault(categoryId, 0.0);
            
            summaryItems.add(new CategorySummaryAdapter.CategorySummaryItem(
                categoryName, categoryColor, timeSpent, totalTime, categoryActivities, previousTimeSpent
            ));
        }
        
        // Sort by time spent (highest first)
        summaryItems.sort((a, b) -> Double.compare(b.getTimeSpent(), a.getTimeSpent()));
        
        // Debug: Log sorted order
        android.util.Log.d("SummaryFragment", "Weekend breakdown sorted order:");
        for (int i = 0; i < summaryItems.size(); i++) {
            CategorySummaryAdapter.CategorySummaryItem item = summaryItems.get(i);
            android.util.Log.d("SummaryFragment", (i + 1) + ". " + item.getCategoryName() + ": " + item.getTimeSpent() + "h");
        }
        
        weekendAdapter.setSummaryItems(summaryItems);
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
        
        // Filter activities based on selected tab
        List<Activity> filteredActivities = filterActivitiesByTab(activities);
        
        // Calculate previous month dates for comparison
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentMonthStart);
        calendar.add(Calendar.MONTH, -1);
        Date previousMonthStart = calendar.getTime();
        
        // Load previous month data for comparison
        viewModel.getActivitiesForPreviousMonth(previousMonthStart, currentMonthStart).observe(getViewLifecycleOwner(), previousActivities -> {
            List<Activity> filteredPreviousActivities = filterActivitiesByTab(previousActivities);
            updateMonthlyCategoryBreakdownWithComparison(filteredActivities, filteredPreviousActivities);
        });
    }
    
    private List<Activity> filterActivitiesByTab(List<Activity> activities) {
        if (activities == null) return new ArrayList<>();
        
        // If "All" tab is selected, return all activities without filtering
        if (currentMonthlyTabMode == MONTHLY_TAB_ALL) {
            return new ArrayList<>(activities);
        }
        
        List<Activity> filteredActivities = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        
        for (Activity activity : activities) {
            calendar.setTime(activity.getDate());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            
            if (currentMonthlyTabMode == MONTHLY_TAB_WEEKDAY) {
                // Monday (2) to Friday (6)
                if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
                    filteredActivities.add(activity);
                }
            } else if (currentMonthlyTabMode == MONTHLY_TAB_WEEKEND) {
                // Saturday (7) and Sunday (1)
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
        
        monthlyAdapter.setSummaryItems(summaryItems);
        
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
        calendar.setTime(currentWeekdayStart);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        currentWeekdayStart = calendar.getTime();
        
        // Re-observe activities with new date
        observeActivities();
    }
    
    private void navigateToNextWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekdayStart);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        currentWeekdayStart = calendar.getTime();
        
        // Re-observe activities with new date
        observeActivities();
    }
    
    private void navigateToPreviousWeekend() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekendStart);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        currentWeekendStart = calendar.getTime();
        
        // Re-observe activities with new date
        observeActivities();
    }
    
    private void navigateToNextWeekend() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekendStart);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        currentWeekendStart = calendar.getTime();
        
        // Re-observe activities with new date
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
        // Update weekday period label
        if (isCurrentWeek()) {
            textViewWeeklyPeriod.setText("This Weekday");
        } else {
            String weekdayLabel = formatWeekdayRange(currentWeekdayStart);
            textViewWeeklyPeriod.setText(weekdayLabel);
        }
        
        // Update weekend period label
        if (isCurrentWeekend()) {
            textViewWeekendPeriod.setText("This Weekend");
        } else {
            String weekendLabel = formatWeekendRange(currentWeekendStart);
            textViewWeekendPeriod.setText(weekendLabel);
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
        week.setTime(currentWeekdayStart);
        
        return current.get(Calendar.YEAR) == week.get(Calendar.YEAR) &&
               current.get(Calendar.WEEK_OF_YEAR) == week.get(Calendar.WEEK_OF_YEAR);
    }
    
    private boolean isCurrentWeekend() {
        Calendar current = Calendar.getInstance();
        Calendar weekend = Calendar.getInstance();
        weekend.setTime(currentWeekendStart);
        
        // Check if current date is in the same weekend (Saturday-Sunday)
        int currentDayOfWeek = current.get(Calendar.DAY_OF_WEEK);
        int weekendDayOfWeek = weekend.get(Calendar.DAY_OF_WEEK);
        
        // Weekend is Saturday (7) to Sunday (1)
        boolean isCurrentWeekend = (currentDayOfWeek == Calendar.SATURDAY || currentDayOfWeek == Calendar.SUNDAY) &&
                                 (weekendDayOfWeek == Calendar.SATURDAY) &&
                                 current.get(Calendar.YEAR) == weekend.get(Calendar.YEAR) &&
                                 current.get(Calendar.WEEK_OF_YEAR) == weekend.get(Calendar.WEEK_OF_YEAR);
        
        return isCurrentWeekend;
    }
    
    private boolean isCurrentMonth() {
        Calendar current = Calendar.getInstance();
        Calendar month = Calendar.getInstance();
        month.setTime(currentMonthStart);
        
        return current.get(Calendar.YEAR) == month.get(Calendar.YEAR) &&
               current.get(Calendar.MONTH) == month.get(Calendar.MONTH);
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
    
    private String formatWeekendRange(Date weekendStart) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(weekendStart);
        
        // Weekend starts on Saturday
        Date start = calendar.getTime();
        
        // Weekend ends on Sunday (next day)
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date end = calendar.getTime();
        
        return weekendFormatter.format(start) + " - " + weekendFormatter.format(end);
    }
    
    private String formatMonthRange(Date monthStart) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(monthStart);
        return monthFormatter.format(calendar.getTime());
    }
}
