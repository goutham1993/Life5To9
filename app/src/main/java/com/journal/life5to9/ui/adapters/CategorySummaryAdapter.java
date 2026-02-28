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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
        
        // Comparison fields
        private double previousTimeSpent;
        private String trendIcon;
        private String statusMessage;
        private int progressPercentage;
        private int trendColor;
        
        public CategorySummaryItem(String categoryName, String categoryColor, double timeSpent, double totalTime, List<Activity> activities) {
            this.categoryName = categoryName;
            this.categoryColor = categoryColor;
            this.timeSpent = timeSpent;
            this.totalTime = totalTime;
            this.percentage = totalTime > 0 ? (int) ((timeSpent / totalTime) * 100) : 0;
            this.activities = activities != null ? activities : new ArrayList<>();
            this.isExpanded = false;
            
            // Initialize comparison fields with defaults
            this.previousTimeSpent = 0.0;
            this.trendIcon = "➡️";
            this.statusMessage = "New category";
            this.progressPercentage = 0;
            this.trendColor = android.R.color.holo_blue_dark;
        }
        
        public CategorySummaryItem(String categoryName, String categoryColor, double timeSpent, double totalTime, List<Activity> activities, double previousTimeSpent) {
            this.categoryName = categoryName;
            this.categoryColor = categoryColor;
            this.timeSpent = timeSpent;
            this.totalTime = totalTime;
            this.percentage = totalTime > 0 ? (int) ((timeSpent / totalTime) * 100) : 0;
            this.activities = activities != null ? activities : new ArrayList<>();
            this.isExpanded = false;
            this.previousTimeSpent = previousTimeSpent;
            
            // Calculate comparison data
            calculateComparisonData();
        }
        
        private void calculateComparisonData() {
            if (previousTimeSpent == 0) {
                // New category
                trendIcon = "🆕";
                statusMessage = "New category";
                progressPercentage = 100;
                trendColor = android.R.color.holo_orange_dark;
            } else {
                double difference = timeSpent - previousTimeSpent;
                double percentageChange = (difference / previousTimeSpent) * 100;
                
                if (percentageChange > 20) {
                    trendIcon = "🔥";
                    statusMessage = "Hot streak!";
                    trendColor = android.R.color.holo_green_dark;
                } else if (percentageChange > 5) {
                    trendIcon = "📈";
                    statusMessage = "Trending up";
                    trendColor = android.R.color.holo_green_light;
                } else if (percentageChange < -20) {
                    trendIcon = "📉";
                    statusMessage = "Needs attention";
                    trendColor = android.R.color.holo_red_dark;
                } else if (percentageChange < -5) {
                    trendIcon = "⚠️";
                    statusMessage = "Less active";
                    trendColor = android.R.color.holo_orange_dark;
                } else {
                    trendIcon = "🎯";
                    statusMessage = "Consistent";
                    trendColor = android.R.color.holo_blue_dark;
                }
                
                progressPercentage = previousTimeSpent > 0 ? (int) ((timeSpent / previousTimeSpent) * 100) : 100;
            }
        }
        
        // Getters
        public String getCategoryName() { return categoryName; }
        public String getCategoryColor() { return categoryColor; }
        public double getTimeSpent() { return timeSpent; }
        public double getTotalTime() { return totalTime; }
        public int getPercentage() { return percentage; }
        public List<Activity> getActivities() { return activities; }
        public boolean isExpanded() { return isExpanded; }
        
        // Calculate unique days count
        public int getDaysCount() {
            if (activities == null || activities.isEmpty()) {
                return 0;
            }
            Set<String> uniqueDays = new HashSet<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            for (Activity activity : activities) {
                if (activity.getDate() != null) {
                    uniqueDays.add(dateFormat.format(activity.getDate()));
                }
            }
            return uniqueDays.size();
        }
        
        // Comparison getters
        public double getPreviousTimeSpent() { return previousTimeSpent; }
        public String getTrendIcon() { return trendIcon; }
        public String getStatusMessage() { return statusMessage; }
        public int getProgressPercentage() { return progressPercentage; }
        public int getTrendColor() { return trendColor; }
        
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
        private TextView textViewDaysAndPercentage;
        private ProgressBar progressBarCategory;
        private ImageView imageViewExpand;
        private LinearLayout layoutActivitiesContainer;
        
        // View mode tracking: true = grouped, false = list
        private boolean isGroupedView = false;
        
        public CategorySummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCategoryColor = itemView.findViewById(R.id.viewCategoryColor);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            textViewTimeSpent = itemView.findViewById(R.id.textViewTimeSpent);
            textViewDaysAndPercentage = itemView.findViewById(R.id.textViewDaysAndPercentage);
            progressBarCategory = itemView.findViewById(R.id.progressBarCategory);
            imageViewExpand = itemView.findViewById(R.id.imageViewExpand);
            layoutActivitiesContainer = itemView.findViewById(R.id.layoutActivitiesContainer);
        }
        
        public void bind(CategorySummaryItem item) {
            // Display category name without trend icon for cleaner look
            textViewCategoryName.setText(item.getCategoryName());
            
            // Display percentage on top line (right side)
            String percentageText = String.format(Locale.getDefault(), "%d%%", item.getPercentage());
            textViewTimeSpent.setText(percentageText);
            
            // Set trend color for percentage text
            int trendColor = itemView.getContext().getColor(item.getTrendColor());
            textViewTimeSpent.setTextColor(trendColor);
            
            // Display hours with comparison and days count on second line
            String timeDisplay = String.format(Locale.getDefault(), "%.1fh", item.getTimeSpent());
            if (item.getPreviousTimeSpent() > 0) {
                double difference = item.getTimeSpent() - item.getPreviousTimeSpent();
                String trendText = difference > 0 ? "+" + String.format(Locale.getDefault(), "%.1fh", difference) : 
                                 String.format(Locale.getDefault(), "%.1fh", difference);
                timeDisplay += " (" + trendText + ")";
            }
            
            int daysCount = item.getDaysCount();
            String hoursAndDaysText = String.format(Locale.getDefault(), "%s • %d days", timeDisplay, daysCount);
            textViewDaysAndPercentage.setText(hoursAndDaysText);
            
            // Set category color
            try {
                String catColor = item.getCategoryColor();
                if (catColor == null || catColor.isEmpty()) {
                    catColor = "#FF2E7D32";
                }
                int color = Color.parseColor(catColor);
                viewCategoryColor.setBackgroundColor(color);
                progressBarCategory.setProgressTintList(android.content.res.ColorStateList.valueOf(color));
            } catch (Exception e) {
                // Use default color if parsing fails
                viewCategoryColor.setBackgroundColor(Color.parseColor("#FF2E7D32"));
                progressBarCategory.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF2E7D32")));
            }
            
            // Set progress based on comparison (current vs previous)
            progressBarCategory.setProgress(item.getProgressPercentage());
            
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
                populateActivitiesWithTabs(item.getActivities());
            } else {
                layoutActivitiesContainer.setVisibility(View.GONE);
                imageViewExpand.setRotation(0f);
                android.util.Log.d("CategorySummaryAdapter", "Hiding activities for: " + item.getCategoryName());
            }
        }
        
        private void populateActivitiesWithTabs(List<Activity> activities) {
            layoutActivitiesContainer.removeAllViews();
            
            if (activities == null || activities.isEmpty()) {
                TextView noActivitiesText = new TextView(itemView.getContext());
                noActivitiesText.setText("No activities found");
                noActivitiesText.setTextAppearance(android.R.style.TextAppearance_Small);
                noActivitiesText.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
                noActivitiesText.setPadding(16, 8, 16, 8);
                layoutActivitiesContainer.addView(noActivitiesText);
                return;
            }
            
            // Create tab selector card
            com.google.android.material.card.MaterialCardView tabCard = new com.google.android.material.card.MaterialCardView(itemView.getContext());
            // Convert dp to pixels for corner radius and elevation
            float density = itemView.getContext().getResources().getDisplayMetrics().density;
            tabCard.setRadius(12f * density);
            tabCard.setCardElevation(2f * density);
            
            LinearLayout tabLayout = new LinearLayout(itemView.getContext());
            tabLayout.setOrientation(LinearLayout.HORIZONTAL);
            tabLayout.setPadding(8, 8, 8, 8);
            
            // List button
            com.google.android.material.button.MaterialButton listButton = new com.google.android.material.button.MaterialButton(
                itemView.getContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle
            );
            listButton.setText("List");
            listButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            listButton.setTextSize(14);
            
            // Grouped button
            com.google.android.material.button.MaterialButton groupedButton = new com.google.android.material.button.MaterialButton(
                itemView.getContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle
            );
            groupedButton.setText("Grouped");
            groupedButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            groupedButton.setTextSize(14);
            
            // Container for activities list
            LinearLayout activitiesContentLayout = new LinearLayout(itemView.getContext());
            activitiesContentLayout.setOrientation(LinearLayout.VERTICAL);
            activitiesContentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            
            // Update button states and content based on current view mode
            updateTabButtons(listButton, groupedButton, activitiesContentLayout, activities);
            
            // Tab button click listeners
            listButton.setOnClickListener(v -> {
                isGroupedView = false;
                updateTabButtons(listButton, groupedButton, activitiesContentLayout, activities);
            });
            
            groupedButton.setOnClickListener(v -> {
                isGroupedView = true;
                updateTabButtons(listButton, groupedButton, activitiesContentLayout, activities);
            });
            
            tabLayout.addView(listButton);
            tabLayout.addView(groupedButton);
            tabCard.addView(tabLayout);
            
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 8);
            tabCard.setLayoutParams(cardParams);
            
            layoutActivitiesContainer.addView(tabCard);
            layoutActivitiesContainer.addView(activitiesContentLayout);
        }
        
        private void updateTabButtons(com.google.android.material.button.MaterialButton listButton,
                                    com.google.android.material.button.MaterialButton groupedButton,
                                    LinearLayout activitiesContentLayout,
                                    List<Activity> activities) {
            // Clear existing content
            activitiesContentLayout.removeAllViews();
            
            // Check theme
            int nightModeFlags = itemView.getContext().getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
            
            // Update button styles
            if (isGroupedView) {
                // Grouped button selected
                groupedButton.setSelected(true);
                groupedButton.setBackgroundTintList(itemView.getContext().getResources().getColorStateList(R.color.selected_date_orange, null));
                groupedButton.setTextColor(itemView.getContext().getResources().getColorStateList(android.R.color.white, null));
                
                // List button unselected
                listButton.setSelected(false);
                if (isDarkTheme) {
                    listButton.setBackgroundTintList(itemView.getContext().getResources().getColorStateList(R.color.surface_variant, null));
                    listButton.setTextColor(itemView.getContext().getResources().getColorStateList(R.color.on_surface_variant, null));
                } else {
                    listButton.setBackgroundTintList(itemView.getContext().getResources().getColorStateList(R.color.white, null));
                    listButton.setTextColor(itemView.getContext().getResources().getColorStateList(R.color.primary, null));
                }
                
                populateGroupedActivities(activities, activitiesContentLayout);
            } else {
                // List button selected
                listButton.setSelected(true);
                listButton.setBackgroundTintList(itemView.getContext().getResources().getColorStateList(R.color.selected_date_orange, null));
                listButton.setTextColor(itemView.getContext().getResources().getColorStateList(android.R.color.white, null));
                
                // Grouped button unselected
                groupedButton.setSelected(false);
                if (isDarkTheme) {
                    groupedButton.setBackgroundTintList(itemView.getContext().getResources().getColorStateList(R.color.surface_variant, null));
                    groupedButton.setTextColor(itemView.getContext().getResources().getColorStateList(R.color.on_surface_variant, null));
                } else {
                    groupedButton.setBackgroundTintList(itemView.getContext().getResources().getColorStateList(R.color.white, null));
                    groupedButton.setTextColor(itemView.getContext().getResources().getColorStateList(R.color.primary, null));
                }
                
                populateActivities(activities, activitiesContentLayout);
            }
        }
        
        private void populateActivities(List<Activity> activities, LinearLayout container) {
            if (activities == null || activities.isEmpty()) {
                TextView noActivitiesText = new TextView(itemView.getContext());
                noActivitiesText.setText("No activities found");
                noActivitiesText.setTextAppearance(android.R.style.TextAppearance_Small);
                noActivitiesText.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
                noActivitiesText.setPadding(16, 8, 16, 8);
                container.addView(noActivitiesText);
                return;
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            
            for (Activity activity : activities) {
                LinearLayout activityItem = createActivityItem(activity, dateFormat);
                container.addView(activityItem);
            }
        }
        
        private LinearLayout createActivityItem(Activity activity, SimpleDateFormat dateFormat) {
            LinearLayout activityItem = new LinearLayout(itemView.getContext());
            activityItem.setOrientation(LinearLayout.VERTICAL);
            activityItem.setPadding(16, 12, 16, 12);
            
            // Use theme-aware background color
            int backgroundColor;
            int textColor;
            int secondaryTextColor;
            
            // Check if dark theme is enabled
            int nightModeFlags = itemView.getContext().getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
            
            if (isDarkTheme) {
                // Dark theme colors
                backgroundColor = itemView.getContext().getColor(android.R.color.background_dark);
                textColor = itemView.getContext().getColor(android.R.color.primary_text_dark);
                secondaryTextColor = itemView.getContext().getColor(android.R.color.secondary_text_dark);
            } else {
                // Light theme colors
                backgroundColor = itemView.getContext().getColor(android.R.color.background_light);
                textColor = itemView.getContext().getColor(android.R.color.primary_text_light);
                secondaryTextColor = itemView.getContext().getColor(android.R.color.secondary_text_light);
            }
            
            // Create rounded background drawable
            android.graphics.drawable.GradientDrawable roundedBackground = new android.graphics.drawable.GradientDrawable();
            roundedBackground.setColor(backgroundColor);
            roundedBackground.setCornerRadius(20f); // 20dp corner radius for pill shape
            roundedBackground.setStroke(1, itemView.getContext().getColor(android.R.color.darker_gray));
            
            activityItem.setBackground(roundedBackground);
            
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
            notesText.setTextColor(textColor);
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
            
            // Date with day of week (now on the left)
            TextView dateText = new TextView(itemView.getContext());
            String dayOfWeek = getDayOfWeek(activity.getDate());
            String formattedDate = dayOfWeek + ", " + dateFormat.format(activity.getDate());
            dateText.setText(formattedDate);
            dateText.setTextAppearance(android.R.style.TextAppearance_Small);
            dateText.setTextColor(secondaryTextColor);
            dateText.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            ));
            timeDateRow.addView(dateText);
            
            // Time (now on the right)
            TextView timeText = new TextView(itemView.getContext());
            timeText.setText(String.format(Locale.getDefault(), "%.1fh", activity.getTimeSpentHours()));
            timeText.setTextAppearance(android.R.style.TextAppearance_Small);
            timeText.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
            timeText.setGravity(android.view.Gravity.END);
            timeDateRow.addView(timeText);
            
            activityItem.addView(timeDateRow);
            return activityItem;
        }
        
        private void populateGroupedActivities(List<Activity> activities, LinearLayout container) {
            if (activities == null || activities.isEmpty()) {
                TextView noActivitiesText = new TextView(itemView.getContext());
                noActivitiesText.setText("No activities found");
                noActivitiesText.setTextAppearance(android.R.style.TextAppearance_Small);
                noActivitiesText.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
                noActivitiesText.setPadding(16, 8, 16, 8);
                container.addView(noActivitiesText);
                return;
            }
            
            // Group activities by notes (activity detail)
            Map<String, List<Activity>> groupedMap = new HashMap<>();
            for (Activity activity : activities) {
                String notes = activity.getNotes() != null ? activity.getNotes() : "";
                if (!groupedMap.containsKey(notes)) {
                    groupedMap.put(notes, new ArrayList<>());
                }
                groupedMap.get(notes).add(activity);
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            
            // Create expandable items for each group
            for (Map.Entry<String, List<Activity>> entry : groupedMap.entrySet()) {
                String activityDetail = entry.getKey();
                List<Activity> activityList = entry.getValue();
                
                // Calculate totals
                int dayCount = activityList.size();
                double totalTime = 0.0;
                for (Activity act : activityList) {
                    totalTime += act.getTimeSpentHours();
                }
                
                // Create group item container
                LinearLayout groupContainer = new LinearLayout(itemView.getContext());
                groupContainer.setOrientation(LinearLayout.VERTICAL);
                
                // Create header (expandable)
                LinearLayout groupHeader = createGroupHeader(activityDetail, dayCount, totalTime);
                
                // Get expand icon reference
                ImageView expandIcon = (ImageView) groupHeader.getChildAt(groupHeader.getChildCount() - 1);
                
                // Create content container (initially hidden)
                LinearLayout groupContent = new LinearLayout(itemView.getContext());
                groupContent.setOrientation(LinearLayout.VERTICAL);
                groupContent.setVisibility(View.GONE);
                groupContent.setPadding(16, 0, 0, 0);
                
                // Add individual activities to content
                for (Activity activity : activityList) {
                    LinearLayout activityItem = createActivityItem(activity, dateFormat);
                    groupContent.addView(activityItem);
                }
                
                // Track expanded state for this group
                boolean[] isExpanded = {false};
                
                // Set click listener to expand/collapse
                groupHeader.setOnClickListener(v -> {
                    isExpanded[0] = !isExpanded[0];
                    if (isExpanded[0]) {
                        groupContent.setVisibility(View.VISIBLE);
                        if (expandIcon != null) {
                            expandIcon.setRotation(180f);
                        }
                    } else {
                        groupContent.setVisibility(View.GONE);
                        if (expandIcon != null) {
                            expandIcon.setRotation(0f);
                        }
                    }
                });
                
                groupContainer.addView(groupHeader);
                groupContainer.addView(groupContent);
                
                LinearLayout.LayoutParams groupParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                groupParams.setMargins(0, 0, 0, 8);
                groupContainer.setLayoutParams(groupParams);
                
                container.addView(groupContainer);
            }
        }
        
        private LinearLayout createGroupHeader(String activityDetail, int dayCount, double totalTime) {
            LinearLayout header = new LinearLayout(itemView.getContext());
            header.setOrientation(LinearLayout.HORIZONTAL);
            header.setPadding(16, 12, 16, 12);
            header.setGravity(android.view.Gravity.CENTER_VERTICAL);
            
            // Use theme-aware colors
            int backgroundColor;
            int textColor;
            int secondaryTextColor;
            
            int nightModeFlags = itemView.getContext().getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            boolean isDarkTheme = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
            
            if (isDarkTheme) {
                backgroundColor = itemView.getContext().getColor(android.R.color.background_dark);
                textColor = itemView.getContext().getColor(android.R.color.primary_text_dark);
                secondaryTextColor = itemView.getContext().getColor(android.R.color.secondary_text_dark);
            } else {
                backgroundColor = itemView.getContext().getColor(android.R.color.background_light);
                textColor = itemView.getContext().getColor(android.R.color.primary_text_light);
                secondaryTextColor = itemView.getContext().getColor(android.R.color.secondary_text_light);
            }
            
            // Create rounded background drawable
            android.graphics.drawable.GradientDrawable roundedBackground = new android.graphics.drawable.GradientDrawable();
            roundedBackground.setColor(backgroundColor);
            roundedBackground.setCornerRadius(20f);
            roundedBackground.setStroke(2, itemView.getContext().getColor(android.R.color.holo_blue_dark));
            
            header.setBackground(roundedBackground);
            
            // Activity detail text
            TextView detailText = new TextView(itemView.getContext());
            detailText.setText(activityDetail.isEmpty() ? "(No description)" : activityDetail);
            detailText.setTextAppearance(android.R.style.TextAppearance_Medium);
            detailText.setTextColor(textColor);
            detailText.setMaxLines(2);
            detailText.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            );
            detailText.setLayoutParams(detailParams);
            header.addView(detailText);
            
            // Count and time info
            TextView infoText = new TextView(itemView.getContext());
            infoText.setText(String.format(Locale.getDefault(), "%d days • %.1fh", dayCount, totalTime));
            infoText.setTextAppearance(android.R.style.TextAppearance_Small);
            infoText.setTextColor(secondaryTextColor);
            infoText.setPadding(8, 0, 8, 0);
            header.addView(infoText);
            
            // Expand icon
            ImageView expandIcon = new ImageView(itemView.getContext());
            expandIcon.setImageResource(R.drawable.ic_expand_more);
            expandIcon.setLayoutParams(new LinearLayout.LayoutParams(
                24, 24
            ));
            expandIcon.setColorFilter(itemView.getContext().getColor(android.R.color.darker_gray));
            header.addView(expandIcon);
            
            return header;
        }
    }
    
    private String getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "Sun";
            case Calendar.MONDAY:
                return "Mon";
            case Calendar.TUESDAY:
                return "Tue";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thu";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";
            default:
                return "";
        }
    }
}
