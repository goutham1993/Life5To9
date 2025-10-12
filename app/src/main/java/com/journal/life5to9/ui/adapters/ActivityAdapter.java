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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    
    private List<Activity> activities = new ArrayList<>();
    private Map<Long, Category> categoryMap = new HashMap<>();
    private OnActivityClickListener listener;
    private OnActivityDeleteListener deleteListener;
    
    public interface OnActivityClickListener {
        void onActivityClick(Activity activity);
        void onActivityLongClick(Activity activity);
    }
    
    public interface OnActivityDeleteListener {
        void onActivityDelete(Activity activity);
    }
    
    public void setOnActivityClickListener(OnActivityClickListener listener) {
        this.listener = listener;
    }
    
    public void setOnActivityDeleteListener(OnActivityDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
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
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.bind(activity);
    }
    
    @Override
    public int getItemCount() {
        return activities.size();
    }
    
    class ActivityViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCategoryName;
        private TextView textViewTimeSpent;
        private TextView textViewNotes;
        private TextView textViewDate;
        private TextView textViewTime;
        
        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            textViewTimeSpent = itemView.findViewById(R.id.textViewTimeSpent);
            textViewNotes = itemView.findViewById(R.id.textViewNotes);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onActivityClick(activities.get(position));
                    }
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (deleteListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deleteListener.onActivityDelete(activities.get(position));
                        return true;
                    }
                }
                return false;
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
                    textViewCategoryName.setTextColor(itemView.getContext().getColor(android.R.color.white));
                }
            } else {
                textViewCategoryName.setTextColor(itemView.getContext().getColor(android.R.color.white));
            }
            
            textViewTimeSpent.setText(String.format(Locale.getDefault(), "%.1fh", activity.getTimeSpentHours()));
            textViewNotes.setText(activity.getNotes() != null ? activity.getNotes() : "No notes");
            
            if (activity.getDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                
                textViewDate.setText(dateFormat.format(activity.getDate()));
                textViewTime.setText(timeFormat.format(activity.getDate()));
            } else {
                textViewDate.setText("No date");
                textViewTime.setText("");
            }
        }
    }
}
