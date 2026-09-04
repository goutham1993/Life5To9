package com.journal.life5to9.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.ui.dialogs.AskAiDialogFragment;
import com.journal.life5to9.utils.CategoryEmojiMapper;
import com.journal.life5to9.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class InsightsFragment extends Fragment {

    private MainViewModel viewModel;

    // UI elements - Ask AI bubble
    private ExtendedFloatingActionButton fabAskAi;

    // UI elements - Category dropdown
    private AutoCompleteTextView categoryDropdown;
    private TextInputLayout categoryDropdownLayout;

    // UI elements - Weekly section
    private MaterialButton buttonWeeklyPrevious;
    private MaterialButton buttonWeeklyNext;
    private TextView textViewWeeklyPeriodLabel;
    private TextView textViewWeeklyTotalHours;
    private TextView textViewWeeklyTotalDays;
    private MaterialCardView cardWeeklyTotalHours;
    private MaterialCardView cardWeeklyChart;
    private MaterialCardView cardWeeklyDaysChart;
    private BarChart weeklyBarChart;
    private BarChart weeklyDaysBarChart;
    private LinearLayout layoutWeeklyEmptyState;
    private TextView textViewWeeklyEmptyMessage;

    // UI elements - Month section
    private MaterialButton buttonMonthPrevious;
    private MaterialButton buttonMonthNext;
    private TextView textViewMonthPeriodLabel;
    private TextView textViewMonthTotalHours;
    private TextView textViewMonthTotalDays;
    private MaterialCardView cardMonthTotal;
    private MaterialCardView cardMonthHoursChart;
    private MaterialCardView cardMonthDaysChart;
    private BarChart monthHoursBarChart;
    private BarChart monthDaysBarChart;
    private LinearLayout layoutMonthEmptyState;
    private TextView textViewMonthEmptyMessage;

    // UI elements - Year section
    private MaterialButton buttonYearlyPrevious;
    private MaterialButton buttonYearlyNext;
    private TextView textViewYearlyPeriodLabel;
    private TextView textViewYearlyTotalHours;
    private TextView textViewYearlyTotalDays;
    private MaterialCardView cardYearlyTotal;
    private MaterialCardView cardYearlyHoursChart;
    private MaterialCardView cardYearlyDaysChart;
    private BarChart yearlyHoursBarChart;
    private BarChart yearlyDaysBarChart;
    private LinearLayout layoutYearlyEmptyState;
    private TextView textViewYearlyEmptyMessage;

    // Data
    private List<Category> categories = new ArrayList<>();
    private Category selectedCategory = null;

    // Navigation state
    private Date currentWeekStart;
    private Date currentMonthStart;
    private int currentYear;

    // Formatters
    private final SimpleDateFormat weekFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
    private final SimpleDateFormat dayOfWeekFormatter = new SimpleDateFormat("EEE", Locale.getDefault());
    private final SimpleDateFormat monthLabelFormatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private final SimpleDateFormat monthShortFormatter = new SimpleDateFormat("MMM", Locale.getDefault());
    private final SimpleDateFormat dayKeyFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        initializeDates();
    }

    private void initializeDates() {
        Calendar calendar = Calendar.getInstance();

        // Set week start to Monday of current week
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        currentWeekStart = calendar.getTime();

        // Set month start to first day of current month
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        currentMonthStart = calendar.getTime();

        currentYear = Calendar.getInstance().get(Calendar.YEAR);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insights, container, false);

        initializeViews(view);
        setupCardBackgrounds(view);
        setupAskAiBubble();
        setupCategoryDropdown();
        setupNavigationButtons();
        updateWeeklyPeriodLabel();
        updateMonthPeriodLabel();
        updateYearlyPeriodLabel();
        observeCategories();

        return view;
    }

    private void initializeViews(View view) {
        fabAskAi = view.findViewById(R.id.fabAskAi);
        categoryDropdown = view.findViewById(R.id.categoryDropdown);
        categoryDropdownLayout = view.findViewById(R.id.categoryDropdownLayout);

        buttonWeeklyPrevious = view.findViewById(R.id.buttonWeeklyPrevious);
        buttonWeeklyNext = view.findViewById(R.id.buttonWeeklyNext);
        textViewWeeklyPeriodLabel = view.findViewById(R.id.textViewWeeklyPeriodLabel);
        textViewWeeklyTotalHours = view.findViewById(R.id.textViewWeeklyTotalHours);
        textViewWeeklyTotalDays = view.findViewById(R.id.textViewWeeklyTotalDays);
        cardWeeklyTotalHours = view.findViewById(R.id.cardWeeklyTotalHours);
        cardWeeklyChart = view.findViewById(R.id.cardWeeklyChart);
        cardWeeklyDaysChart = view.findViewById(R.id.cardWeeklyDaysChart);
        weeklyBarChart = view.findViewById(R.id.weeklyBarChart);
        weeklyDaysBarChart = view.findViewById(R.id.weeklyDaysBarChart);
        layoutWeeklyEmptyState = view.findViewById(R.id.layoutWeeklyEmptyState);
        textViewWeeklyEmptyMessage = view.findViewById(R.id.textViewWeeklyEmptyMessage);

        buttonMonthPrevious = view.findViewById(R.id.buttonMonthPrevious);
        buttonMonthNext = view.findViewById(R.id.buttonMonthNext);
        textViewMonthPeriodLabel = view.findViewById(R.id.textViewMonthPeriodLabel);
        textViewMonthTotalHours = view.findViewById(R.id.textViewMonthTotalHours);
        textViewMonthTotalDays = view.findViewById(R.id.textViewMonthTotalDays);
        cardMonthTotal = view.findViewById(R.id.cardMonthTotal);
        cardMonthHoursChart = view.findViewById(R.id.cardMonthHoursChart);
        cardMonthDaysChart = view.findViewById(R.id.cardMonthDaysChart);
        monthHoursBarChart = view.findViewById(R.id.monthHoursBarChart);
        monthDaysBarChart = view.findViewById(R.id.monthDaysBarChart);
        layoutMonthEmptyState = view.findViewById(R.id.layoutMonthEmptyState);
        textViewMonthEmptyMessage = view.findViewById(R.id.textViewMonthEmptyMessage);

        buttonYearlyPrevious = view.findViewById(R.id.buttonYearlyPrevious);
        buttonYearlyNext = view.findViewById(R.id.buttonYearlyNext);
        textViewYearlyPeriodLabel = view.findViewById(R.id.textViewYearlyPeriodLabel);
        textViewYearlyTotalHours = view.findViewById(R.id.textViewYearlyTotalHours);
        textViewYearlyTotalDays = view.findViewById(R.id.textViewYearlyTotalDays);
        cardYearlyTotal = view.findViewById(R.id.cardYearlyTotal);
        cardYearlyHoursChart = view.findViewById(R.id.cardYearlyHoursChart);
        cardYearlyDaysChart = view.findViewById(R.id.cardYearlyDaysChart);
        yearlyHoursBarChart = view.findViewById(R.id.yearlyHoursBarChart);
        yearlyDaysBarChart = view.findViewById(R.id.yearlyDaysBarChart);
        layoutYearlyEmptyState = view.findViewById(R.id.layoutYearlyEmptyState);
        textViewYearlyEmptyMessage = view.findViewById(R.id.textViewYearlyEmptyMessage);

        setupHoursBarChart(weeklyBarChart);
        setupDaysBarChart(weeklyDaysBarChart);
        setupHoursBarChart(monthHoursBarChart);
        setupDaysBarChart(monthDaysBarChart);
        setupHoursBarChart(yearlyHoursBarChart);
        setupDaysBarChart(yearlyDaysBarChart);
    }

    private void setupCardBackgrounds(View view) {
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;

        int backgroundColor;
        if (isDarkTheme) {
            backgroundColor = getContext().getColor(android.R.color.background_dark);
        } else {
            backgroundColor = getContext().getColor(android.R.color.background_light);
        }

        MaterialCardView[] cards = {
                view.findViewById(R.id.cardCategoryDropdown),
                cardWeeklyTotalHours, cardWeeklyChart, cardWeeklyDaysChart,
                cardMonthTotal, cardMonthHoursChart, cardMonthDaysChart,
                cardYearlyTotal, cardYearlyHoursChart, cardYearlyDaysChart
        };
        for (MaterialCardView card : cards) {
            if (card != null) {
                card.setCardBackgroundColor(backgroundColor);
            }
        }
    }

    private void setupHoursBarChart(BarChart chart) {
        setupBarChartBase(chart);
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.1fh", value);
            }
        });
    }

    private void setupDaysBarChart(BarChart chart) {
        setupBarChartBase(chart);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.0f", value);
            }
        });
    }

    private void setupBarChartBase(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setFitBars(true);
        chart.setExtraBottomOffset(10f);

        int nightModeFlags = getContext().getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        int textColor = isDarkTheme ? Color.WHITE : Color.DKGRAY;

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(textColor);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(textColor);

        chart.getAxisRight().setEnabled(false);
    }

    private void setupAskAiBubble() {
        fabAskAi.setOnClickListener(v -> {
            if (getParentFragmentManager().findFragmentByTag(AskAiDialogFragment.TAG) != null) {
                return;
            }
            AskAiDialogFragment.newInstance().show(getParentFragmentManager(), AskAiDialogFragment.TAG);
        });
    }
    private void setupCategoryDropdown() {
        categoryDropdown.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < categories.size()) {
                selectedCategory = categories.get(position);
                loadAllChartData();
            }
        });
    }

    private void setupNavigationButtons() {
        buttonWeeklyPrevious.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentWeekStart);
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            currentWeekStart = calendar.getTime();
            updateWeeklyPeriodLabel();
            if (selectedCategory != null) {
                loadWeeklyData();
            }
        });
        buttonWeeklyNext.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentWeekStart);
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            currentWeekStart = calendar.getTime();
            updateWeeklyPeriodLabel();
            if (selectedCategory != null) {
                loadWeeklyData();
            }
        });

        buttonMonthPrevious.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentMonthStart);
            calendar.add(Calendar.MONTH, -1);
            currentMonthStart = calendar.getTime();
            updateMonthPeriodLabel();
            if (selectedCategory != null) {
                loadMonthData();
            }
        });
        buttonMonthNext.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentMonthStart);
            calendar.add(Calendar.MONTH, 1);
            currentMonthStart = calendar.getTime();
            updateMonthPeriodLabel();
            if (selectedCategory != null) {
                loadMonthData();
            }
        });

        buttonYearlyPrevious.setOnClickListener(v -> {
            currentYear--;
            updateYearlyPeriodLabel();
            if (selectedCategory != null) {
                loadYearlyData();
            }
        });
        buttonYearlyNext.setOnClickListener(v -> {
            currentYear++;
            updateYearlyPeriodLabel();
            if (selectedCategory != null) {
                loadYearlyData();
            }
        });
    }

    private void observeCategories() {
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                this.categories = categories;
                updateCategoryDropdown();
            }
        });
    }

    private void updateCategoryDropdown() {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            String emoji = CategoryEmojiMapper.getEmojiForCategory(category.getName());
            categoryNames.add(emoji + " " + category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categoryNames
        );
        categoryDropdown.setAdapter(adapter);
    }

    // ============ Period Labels ============

    private void updateWeeklyPeriodLabel() {
        if (isCurrentWeek()) {
            textViewWeeklyPeriodLabel.setText("This Week");
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentWeekStart);
            String start = weekFormatter.format(currentWeekStart);
            calendar.add(Calendar.DAY_OF_MONTH, 6);
            String end = weekFormatter.format(calendar.getTime());
            textViewWeeklyPeriodLabel.setText(start + " - " + end);
        }
    }

    private void updateMonthPeriodLabel() {
        if (isCurrentMonth()) {
            textViewMonthPeriodLabel.setText("This Month");
        } else {
            textViewMonthPeriodLabel.setText(monthLabelFormatter.format(currentMonthStart));
        }
    }

    private void updateYearlyPeriodLabel() {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        if (currentYear == thisYear) {
            textViewYearlyPeriodLabel.setText(currentYear + " (This Year)");
        } else {
            textViewYearlyPeriodLabel.setText(String.valueOf(currentYear));
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

    // ============ Load Data ============

    private void loadAllChartData() {
        if (selectedCategory == null) {
            showWeeklyEmptyState("Select a category to see weekly insights");
            showMonthEmptyState("Select a category to see monthly insights");
            showYearlyEmptyState("Select a category to see yearly insights");
            return;
        }
        loadWeeklyData();
        loadMonthData();
        loadYearlyData();
    }

    private List<Activity> filterBySelectedCategory(List<Activity> activities) {
        List<Activity> filtered = new ArrayList<>();
        if (activities == null || selectedCategory == null) {
            return filtered;
        }
        for (Activity activity : activities) {
            if (activity.getCategoryId() == selectedCategory.getId()) {
                filtered.add(activity);
            }
        }
        return filtered;
    }

    private void loadWeeklyData() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeekStart);
        Date weekStart = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date weekEnd = calendar.getTime();

        viewModel.getActivitiesForDateRange(weekStart, weekEnd).observe(getViewLifecycleOwner(), activities -> {
            if (selectedCategory == null) return;

            if (activities == null) {
                showWeeklyEmptyState("No data available for this week");
                return;
            }

            List<Activity> filtered = filterBySelectedCategory(activities);
            if (filtered.isEmpty()) {
                showWeeklyEmptyState("No activities for this category this week");
                return;
            }

            double[] dailyHours = new double[7];
            int[] dailyActive = new int[7];
            Set<String>[] dayKeys = new Set[7];
            for (int i = 0; i < 7; i++) {
                dayKeys[i] = new HashSet<>();
            }

            Calendar activityCal = Calendar.getInstance();
            for (Activity activity : filtered) {
                activityCal.setTime(activity.getDate());
                int dayOfWeek = activityCal.get(Calendar.DAY_OF_WEEK);
                int index = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
                dailyHours[index] += activity.getTimeSpentHours();
                dayKeys[index].add(dayKeyFormatter.format(activity.getDate()));
            }

            List<BarEntry> hoursEntries = new ArrayList<>();
            List<BarEntry> daysEntries = new ArrayList<>();
            String[] labels = new String[7];
            Calendar labelCal = Calendar.getInstance();
            labelCal.setTime(currentWeekStart);

            double totalHours = 0;
            int totalDays = 0;
            Set<String> uniqueDays = new HashSet<>();

            for (int i = 0; i < 7; i++) {
                hoursEntries.add(new BarEntry(i, (float) dailyHours[i]));
                dailyActive[i] = dayKeys[i].isEmpty() ? 0 : 1;
                daysEntries.add(new BarEntry(i, dailyActive[i]));
                labels[i] = dayOfWeekFormatter.format(labelCal.getTime());
                labelCal.add(Calendar.DAY_OF_MONTH, 1);
                totalHours += dailyHours[i];
                uniqueDays.addAll(dayKeys[i]);
            }
            totalDays = uniqueDays.size();

            updateWeeklyCharts(hoursEntries, daysEntries, labels, totalHours, totalDays);
        });
    }

    private void loadMonthData() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentMonthStart);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date monthStart = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date monthEnd = calendar.getTime();

        viewModel.getActivitiesForDateRange(monthStart, monthEnd).observe(getViewLifecycleOwner(), activities -> {
            if (selectedCategory == null) return;

            if (activities == null) {
                showMonthEmptyState("No data available for this month");
                return;
            }

            List<Activity> filtered = filterBySelectedCategory(activities);
            if (filtered.isEmpty()) {
                showMonthEmptyState("No activities for this category this month");
                return;
            }

            double[] dailyHours = new double[daysInMonth];
            Set<String>[] dailyKeys = new Set[daysInMonth];
            for (int i = 0; i < daysInMonth; i++) {
                dailyKeys[i] = new HashSet<>();
            }

            // Week-of-month buckets: days 1-7, 8-14, 15-21, 22-28, 29-31
            int weekBucketCount = (daysInMonth + 6) / 7;
            Set<String>[] weekDayKeys = new Set[weekBucketCount];
            for (int i = 0; i < weekBucketCount; i++) {
                weekDayKeys[i] = new HashSet<>();
            }

            Calendar activityCal = Calendar.getInstance();
            for (Activity activity : filtered) {
                activityCal.setTime(activity.getDate());
                int dayIndex = activityCal.get(Calendar.DAY_OF_MONTH) - 1;
                if (dayIndex < 0 || dayIndex >= daysInMonth) continue;

                String key = dayKeyFormatter.format(activity.getDate());
                dailyHours[dayIndex] += activity.getTimeSpentHours();
                dailyKeys[dayIndex].add(key);

                int weekIndex = dayIndex / 7;
                weekDayKeys[weekIndex].add(key);
            }

            List<BarEntry> hoursEntries = new ArrayList<>();
            String[] dayLabels = new String[daysInMonth];
            double totalHours = 0;
            Set<String> uniqueDays = new HashSet<>();

            for (int i = 0; i < daysInMonth; i++) {
                hoursEntries.add(new BarEntry(i, (float) dailyHours[i]));
                dayLabels[i] = String.valueOf(i + 1);
                totalHours += dailyHours[i];
                uniqueDays.addAll(dailyKeys[i]);
            }

            List<BarEntry> daysEntries = new ArrayList<>();
            String[] weekLabels = new String[weekBucketCount];
            for (int i = 0; i < weekBucketCount; i++) {
                daysEntries.add(new BarEntry(i, weekDayKeys[i].size()));
                weekLabels[i] = "W" + (i + 1);
            }

            updateMonthCharts(hoursEntries, dayLabels, daysEntries, weekLabels, totalHours, uniqueDays.size(), daysInMonth);
        });
    }

    private void loadYearlyData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(currentYear, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date yearStart = calendar.getTime();

        calendar.set(currentYear, Calendar.DECEMBER, 31, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date yearEnd = calendar.getTime();

        viewModel.getActivitiesForDateRange(yearStart, yearEnd).observe(getViewLifecycleOwner(), activities -> {
            if (selectedCategory == null) return;

            if (activities == null) {
                showYearlyEmptyState("No data available for " + currentYear);
                return;
            }

            List<Activity> filtered = filterBySelectedCategory(activities);
            if (filtered.isEmpty()) {
                showYearlyEmptyState("No activities for this category in " + currentYear);
                return;
            }

            double[] monthlyHours = new double[12];
            Set<String>[] monthlyDayKeys = new Set[12];
            for (int i = 0; i < 12; i++) {
                monthlyDayKeys[i] = new HashSet<>();
            }

            Calendar activityCal = Calendar.getInstance();
            for (Activity activity : filtered) {
                activityCal.setTime(activity.getDate());
                int month = activityCal.get(Calendar.MONTH);
                monthlyHours[month] += activity.getTimeSpentHours();
                monthlyDayKeys[month].add(dayKeyFormatter.format(activity.getDate()));
            }

            List<BarEntry> hoursEntries = new ArrayList<>();
            List<BarEntry> daysEntries = new ArrayList<>();
            String[] labels = new String[12];
            Calendar labelCal = Calendar.getInstance();
            double totalHours = 0;
            Set<String> allDays = new HashSet<>();

            for (int i = 0; i < 12; i++) {
                hoursEntries.add(new BarEntry(i, (float) monthlyHours[i]));
                daysEntries.add(new BarEntry(i, monthlyDayKeys[i].size()));
                labelCal.set(currentYear, i, 1);
                labels[i] = monthShortFormatter.format(labelCal.getTime());
                totalHours += monthlyHours[i];
                allDays.addAll(monthlyDayKeys[i]);
            }

            updateYearlyCharts(hoursEntries, daysEntries, labels, totalHours, allDays.size());
        });
    }

    // ============ Update Charts ============

    private void updateWeeklyCharts(List<BarEntry> hoursEntries, List<BarEntry> daysEntries,
                                    String[] labels, double totalHours, int totalDays) {
        weeklyBarChart.setVisibility(View.VISIBLE);
        layoutWeeklyEmptyState.setVisibility(View.GONE);
        cardWeeklyTotalHours.setVisibility(View.VISIBLE);
        cardWeeklyDaysChart.setVisibility(View.VISIBLE);

        textViewWeeklyTotalHours.setText(String.format(Locale.getDefault(), "%.1fh", totalHours));
        textViewWeeklyTotalDays.setText(String.format(Locale.getDefault(), "• %d/7 days", totalDays));

        applyHoursChart(weeklyBarChart, hoursEntries, labels, 0.6f, 11f, false);
        applyDaysChart(weeklyDaysBarChart, daysEntries, labels, 0.6f, 11f, false);
    }

    private void updateMonthCharts(List<BarEntry> hoursEntries, String[] dayLabels,
                                   List<BarEntry> daysEntries, String[] weekLabels,
                                   double totalHours, int totalDays, int daysInMonth) {
        monthHoursBarChart.setVisibility(View.VISIBLE);
        layoutMonthEmptyState.setVisibility(View.GONE);
        cardMonthTotal.setVisibility(View.VISIBLE);
        cardMonthDaysChart.setVisibility(View.VISIBLE);

        textViewMonthTotalHours.setText(String.format(Locale.getDefault(), "%.1fh", totalHours));
        textViewMonthTotalDays.setText(String.format(Locale.getDefault(), "• %d/%d days", totalDays, daysInMonth));

        applyHoursChart(monthHoursBarChart, hoursEntries, dayLabels, 0.7f, 9f, true);
        applyDaysChart(monthDaysBarChart, daysEntries, weekLabels, 0.6f, 11f, false);
    }

    private void updateYearlyCharts(List<BarEntry> hoursEntries, List<BarEntry> daysEntries,
                                    String[] labels, double totalHours, int totalDays) {
        yearlyHoursBarChart.setVisibility(View.VISIBLE);
        layoutYearlyEmptyState.setVisibility(View.GONE);
        cardYearlyTotal.setVisibility(View.VISIBLE);
        cardYearlyDaysChart.setVisibility(View.VISIBLE);

        textViewYearlyTotalHours.setText(String.format(Locale.getDefault(), "%.1fh", totalHours));
        textViewYearlyTotalDays.setText(String.format(Locale.getDefault(), "• %d days", totalDays));

        applyHoursChart(yearlyHoursBarChart, hoursEntries, labels, 0.7f, 10f, true);
        applyDaysChart(yearlyDaysBarChart, daysEntries, labels, 0.7f, 10f, true);
    }

    private void applyHoursChart(BarChart chart, List<BarEntry> entries, String[] labels,
                                 float barWidth, float valueTextSize, boolean rotateLabels) {
        int barColor = getCategoryColor();

        BarDataSet dataSet = new BarDataSet(entries, "Hours");
        dataSet.setColor(barColor);
        dataSet.setValueTextSize(valueTextSize);
        dataSet.setValueTextColor(getChartTextColor());
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "";
                return String.format(Locale.getDefault(), "%.1f", value);
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(barWidth);
        chart.setData(barData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.length, false);
        xAxis.setTextSize(rotateLabels ? 10f : 12f);
        xAxis.setLabelRotationAngle(rotateLabels ? -45f : 0f);

        chart.animateY(500);
        chart.invalidate();
    }

    private void applyDaysChart(BarChart chart, List<BarEntry> entries, String[] labels,
                                float barWidth, float valueTextSize, boolean rotateLabels) {
        int barColor = getCategoryColor();

        BarDataSet dataSet = new BarDataSet(entries, "Days");
        dataSet.setColor(barColor);
        dataSet.setValueTextSize(valueTextSize);
        dataSet.setValueTextColor(getChartTextColor());
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "";
                return String.format(Locale.getDefault(), "%.0f", value);
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(barWidth);
        chart.setData(barData);

        float maxVal = 0f;
        for (BarEntry entry : entries) {
            maxVal = Math.max(maxVal, entry.getY());
        }
        chart.getAxisLeft().setAxisMaximum(Math.max(maxVal, 1f) + 0.5f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.length, false);
        xAxis.setTextSize(rotateLabels ? 10f : 12f);
        xAxis.setLabelRotationAngle(rotateLabels ? -45f : 0f);

        chart.animateY(500);
        chart.invalidate();
    }

    // ============ Empty States ============

    private void showWeeklyEmptyState(String message) {
        weeklyBarChart.setVisibility(View.GONE);
        layoutWeeklyEmptyState.setVisibility(View.VISIBLE);
        cardWeeklyTotalHours.setVisibility(View.GONE);
        cardWeeklyDaysChart.setVisibility(View.GONE);
        textViewWeeklyEmptyMessage.setText(message);
        weeklyBarChart.clear();
        weeklyDaysBarChart.clear();
    }

    private void showMonthEmptyState(String message) {
        monthHoursBarChart.setVisibility(View.GONE);
        layoutMonthEmptyState.setVisibility(View.VISIBLE);
        cardMonthTotal.setVisibility(View.GONE);
        cardMonthDaysChart.setVisibility(View.GONE);
        textViewMonthEmptyMessage.setText(message);
        monthHoursBarChart.clear();
        monthDaysBarChart.clear();
    }

    private void showYearlyEmptyState(String message) {
        yearlyHoursBarChart.setVisibility(View.GONE);
        layoutYearlyEmptyState.setVisibility(View.VISIBLE);
        cardYearlyTotal.setVisibility(View.GONE);
        cardYearlyDaysChart.setVisibility(View.GONE);
        textViewYearlyEmptyMessage.setText(message);
        yearlyHoursBarChart.clear();
        yearlyDaysBarChart.clear();
    }

    // ============ Helpers ============

    private int getCategoryColor() {
        if (selectedCategory != null) {
            try {
                return Color.parseColor(selectedCategory.getColor());
            } catch (Exception e) {
                // fall through
            }
        }
        return getContext().getColor(R.color.primary);
    }

    private int getChartTextColor() {
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        return isDarkTheme ? Color.WHITE : Color.DKGRAY;
    }
}
