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
import com.google.android.material.textfield.TextInputLayout;
import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
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

public class InsightsFragment extends Fragment {

    private MainViewModel viewModel;

    // UI elements - Category dropdown
    private AutoCompleteTextView categoryDropdown;
    private TextInputLayout categoryDropdownLayout;

    // UI elements - Weekly section
    private MaterialButton buttonWeeklyPrevious;
    private MaterialButton buttonWeeklyNext;
    private TextView textViewWeeklyPeriodLabel;
    private TextView textViewWeeklyTotalHours;
    private MaterialCardView cardWeeklyTotalHours;
    private MaterialCardView cardWeeklyChart;
    private BarChart weeklyBarChart;
    private LinearLayout layoutWeeklyEmptyState;
    private TextView textViewWeeklyEmptyMessage;

    // UI elements - Monthly section
    private MaterialButton buttonMonthlyPrevious;
    private MaterialButton buttonMonthlyNext;
    private TextView textViewMonthlyPeriodLabel;
    private TextView textViewMonthlyTotalHours;
    private MaterialCardView cardMonthlyTotalHours;
    private MaterialCardView cardMonthlyChart;
    private BarChart monthlyBarChart;
    private LinearLayout layoutMonthlyEmptyState;
    private TextView textViewMonthlyEmptyMessage;

    // Data
    private List<Category> categories = new ArrayList<>();
    private Category selectedCategory = null;

    // Navigation state
    private Date currentWeekStart;
    private int currentYear;

    // Formatters
    private SimpleDateFormat weekFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
    private SimpleDateFormat dayOfWeekFormatter = new SimpleDateFormat("EEE", Locale.getDefault());
    private SimpleDateFormat monthShortFormatter = new SimpleDateFormat("MMM", Locale.getDefault());

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

