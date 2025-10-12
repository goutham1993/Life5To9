package com.journal.life5to9.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.journal.life5to9.ui.fragments.ActivityFragment;
import com.journal.life5to9.ui.fragments.CategoriesFragment;
import com.journal.life5to9.ui.fragments.SummaryFragment;
import com.journal.life5to9.ui.fragments.CalendarFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    
    private static final int TAB_COUNT = 4;
    private ActivityFragment activityFragment;
    
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    public ActivityFragment getActivityFragment() {
        return activityFragment;
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                activityFragment = new ActivityFragment();
                return activityFragment;
            case 1:
                return new CategoriesFragment();
            case 2:
                return new SummaryFragment();
            case 3:
                return new CalendarFragment();
            default:
                activityFragment = new ActivityFragment();
                return activityFragment;
        }
    }
    
    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
