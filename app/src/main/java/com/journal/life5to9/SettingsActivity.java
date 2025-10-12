package com.journal.life5to9;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.journal.life5to9.utils.NotificationScheduler;

import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    
    private static final String PREFS_NAME = "life5to9_settings";
    private static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
    private static final String KEY_NOTIFICATION_HOUR = "notification_hour";
    private static final String KEY_NOTIFICATION_MINUTE = "notification_minute";
    
    private Switch switchNotifications;
    private MaterialButton buttonTimePicker;
    private TextView textViewTime;
    private MaterialButton buttonExport, buttonImport;
    
    private SharedPreferences preferences;
    private int selectedHour = 21; // Default 9 PM
    private int selectedMinute = 0;
    private boolean notificationsEnabled = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        initializeViews();
        loadSettings();
        setupClickListeners();
    }
    
    private void initializeViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        switchNotifications = findViewById(R.id.switchNotifications);
        buttonTimePicker = findViewById(R.id.buttonTimePicker);
        textViewTime = findViewById(R.id.textViewTime);
        buttonExport = findViewById(R.id.buttonExport);
        buttonImport = findViewById(R.id.buttonImport);
    }
    
    private void loadSettings() {
        notificationsEnabled = preferences.getBoolean(KEY_NOTIFICATION_ENABLED, true);
        selectedHour = preferences.getInt(KEY_NOTIFICATION_HOUR, 21);
        selectedMinute = preferences.getInt(KEY_NOTIFICATION_MINUTE, 0);
        
        switchNotifications.setChecked(notificationsEnabled);
        updateTimeDisplay();
        
        // Enable/disable time picker based on notification switch
        buttonTimePicker.setEnabled(notificationsEnabled);
    }
    
    private void setupClickListeners() {
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            notificationsEnabled = isChecked;
            buttonTimePicker.setEnabled(isChecked);
            
            if (isChecked) {
                // Schedule notification with current time
                NotificationScheduler.scheduleDailyReminder(this, selectedHour, selectedMinute);
                Toast.makeText(this, "Daily reminders enabled", Toast.LENGTH_SHORT).show();
            } else {
                // Cancel notifications
                NotificationScheduler.cancelDailyReminder(this);
                Toast.makeText(this, "Daily reminders disabled", Toast.LENGTH_SHORT).show();
            }
            
            saveSettings();
        });
        
        buttonTimePicker.setOnClickListener(v -> showTimePicker());
        
        buttonExport.setOnClickListener(v -> {
            Intent exportIntent = new Intent(this, ExportImportActivity.class);
            startActivity(exportIntent);
        });
        
        buttonImport.setOnClickListener(v -> {
            Intent importIntent = new Intent(this, ExportImportActivity.class);
            startActivity(importIntent);
        });
    }
    
    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                updateTimeDisplay();
                
                if (notificationsEnabled) {
                    // Reschedule with new time
                    NotificationScheduler.scheduleDailyReminder(this, selectedHour, selectedMinute);
                    Toast.makeText(this, "Reminder time updated", Toast.LENGTH_SHORT).show();
                }
                
                saveSettings();
            },
            selectedHour,
            selectedMinute,
            false // 24-hour format
        );
        
        timePickerDialog.setTitle("Select Reminder Time");
        timePickerDialog.show();
    }
    
    private void updateTimeDisplay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        
        String timeText = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
        textViewTime.setText(timeText);
    }
    
    private void saveSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_NOTIFICATION_ENABLED, notificationsEnabled);
        editor.putInt(KEY_NOTIFICATION_HOUR, selectedHour);
        editor.putInt(KEY_NOTIFICATION_MINUTE, selectedMinute);
        editor.apply();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
