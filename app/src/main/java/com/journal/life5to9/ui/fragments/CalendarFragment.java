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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private TextView textViewMonthYear;
    private ImageButton buttonPreviousMonth, buttonNextMonth;
    private LinearLayout layoutDayTitles;
    private LinearLayout layoutSelectedDateDetails;
    private TextView textViewSelectedDateTitle;
    private TextView textViewNoActivitiesForDate;
    private RecyclerView recyclerViewSelectedDateActivities;
    
    private MainViewModel viewModel;
    private ActivityAdapter activityAdapter;
    
    private YearMonth currentCalendarMonth;
    private LocalDate selectedDate = null;
    private Set<LocalDate> activityDates = new HashSet<>();
    
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault());

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
        layoutDayTitles = view.findViewById(R.id.layoutDayTitles);
        layoutSelectedDateDetails = view.findViewById(R.id.layoutSelectedDateDetails);
        textViewSelectedDateTitle = view.findViewById(R.id.textViewSelectedDateTitle);
        textViewNoActivitiesForDate = view.findViewById(R.id.textViewNoActivitiesForDate);
        recyclerViewSelectedDateActivities = view.findViewById(R.id.recyclerViewSelectedDateActivities);
        
        // Setup RecyclerView
        recyclerViewSelectedDateActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        activityAdapter = new ActivityAdapter();
        recyclerViewSelectedDateActivities.setAdapter(activityAdapter);
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
                
                if (day.getPosition() == DayPosition.MonthDate) {
                    highlightView.setVisibility(activityDates.contains(day.getDate()) ? View.VISIBLE : View.INVISIBLE);
                    
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
            currentCalendarMonth = currentCalendarMonth.minusMonths(1);
            calendarView.scrollToMonth(currentCalendarMonth);
            updateMonthYearDisplay();
            loadActivitiesForMonth();
        });
        
        buttonNextMonth.setOnClickListener(v -> {
            currentCalendarMonth = currentCalendarMonth.plusMonths(1);
            calendarView.scrollToMonth(currentCalendarMonth);
            updateMonthYearDisplay();
            loadActivitiesForMonth();
        });
    }

    private void updateMonthYearDisplay() {
        textViewMonthYear.setText(monthYearFormatter.format(currentCalendarMonth));
    }

    private void observeViewModel() {
        // Observe categories first
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                activityAdapter.setCategories(categories);
            }
        });
        
        // Observe activities for the current month
        viewModel.getAllActivities().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null) {
                updateActivityDates(activities);
                calendarView.notifyMonthChanged(currentCalendarMonth);
            }
        });
        
        // Observe activities for selected date
        if (selectedDate != null) {
            loadActivitiesForSelectedDate();
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

    // ViewContainer for each day cell
    static class DayViewContainer extends ViewContainer {
        TextView textView;
        View highlightView;
        CalendarDay day;

        public DayViewContainer(View view) {
            super(view);
            textView = view.findViewById(R.id.calendarDayText);
            highlightView = view.findViewById(R.id.calendarDayHighlightView);
        }
    }
}
