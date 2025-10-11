package com.journal.life5to9.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        private List<Activity> activities;
        private boolean isExpanded;
        
        public CategorySummaryItem(String categoryName, String categoryColor, double timeSpent, double totalTime, List<Activity> activities) {
            this.categoryName = categoryName;
            this.categoryColor = categoryColor;
            this.timeSpent = timeSpent;
            this.totalTime = totalTime;
            this.percentage = totalTime > 0 ? (int) ((timeSpent / totalTime) * 100) : 0;
            this.activities = activities != null ? activities : new ArrayList<>();
            this.isExpanded = false;
        }
        
        // Getters
        public String getCategoryName() { return categoryName; }
        public String getCategoryColor() { return categoryColor; }
        public double getTimeSpent() { return timeSpent; }
        public double getTotalTime() { return totalTime; }
        public int getPercentage() { return percentage; }
        public List<Activity> getActivities() { return activities; }
        public boolean isExpanded() { return isExpanded; }
        
        // Setters
        public void setExpanded(boolean expanded) { this.isExpanded = expanded; }
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
        private ImageView imageViewExpand;
        private LinearLayout layoutActivitiesContainer;
        private RecyclerView recyclerViewActivities;
        private ActivityDetailAdapter activityDetailAdapter;
        
        public CategorySummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCategoryColor = itemView.findViewById(R.id.viewCategoryColor);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            textViewTimeSpent = itemView.findViewById(R.id.textViewTimeSpent);
            progressBarCategory = itemView.findViewById(R.id.progressBarCategory);
            imageViewExpand = itemView.findViewById(R.id.imageViewExpand);
            layoutActivitiesContainer = itemView.findViewById(R.id.layoutActivitiesContainer);
            recyclerViewActivities = itemView.findViewById(R.id.recyclerViewActivities);
            
            // Setup activities recycler view
            activityDetailAdapter = new ActivityDetailAdapter();
            recyclerViewActivities.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerViewActivities.setAdapter(activityDetailAdapter);
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
            
            // Setup expand/collapse functionality
            itemView.setOnClickListener(v -> {
                item.setExpanded(!item.isExpanded());
                updateExpandedState(item);
            });
            
            updateExpandedState(item);
        }
        
        private void updateExpandedState(CategorySummaryItem item) {
            if (item.isExpanded()) {
                layoutActivitiesContainer.setVisibility(View.VISIBLE);
                imageViewExpand.setRotation(180f);
                activityDetailAdapter.setActivities(item.getActivities());
            } else {
                layoutActivitiesContainer.setVisibility(View.GONE);
                imageViewExpand.setRotation(0f);
            }
        }
    }
    
    // Inner class for activity detail adapter
    private static class ActivityDetailAdapter extends RecyclerView.Adapter<ActivityDetailAdapter.ActivityDetailViewHolder> {
        private List<Activity> activities = new ArrayList<>();
        private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        
        public void setActivities(List<Activity> activities) {
            this.activities = activities != null ? activities : new ArrayList<>();
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public ActivityDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_activity_detail, parent, false);
            return new ActivityDetailViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ActivityDetailViewHolder holder, int position) {
            Activity activity = activities.get(position);
            holder.bind(activity);
        }
        
        @Override
        public int getItemCount() {
            return activities.size();
        }
        
        class ActivityDetailViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewActivityNotes;
            private TextView textViewActivityTime;
            private TextView textViewActivityDate;
            
            public ActivityDetailViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewActivityNotes = itemView.findViewById(R.id.textViewActivityNotes);
                textViewActivityTime = itemView.findViewById(R.id.textViewActivityTime);
                textViewActivityDate = itemView.findViewById(R.id.textViewActivityDate);
            }
            
            public void bind(Activity activity) {
                textViewActivityNotes.setText(activity.getNotes() != null ? activity.getNotes() : "No notes");
                textViewActivityTime.setText(String.format(Locale.getDefault(), "%.1fh", activity.getTimeSpentHours()));
                textViewActivityDate.setText(dateFormat.format(activity.getDate()));
            }
        }
    }
}
