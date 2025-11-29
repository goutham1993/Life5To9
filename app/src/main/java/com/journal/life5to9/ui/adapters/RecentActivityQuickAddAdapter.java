package com.journal.life5to9.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.utils.CategoryEmojiMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecentActivityQuickAddAdapter extends RecyclerView.Adapter<RecentActivityQuickAddAdapter.RecentActivityViewHolder> {
    
    private List<Activity> activities = new ArrayList<>();
    private Map<Long, Category> categoryMap = new HashMap<>();
    private OnRecentActivityClickListener listener;
    
    public interface OnRecentActivityClickListener {
        void onRecentActivityClick(Activity activity);
    }
    
    public void setOnRecentActivityClickListener(OnRecentActivityClickListener listener) {
        this.listener = listener;
    }
    
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
        notifyDataSetChanged();
    }
    
    public void setCategories(List<Category> categories) {
        categoryMap.clear();
        for (Category category : categories) {
            categoryMap.put(category.getId(), category);
        }
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public RecentActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_activity_quick_add, parent, false);
        return new RecentActivityViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecentActivityViewHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.bind(activity);
    }
    
    @Override
    public int getItemCount() {
        return activities.size();
    }
    
    class RecentActivityViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCategoryName;
        private TextView textViewNotes;
        private TextView textViewTimeSpent;
        
        public RecentActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            textViewNotes = itemView.findViewById(R.id.textViewNotes);
            textViewTimeSpent = itemView.findViewById(R.id.textViewTimeSpent);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRecentActivityClick(activities.get(position));
                    }
                }
            });
        }
        
        public void bind(Activity activity) {
            // Look up category name by ID
            Category category = categoryMap.get(activity.getCategoryId());
            String categoryName = category != null ? category.getName() : "Unknown Category";
            
            // Get emoji for the category
            String emoji = CategoryEmojiMapper.getEmojiForCategory(categoryName);
            
            // Display category name with emoji
            textViewCategoryName.setText(emoji + " " + categoryName);
            
            // Set category color
            if (category != null && category.getColor() != null) {
                try {
                    int color = android.graphics.Color.parseColor(category.getColor());
                    textViewCategoryName.setTextColor(color);
                } catch (IllegalArgumentException e) {
                    // Use default color if parsing fails
                    textViewCategoryName.setTextColor(itemView.getContext().getColor(R.color.primary));
                }
            } else {
                textViewCategoryName.setTextColor(itemView.getContext().getColor(R.color.primary));
            }
            
            // Display notes (truncate if too long)
            String notes = activity.getNotes() != null && !activity.getNotes().isEmpty() 
                ? activity.getNotes() 
                : "No notes";
            if (notes.length() > 40) {
                notes = notes.substring(0, 37) + "...";
            }
            textViewNotes.setText(notes);
            
            // Display time spent
            textViewTimeSpent.setText(String.format(Locale.getDefault(), "%.1fh", activity.getTimeSpentHours()));
        }
    }
}

