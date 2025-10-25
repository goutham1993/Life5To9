package com.journal.life5to9.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Category;

import java.util.Arrays;
import java.util.List;

public class AddCategoryDialog extends DialogFragment {
    
    public interface OnCategoryAddedListener {
        void onCategoryAdded(String name, String description, String color, String icon);
    }
    
    public interface OnDismissListener {
        void onDismiss();
    }
    
    private OnCategoryAddedListener listener;
    private OnDismissListener dismissListener;
    private List<Category> existingCategories;
    
    private TextInputEditText editTextCategoryName;
    private TextInputEditText editTextCategoryDescription;
    private TextInputEditText editTextCategoryColor;
    private TextInputEditText editTextCategoryIcon;
    private Button buttonCancel;
    private Button buttonSave;
    
    // Default colors for categories
    private final List<String> defaultColors = Arrays.asList(
        "#FF5722", "#E91E63", "#9C27B0", "#3F51B5", "#FF9800", 
        "#4CAF50", "#607D8B", "#795548", "#FFC107", "#00BCD4"
    );
    
    // Default icons for categories
    private final List<String> defaultIcons = Arrays.asList(
        "home", "work", "school", "fitness_center", "family_restroom",
        "movie", "music_note", "restaurant", "shopping_cart", "flight"
    );
    
    public static AddCategoryDialog newInstance() {
        return new AddCategoryDialog();
    }
    
    public void setOnCategoryAddedListener(OnCategoryAddedListener listener) {
        this.listener = listener;
    }
    
    public void setOnDismissListener(OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }
    
    public void setExistingCategories(List<Category> existingCategories) {
        this.existingCategories = existingCategories;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_category, null);
        
        initializeViews(view);
        setupClickListeners();
        setupDefaultValues();
        
        builder.setView(view);
        return builder.create();
    }
    
    private void initializeViews(View view) {
        editTextCategoryName = view.findViewById(R.id.editTextCategoryName);
        editTextCategoryDescription = view.findViewById(R.id.editTextCategoryDescription);
        editTextCategoryColor = view.findViewById(R.id.editTextCategoryColor);
        editTextCategoryIcon = view.findViewById(R.id.editTextCategoryIcon);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonSave = view.findViewById(R.id.buttonSave);
    }
    
    private void setupClickListeners() {
        buttonCancel.setOnClickListener(v -> dismiss());
        
        buttonSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveCategory();
            }
        });
    }
    
    private void setupDefaultValues() {
        // Set default color (first from the list)
        editTextCategoryColor.setText(defaultColors.get(0));
        
        // Set default icon (first from the list)
        editTextCategoryIcon.setText(defaultIcons.get(0));
    }
    
    private boolean validateInput() {
        String name = editTextCategoryName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a category name", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (name.length() < 2) {
            Toast.makeText(requireContext(), "Category name must be at least 2 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Check for duplicate category names (case-insensitive)
        if (existingCategories != null) {
            for (Category category : existingCategories) {
                if (category.getName().equalsIgnoreCase(name)) {
                    Toast.makeText(requireContext(), "A category with this name already exists", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private void saveCategory() {
        String name = editTextCategoryName.getText().toString().trim();
        String description = editTextCategoryDescription.getText().toString().trim();
        String color = editTextCategoryColor.getText().toString().trim();
        String icon = editTextCategoryIcon.getText().toString().trim();
        
        // Use defaults if empty
        if (color.isEmpty()) {
            color = defaultColors.get(0);
        }
        if (icon.isEmpty()) {
            icon = defaultIcons.get(0);
        }
        
        if (listener != null) {
            listener.onCategoryAdded(name, description, color, icon);
        }
        
        dismiss();
    }
    
    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
    }
}
