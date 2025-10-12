package com.journal.life5to9.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.utils.CategoryEmojiMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddActivityDialog extends DialogFragment {
    
    public interface OnActivityAddedListener {
        void onActivityAdded(long categoryId, String notes, double timeSpent, Date date);
    }
    
    private OnActivityAddedListener listener;
    private List<Category> categories = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;
    private ArrayAdapter<String> notesAdapter;
    
    private AutoCompleteTextView autoCompleteCategory;
    private AutoCompleteTextView autoCompleteNotes;
    private TextInputEditText editTextTimeSpent;
    private TextInputEditText editTextDate;
    private TextInputEditText editTextTime;
    private Button buttonCancel;
    private Button buttonSave;
    
    private Date selectedDate;
    private int selectedCategoryId = -1;
    private boolean isDatePreSelected = false;
    private int selectedHour = 17; // Default to 5 PM
    private int selectedMinute = 0;
    
    public static AddActivityDialog newInstance() {
        return new AddActivityDialog();
    }
    
    public void setCategories(List<Category> categories) {
        this.categories = categories;
        if (categoryAdapter != null) {
            updateCategoryAdapter();
        } else if (categories != null && !categories.isEmpty() && isAdded()) {
            // Setup dropdown if it hasn't been set up yet and fragment is attached
            setupCategoryDropdown();
        }
        
        // If dialog is already shown and we have categories, setup dropdown
        if (categories != null && !categories.isEmpty() && isAdded() && autoCompleteCategory != null) {
            setupCategoryDropdown();
        }
    }
    
    public void setOnActivityAddedListener(OnActivityAddedListener listener) {
        this.listener = listener;
    }
    
    public void setSelectedDate(Date date) {
        this.selectedDate = date;
        this.isDatePreSelected = (date != null);
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_activity, null);
        
        initializeViews(view);
        setupDateAndTime();
        setupClickListeners();
        setupAutoCompleteAdapters();
        
        // Setup category dropdown if categories are available
        if (categories != null && !categories.isEmpty()) {
            setupCategoryDropdown();
        }
        
        builder.setView(view);
        AlertDialog dialog = builder.create();
        
        // Set up dropdown after dialog is shown
        dialog.setOnShowListener(dialogInterface -> {
            if (categories != null && !categories.isEmpty()) {
                setupCategoryDropdown();
            }
        });
        
        return dialog;
    }
    
    private void initializeViews(View view) {
        autoCompleteCategory = view.findViewById(R.id.autoCompleteCategory);
        autoCompleteNotes = view.findViewById(R.id.autoCompleteNotes);
        editTextTimeSpent = view.findViewById(R.id.editTextTimeSpent);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextTime = view.findViewById(R.id.editTextTime);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonSave = view.findViewById(R.id.buttonSave);
    }
    
    private void setupDateAndTime() {
        // Use pre-selected date if available, otherwise use current date
        if (!isDatePreSelected || selectedDate == null) {
            Calendar calendar = Calendar.getInstance();
            selectedDate = calendar.getTime();
        }
        
        // Normalize the date to start of day for consistent database queries
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDate = cal.getTime();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        
        // Set the time on the selected date
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(selectedDate);
        timeCal.set(Calendar.HOUR_OF_DAY, selectedHour);
        timeCal.set(Calendar.MINUTE, selectedMinute);
        timeCal.set(Calendar.SECOND, 0);
        timeCal.set(Calendar.MILLISECOND, 0);
        Date dateWithTime = timeCal.getTime();
        
        // Ensure selectedDate is not null before formatting
        if (selectedDate != null) {
            editTextDate.setText(dateFormat.format(selectedDate));
            editTextTime.setText(timeFormat.format(dateWithTime));
        } else {
            // Fallback to current date if still null
            Date currentDate = new Date();
            editTextDate.setText(dateFormat.format(currentDate));
            editTextTime.setText(timeFormat.format(currentDate));
            selectedDate = currentDate;
        }
        
        // Set click listeners for date and time pickers
        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextTime.setOnClickListener(v -> showTimePicker());
    }
    
    private void setupCategoryDropdown() {
        // Check if fragment is still attached before accessing context
        if (!isAdded() || getContext() == null || categories == null || categories.isEmpty()) {
            return;
        }

        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            String emoji = CategoryEmojiMapper.getEmojiForCategory(category.getName());
            categoryNames.add(emoji + " " + category.getName());
        }

        categoryAdapter = new ArrayAdapter<>(getContext(),
            android.R.layout.simple_dropdown_item_1line, categoryNames);
        autoCompleteCategory.setAdapter(categoryAdapter);
        
        // Set threshold to 0 so dropdown shows immediately on click
        autoCompleteCategory.setThreshold(0);
        
        // Enable dropdown on focus
        autoCompleteCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                autoCompleteCategory.showDropDown();
            }
        });
        
        // Set click listener to show dropdown
        autoCompleteCategory.setOnClickListener(v -> {
            autoCompleteCategory.showDropDown();
        });
        
        autoCompleteCategory.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategoryText = (String) parent.getItemAtPosition(position);
            // Extract category name by removing emoji and space
            String selectedCategoryName = selectedCategoryText.substring(selectedCategoryText.indexOf(" ") + 1);
            for (Category category : categories) {
                if (category.getName().equals(selectedCategoryName)) {
                    selectedCategoryId = (int) category.getId();
                    // Load notes for the selected category
                    loadNotesForCategory(selectedCategoryId);
                    break;
                }
            }
        });
    }
    
    private void updateCategoryAdapter() {
        // Check if fragment is still attached before updating adapter
        if (!isAdded() || categoryAdapter == null) {
            return;
        }

        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            String emoji = CategoryEmojiMapper.getEmojiForCategory(category.getName());
            categoryNames.add(emoji + " " + category.getName());
        }

        categoryAdapter.clear();
        categoryAdapter.addAll(categoryNames);
        categoryAdapter.notifyDataSetChanged();
    }
    
    private void setupAutoCompleteAdapters() {
        // Adapter for Notes
        notesAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        autoCompleteNotes.setAdapter(notesAdapter);
        autoCompleteNotes.setThreshold(1); // Show suggestions after 1 character
    }
    
    public void setNotesSuggestions(List<String> notes) {
        if (notesAdapter != null && notes != null) {
            notesAdapter.clear();
            notesAdapter.addAll(notes);
            notesAdapter.notifyDataSetChanged();
        }
    }
    
    public void loadNotesForCategory(long categoryId) {
        if (getActivity() != null && getActivity() instanceof com.journal.life5to9.MainActivity) {
            com.journal.life5to9.MainActivity mainActivity = (com.journal.life5to9.MainActivity) getActivity();
            mainActivity.loadNotesForCategory(categoryId, this);
        }
    }
    
    private void setupClickListeners() {
        buttonCancel.setOnClickListener(v -> dismiss());
        
        buttonSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveActivity();
            }
        });
    }
    
    private boolean validateInput() {
        if (selectedCategoryId == -1) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        String timeSpentText = editTextTimeSpent.getText().toString().trim();
        if (timeSpentText.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter time spent", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        try {
            double timeSpent = Double.parseDouble(timeSpentText);
            if (timeSpent <= 0) {
                Toast.makeText(requireContext(), "Time spent must be greater than 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Please enter a valid number for time spent", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private void saveActivity() {
        String notes = autoCompleteNotes.getText().toString().trim();
        double timeSpent = Double.parseDouble(editTextTimeSpent.getText().toString().trim());
        
        // Create the final date with the selected time
        Calendar finalCal = Calendar.getInstance();
        finalCal.setTime(selectedDate);
        finalCal.set(Calendar.HOUR_OF_DAY, selectedHour);
        finalCal.set(Calendar.MINUTE, selectedMinute);
        finalCal.set(Calendar.SECOND, 0);
        finalCal.set(Calendar.MILLISECOND, 0);
        Date finalDate = finalCal.getTime();
        
        android.util.Log.d("AddActivityDialog", "Saving activity with date: " + finalDate + " (time: " + selectedHour + ":" + selectedMinute + ")");
        android.util.Log.d("AddActivityDialog", "Final date timestamp: " + finalDate.getTime());
        
        if (listener != null) {
            listener.onActivityAdded(selectedCategoryId, notes, timeSpent, finalDate);
        }
        
        dismiss();
    }
    
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);
                    // Normalize to start of day
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    selectedCalendar.set(Calendar.MINUTE, 0);
                    selectedCalendar.set(Calendar.SECOND, 0);
                    selectedCalendar.set(Calendar.MILLISECOND, 0);
                    selectedDate = selectedCalendar.getTime();
                    updateDateDisplay();
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }
    
    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        editTextDate.setText(dateFormat.format(selectedDate));
        // Also update the time display to reflect the new date
        updateTimeDisplay();
    }
    
    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            requireContext(),
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    selectedHour = hourOfDay;
                    selectedMinute = minute;
                    updateTimeDisplay();
                }
            },
            selectedHour,
            selectedMinute,
            false // 24-hour format
        );
        
        timePickerDialog.show();
    }
    
    private void updateTimeDisplay() {
        // Create a date with the selected time
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(selectedDate);
        timeCal.set(Calendar.HOUR_OF_DAY, selectedHour);
        timeCal.set(Calendar.MINUTE, selectedMinute);
        timeCal.set(Calendar.SECOND, 0);
        timeCal.set(Calendar.MILLISECOND, 0);
        Date dateWithTime = timeCal.getTime();
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        editTextTime.setText(timeFormat.format(dateWithTime));
    }
}
