package com.journal.life5to9.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.utils.CategoryEmojiMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DayActivitiesAdapter extends RecyclerView.Adapter<DayActivitiesAdapter.DayViewHolder> {

    private List<DayActivities> dayActivitiesList;
    private Map<Long, Category> categoryMap;
    private Context context;
    private SimpleDateFormat dayNameFormat;
    private SimpleDateFormat dayDateFormat;

    public DayActivitiesAdapter(Context context) {
        this.context = context;
        this.dayActivitiesList = new ArrayList<>();
        this.categoryMap = new HashMap<>();
        this.dayNameFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        this.dayDateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
    }

    public void setDayActivities(List<DayActivities> dayActivities) {
        this.dayActivitiesList = dayActivities != null ? dayActivities : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCategories(List<Category> categories) {
        categoryMap.clear();
        if (categories != null) {
            for (Category category : categories) {
                categoryMap.put(category.getId(), category);
            }
        }
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day_activities, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        DayActivities dayActivities = dayActivitiesList.get(position);
        holder.bind(dayActivities);
    }

    @Override
    public int getItemCount() {
        return dayActivitiesList.size();
    }

    public class DayViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDayName;
        private TextView textViewDayDate;
        private TextView textViewDayTotal;
        private TextView textViewCategoryPreview;
        private LinearLayout layoutActivities;
        private TextView textViewNoActivities;
        private LinearLayout layoutDayHeader;
        private ImageView imageViewExpand;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDayName = itemView.findViewById(R.id.textViewDayName);
            textViewDayDate = itemView.findViewById(R.id.textViewDayDate);
            textViewDayTotal = itemView.findViewById(R.id.textViewDayTotal);
            textViewCategoryPreview = itemView.findViewById(R.id.textViewCategoryPreview);
            layoutActivities = itemView.findViewById(R.id.layoutActivities);
            textViewNoActivities = itemView.findViewById(R.id.textViewNoActivities);
            layoutDayHeader = itemView.findViewById(R.id.layoutDayHeader);
            imageViewExpand = itemView.findViewById(R.id.imageViewExpand);
        }

        public void bind(DayActivities dayActivities) {
            // Set day name and date
            textViewDayName.setText(dayNameFormat.format(dayActivities.getDate()));
            textViewDayDate.setText(dayDateFormat.format(dayActivities.getDate()));
            
            // Calculate total time for the day
            double totalTime = 0.0;
            for (Activity activity : dayActivities.getActivities()) {
                totalTime += activity.getTimeSpentHours();
            }
            textViewDayTotal.setText(String.format(Locale.getDefault(), "%.1fh", totalTime));

            // Generate category preview
            String categoryPreview = generateCategoryPreview(dayActivities.getActivities());
            textViewCategoryPreview.setText(categoryPreview);

            // Set up click listener for expand/collapse
            layoutDayHeader.setOnClickListener(v -> {
                dayActivities.setExpanded(!dayActivities.isExpanded());
                updateExpandedState(dayActivities);
            });

            // Update the expanded state
            updateExpandedState(dayActivities);
        }

        private void updateExpandedState(DayActivities dayActivities) {
            if (dayActivities.isExpanded()) {
                // Show activities
                layoutActivities.setVisibility(View.VISIBLE);
                imageViewExpand.setRotation(180f); // Rotate arrow down
                
                // Clear and populate activities
                layoutActivities.removeAllViews();
                
                if (dayActivities.getActivities().isEmpty()) {
                    textViewNoActivities.setVisibility(View.VISIBLE);
                    layoutActivities.addView(textViewNoActivities);
                } else {
                    textViewNoActivities.setVisibility(View.GONE);
                    
                    // Add each activity
                    for (Activity activity : dayActivities.getActivities()) {
                        View activityView = createActivityView(activity);
                        layoutActivities.addView(activityView);
                    }
                }
            } else {
                // Hide activities
                layoutActivities.setVisibility(View.GONE);
                imageViewExpand.setRotation(0f); // Reset arrow
            }
        }

        private View createActivityView(Activity activity) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_activity_in_day, layoutActivities, false);
            
            TextView textViewCategoryName = view.findViewById(R.id.textViewCategoryName);
            TextView textViewActivityNotes = view.findViewById(R.id.textViewActivityNotes);
            TextView textViewTimeSpent = view.findViewById(R.id.textViewTimeSpent);
            View viewCategoryColor = view.findViewById(R.id.viewCategoryColor);

            // Set category name and color
            Category category = categoryMap.get(activity.getCategoryId());
            if (category != null) {
                textViewCategoryName.setText(category.getName());
                try {
                    int color = android.graphics.Color.parseColor(category.getColor());
                    viewCategoryColor.setBackgroundColor(color);
                } catch (Exception e) {
                    viewCategoryColor.setBackgroundColor(android.graphics.Color.GRAY);
                }
            } else {
                textViewCategoryName.setText("Unknown Category");
                viewCategoryColor.setBackgroundColor(android.graphics.Color.GRAY);
            }

            // Set activity details
            textViewActivityNotes.setText(activity.getNotes() != null ? activity.getNotes() : "No notes");
            textViewTimeSpent.setText(String.format(Locale.getDefault(), "%.1fh", activity.getTimeSpentHours()));

            return view;
        }

        private String generateCategoryPreview(List<Activity> activities) {
            if (activities == null || activities.isEmpty()) {
                return "No activities";
            }

            // Collect unique category names with emojis
            Set<String> categoryNamesWithEmojis = new LinkedHashSet<>();
            for (Activity activity : activities) {
                Category category = categoryMap.get(activity.getCategoryId());
                if (category != null) {
                    String emoji = CategoryEmojiMapper.getEmojiForCategory(category.getName());
                    categoryNamesWithEmojis.add(emoji + " " + category.getName());
                }
            }

            if (categoryNamesWithEmojis.isEmpty()) {
                return "📝 Unknown categories";
            }

            // Join category names with emojis with commas
            return String.join(", ", categoryNamesWithEmojis);
        }
    }

    public static class DayActivities {
        private Date date;
        private List<Activity> activities;
        private boolean isExpanded;

        public DayActivities(Date date, List<Activity> activities) {
            this.date = date;
            this.activities = activities;
            this.isExpanded = false; // Start collapsed
        }

        public Date getDate() {
            return date;
        }

        public List<Activity> getActivities() {
            return activities;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean expanded) {
            isExpanded = expanded;
        }
    }
}
