package com.chartianz.ipocheckkar.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.chartianz.ipocheckkar.modal.IPOInfo;
import com.chartianz.ipocheckkar.ui.fragments.detailed_fragments.subscription;

import java.util.List;

public class IPODetailsPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private IPOInfo ipoInfo;

    public IPODetailsPageAdapter(FragmentManager fm, List<Fragment> fragments, IPOInfo ipoInfo) {
        super(fm);
        this.fragments = fragments;
        this.ipoInfo = ipoInfo;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1: return subscription.newInstance(ipoInfo);
            default: return subscription.newInstance(ipoInfo);
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}


