package com.journal.life5to9.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.ui.adapters.CategoryAdapter;
import com.journal.life5to9.ui.dialogs.AddCategoryDialog;
import com.journal.life5to9.viewmodel.MainViewModel;

import java.util.List;

public class CategoriesFragment extends Fragment {
    
    private MainViewModel viewModel;
    private RecyclerView recyclerViewCategories;
    private TextView textViewEmpty;
    private View emptyStateLayout;
    private CategoryAdapter adapter;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        emptyStateLayout = view.findViewById(R.id.emptyState);
        
        // Set empty state text
        if (emptyStateLayout != null) {
            TextView emptyTitle = emptyStateLayout.findViewById(R.id.textViewEmptyTitle);
            TextView emptyBody = emptyStateLayout.findViewById(R.id.textViewEmptyBody);
            if (emptyTitle != null) emptyTitle.setText("No categories");
            if (emptyBody != null) emptyBody.setText("Tap + to create your first category");
        }
        
        setupRecyclerView();
        observeData();
        
        return view;
    }
    
    private void setupRecyclerView() {
        adapter = new CategoryAdapter();
        adapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                // Handle category click - could show activities for this category
                // TODO: Implement filter by category functionality
            }
            
            @Override
            public void onCategoryLongClick(Category category) {
                // Show delete confirmation dialog
                showDeleteCategoryDialog(category);
            }
        });
        
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCategories.setAdapter(adapter);
    }
    
    private void observeData() {
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                adapter.setCategories(categories);
                textViewEmpty.setVisibility(View.GONE);
                if (emptyStateLayout != null) emptyStateLayout.setVisibility(View.GONE);
                recyclerViewCategories.setVisibility(View.VISIBLE);
            } else {
                textViewEmpty.setVisibility(View.GONE);
                if (emptyStateLayout != null) emptyStateLayout.setVisibility(View.VISIBLE);
                recyclerViewCategories.setVisibility(View.GONE);
            }
        });
    }
    
    // Method to be called by MainActivity when FAB is clicked
    public void showAddCategoryDialog() {
        AddCategoryDialog dialog = AddCategoryDialog.newInstance();
        
        dialog.setOnCategoryAddedListener((name, description, color, icon) -> {
            viewModel.addCategory(name, color, icon);
        });
        
        dialog.show(getParentFragmentManager(), "AddCategoryDialog");
    }
    
    private void showDeleteCategoryDialog(Category category) {
        // Don't allow deletion of default categories
        if (category.isDefault()) {
            Toast.makeText(getContext(), "Default categories cannot be deleted", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Category")
                .setMessage("Are you sure you want to delete the category \"" + category.getName() + "\"?\n\n" +
                        "⚠️ WARNING: All activities associated with this category will lose their category reference and may not be visible in category-based views.\n\n" +
                        "This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete the category
                    viewModel.deleteCategory(category);
                    Toast.makeText(getContext(), "Category deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Do nothing, just dismiss the dialog
                })
                .show();
    }
}
