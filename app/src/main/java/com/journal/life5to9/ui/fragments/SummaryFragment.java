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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SummaryFragment extends Fragment {

    private MainViewModel viewModel;

    // Period selector
    private MaterialButtonToggleGroup toggleGroupPeriod;
    private MaterialButton buttonPeriodWeek;
    private MaterialButton buttonPeriodMonth;
    private MaterialButton buttonPeriodWeekend;

    // Navigation
    private MaterialButton buttonPrevPeriod;
    private MaterialButton buttonNextPeriod;
    private TextView textViewPeriodLabel;

    // Hero card
    private TextView textViewHeroLabel;
    private TextView textViewHeroValue;
    private TextView textViewHeroDelta;
    private TextView textViewHeroSubtitle;

    // Top categories
    private RecyclerView recyclerViewCategoryBreakdown;
    private CategorySummaryAdapter categoryAdapter;

    // Patterns
    private TextView textViewMostActiveDay;
    private TextView textViewActiveDays;
    private TextView textViewAvgPerDay;

    // Nudge
    private TextView textViewNudgeTitle;
    private TextView textViewNudgeMessage;

    // Data
    private List<Category> categories;

    // Period constants
    private static final int PERIOD_WEEK = 0;
    private static final int PERIOD_MONTH = 1;
    private static final int PERIOD_WEEKEND = 2;
    private int currentPeriod = PERIOD_WEEK;

    // Current navigation dates
    private Date currentWeekStart;
    private Date currentMonthStart;
    private Date currentWeekendStart;

    // Date formatters
    private SimpleDateFormat weekFormatter;
    private SimpleDateFormat monthFormatter;
    private SimpleDateFormat weekendFormatter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        weekFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
        monthFormatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        weekendFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());

        initializeDates();
    }

    private void initializeDates() {
        Calendar cal = Calendar.getInstance();

        // Week start (Monday)
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        cal.add(Calendar.DAY_OF_MONTH, -daysFromMonday);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        currentWeekStart = cal.getTime();

        // Weekend start (Saturday)
        cal = Calendar.getInstance();
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysFromSaturday;
        if (dayOfWeek == Calendar.SATURDAY) {
            daysFromSaturday = 0;
        } else if (dayOfWeek == Calendar.SUNDAY) {
            daysFromSaturday = 1;
        } else {
            daysFromSaturday = dayOfWeek - Calendar.SATURDAY;
        }
        cal.add(Calendar.DAY_OF_MONTH, -daysFromSaturday);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        currentWeekendStart = cal.getTime();

        // Month start
        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        currentMonthStart = cal.getTime();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        initializeViews(view);
        setupListeners();
        setupRecyclerView();
        observeData();
        return view;
    }

    private void initializeViews(View view) {
        toggleGroupPeriod = view.findViewById(R.id.toggleGroupPeriod);
        buttonPeriodWeek = view.findViewById(R.id.buttonPeriodWeek);
        buttonPeriodMonth = view.findViewById(R.id.buttonPeriodMonth);
        buttonPeriodWeekend = view.findViewById(R.id.buttonPeriodWeekend);

        buttonPrevPeriod = view.findViewById(R.id.buttonPrevPeriod);
        buttonNextPeriod = view.findViewById(R.id.buttonNextPeriod);
        textViewPeriodLabel = view.findViewById(R.id.textViewPeriodLabel);

        textViewHeroLabel = view.findViewById(R.id.textViewHeroLabel);
        textViewHeroValue = view.findViewById(R.id.textViewHeroValue);
        textViewHeroDelta = view.findViewById(R.id.textViewHeroDelta);
        textViewHeroSubtitle = view.findViewById(R.id.textViewHeroSubtitle);

        recyclerViewCategoryBreakdown = view.findViewById(R.id.recyclerViewCategoryBreakdown);

        textViewMostActiveDay = view.findViewById(R.id.textViewMostActiveDay);
        textViewActiveDays = view.findViewById(R.id.textViewActiveDays);
        textViewAvgPerDay = view.findViewById(R.id.textViewAvgPerDay);

        textViewNudgeTitle = view.findViewById(R.id.textViewNudgeTitle);
        textViewNudgeMessage = view.findViewById(R.id.textViewNudgeMessage);
    }

    private void setupListeners() {
        toggleGroupPeriod.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.buttonPeriodWeek) {
                    currentPeriod = PERIOD_WEEK;
                } else if (checkedId == R.id.buttonPeriodMonth) {
                    currentPeriod = PERIOD_MONTH;
                } else if (checkedId == R.id.buttonPeriodWeekend) {
                    currentPeriod = PERIOD_WEEKEND;
                }
                updatePeriodLabel();
                loadCurrentPeriodData();
            }
        });

        buttonPrevPeriod.setOnClickListener(v -> navigatePrevious());
        buttonNextPeriod.setOnClickListener(v -> navigateNext());
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategorySummaryAdapter();
        recyclerViewCategoryBreakdown.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCategoryBreakdown.setNestedScrollingEnabled(false);
        recyclerViewCategoryBreakdown.setAdapter(categoryAdapter);
    }

    private void observeData() {
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            this.categories = categories;
            updatePeriodLabel();
            loadCurrentPeriodData();
        });
    }

    private void loadCurrentPeriodData() {
        switch (currentPeriod) {
            case PERIOD_WEEK:
                loadWeekData();
                break;
            case PERIOD_MONTH:
                loadMonthData();
                break;
            case PERIOD_WEEKEND:
                loadWeekendData();
                break;
        }
    }

    private void loadWeekData() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentWeekStart);
        cal.add(Calendar.DAY_OF_MONTH, 4);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date weekEnd = cal.getTime();

        // Previous week
        cal.setTime(currentWeekStart);
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        Date prevWeekStart = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 4);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date prevWeekEnd = cal.getTime();

        viewModel.getActivitiesForWeekdays(currentWeekStart, weekEnd).observe(getViewLifecycleOwner(), activities -> {
            double total = calculateTotalTime(activities);
            textViewHeroLabel.setText("Total time tracked");
            textViewHeroValue.setText(String.format(Locale.getDefault(), "%.1fh", total));

            // Load previous for comparison
            viewModel.getActivitiesForWeekdays(prevWeekStart, prevWeekEnd).observe(getViewLifecycleOwner(), prevActivities -> {
                double prevTotal = calculateTotalTime(prevActivities);
                updateDelta(total, prevTotal, "vs last week");
                updateCategoryBreakdown(activities, prevActivities);
            });

            updatePatterns(activities);
            updateNudge(activities, total);
        });
    }

    private void loadMonthData() {
        viewModel.getActivitiesForMonth(currentMonthStart).observe(getViewLifecycleOwner(), activities -> {
            double total = calculateTotalTime(activities);
            textViewHeroLabel.setText("Total time tracked");
            textViewHeroValue.setText(String.format(Locale.getDefault(), "%.1fh", total));

            // Previous month
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentMonthStart);
            cal.add(Calendar.MONTH, -1);
            Date prevMonthStart = cal.getTime();

            viewModel.getActivitiesForPreviousMonth(prevMonthStart, currentMonthStart).observe(getViewLifecycleOwner(), prevActivities -> {
                double prevTotal = calculateTotalTime(prevActivities);
                updateDelta(total, prevTotal, "vs last month");
                updateCategoryBreakdown(activities, prevActivities);
            });

            updatePatterns(activities);
            updateNudge(activities, total);
        });
    }

    private void loadWeekendData() {
        viewModel.getActivitiesForWeekend(currentWeekendStart).observe(getViewLifecycleOwner(), activities -> {
            double total = calculateTotalTime(activities);
            textViewHeroLabel.setText("Weekend time tracked");
            textViewHeroValue.setText(String.format(Locale.getDefault(), "%.1fh", total));

            // Previous weekend
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentWeekendStart);
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            Date prevWeekendStart = cal.getTime();

            viewModel.getActivitiesForPreviousWeekend(prevWeekendStart, currentWeekendStart).observe(getViewLifecycleOwner(), prevActivities -> {
                double prevTotal = calculateTotalTime(prevActivities);
                updateDelta(total, prevTotal, "vs last weekend");
                updateCategoryBreakdown(activities, prevActivities);
            });

            updatePatterns(activities);
            updateNudge(activities, total);
        });
    }

    private void updateDelta(double current, double previous, String subtitle) {
        if (previous > 0) {
            double diff = current - previous;
            String sign = diff >= 0 ? "+" : "";
            textViewHeroDelta.setText(sign + String.format(Locale.getDefault(), "%.1fh", diff));
            textViewHeroDelta.setVisibility(View.VISIBLE);
            if (diff >= 0) {
                textViewHeroDelta.setTextColor(requireContext().getColor(R.color.primary));
            } else {
                textViewHeroDelta.setTextColor(requireContext().getColor(R.color.error));
            }
        } else {
            textViewHeroDelta.setVisibility(View.GONE);
        }
        textViewHeroSubtitle.setText(subtitle);
    }

    private void updateCategoryBreakdown(List<Activity> activities, List<Activity> previousActivities) {
        if (activities == null || activities.isEmpty()) {
            categoryAdapter.setSummaryItems(new ArrayList<>());
            return;
        }

        Map<Long, List<Activity>> categoryActivitiesMap = new HashMap<>();
        Map<Long, Double> categoryTimeMap = new HashMap<>();
        for (Activity activity : activities) {
            long categoryId = activity.getCategoryId();
            if (!categoryActivitiesMap.containsKey(categoryId)) {
                categoryActivitiesMap.put(categoryId, new ArrayList<>());
            }
            categoryActivitiesMap.get(categoryId).add(activity);
            categoryTimeMap.put(categoryId, categoryTimeMap.getOrDefault(categoryId, 0.0) + activity.getTimeSpentHours());
        }

        Map<Long, Double> prevCategoryTimeMap = new HashMap<>();
        if (previousActivities != null) {
            for (Activity activity : previousActivities) {
                long categoryId = activity.getCategoryId();
                prevCategoryTimeMap.put(categoryId, prevCategoryTimeMap.getOrDefault(categoryId, 0.0) + activity.getTimeSpentHours());
            }
        }

        List<CategorySummaryAdapter.CategorySummaryItem> summaryItems = new ArrayList<>();
        double totalTime = calculateTotalTime(activities);

        for (Map.Entry<Long, Double> entry : categoryTimeMap.entrySet()) {
            long categoryId = entry.getKey();
            double timeSpent = entry.getValue();
            List<Activity> catActivities = categoryActivitiesMap.get(categoryId);

            String categoryName = "Unknown Category";
            String categoryColor = "#FF2E7D32";

            if (categories != null) {
                for (Category category : categories) {
                    if (category.getId() == categoryId) {
                        String emoji = CategoryEmojiMapper.getEmojiForCategory(category.getName());
                        categoryName = emoji + " " + category.getName();
                        categoryColor = category.getColor();
                        break;
                    }
                }
            }

            double previousTimeSpent = prevCategoryTimeMap.getOrDefault(categoryId, 0.0);
            summaryItems.add(new CategorySummaryAdapter.CategorySummaryItem(
                categoryName, categoryColor, timeSpent, totalTime, catActivities, previousTimeSpent
            ));
        }

        summaryItems.sort((a, b) -> Double.compare(b.getTimeSpent(), a.getTimeSpent()));
        categoryAdapter.setSummaryItems(summaryItems);
    }

    private void updatePatterns(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            textViewMostActiveDay.setText("--");
            textViewActiveDays.setText("0");
            textViewAvgPerDay.setText("--");
            return;
        }

        // Most active day
        Map<Integer, Double> dayTimeMap = new HashMap<>();
        Set<String> uniqueDays = new HashSet<>();
        SimpleDateFormat dayKeyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        for (Activity activity : activities) {
            cal.setTime(activity.getDate());
            int dow = cal.get(Calendar.DAY_OF_WEEK);
            dayTimeMap.put(dow, dayTimeMap.getOrDefault(dow, 0.0) + activity.getTimeSpentHours());
            uniqueDays.add(dayKeyFormat.format(activity.getDate()));
        }

        int mostActiveDay = Calendar.MONDAY;
        double maxTime = 0;
        for (Map.Entry<Integer, Double> entry : dayTimeMap.entrySet()) {
            if (entry.getValue() > maxTime) {
                maxTime = entry.getValue();
                mostActiveDay = entry.getKey();
            }
        }
        textViewMostActiveDay.setText(getDayName(mostActiveDay));

        // Active days
        textViewActiveDays.setText(String.valueOf(uniqueDays.size()));

        // Average per day
        double total = calculateTotalTime(activities);
        int daysCount = uniqueDays.size();
        if (daysCount > 0) {
            textViewAvgPerDay.setText(String.format(Locale.getDefault(), "%.1fh", total / daysCount));
        } else {
            textViewAvgPerDay.setText("--");
        }
    }

    private void updateNudge(List<Activity> activities, double totalHours) {
        if (activities == null || activities.isEmpty()) {
            textViewNudgeTitle.setText("\uD83D\uDCA1 Get Started");
            textViewNudgeMessage.setText("Start tracking your activities to see personalized insights and patterns here.");
            return;
        }

        if (totalHours < 5) {
            textViewNudgeTitle.setText("\uD83C\uDF31 Building Momentum");
            textViewNudgeMessage.setText("You've logged " + String.format(Locale.getDefault(), "%.1f", totalHours) + " hours so far. Keep going!");
        } else if (totalHours < 15) {
            textViewNudgeTitle.setText("\uD83D\uDD25 Great Progress");
            textViewNudgeMessage.setText("You're on track! Try to maintain consistency across different categories.");
        } else {
            textViewNudgeTitle.setText("\uD83C\uDFC6 Amazing Work");
            textViewNudgeMessage.setText("You've tracked over " + String.format(Locale.getDefault(), "%.0f", totalHours) + " hours! Your dedication is paying off.");
        }
    }

    private void navigatePrevious() {
        Calendar cal = Calendar.getInstance();
        switch (currentPeriod) {
            case PERIOD_WEEK:
                cal.setTime(currentWeekStart);
                cal.add(Calendar.WEEK_OF_YEAR, -1);
                currentWeekStart = cal.getTime();
                break;
            case PERIOD_MONTH:
                cal.setTime(currentMonthStart);
                cal.add(Calendar.MONTH, -1);
                currentMonthStart = cal.getTime();
                break;
            case PERIOD_WEEKEND:
                cal.setTime(currentWeekendStart);
                cal.add(Calendar.WEEK_OF_YEAR, -1);
                currentWeekendStart = cal.getTime();
                break;
        }
        updatePeriodLabel();
        loadCurrentPeriodData();
    }

    private void navigateNext() {
        Calendar cal = Calendar.getInstance();
        switch (currentPeriod) {
            case PERIOD_WEEK:
                cal.setTime(currentWeekStart);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                currentWeekStart = cal.getTime();
                break;
            case PERIOD_MONTH:
                cal.setTime(currentMonthStart);
                cal.add(Calendar.MONTH, 1);
                currentMonthStart = cal.getTime();
                break;
            case PERIOD_WEEKEND:
                cal.setTime(currentWeekendStart);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                currentWeekendStart = cal.getTime();
                break;
        }
        updatePeriodLabel();
        loadCurrentPeriodData();
    }

    private void updatePeriodLabel() {
        switch (currentPeriod) {
            case PERIOD_WEEK:
                if (isCurrentWeek()) {
                    textViewPeriodLabel.setText("This Week");
                } else {
                    textViewPeriodLabel.setText(formatWeekRange(currentWeekStart));
                }
                break;
            case PERIOD_MONTH:
                if (isCurrentMonth()) {
                    textViewPeriodLabel.setText("This Month");
                } else {
                    textViewPeriodLabel.setText(monthFormatter.format(currentMonthStart));
                }
                break;
            case PERIOD_WEEKEND:
                if (isCurrentWeekend()) {
                    textViewPeriodLabel.setText("This Weekend");
                } else {
                    textViewPeriodLabel.setText(formatWeekendRange(currentWeekendStart));
                }
                break;
        }
    }

    // Utility methods

    private double calculateTotalTime(List<Activity> activities) {
        if (activities == null) return 0.0;
        double total = 0.0;
        for (Activity activity : activities) {
            total += activity.getTimeSpentHours();
        }
        return total;
    }

    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY: return "Sunday";
            case Calendar.MONDAY: return "Monday";
            case Calendar.TUESDAY: return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY: return "Thursday";
            case Calendar.FRIDAY: return "Friday";
            case Calendar.SATURDAY: return "Saturday";
            default: return "--";
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

    private boolean isCurrentWeekend() {
        Calendar current = Calendar.getInstance();
        Calendar weekend = Calendar.getInstance();
        weekend.setTime(currentWeekendStart);
        int currentDow = current.get(Calendar.DAY_OF_WEEK);
        return (currentDow == Calendar.SATURDAY || currentDow == Calendar.SUNDAY) &&
               current.get(Calendar.YEAR) == weekend.get(Calendar.YEAR) &&
               current.get(Calendar.WEEK_OF_YEAR) == weekend.get(Calendar.WEEK_OF_YEAR);
    }

    private String formatWeekRange(Date weekStart) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(weekStart);
        Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 4);
        Date end = cal.getTime();
        return weekFormatter.format(start) + " - " + weekFormatter.format(end);
    }

    private String formatWeekendRange(Date weekendStart) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(weekendStart);
        Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();
        return weekendFormatter.format(start) + " - " + weekendFormatter.format(end);
    }
}
