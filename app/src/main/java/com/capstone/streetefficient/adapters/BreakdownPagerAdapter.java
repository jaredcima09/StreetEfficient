package com.capstone.streetefficient.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.capstone.streetefficient.fragments.BreakdownFragment;

import java.util.ArrayList;

public class BreakdownPagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<BreakdownFragment> breakdownFragments;

    public BreakdownPagerAdapter(@NonNull FragmentManager fm, ArrayList<BreakdownFragment> breakdownFragments) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.breakdownFragments = breakdownFragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return breakdownFragments.get(position);
    }

    @Override
    public int getCount() {
        return breakdownFragments.size();
    }

}


