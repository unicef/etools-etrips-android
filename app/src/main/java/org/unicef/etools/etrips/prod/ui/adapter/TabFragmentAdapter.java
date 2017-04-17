package org.unicef.etools.etrips.prod.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class TabFragmentAdapter extends FragmentStatePagerAdapter {

    private static final String LOG_TAG = TabFragmentAdapter.class.getSimpleName();

    private final ArrayList<Fragment> mFragments = new ArrayList<>();
    private final ArrayList<String> mFragmentTitles = new ArrayList<>();

    public TabFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }

    public void addTitle(String title) {
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }


}
