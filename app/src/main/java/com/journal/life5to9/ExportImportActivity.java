package com.journal.life5to9;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.journal.life5to9.data.database.AppDatabase;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.utils.ExportImportHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExportImportActivity extends AppCompatActivity {

    private MaterialRadioButton radioJson, radioCsv;
    private TextInputEditText editTextExportPath;
    private MaterialButton buttonSelectPath, buttonExport, buttonImport;
    private MaterialCardView cardExport, cardImport;
    
    private String selectedFormat = "json";
    private String exportPath = "";
    private ExportImportHelper exportImportHelper;
    
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int MANAGE_STORAGE_PERMISSION_CODE = 101;
    
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ActivityResultLauncher<Intent> directoryPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_import);
        
        initializeViews();
        setupClickListeners();
        setupFilePickers();
        checkPermissions();
        
        exportImportHelper = new ExportImportHelper(this);
        setDefaultExportPath();
    }
    
    private void initializeViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Export/Import Data");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        radioJson = findViewById(R.id.radioJson);
        radioCsv = findViewById(R.id.radioCsv);
        editTextExportPath = findViewById(R.id.editTextExportPath);
        buttonSelectPath = findViewById(R.id.buttonSelectPath);
        buttonExport = findViewById(R.id.buttonExport);
        buttonImport = findViewById(R.id.buttonImport);
        cardExport = findViewById(R.id.cardExport);
        cardImport = findViewById(R.id.cardImport);
        
        // Set default format
        radioJson.setChecked(true);
    }
    
    private void setupClickListeners() {
        radioJson.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedFormat = "json";
                updateExportPath();
            }
        });
        
        radioCsv.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedFormat = "csv";
                updateExportPath();
            }
        });
        
        buttonSelectPath.setOnClickListener(v -> selectExportPath());
        
        buttonExport.setOnClickListener(v -> exportData());
        
        buttonImport.setOnClickListener(v -> importData());
    }
    
    private void setupFilePickers() {
        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            editTextExportPath.setText(uri.getPath());
                            exportPath = uri.getPath();
                        }
                    }
                }
            }
        );
        
        directoryPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            String path = uri.getPath() + "/Life5To9_Export." + selectedFormat;
                            editTextExportPath.setText(path);
                            exportPath = path;
                        }
                    }
                }
            }
        );
    }
    
    private void setDefaultExportPath() {
        updateExportPath();
    }
    
    private void updateExportPath() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        
        // Create timestamp for unique filename
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = timestampFormat.format(new Date());
        
        String defaultPath = downloadsDir.getAbsolutePath() + "/Life5To9_Export_" + timestamp + "." + selectedFormat;
        editTextExportPath.setText(defaultPath);
        exportPath = defaultPath;
    }
    
    private void selectExportPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                openDirectoryPicker();
            } else {
                requestManageStoragePermission();
            }
        } else {
            if (checkStoragePermission()) {
                openDirectoryPicker();
            } else {
                requestStoragePermission();
            }
        }
    }
    
    private void openDirectoryPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        directoryPickerLauncher.launch(intent);
    }
    
    private void exportData() {
        if (exportPath.isEmpty()) {
            Toast.makeText(this, "Please select an export path", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check permissions before export
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(this, "Storage access required. Please grant permission first.", Toast.LENGTH_LONG).show();
                requestManageStoragePermission();
                return;
            }
        } else {
            if (!checkStoragePermission()) {
                Toast.makeText(this, "Storage permission required. Please grant permission first.", Toast.LENGTH_LONG).show();
                requestStoragePermission();
                return;
            }
        }
        
        new Thread(() -> {
            try {
                boolean success;
                if (selectedFormat.equals("json")) {
                    success = exportImportHelper.exportToJson(exportPath);
                } else if (selectedFormat.equals("csv")) {
                    success = exportImportHelper.exportToCsv(exportPath);
                } else {
                    success = false;
                }
 
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "Data exported successfully to: " + exportPath, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Export failed. Please check file path and permissions.", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
    
    private void importData() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/json", "text/csv", "text/plain"});
        filePickerLauncher.launch(intent);
    }
    
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                requestManageStoragePermission();
            }
        } else {
            if (!checkStoragePermission()) {
                requestStoragePermission();
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied. Export may not work.", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MANAGE_STORAGE_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Storage access granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Storage access denied. Export may not work.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        }, STORAGE_PERMISSION_CODE);
    }
    
    private void requestManageStoragePermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, MANAGE_STORAGE_PERMISSION_CODE);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
