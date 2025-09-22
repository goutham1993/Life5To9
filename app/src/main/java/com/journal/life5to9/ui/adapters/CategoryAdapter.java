package com.journal.life5to9.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.journal.life5to9.R;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.utils.CategoryEmojiMapper;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    
    private List<Category> categories = new ArrayList<>();
    private OnCategoryClickListener listener;
    
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
        void onCategoryLongClick(Category category);
    }
    
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }
    
    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }
    
    @Override
    public int getItemCount() {
        return categories.size();
    }
    
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCategoryEmoji;
        private TextView textViewCategoryName;
        private TextView textViewActivityCount;
        private TextView textViewTotalTime;
        private TextView textViewIsDefault;
        
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryEmoji = itemView.findViewById(R.id.textViewCategoryEmoji);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            textViewActivityCount = itemView.findViewById(R.id.textViewActivityCount);
            textViewTotalTime = itemView.findViewById(R.id.textViewTotalTime);
            textViewIsDefault = itemView.findViewById(R.id.textViewIsDefault);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCategoryClick(categories.get(position));
                    }
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCategoryLongClick(categories.get(position));
                        return true;
                    }
                }
                return false;
            });
        }
        
        public void bind(Category category) {
            // Get emoji for the category
            String emoji = CategoryEmojiMapper.getEmojiForCategory(category.getName());
            textViewCategoryEmoji.setText(emoji);
            
            textViewCategoryName.setText(category.getName());
            
            // For now, show placeholder data until we implement activity counting
            textViewActivityCount.setText("0 activities");
            textViewTotalTime.setText("0.0 hours total");
            
            // Show default badge if it's a default category
            if (category.isDefault()) {
                textViewIsDefault.setVisibility(View.VISIBLE);
            } else {
                textViewIsDefault.setVisibility(View.GONE);
            }
        }
    }
}
