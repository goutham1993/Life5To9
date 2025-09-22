package com.journal.life5to9.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
                // Handle long click - could show edit/delete options
                // TODO: Implement edit/delete category functionality
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
                recyclerViewCategories.setVisibility(View.VISIBLE);
            } else {
                textViewEmpty.setVisibility(View.VISIBLE);
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
}
