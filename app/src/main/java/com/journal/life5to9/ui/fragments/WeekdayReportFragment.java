package com.journal.life5to9.ui.fragments;

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

import com.google.android.material.button.MaterialButton;
import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.ui.adapters.CategorySummaryAdapter;
import com.journal.life5to9.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeekdayReportFragment extends Fragment {
    
    private MainViewModel viewModel;
    private TextView textViewPeriod;
    private TextView textViewTotalHours;
    private MaterialButton buttonPrevious;
    private MaterialButton buttonNext;
    
    // Quick stats
    private TextView textViewStat1Value; // Activities count
    private TextView textViewStat2Value; // Avg per day
    private TextView textViewStat3Value; // Top category
    private TextView textViewStat4Value; // Week-over-week change
    
    // Category breakdown
    private RecyclerView recyclerViewCategoryBreakdown;
    private CategorySummaryAdapter categoryAdapter;
    private List<Category> categories;
    
    // Insights
    private LinearLayout layoutInsights;
    
    // Current navigation state
    private Date currentWeekdayStart;
    
    // Date formatter
    private SimpleDateFormat dateFormatter;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        dateFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
        
        // Initialize with current weekday period
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
        currentWeekdayStart = calendar.getTime();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_base, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        observeData();
        
        return view;
    }
    
    private void initializeViews(View view) {
        textViewPeriod = view.findViewById(R.id.textViewPeriod);
        textViewTotalHours = view.findViewById(R.id.textViewTotalHours);
        buttonPrevious = view.findViewById(R.id.buttonPrevious);
        buttonNext = view.findViewById(R.id.buttonNext);
        
        textViewStat1Value = view.findViewById(R.id.textViewStat1Value);
        textViewStat2Value = view.findViewById(R.id.textViewStat2Value);
        textViewStat3Value = view.findViewById(R.id.textViewStat3Value);
        textViewStat4Value = view.findViewById(R.id.textViewStat4Value);
        
        recyclerViewCategoryBreakdown = view.findViewById(R.id.recyclerViewCategoryBreakdown);
        layoutInsights = view.findViewById(R.id.layoutInsights);
    }
    
    private void setupRecyclerView() {
        categoryAdapter = new CategorySummaryAdapter();
        recyclerViewCategoryBreakdown.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCategoryBreakdown.setAdapter(categoryAdapter);
    }
    
    private void setupListeners() {
        buttonPrevious.setOnClickListener(v -> navigateToPreviousWeek());
        buttonNext.setOnClickListener(v -> navigateToNextWeek());
    }
    
    private void navigateToPreviousWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekdayStart);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        currentWeekdayStart = calendar.getTime();
        updatePeriodLabel();
        observeActivities();
    }
    
    private void navigateToNextWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekdayStart);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date nextWeekStart = calendar.getTime();
        
        // Don't allow navigation to future weeks
        Date now = new Date();
        if (nextWeekStart.before(now) || nextWeekStart.equals(now) || 
            (calendar.get(Calendar.WEEK_OF_YEAR) <= Calendar.getInstance().get(Calendar.WEEK_OF_YEAR))) {
            currentWeekdayStart = nextWeekStart;
            updatePeriodLabel();
            observeActivities();
        }
    }
    
    private void observeData() {
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            this.categories = categories;
            observeActivities();
        });
    }
    
    private void observeActivities() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekdayStart);
        calendar.add(Calendar.DAY_OF_MONTH, 4); // Add 4 days to get to Friday
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date weekdayEnd = calendar.getTime();
        
        updatePeriodLabel();
        
        viewModel.getActivitiesForWeekdays(currentWeekdayStart, weekdayEnd).observe(getViewLifecycleOwner(), activities -> {
            if (activities != null) {
                updateReport(activities);
            }
        });
    }
    
    private void updatePeriodLabel() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekdayStart);
        Date monday = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 4);
        Date friday = calendar.getTime();
        
        String period = dateFormatter.format(monday) + " - " + dateFormatter.format(friday);
        textViewPeriod.setText(period);
    }
    
    private void updateReport(List<Activity> activities) {
        // Calculate total time
        double totalTime = calculateTotalTime(activities);
        textViewTotalHours.setText(String.format(Locale.getDefault(), "%.1f hours", totalTime));
        
        // Update quick stats
        updateQuickStats(activities, totalTime);
        
        // Update category breakdown
        updateCategoryBreakdown(activities, totalTime);
        
        // Update insights
        updateInsights(activities, totalTime);
    }
    
    private double calculateTotalTime(List<Activity> activities) {
        double total = 0.0;
        for (Activity activity : activities) {
            total += activity.getTimeSpentHours();
        }
        return total;
    }
    
    private void updateQuickStats(List<Activity> activities, double totalTime) {
        // Activities count
        int activityCount = activities.size();
        textViewStat1Value.setText(String.valueOf(activityCount));
        
        // Average per day (5 weekdays)
        double avgPerDay = totalTime / 5.0;
        textViewStat2Value.setText(String.format(Locale.getDefault(), "%.1fh", avgPerDay));
        
        // Top category
        if (categories != null && !activities.isEmpty()) {
            Map<Long, Double> categoryTimeMap = new HashMap<>();
            for (Activity activity : activities) {
                long categoryId = activity.getCategoryId();
                double timeSpent = activity.getTimeSpentHours();
                categoryTimeMap.put(categoryId, categoryTimeMap.getOrDefault(categoryId, 0.0) + timeSpent);
            }
            
            Long topCategoryId = Collections.max(categoryTimeMap.entrySet(), 
                Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            
            for (Category category : categories) {
                if (category.getId() == topCategoryId) {
                    textViewStat3Value.setText(category.getName());
                    break;
                }
            }
        } else {
            textViewStat3Value.setText("--");
        }
        
        // Week-over-week change
        calculateWeekOverWeekChange(totalTime);
    }
    
    private void calculateWeekOverWeekChange(double currentTotal) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekdayStart);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date previousWeekStart = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 4);
        Date previousWeekEnd = calendar.getTime();
        
        viewModel.getActivitiesForWeekdays(previousWeekStart, previousWeekEnd).observe(getViewLifecycleOwner(), previousActivities -> {
            if (previousActivities != null && !previousActivities.isEmpty()) {
                double previousTotal = calculateTotalTime(previousActivities);
                double change = currentTotal - previousTotal;
                double percentageChange = previousTotal > 0 ? (change / previousTotal) * 100 : 0;
                
                String changeText = String.format(Locale.getDefault(), "%+.1f%%", percentageChange);
                textViewStat4Value.setText(changeText);
                
                // Set color based on change
                int color = percentageChange >= 0 ? 
                    getResources().getColor(android.R.color.holo_green_dark, null) :
                    getResources().getColor(android.R.color.holo_red_dark, null);
                textViewStat4Value.setTextColor(color);
            } else {
                textViewStat4Value.setText("New");
                textViewStat4Value.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, null));
            }
        });
    }
    
    private void updateCategoryBreakdown(List<Activity> activities, double totalTime) {
        if (categories == null || categories.isEmpty()) {
            return;
        }
        
        Map<Long, List<Activity>> categoryActivitiesMap = new HashMap<>();
        Map<Long, Double> categoryTimeMap = new HashMap<>();
        
        for (Activity activity : activities) {
            long categoryId = activity.getCategoryId();
            double timeSpent = activity.getTimeSpentHours();
            
            if (!categoryActivitiesMap.containsKey(categoryId)) {
                categoryActivitiesMap.put(categoryId, new ArrayList<>());
            }
            categoryActivitiesMap.get(categoryId).add(activity);
            categoryTimeMap.put(categoryId, categoryTimeMap.getOrDefault(categoryId, 0.0) + timeSpent);
        }
        
        // Get previous week data for comparison
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekdayStart);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date previousWeekStart = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 4);
        Date previousWeekEnd = calendar.getTime();
        
        viewModel.getActivitiesForWeekdays(previousWeekStart, previousWeekEnd).observe(getViewLifecycleOwner(), previousActivities -> {
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
            
            for (Category category : categories) {
                long categoryId = category.getId();
                double timeSpent = categoryTimeMap.getOrDefault(categoryId, 0.0);
                List<Activity> categoryActivities = categoryActivitiesMap.getOrDefault(categoryId, new ArrayList<>());
                double previousTimeSpent = previousCategoryTimeMap.getOrDefault(categoryId, 0.0);
                
                if (timeSpent > 0 || previousTimeSpent > 0) {
                    CategorySummaryAdapter.CategorySummaryItem item = new CategorySummaryAdapter.CategorySummaryItem(
                        category.getName(),
                        category.getColor(),
                        timeSpent,
                        totalTime,
                        categoryActivities,
                        previousTimeSpent
                    );
                    summaryItems.add(item);
                }
            }
            
            // Sort by time spent (descending)
            summaryItems.sort((a, b) -> Double.compare(b.getTimeSpent(), a.getTimeSpent()));
            
            categoryAdapter.setSummaryItems(summaryItems);
        });
    }
    
    private void updateInsights(List<Activity> activities, double totalTime) {
        layoutInsights.removeAllViews();
        
        if (activities.isEmpty()) {
            addInsightCard("No activities recorded this week.", "Start tracking to see insights!");
            return;
        }
        
        // Most active day
        Map<Integer, Double> dayTimeMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        for (Activity activity : activities) {
            calendar.setTime(activity.getDate());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            dayTimeMap.put(dayOfWeek, dayTimeMap.getOrDefault(dayOfWeek, 0.0) + activity.getTimeSpentHours());
        }
        
        if (!dayTimeMap.isEmpty()) {
            int mostActiveDay = Collections.max(dayTimeMap.entrySet(), 
                Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            String dayName = getDayName(mostActiveDay);
            double dayTime = dayTimeMap.get(mostActiveDay);
            addInsightCard("Most Active Day", dayName + " (" + String.format(Locale.getDefault(), "%.1fh", dayTime) + ")");
        }
        
        // Average session duration
        if (!activities.isEmpty()) {
            double avgSession = totalTime / activities.size();
            addInsightCard("Average Session", String.format(Locale.getDefault(), "%.1f hours", avgSession));
        }
        
        // Coverage percentage (35 hours = 5 days * 7 hours from 5PM to 12AM)
        double coveragePercent = (totalTime / 35.0) * 100;
        addInsightCard("Time Coverage", String.format(Locale.getDefault(), "%.1f%% of available time", coveragePercent));
    }
    
    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY: return "Monday";
            case Calendar.TUESDAY: return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY: return "Thursday";
            case Calendar.FRIDAY: return "Friday";
            default: return "";
        }
    }
    
    private void addInsightCard(String title, String value) {
        TextView textView = new TextView(getContext());
        textView.setText(title + ": " + value);
        textView.setTextAppearance(android.R.style.TextAppearance_Medium);
        textView.setPadding(16, 8, 16, 8);
        layoutInsights.addView(textView);
    }
}