        // Set current year
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insights, container, false);

        initializeViews(view);
        setupCardBackgrounds(view);
        setupCategoryDropdown();
        setupNavigationButtons();
        updateWeeklyPeriodLabel();
        updateMonthlyPeriodLabel();
        observeCategories();

        return view;
    }

    private void initializeViews(View view) {
        // Category dropdown
        categoryDropdown = view.findViewById(R.id.categoryDropdown);
        categoryDropdownLayout = view.findViewById(R.id.categoryDropdownLayout);

        // Weekly section
        buttonWeeklyPrevious = view.findViewById(R.id.buttonWeeklyPrevious);
        buttonWeeklyNext = view.findViewById(R.id.buttonWeeklyNext);
        textViewWeeklyPeriodLabel = view.findViewById(R.id.textViewWeeklyPeriodLabel);
        textViewWeeklyTotalHours = view.findViewById(R.id.textViewWeeklyTotalHours);
        cardWeeklyTotalHours = view.findViewById(R.id.cardWeeklyTotalHours);
        cardWeeklyChart = view.findViewById(R.id.cardWeeklyChart);
        weeklyBarChart = view.findViewById(R.id.weeklyBarChart);
        layoutWeeklyEmptyState = view.findViewById(R.id.layoutWeeklyEmptyState);
        textViewWeeklyEmptyMessage = view.findViewById(R.id.textViewWeeklyEmptyMessage);

        // Monthly section
        buttonMonthlyPrevious = view.findViewById(R.id.buttonMonthlyPrevious);
        buttonMonthlyNext = view.findViewById(R.id.buttonMonthlyNext);
        textViewMonthlyPeriodLabel = view.findViewById(R.id.textViewMonthlyPeriodLabel);
        textViewMonthlyTotalHours = view.findViewById(R.id.textViewMonthlyTotalHours);
        cardMonthlyTotalHours = view.findViewById(R.id.cardMonthlyTotalHours);
        cardMonthlyChart = view.findViewById(R.id.cardMonthlyChart);
        monthlyBarChart = view.findViewById(R.id.monthlyBarChart);
        layoutMonthlyEmptyState = view.findViewById(R.id.layoutMonthlyEmptyState);
        textViewMonthlyEmptyMessage = view.findViewById(R.id.textViewMonthlyEmptyMessage);

        setupBarChart(weeklyBarChart);
        setupBarChart(monthlyBarChart);
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
                cardWeeklyTotalHours, cardWeeklyChart,
                cardMonthlyTotalHours, cardMonthlyChart
        };
        for (MaterialCardView card : cards) {
            if (card != null) {
                card.setCardBackgroundColor(backgroundColor);
            }
        }
    }

    private void setupBarChart(BarChart chart) {
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

        // Check dark mode for text colors
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        int textColor = isDarkTheme ? Color.WHITE : Color.DKGRAY;

        // X axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(textColor);

        // Y axis (left)
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(textColor);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.1fh", value);
            }
        });

        // Y axis (right) - disabled
        chart.getAxisRight().setEnabled(false);
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
        // Weekly navigation
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

        // Year navigation for monthly chart
        buttonMonthlyPrevious.setOnClickListener(v -> {
            currentYear--;
            updateMonthlyPeriodLabel();
            if (selectedCategory != null) {
                loadMonthlyData();
            }
        });
        buttonMonthlyNext.setOnClickListener(v -> {
            currentYear++;
            updateMonthlyPeriodLabel();
            if (selectedCategory != null) {
                loadMonthlyData();
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

    private void updateMonthlyPeriodLabel() {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        if (currentYear == thisYear) {
            textViewMonthlyPeriodLabel.setText(String.valueOf(currentYear) + " (This Year)");
        } else {
            textViewMonthlyPeriodLabel.setText(String.valueOf(currentYear));
        }
    }

    private boolean isCurrentWeek() {
        Calendar current = Calendar.getInstance();
        Calendar week = Calendar.getInstance();
        week.setTime(currentWeekStart);
        return current.get(Calendar.YEAR) == week.get(Calendar.YEAR) &&
                current.get(Calendar.WEEK_OF_YEAR) == week.get(Calendar.WEEK_OF_YEAR);
    }

    

    // ============ Load Data ============

    private void loadAllChartData() {
        if (selectedCategory == null) {
            showWeeklyEmptyState("Select a category to see weekly insights");
            showMonthlyEmptyState("Select a category to see monthly insights");
            return;
        }
        loadWeeklyData();
        loadMonthlyData();
    }

    private void loadWeeklyData() {
        // Calculate week range: Monday to Sunday
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

            // Filter by selected category
            List<Activity> filtered = new ArrayList<>();
            for (Activity activity : activities) {
                if (activity.getCategoryId() == selectedCategory.getId()) {
                    filtered.add(activity);
                }
            }

            if (filtered.isEmpty()) {
                showWeeklyEmptyState("No activities for this category this week");
                return;
            }

            // Group by day of week (Mon-Sun)
            Map<Integer, Double> dailyHours = new HashMap<>();
            for (int i = 0; i < 7; i++) {
                dailyHours.put(i, 0.0);
            }

            Calendar activityCal = Calendar.getInstance();
            for (Activity activity : filtered) {
                activityCal.setTime(activity.getDate());
                int dayOfWeek = activityCal.get(Calendar.DAY_OF_WEEK);
                // Convert to Mon=0, Tue=1, ..., Sun=6
                int index = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
                dailyHours.put(index, dailyHours.get(index) + activity.getTimeSpentHours());
            }

            // Build chart entries
            List<BarEntry> entries = new ArrayList<>();
            String[] labels = new String[7];
            Calendar labelCal = Calendar.getInstance();
            labelCal.setTime(currentWeekStart);

            double total = 0;
            for (int i = 0; i < 7; i++) {
                float value = dailyHours.get(i).floatValue();
                entries.add(new BarEntry(i, value));
                labels[i] = dayOfWeekFormatter.format(labelCal.getTime());
                labelCal.add(Calendar.DAY_OF_MONTH, 1);
                total += value;
            }

            updateWeeklyChart(entries, labels, total);
        });
    }

    private void loadMonthlyData() {
        // Calculate full year range: Jan 1 to Dec 31
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
                showMonthlyEmptyState("No data available for " + currentYear);
                return;
            }

            // Filter by selected category
            List<Activity> filtered = new ArrayList<>();
            for (Activity activity : activities) {
                if (activity.getCategoryId() == selectedCategory.getId()) {
                    filtered.add(activity);
                }
            }

            if (filtered.isEmpty()) {
                showMonthlyEmptyState("No activities for this category in " + currentYear);
                return;
            }

            // Group hours by month (0=Jan .. 11=Dec)
            double[] monthlyHours = new double[12];
            Calendar activityCal = Calendar.getInstance();
            for (Activity activity : filtered) {
                activityCal.setTime(activity.getDate());
                int month = activityCal.get(Calendar.MONTH); // 0-based
                monthlyHours[month] += activity.getTimeSpentHours();
            }

            // Build chart entries and labels
            List<BarEntry> entries = new ArrayList<>();
            String[] labels = new String[12];
            Calendar labelCal = Calendar.getInstance();
            double total = 0;

            for (int i = 0; i < 12; i++) {
                entries.add(new BarEntry(i, (float) monthlyHours[i]));
                labelCal.set(currentYear, i, 1);
                labels[i] = monthShortFormatter.format(labelCal.getTime());
                total += monthlyHours[i];
            }

            updateMonthlyChart(entries, labels, total);
        });
    }

    // ============ Update Charts ============

    private void updateWeeklyChart(List<BarEntry> entries, String[] labels, double totalHours) {
        weeklyBarChart.setVisibility(View.VISIBLE);
        layoutWeeklyEmptyState.setVisibility(View.GONE);
        cardWeeklyTotalHours.setVisibility(View.VISIBLE);

        textViewWeeklyTotalHours.setText(String.format(Locale.getDefault(), "%.1fh", totalHours));

        int barColor = getCategoryColor();

        BarDataSet dataSet = new BarDataSet(entries, "Hours");
        dataSet.setColor(barColor);
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(getChartTextColor());
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "";
                return String.format(Locale.getDefault(), "%.1f", value);
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        weeklyBarChart.setData(barData);

        XAxis xAxis = weeklyBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.length);

        weeklyBarChart.animateY(500);
        weeklyBarChart.invalidate();
    }

    private void updateMonthlyChart(List<BarEntry> entries, String[] labels, double totalHours) {
        monthlyBarChart.setVisibility(View.VISIBLE);
        layoutMonthlyEmptyState.setVisibility(View.GONE);
        cardMonthlyTotalHours.setVisibility(View.VISIBLE);

        textViewMonthlyTotalHours.setText(String.format(Locale.getDefault(), "%.1fh", totalHours));

        int barColor = getCategoryColor();

        BarDataSet dataSet = new BarDataSet(entries, "Hours");
        dataSet.setColor(barColor);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(getChartTextColor());
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "";
                return String.format(Locale.getDefault(), "%.1f", value);
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);

        monthlyBarChart.setData(barData);

        // X-axis: fit all 12 month labels
        XAxis xAxis = monthlyBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.length, false);
        xAxis.setTextSize(10f);
        xAxis.setLabelRotationAngle(-45f);

        monthlyBarChart.animateY(500);
        monthlyBarChart.invalidate();
    }

    // ============ Empty States ============

    private void showWeeklyEmptyState(String message) {
        weeklyBarChart.setVisibility(View.GONE);
        layoutWeeklyEmptyState.setVisibility(View.VISIBLE);
        cardWeeklyTotalHours.setVisibility(View.GONE);
        textViewWeeklyEmptyMessage.setText(message);
        weeklyBarChart.clear();
    }

    private void showMonthlyEmptyState(String message) {
        monthlyBarChart.setVisibility(View.GONE);
        layoutMonthlyEmptyState.setVisibility(View.VISIBLE);
        cardMonthlyTotalHours.setVisibility(View.GONE);
        textViewMonthlyEmptyMessage.setText(message);
        monthlyBarChart.clear();
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
