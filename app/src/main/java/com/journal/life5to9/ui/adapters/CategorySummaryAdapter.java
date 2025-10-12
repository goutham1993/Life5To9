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
        
        public CategorySummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCategoryColor = itemView.findViewById(R.id.viewCategoryColor);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            textViewTimeSpent = itemView.findViewById(R.id.textViewTimeSpent);
            progressBarCategory = itemView.findViewById(R.id.progressBarCategory);
            imageViewExpand = itemView.findViewById(R.id.imageViewExpand);
            layoutActivitiesContainer = itemView.findViewById(R.id.layoutActivitiesContainer);
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
                android.util.Log.d("CategorySummaryAdapter", "Category clicked: " + item.getCategoryName() + ", was expanded: " + item.isExpanded());
                item.setExpanded(!item.isExpanded());
                android.util.Log.d("CategorySummaryAdapter", "Now expanded: " + item.isExpanded());
                updateExpandedState(item);
            });
            
            updateExpandedState(item);
        }
        
        private void updateExpandedState(CategorySummaryItem item) {
            android.util.Log.d("CategorySummaryAdapter", "updateExpandedState: " + item.getCategoryName() + " expanded=" + item.isExpanded());
            if (item.isExpanded()) {
                layoutActivitiesContainer.setVisibility(View.VISIBLE);
                imageViewExpand.setRotation(180f);
                android.util.Log.d("CategorySummaryAdapter", "Showing activities for: " + item.getCategoryName());
                populateActivities(item.getActivities());
            } else {
                layoutActivitiesContainer.setVisibility(View.GONE);
                imageViewExpand.setRotation(0f);
                android.util.Log.d("CategorySummaryAdapter", "Hiding activities for: " + item.getCategoryName());
            }
        }
        
        private void populateActivities(List<Activity> activities) {
            layoutActivitiesContainer.removeAllViews();
            
            android.util.Log.d("CategorySummaryAdapter", "populateActivities called with " + (activities != null ? activities.size() : "null") + " activities");
            
            if (activities == null || activities.isEmpty()) {
                TextView noActivitiesText = new TextView(itemView.getContext());
                noActivitiesText.setText("No activities found");
                noActivitiesText.setTextAppearance(android.R.style.TextAppearance_Small);
                noActivitiesText.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
                noActivitiesText.setPadding(16, 8, 16, 8);
                layoutActivitiesContainer.addView(noActivitiesText);
                return;
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            
            for (Activity activity : activities) {
                LinearLayout activityItem = new LinearLayout(itemView.getContext());
                activityItem.setOrientation(LinearLayout.VERTICAL);
                activityItem.setPadding(16, 12, 16, 12);
                
                // Use theme-aware background color
                int backgroundColor = itemView.getContext().getColor(android.R.color.background_light);
                activityItem.setBackgroundColor(backgroundColor);
                
                // Add margin between items
                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                itemParams.setMargins(0, 0, 0, 8);
                activityItem.setLayoutParams(itemParams);
                
                // Notes
                TextView notesText = new TextView(itemView.getContext());
                notesText.setText(activity.getNotes());
                notesText.setTextAppearance(android.R.style.TextAppearance_Medium);
                notesText.setTextColor(itemView.getContext().getColor(android.R.color.primary_text_light));
                notesText.setMaxLines(2);
                notesText.setEllipsize(android.text.TextUtils.TruncateAt.END);
                activityItem.addView(notesText);
                
                // Time and Date row
                LinearLayout timeDateRow = new LinearLayout(itemView.getContext());
                timeDateRow.setOrientation(LinearLayout.HORIZONTAL);
                timeDateRow.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                timeDateRow.setPadding(0, 4, 0, 0);
                
                // Time
                TextView timeText = new TextView(itemView.getContext());
                timeText.setText(String.format(Locale.getDefault(), "%.1fh", activity.getTimeSpentHours()));
                timeText.setTextAppearance(android.R.style.TextAppearance_Small);
                timeText.setTextColor(itemView.getContext().getColor(android.R.color.holo_blue_dark));
                timeText.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                ));
                timeDateRow.addView(timeText);
                
                // Date
                TextView dateText = new TextView(itemView.getContext());
                dateText.setText(dateFormat.format(activity.getDate()));
                dateText.setTextAppearance(android.R.style.TextAppearance_Small);
                dateText.setTextColor(itemView.getContext().getColor(android.R.color.secondary_text_light));
                dateText.setGravity(android.view.Gravity.END);
                timeDateRow.addView(dateText);
                
                activityItem.addView(timeDateRow);
                layoutActivitiesContainer.addView(activityItem);
            }
        }
    }
}
