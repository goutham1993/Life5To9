package com.journal.life5to9.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.journal.life5to9.R;
import com.journal.life5to9.ui.adapters.ReportsViewPagerAdapter;

public class ReportsFragment extends Fragment {
    
    private ViewPager2 reportsViewPager;
    private TabLayout reportsTabLayout;
    private ReportsViewPagerAdapter reportsAdapter;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        
        reportsViewPager = view.findViewById(R.id.reportsViewPager);
        reportsTabLayout = view.findViewById(R.id.reportsTabLayout);
        
        setupReportsPager();
        
        return view;
    }
    
    private void setupReportsPager() {
        reportsAdapter = new ReportsViewPagerAdapter(getChildFragmentManager(), getLifecycle());
        reportsViewPager.setAdapter(reportsAdapter);
        
        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(reportsTabLayout, reportsViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Weekday");
                    break;
                case 1:
                    tab.setText("Weekend");
                    break;
                case 2:
                    tab.setText("Monthly");
                    break;
            }
        }).attach();
    }
}

