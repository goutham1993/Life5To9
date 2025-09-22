package com.journal.life5to9.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.journal.life5to9.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategorySummaryAdapter extends RecyclerView.Adapter<CategorySummaryAdapter.CategorySummaryViewHolder> {
    
    private List<CategorySummaryItem> summaryItems = new ArrayList<>();
    
    public static class CategorySummaryItem {
        private String categoryName;
        private String categoryColor;
        private double timeSpent;
        private double totalTime;
        private int percentage;
        
        public CategorySummaryItem(String categoryName, String categoryColor, double timeSpent, double totalTime) {
            this.categoryName = categoryName;
            this.categoryColor = categoryColor;
            this.timeSpent = timeSpent;
            this.totalTime = totalTime;
            this.percentage = totalTime > 0 ? (int) ((timeSpent / totalTime) * 100) : 0;
        }
        
        // Getters
        public String getCategoryName() { return categoryName; }
        public String getCategoryColor() { return categoryColor; }
        public double getTimeSpent() { return timeSpent; }
        public double getTotalTime() { return totalTime; }
        public int getPercentage() { return percentage; }
    }
    
    public void setSummaryItems(List<CategorySummaryItem> summaryItems) {
        this.summaryItems = summaryItems;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public CategorySummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_summary, parent, false);
        return new CategorySummaryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategorySummaryViewHolder holder, int position) {
        CategorySummaryItem item = summaryItems.get(position);
        holder.bind(item);
    }
    
    @Override
    public int getItemCount() {
        return summaryItems.size();
    }
    
    class CategorySummaryViewHolder extends RecyclerView.ViewHolder {
        private View viewCategoryColor;
        private TextView textViewCategoryName;
        private TextView textViewTimeSpent;
        private ProgressBar progressBarCategory;
        
        public CategorySummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCategoryColor = itemView.findViewById(R.id.viewCategoryColor);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            textViewTimeSpent = itemView.findViewById(R.id.textViewTimeSpent);
            progressBarCategory = itemView.findViewById(R.id.progressBarCategory);
        }
        
        public void bind(CategorySummaryItem item) {
            textViewCategoryName.setText(item.getCategoryName());
            textViewTimeSpent.setText(String.format(Locale.getDefault(), "%.1fh", item.getTimeSpent()));
            
            // Set category color
            try {
                int color = Color.parseColor(item.getCategoryColor());
                viewCategoryColor.setBackgroundColor(color);
                progressBarCategory.setProgressTintList(android.content.res.ColorStateList.valueOf(color));
            } catch (IllegalArgumentException e) {
                // Use default color if parsing fails
                viewCategoryColor.setBackgroundColor(Color.parseColor("#FF2E7D32"));
                progressBarCategory.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF2E7D32")));
            }
            
            // Set progress
            progressBarCategory.setProgress(item.getPercentage());
        }
    }
}
