package com.journal.life5to9.ui.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.ui.adapters.ActivityAdapter;
import com.journal.life5to9.viewmodel.MainViewModel;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.android.material.button.MaterialButton;
import com.journal.life5to9.data.entity.Category;
import android.graphics.Color;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private TextView textViewMonthYear;
    private ImageButton buttonPreviousMonth, buttonNextMonth;
    private MaterialButton buttonMonthView, buttonWeekView, buttonDayView;
    private LinearLayout layoutDayTitles;
    private LinearLayout layoutSelectedDateDetails;
    private TextView textViewSelectedDateTitle;
    private TextView textViewNoActivitiesForDate;
    private RecyclerView recyclerViewSelectedDateActivities;
    
    // Week and Day view containers
    private android.widget.ScrollView scrollViewWeek;
    private LinearLayout layoutWeekView;
    private android.widget.ScrollView scrollViewDay;
    private LinearLayout layoutDayView;
    
    private MainViewModel viewModel;
    private ActivityAdapter activityAdapter;
    
    private YearMonth currentCalendarMonth;
    private LocalDate selectedDate = null;
    private LocalDate currentWeekStart = null; // For week view
    private LocalDate currentDay = null; // For day view
    private Set<LocalDate> activityDates = new HashSet<>();
    private Map<LocalDate, List<Activity>> activitiesByDate = new HashMap<>();
    private List<Category> categories = new ArrayList<>();
    
    // View mode: 0 = Month, 1 = Week, 2 = Day
    private int currentViewMode = 0;
    
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault());
    private final DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("MMM d - MMM d, yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        initializeViewModel();
        setupCalendar();
        setupDayOfWeekTitles();
        setupMonthNavigation();
        observeViewModel();
        
        // Initialize view visibility
        updateViewVisibility();
        
        // Auto-select today's date if it's in the current view
        if (currentCalendarMonth.equals(YearMonth.now())) {
            selectDateAndLoadActivities(LocalDate.now());
        }
    }

    private void initializeViews(View view) {
        calendarView = view.findViewById(R.id.calendarView);
        textViewMonthYear = view.findViewById(R.id.textViewMonthYear);
        buttonPreviousMonth = view.findViewById(R.id.buttonPreviousMonth);
        buttonNextMonth = view.findViewById(R.id.buttonNextMonth);
        buttonMonthView = view.findViewById(R.id.buttonMonthView);
        buttonWeekView = view.findViewById(R.id.buttonWeekView);
        buttonDayView = view.findViewById(R.id.buttonDayView);
        layoutDayTitles = view.findViewById(R.id.layoutDayTitles);
        layoutSelectedDateDetails = view.findViewById(R.id.layoutSelectedDateDetails);
        textViewSelectedDateTitle = view.findViewById(R.id.textViewSelectedDateTitle);
        textViewNoActivitiesForDate = view.findViewById(R.id.textViewNoActivitiesForDate);
        recyclerViewSelectedDateActivities = view.findViewById(R.id.recyclerViewSelectedDateActivities);
        
        // Week and Day view containers
        scrollViewWeek = view.findViewById(R.id.scrollViewWeek);
        layoutWeekView = view.findViewById(R.id.layoutWeekView);
        scrollViewDay = view.findViewById(R.id.scrollViewDay);
        layoutDayView = view.findViewById(R.id.layoutDayView);
        
        // Setup RecyclerView
        recyclerViewSelectedDateActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        activityAdapter = new ActivityAdapter();
        recyclerViewSelectedDateActivities.setAdapter(activityAdapter);
        
        // Setup view mode switcher
        setupViewModeSwitcher();
        
        // Initialize current week and day
        LocalDate today = LocalDate.now();
        currentWeekStart = today.with(java.time.DayOfWeek.MONDAY);
        currentDay = today;
    }
    
    private void setupViewModeSwitcher() {
        buttonMonthView.setOnClickListener(v -> switchViewMode(0));
        buttonWeekView.setOnClickListener(v -> switchViewMode(1));
        buttonDayView.setOnClickListener(v -> switchViewMode(2));
        
        // Set initial state
        updateViewModeButtons();
    }
    
    private void switchViewMode(int mode) {
        currentViewMode = mode;
        updateViewModeButtons();
        updateViewVisibility();
        
        switch (mode) {
            case 0: // Month view
                if (currentCalendarMonth == null) {
                    currentCalendarMonth = YearMonth.now();
                }
                calendarView.scrollToMonth(currentCalendarMonth);
                updateMonthYearDisplay();
                break;
            case 1: // Week view
                if (currentWeekStart == null) {
                    currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
                }
                populateWeekView();
                updateWeekDisplay();
                break;
            case 2: // Day view
                if (currentDay == null) {
                    currentDay = LocalDate.now();
                }
                populateDayView();
                updateDayDisplay();
                break;
        }
    }
    
    private void updateViewVisibility() {
        // Show/hide appropriate views
        if (currentViewMode == 0) {
            // Month view
            calendarView.setVisibility(View.VISIBLE);
            layoutDayTitles.setVisibility(View.VISIBLE);
            scrollViewWeek.setVisibility(View.GONE);
            scrollViewDay.setVisibility(View.GONE);
            layoutSelectedDateDetails.setVisibility(View.GONE);
        } else if (currentViewMode == 1) {
            // Week view
            calendarView.setVisibility(View.GONE);
            layoutDayTitles.setVisibility(View.VISIBLE);
            scrollViewWeek.setVisibility(View.VISIBLE);
            scrollViewDay.setVisibility(View.GONE);
            layoutSelectedDateDetails.setVisibility(View.GONE);
        } else {
            // Day view
            calendarView.setVisibility(View.GONE);
            layoutDayTitles.setVisibility(View.GONE);
            scrollViewWeek.setVisibility(View.GONE);
            scrollViewDay.setVisibility(View.VISIBLE);
            layoutSelectedDateDetails.setVisibility(View.GONE);
        }
    }
    
    private void updateViewModeButtons() {
        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        
        // Reset all buttons
        MaterialButton[] buttons = {buttonMonthView, buttonWeekView, buttonDayView};
        for (MaterialButton button : buttons) {
            button.setSelected(false);
            if (isDarkTheme) {
                button.setBackgroundTintList(getResources().getColorStateList(R.color.surface_variant, null));
                button.setTextColor(getResources().getColorStateList(R.color.on_surface_variant, null));
            } else {
                button.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
                button.setTextColor(getResources().getColorStateList(R.color.primary, null));
            }
        }
        
        // Set selected button
        MaterialButton selectedButton = buttons[currentViewMode];
        selectedButton.setSelected(true);
        selectedButton.setBackgroundTintList(getResources().getColorStateList(R.color.selected_date_orange, null));
        selectedButton.setTextColor(getResources().getColorStateList(android.R.color.white, null));
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    private void setupCalendar() {
        currentCalendarMonth = YearMonth.now();
        
        // Define the start and end month for the calendar
        YearMonth startMonth = currentCalendarMonth.minusYears(10);
        YearMonth endMonth = currentCalendarMonth.plusYears(10);
        List<DayOfWeek> daysOfWeek = daysOfWeek(DayOfWeek.MONDAY);
        
        calendarView.setup(startMonth, endMonth, daysOfWeek.get(0));
        calendarView.scrollToMonth(currentCalendarMonth);
        
        // Month scroll listener
        calendarView.setMonthScrollListener(calendarMonth -> {
            currentCalendarMonth = calendarMonth.getYearMonth();
            updateMonthYearDisplay();
            loadActivitiesForMonth();
            return null;
        });
        
        updateMonthYearDisplay();
        
        // Day binder
        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @Override
            public DayViewContainer create(View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(DayViewContainer container, CalendarDay day) {
                container.day = day;
                TextView textView = container.textView;
                textView.setText(String.valueOf(day.getDate().getDayOfMonth()));
                
                View highlightView = container.highlightView;
                LinearLayout categoryIndicators = container.categoryIndicators;
                
                // Clear existing category indicators
                categoryIndicators.removeAllViews();
                
                if (day.getPosition() == DayPosition.MonthDate) {
                    // Get activities for this date
                    List<Activity> dayActivities = activitiesByDate.getOrDefault(day.getDate(), new ArrayList<>());
                    
                    // Group activities by category
                    Map<Long, Integer> categoryCounts = new HashMap<>();
                    Map<Long, String> categoryColors = new HashMap<>();
                    
                    for (Activity activity : dayActivities) {
                        long categoryId = activity.getCategoryId();
                        categoryCounts.put(categoryId, categoryCounts.getOrDefault(categoryId, 0) + 1);
                        
                        // Find category color
                        if (!categoryColors.containsKey(categoryId)) {
                            for (Category category : categories) {
                                if (category.getId() == categoryId) {
                                    categoryColors.put(categoryId, category.getColor());
                                    break;
                                }
                            }
                        }
                    }
                    
                    // Display category indicators (max 4 to avoid crowding)
                    int indicatorCount = 0;
                    for (Map.Entry<Long, Integer> entry : categoryCounts.entrySet()) {
                        if (indicatorCount >= 4) break; // Limit to 4 indicators
                        
                        long categoryId = entry.getKey();
                        String categoryColor = categoryColors.getOrDefault(categoryId, "#FF2E7D32");
                        
                        // Create colored dot indicator
                        View indicator = new View(getContext());
                        float density = getResources().getDisplayMetrics().density;
                        int size = (int) (8 * density);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                        params.setMargins((int)(2 * density), 0, (int)(2 * density), 0);
                        indicator.setLayoutParams(params);
                        
                        // Create circular background
                        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
                        drawable.setShape(android.graphics.drawable.GradientDrawable.OVAL);
                        try {
                            drawable.setColor(Color.parseColor(categoryColor));
                        } catch (IllegalArgumentException e) {
                            drawable.setColor(Color.parseColor("#FF2E7D32"));
                        }
                        indicator.setBackground(drawable);
                        
                        categoryIndicators.addView(indicator);
                        indicatorCount++;
                    }
                    
                    highlightView.setVisibility(activityDates.contains(day.getDate()) ? View.INVISIBLE : View.INVISIBLE);
                    
                    // Check if dark theme is enabled
                    int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
                    boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
                    
                    // Handle today highlighting
                    if (day.getDate().equals(LocalDate.now())) {
                        textView.setTextColor(getResources().getColor(android.R.color.white));
                        textView.setBackgroundColor(getResources().getColor(R.color.primary));
                    } else {
                        // Use theme-aware text color for current month dates
                        if (isDarkTheme) {
                            textView.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
                        } else {
                            textView.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                        }
                        textView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                    
                    // Handle selected date highlighting
                    if (day.getDate().equals(selectedDate)) {
                        textView.setBackgroundColor(getResources().getColor(R.color.selected_date_orange));
                        textView.setTextColor(getResources().getColor(android.R.color.white));
                    }
                    
                    // Click listener
                    container.getView().setOnClickListener(v -> {
                        if (day.getDate().getMonth() == currentCalendarMonth.getMonth()) {
                            selectDateAndLoadActivities(day.getDate());
                        }
                    });
                    
                } else {
                    textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    highlightView.setVisibility(View.INVISIBLE);
                    container.getView().setOnClickListener(null);
                }
            }
        });
    }

    private void setupDayOfWeekTitles() {
        layoutDayTitles.removeAllViews();
        List<DayOfWeek> daysOfWeek = daysOfWeek(DayOfWeek.MONDAY);
        for (DayOfWeek dayOfWeek : daysOfWeek) {
            TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.calendar_day_title_layout, layoutDayTitles, false);
            tv.setText(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            layoutDayTitles.addView(tv);
        }
    }

    private void setupMonthNavigation() {
        buttonPreviousMonth.setOnClickListener(v -> {
            switch (currentViewMode) {
                case 0: // Month view
                    currentCalendarMonth = currentCalendarMonth.minusMonths(1);
                    calendarView.scrollToMonth(currentCalendarMonth);
                    updateMonthYearDisplay();
                    loadActivitiesForMonth();
                    break;
                case 1: // Week view
                    currentWeekStart = currentWeekStart.minusWeeks(1);
                    populateWeekView();
                    updateWeekDisplay();
                    break;
                case 2: // Day view
                    currentDay = currentDay.minusDays(1);
                    populateDayView();
                    updateDayDisplay();
                    break;
            }
        });
        
        buttonNextMonth.setOnClickListener(v -> {
            switch (currentViewMode) {
                case 0: // Month view
                    currentCalendarMonth = currentCalendarMonth.plusMonths(1);
                    calendarView.scrollToMonth(currentCalendarMonth);
                    updateMonthYearDisplay();
                    loadActivitiesForMonth();
                    break;
                case 1: // Week view
                    currentWeekStart = currentWeekStart.plusWeeks(1);
                    populateWeekView();
                    updateWeekDisplay();
                    break;
                case 2: // Day view
                    currentDay = currentDay.plusDays(1);
                    populateDayView();
                    updateDayDisplay();
                    break;
            }
        });
    }

    private void updateMonthYearDisplay() {
        textViewMonthYear.setText(monthYearFormatter.format(currentCalendarMonth));
    }

    private void observeViewModel() {
        // Observe categories first
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categoriesList -> {
            if (categoriesList != null) {
                this.categories = categoriesList;
                activityAdapter.setCategories(categoriesList);
                calendarView.notifyMonthChanged(currentCalendarMonth);
                // Refresh current view
                refreshCurrentView();
            }
        });
        
        // Observe activities for the current month
        viewModel.getAllActivities().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null) {
                updateActivityDates(activities);
                updateActivitiesByDate(activities);
                calendarView.notifyMonthChanged(currentCalendarMonth);
                // Refresh current view
                refreshCurrentView();
            }
        });
        
        // Observe activities for selected date
        if (selectedDate != null) {
            loadActivitiesForSelectedDate();
        }
    }
    
    private void refreshCurrentView() {
        switch (currentViewMode) {
            case 1: // Week view
                populateWeekView();
                break;
            case 2: // Day view
                populateDayView();
                break;
        }
    }
    
    private void updateActivitiesByDate(List<Activity> activities) {
        activitiesByDate.clear();
        for (Activity activity : activities) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(activity.getDate());
            LocalDate activityDate = LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            
            if (!activitiesByDate.containsKey(activityDate)) {
                activitiesByDate.put(activityDate, new ArrayList<>());
            }
            activitiesByDate.get(activityDate).add(activity);
        }
    }

    private void updateActivityDates(List<Activity> activities) {
        activityDates.clear();
        for (Activity activity : activities) {
            // Convert Date to LocalDate
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(activity.getDate());
            LocalDate activityDate = LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, // Calendar months are 0-indexed
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            activityDates.add(activityDate);
        }
    }

    private void loadActivitiesForMonth() {
        // This will be handled by the observeViewModel method
        // The calendar will automatically update when activities change
    }

    private void selectDateAndLoadActivities(LocalDate date) {
        LocalDate oldSelectedDate = selectedDate;
        selectedDate = date;
        
        // Update calendar
        if (oldSelectedDate != null) {
            calendarView.notifyDateChanged(oldSelectedDate);
        }
        if (selectedDate != null) {
            calendarView.notifyDateChanged(selectedDate);
        }
        
        loadActivitiesForSelectedDate();
    }

    private void loadActivitiesForSelectedDate() {
        if (selectedDate == null) {
            layoutSelectedDateDetails.setVisibility(View.GONE);
            return;
        }
        
        // Convert LocalDate to Date for the ViewModel
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedDate.getYear(), selectedDate.getMonthValue() - 1, selectedDate.getDayOfMonth(), 0, 0, 0);
        Date startDate = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDate = calendar.getTime();
        
        // Load activities for the selected date
        viewModel.getActivitiesForDateRange(startDate, endDate).observe(getViewLifecycleOwner(), activities -> {
            if (activities != null && !activities.isEmpty()) {
                activityAdapter.setActivities(activities);
                recyclerViewSelectedDateActivities.setVisibility(View.VISIBLE);
                textViewNoActivitiesForDate.setVisibility(View.GONE);
                layoutSelectedDateDetails.setVisibility(View.VISIBLE);
                textViewSelectedDateTitle.setText("Activities for: " + dateFormatter.format(selectedDate));
            } else {
                activityAdapter.setActivities(new ArrayList<>());
                recyclerViewSelectedDateActivities.setVisibility(View.GONE);
                textViewNoActivitiesForDate.setVisibility(View.VISIBLE);
                layoutSelectedDateDetails.setVisibility(View.VISIBLE);
                textViewSelectedDateTitle.setText("Activities for: " + dateFormatter.format(selectedDate));
            }
        });
    }

    private void populateWeekView() {
        layoutWeekView.removeAllViews();
        
        if (currentWeekStart == null) {
            currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        }
        
        // Create 7 day cells for the week
        for (int i = 0; i < 7; i++) {
            LocalDate dayDate = currentWeekStart.plusDays(i);
            View dayCell = createWeekDayCell(dayDate);
            layoutWeekView.addView(dayCell);
        }
    }
    
    private View createWeekDayCell(LocalDate date) {
        // Inflate the day layout
        View dayView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_day_layout, null);
        
        TextView dayText = dayView.findViewById(R.id.calendarDayText);
        LinearLayout categoryIndicators = dayView.findViewById(R.id.layoutCategoryIndicators);
        View highlightView = dayView.findViewById(R.id.calendarDayHighlightView);
        
        // Set day number
        dayText.setText(String.valueOf(date.getDayOfMonth()));
        
        // Clear existing indicators
        categoryIndicators.removeAllViews();
        
        // Get activities for this date
        List<Activity> dayActivities = activitiesByDate.getOrDefault(date, new ArrayList<>());
        
        // Group activities by category
        Map<Long, Integer> categoryCounts = new HashMap<>();
        Map<Long, String> categoryColors = new HashMap<>();
        
        for (Activity activity : dayActivities) {
            long categoryId = activity.getCategoryId();
            categoryCounts.put(categoryId, categoryCounts.getOrDefault(categoryId, 0) + 1);
            
            // Find category color
            if (!categoryColors.containsKey(categoryId)) {
                for (Category category : categories) {
                    if (category.getId() == categoryId) {
                        categoryColors.put(categoryId, category.getColor());
                        break;
                    }
                }
            }
        }
        
        // Display category indicators (max 6 for week view)
        int indicatorCount = 0;
        for (Map.Entry<Long, Integer> entry : categoryCounts.entrySet()) {
            if (indicatorCount >= 6) break;
            
            long categoryId = entry.getKey();
            String categoryColor = categoryColors.getOrDefault(categoryId, "#FF2E7D32");
            
            // Create colored dot indicator
            View indicator = new View(getContext());
            float density = getResources().getDisplayMetrics().density;
            int size = (int) (10 * density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins((int)(3 * density), 0, (int)(3 * density), 0);
            indicator.setLayoutParams(params);
            
            // Create circular background
            android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
            drawable.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            try {
                drawable.setColor(Color.parseColor(categoryColor));
            } catch (IllegalArgumentException e) {
                drawable.setColor(Color.parseColor("#FF2E7D32"));
            }
            indicator.setBackground(drawable);
            
            categoryIndicators.addView(indicator);
            indicatorCount++;
        }
        
        // Set layout params for week view (equal width, minimum height)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        params.height = (int) (200 * getResources().getDisplayMetrics().density); // Minimum height for week cells
        params.setMargins(4, 4, 4, 4);
        dayView.setLayoutParams(params);
        
        // Handle today highlighting
        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        
        if (date.equals(LocalDate.now())) {
            dayText.setTextColor(getResources().getColor(android.R.color.white));
            dayText.setBackgroundColor(getResources().getColor(R.color.primary));
        } else {
            if (isDarkTheme) {
                dayText.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
            } else {
                dayText.setTextColor(getResources().getColor(android.R.color.primary_text_light));
            }
            dayText.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
        
        // Handle selected date
        if (date.equals(selectedDate)) {
            dayText.setBackgroundColor(getResources().getColor(R.color.selected_date_orange));
            dayText.setTextColor(getResources().getColor(android.R.color.white));
        }
        
        // Click listener
        dayView.setOnClickListener(v -> {
            selectDateAndLoadActivities(date);
            currentDay = date;
            if (currentViewMode == 2) {
                populateDayView();
                updateDayDisplay();
            }
        });
        
        return dayView;
    }
    
    private void populateDayView() {
        layoutDayView.removeAllViews();
        
        if (currentDay == null) {
            currentDay = LocalDate.now();
        }
        
        // Create header
        TextView headerText = new TextView(getContext());
        headerText.setText(dateFormatter.format(currentDay));
        headerText.setTextSize(24);
        headerText.setTypeface(null, android.graphics.Typeface.BOLD);
        headerText.setTextColor(getResources().getColor(R.color.on_surface));
        headerText.setPadding(0, 0, 0, 16);
        layoutDayView.addView(headerText);
        
        // Get activities for this day
        List<Activity> dayActivities = activitiesByDate.getOrDefault(currentDay, new ArrayList<>());
        
        if (dayActivities.isEmpty()) {
            TextView noActivitiesText = new TextView(getContext());
            noActivitiesText.setText("No activities recorded for this day");
            noActivitiesText.setTextSize(16);
            noActivitiesText.setTextColor(getResources().getColor(R.color.on_surface_variant));
            noActivitiesText.setPadding(0, 16, 0, 16);
            layoutDayView.addView(noActivitiesText);
            return;
        }
        
        // Group activities by category
        Map<Long, List<Activity>> activitiesByCategory = new HashMap<>();
        Map<Long, Category> categoryMap = new HashMap<>();
        
        for (Activity activity : dayActivities) {
            long categoryId = activity.getCategoryId();
            if (!activitiesByCategory.containsKey(categoryId)) {
                activitiesByCategory.put(categoryId, new ArrayList<>());
            }
            activitiesByCategory.get(categoryId).add(activity);
            
            // Find category
            if (!categoryMap.containsKey(categoryId)) {
                for (Category category : categories) {
                    if (category.getId() == categoryId) {
                        categoryMap.put(categoryId, category);
                        break;
                    }
                }
            }
        }
        
        // Create category cards
        for (Map.Entry<Long, List<Activity>> entry : activitiesByCategory.entrySet()) {
            long categoryId = entry.getKey();
            List<Activity> categoryActivities = entry.getValue();
            Category category = categoryMap.get(categoryId);
            
            if (category == null) continue;
            
            // Create category card
            com.google.android.material.card.MaterialCardView categoryCard = new com.google.android.material.card.MaterialCardView(getContext());
            float density = getResources().getDisplayMetrics().density;
            categoryCard.setRadius(12f * density);
            categoryCard.setCardElevation(2f * density);
            
            LinearLayout cardContent = new LinearLayout(getContext());
            cardContent.setOrientation(LinearLayout.VERTICAL);
            cardContent.setPadding((int)(16 * density), (int)(16 * density), (int)(16 * density), (int)(16 * density));
            
            // Category header
            LinearLayout categoryHeader = new LinearLayout(getContext());
            categoryHeader.setOrientation(LinearLayout.HORIZONTAL);
            categoryHeader.setGravity(android.view.Gravity.CENTER_VERTICAL);
            
            // Category color indicator
            View colorIndicator = new View(getContext());
            LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams((int)(24 * density), (int)(24 * density));
            colorParams.setMargins(0, 0, (int)(12 * density), 0);
            colorIndicator.setLayoutParams(colorParams);
            android.graphics.drawable.GradientDrawable colorDrawable = new android.graphics.drawable.GradientDrawable();
            colorDrawable.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            try {
                colorDrawable.setColor(Color.parseColor(category.getColor()));
            } catch (IllegalArgumentException e) {
                colorDrawable.setColor(Color.parseColor("#FF2E7D32"));
            }
            colorIndicator.setBackground(colorDrawable);
            categoryHeader.addView(colorIndicator);
            
            // Category name
            TextView categoryName = new TextView(getContext());
            categoryName.setText(category.getName());
            categoryName.setTextSize(18);
            categoryName.setTypeface(null, android.graphics.Typeface.BOLD);
            categoryName.setTextColor(getResources().getColor(R.color.on_surface));
            categoryHeader.addView(categoryName);
            
            cardContent.addView(categoryHeader);
            
            // Calculate total time
            double totalTime = 0.0;
            for (Activity activity : categoryActivities) {
                totalTime += activity.getTimeSpentHours();
            }
            
            // Total time text
            TextView totalTimeText = new TextView(getContext());
            totalTimeText.setText(String.format(Locale.getDefault(), "Total: %.1f hours", totalTime));
            totalTimeText.setTextSize(14);
            totalTimeText.setTextColor(getResources().getColor(R.color.on_surface_variant));
            totalTimeText.setPadding(0, (int)(8 * density), 0, (int)(12 * density));
            cardContent.addView(totalTimeText);
            
            // Activities list
            for (Activity activity : categoryActivities) {
                View activityItem = createActivityItemView(activity);
                cardContent.addView(activityItem);
            }
            
            categoryCard.addView(cardContent);
            
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, (int)(16 * density));
            categoryCard.setLayoutParams(cardParams);
            
            layoutDayView.addView(categoryCard);
        }
    }
    
    private View createActivityItemView(Activity activity) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(0, 0, 0, (int)(8 * getResources().getDisplayMetrics().density));
        
        // Activity notes/description
        if (activity.getNotes() != null && !activity.getNotes().isEmpty()) {
            TextView notesText = new TextView(getContext());
            notesText.setText(activity.getNotes());
            notesText.setTextSize(14);
            notesText.setTextColor(getResources().getColor(R.color.on_surface));
            itemLayout.addView(notesText);
        }
        
        // Time spent
        TextView timeText = new TextView(getContext());
        timeText.setText(String.format(Locale.getDefault(), "%.1f hours", activity.getTimeSpentHours()));
        timeText.setTextSize(12);
        timeText.setTextColor(getResources().getColor(R.color.on_surface_variant));
        itemLayout.addView(timeText);
        
        return itemLayout;
    }
    
    private void updateWeekDisplay() {
        if (currentWeekStart != null) {
            LocalDate weekEnd = currentWeekStart.plusDays(6);
            String weekText = currentWeekStart.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())) +
                             " - " + weekEnd.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()));
            textViewMonthYear.setText(weekText);
        }
    }
    
    private void updateDayDisplay() {
        if (currentDay != null) {
            textViewMonthYear.setText(dateFormatter.format(currentDay));
        }
    }

    // ViewContainer for each day cell
    static class DayViewContainer extends ViewContainer {
        TextView textView;
        View highlightView;
        LinearLayout categoryIndicators;
        CalendarDay day;

        public DayViewContainer(View view) {
            super(view);
            textView = view.findViewById(R.id.calendarDayText);
            highlightView = view.findViewById(R.id.calendarDayHighlightView);
            categoryIndicators = view.findViewById(R.id.layoutCategoryIndicators);
        }
    }
}
