package com.journal.life5to9.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.journal.life5to9.ui.fragments.WeekdayReportFragment;
import com.journal.life5to9.ui.fragments.WeekendReportFragment;
import com.journal.life5to9.ui.fragments.MonthlyReportFragment;

public class ReportsViewPagerAdapter extends FragmentStateAdapter {
    
    private static final int TAB_COUNT = 3;
    
    public ReportsViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WeekdayReportFragment();
            case 1:
                return new WeekendReportFragment();
            case 2:
                return new MonthlyReportFragment();
            default:
                return new WeekdayReportFragment();
        }
    }
    
    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}

