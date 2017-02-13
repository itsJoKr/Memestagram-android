package dev.jokr.memestagram.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import dev.jokr.memestagram.ui.memes.MemesListFragment;

/**
 * Created by jokr on 27.12.16..
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {


    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) return MemesListFragment.newInstance(MemesListFragment.DANK);
        if (position == 1) return MemesListFragment.newInstance(MemesListFragment.FRESH);
        if (position == 2) return MemesListFragment.newInstance(MemesListFragment.SUBBED);
        if (position == 3) return MemesListFragment.newInstance(MemesListFragment.RANDOM);
        else throw new IllegalArgumentException("Invalid position for fragment " + position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) return "DANK";
        if (position == 1) return "FRESH";
        if (position == 2) return "SUBBED";
        if (position == 3) return "RANDOM";
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return 4;
    }
}
