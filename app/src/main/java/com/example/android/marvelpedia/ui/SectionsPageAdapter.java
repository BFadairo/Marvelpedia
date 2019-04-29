package com.example.android.marvelpedia.ui;

import com.example.android.marvelpedia.ui.Fragments.MasterListFragment;
import com.example.android.marvelpedia.ui.Fragments.TeamFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPageAdapter extends FragmentPagerAdapter {

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MasterListFragment.newInstance();
            case 1:
                return TeamFragment.newInstance();
            default:
                return MasterListFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
