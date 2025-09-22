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
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        
        textViewWeeklyTotal = view.findViewById(R.id.textViewWeeklyTotal);
        textViewMonthlyTotal = view.findViewById(R.id.textViewMonthlyTotal);
        recyclerViewWeeklyCategoryBreakdown = view.findViewById(R.id.recyclerViewWeeklyCategoryBreakdown);
        recyclerViewMonthlyCategoryBreakdown = view.findViewById(R.id.recyclerViewMonthlyCategoryBreakdown);
        
        // Initialize dropdown elements
        buttonWeeklyDropdown = view.findViewById(R.id.buttonWeeklyDropdown);
        buttonMonthlyDropdown = view.findViewById(R.id.buttonMonthlyDropdown);
        layoutWeeklyBreakdown = view.findViewById(R.id.layoutWeeklyBreakdown);
        layoutMonthlyBreakdown = view.findViewById(R.id.layoutMonthlyBreakdown);
        
        setupRecyclerView();
        setupDropdownListeners();
        observeData();
        
        return view;
    }
    
    private void setupRecyclerView() {
        // Setup weekly breakdown
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
    
    private void toggleMonthlyBreakdown() {
        isMonthlyExpanded = !isMonthlyExpanded;
        if (isMonthlyExpanded) {
            layoutMonthlyBreakdown.setVisibility(android.view.View.VISIBLE);
            buttonMonthlyDropdown.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
            buttonMonthlyDropdown.setIconSize(32);
        } else {
            layoutMonthlyBreakdown.setVisibility(android.view.View.GONE);
            buttonMonthlyDropdown.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_more));
            buttonMonthlyDropdown.setIconSize(32);
        }
    }
    
    private void observeData() {
        // Get current week and month dates
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        
        // Get start of current week (Monday)
        calendar.setTime(now);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date weekStart = calendar.getTime();
        
        // Get start of month
        calendar.setTime(now);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date monthStart = calendar.getTime();
        
        // Debug: Log the week calculation
        android.util.Log.d("SummaryFragment", "Week start: " + weekStart);
        android.util.Log.d("SummaryFragment", "Current date: " + now);
        
        // Store dates for later use
        this.weekStart = weekStart;
        this.monthStart = monthStart;
        
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
    private Date monthStart;
    
    private void observeActivities() {
        // Observe weekly activities
        viewModel.getActivitiesForWeek(weekStart).observe(getViewLifecycleOwner(), activities -> {
            double weeklyTotal = calculateTotalTime(activities);
            android.util.Log.d("SummaryFragment", "Weekly activities count: " + (activities != null ? activities.size() : 0));
            android.util.Log.d("SummaryFragment", "Weekly total: " + weeklyTotal + " hours");
            textViewWeeklyTotal.setText(String.format(Locale.getDefault(), "%.1f hours", weeklyTotal));
            updateWeeklyCategoryBreakdown(activities);
        });
        
        // Observe monthly activities
        viewModel.getActivitiesForMonth(monthStart).observe(getViewLifecycleOwner(), activities -> {
            double monthlyTotal = calculateTotalTime(activities);
            android.util.Log.d("SummaryFragment", "Monthly activities count: " + (activities != null ? activities.size() : 0));
            android.util.Log.d("SummaryFragment", "Monthly total: " + monthlyTotal + " hours");
            textViewMonthlyTotal.setText(String.format(Locale.getDefault(), "%.1f hours", monthlyTotal));
            updateMonthlyCategoryBreakdown(activities);
        });
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
        
        // Group activities by category
        Map<Long, Double> categoryTimeMap = new HashMap<>();
        for (Activity activity : activities) {
            long categoryId = activity.getCategoryId();
            double timeSpent = activity.getTimeSpentHours();
            android.util.Log.d("SummaryFragment", "Weekly Activity: ID=" + activity.getId() + ", CategoryID=" + categoryId + ", Time=" + timeSpent + "h, Notes=" + activity.getNotes());
            categoryTimeMap.put(categoryId, categoryTimeMap.getOrDefault(categoryId, 0.0) + timeSpent);
        }
        
        // Create summary items
        List<CategorySummaryAdapter.CategorySummaryItem> summaryItems = new ArrayList<>();
        double totalTime = calculateTotalTime(activities);
        
        for (Map.Entry<Long, Double> entry : categoryTimeMap.entrySet()) {
            long categoryId = entry.getKey();
            double timeSpent = entry.getValue();
            
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
            
            summaryItems.add(new CategorySummaryAdapter.CategorySummaryItem(
                categoryName, categoryColor, timeSpent, totalTime
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
    
    private void updateMonthlyCategoryBreakdown(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            layoutMonthlyBreakdown.setVisibility(android.view.View.GONE);
            return;
        }
        
        // Only show if expanded
        if (isMonthlyExpanded) {
            layoutMonthlyBreakdown.setVisibility(android.view.View.VISIBLE);
        }
        
        // Group activities by category
        Map<Long, Double> categoryTimeMap = new HashMap<>();
        for (Activity activity : activities) {
            long categoryId = activity.getCategoryId();
            double timeSpent = activity.getTimeSpentHours();
            android.util.Log.d("SummaryFragment", "Monthly Activity: ID=" + activity.getId() + ", CategoryID=" + categoryId + ", Time=" + timeSpent + "h, Notes=" + activity.getNotes());
            categoryTimeMap.put(categoryId, categoryTimeMap.getOrDefault(categoryId, 0.0) + timeSpent);
        }
        
        // Create summary items
        List<CategorySummaryAdapter.CategorySummaryItem> summaryItems = new ArrayList<>();
        double totalTime = calculateTotalTime(activities);
        
        for (Map.Entry<Long, Double> entry : categoryTimeMap.entrySet()) {
            long categoryId = entry.getKey();
            double timeSpent = entry.getValue();
            
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
            
            summaryItems.add(new CategorySummaryAdapter.CategorySummaryItem(
                categoryName, categoryColor, timeSpent, totalTime
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
    }
}
