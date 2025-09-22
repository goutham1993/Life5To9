package com.journal.life5to9.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.utils.CategoryEmojiMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditActivityDialog extends DialogFragment {

    public interface OnActivityUpdatedListener {
        void onActivityUpdated(Activity activity);
    }

    private OnActivityUpdatedListener listener;
    private List<Category> categories;
    private Activity activityToEdit;

    private AutoCompleteTextView autoCompleteCategory;
    private TextInputEditText editTextNotes;
    private TextInputEditText editTextTimeSpent;
    private TextInputEditText editTextDate;
    private TextInputEditText editTextTime;
    private Button buttonCancel;
    private Button buttonUpdate;

    private Date selectedDate;
    private int selectedCategoryId = -1;
    private int selectedHour = 17; // Default to 5 PM
    private int selectedMinute = 0;
    private ArrayAdapter<String> categoryAdapter;

    public static EditActivityDialog newInstance(Activity activity) {
        EditActivityDialog dialog = new EditActivityDialog();
        dialog.activityToEdit = activity;
        return dialog;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        if (categoryAdapter != null) {
            updateCategoryAdapter();
        } else if (categories != null && !categories.isEmpty() && isAdded()) {
            setupCategoryDropdown();
        }
    }

    public void setOnActivityUpdatedListener(OnActivityUpdatedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_activity, null);

        initializeViews(view);
        populateFields();
        setupDateAndTime();
        setupClickListeners();

        // Setup category dropdown if categories are available
        if (categories != null && !categories.isEmpty()) {
            setupCategoryDropdown();
        }

        builder.setView(view);
        androidx.appcompat.app.AlertDialog dialog = builder.create();

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
        editTextNotes = view.findViewById(R.id.editTextNotes);
        editTextTimeSpent = view.findViewById(R.id.editTextTimeSpent);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextTime = view.findViewById(R.id.editTextTime);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
    }

    private void populateFields() {
        if (activityToEdit != null) {
            // Set notes
            editTextNotes.setText(activityToEdit.getNotes());
            
            // Set time spent
            editTextTimeSpent.setText(String.format(Locale.getDefault(), "%.1f", activityToEdit.getTimeSpentHours()));
            
            // Set category ID
            selectedCategoryId = (int) activityToEdit.getCategoryId();
            
            // Set date and time
            if (activityToEdit.getDate() != null) {
                selectedDate = activityToEdit.getDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(selectedDate);
                selectedHour = cal.get(Calendar.HOUR_OF_DAY);
                selectedMinute = cal.get(Calendar.MINUTE);
            }
        }
    }

    private void setupDateAndTime() {
        if (selectedDate == null) {
            Calendar calendar = Calendar.getInstance();
            selectedDate = calendar.getTime();
        }

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

        editTextDate.setText(dateFormat.format(selectedDate));
        editTextTime.setText(timeFormat.format(dateWithTime));

        // Set click listeners for date and time pickers
        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextTime.setOnClickListener(v -> showTimePicker());
    }

    private void setupCategoryDropdown() {
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
                    break;
                }
            }
        });

        // Set the current category as selected
        if (selectedCategoryId != -1) {
            for (Category category : categories) {
                if (category.getId() == selectedCategoryId) {
                    String emoji = CategoryEmojiMapper.getEmojiForCategory(category.getName());
                    autoCompleteCategory.setText(emoji + " " + category.getName(), false);
                    break;
                }
            }
        }
    }

    private void updateCategoryAdapter() {
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

    private void setupClickListeners() {
        buttonCancel.setOnClickListener(v -> dismiss());

        buttonUpdate.setOnClickListener(v -> {
            if (validateInput()) {
                updateActivity();
            }
        });
    }

    private boolean validateInput() {
        String notes = editTextNotes.getText().toString().trim();
        String timeSpentText = editTextTimeSpent.getText().toString().trim();

        if (notes.isEmpty()) {
            editTextNotes.setError("Notes cannot be empty");
            editTextNotes.requestFocus();
            return false;
        }

        if (timeSpentText.isEmpty()) {
            editTextTimeSpent.setError("Time spent cannot be empty");
            editTextTimeSpent.requestFocus();
            return false;
        }

        try {
            double timeSpent = Double.parseDouble(timeSpentText);
            if (timeSpent <= 0) {
                editTextTimeSpent.setError("Time spent must be greater than 0");
                editTextTimeSpent.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            editTextTimeSpent.setError("Please enter a valid number");
            editTextTimeSpent.requestFocus();
            return false;
        }

        if (selectedCategoryId == -1) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateActivity() {
        String notes = editTextNotes.getText().toString().trim();
        double timeSpent = Double.parseDouble(editTextTimeSpent.getText().toString().trim());

        // Create the final date with the selected time
        Calendar finalCal = Calendar.getInstance();
        finalCal.setTime(selectedDate);
        finalCal.set(Calendar.HOUR_OF_DAY, selectedHour);
        finalCal.set(Calendar.MINUTE, selectedMinute);
        finalCal.set(Calendar.SECOND, 0);
        finalCal.set(Calendar.MILLISECOND, 0);
        Date finalDate = finalCal.getTime();

        // Update the activity object
        activityToEdit.setCategoryId(selectedCategoryId);
        activityToEdit.setNotes(notes);
        activityToEdit.setTimeSpentHours(timeSpent);
        activityToEdit.setDate(finalDate);
        activityToEdit.setUpdatedAt(new Date());

        if (listener != null) {
            listener.onActivityUpdated(activityToEdit);
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
