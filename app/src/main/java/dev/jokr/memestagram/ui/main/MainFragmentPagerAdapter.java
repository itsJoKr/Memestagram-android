package dev.jokr.memestagram.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by jokr on 27.12.16..
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {


    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new MemesListFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) return "DANK";
        if (position == 1) return "FRESH";
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
