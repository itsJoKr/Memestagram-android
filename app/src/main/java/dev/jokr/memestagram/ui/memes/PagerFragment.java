package dev.jokr.memestagram.ui.memes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.ui.main.MainActivity;
import dev.jokr.memestagram.ui.main.MainFragmentPagerAdapter;

/**
 * Created by jokr on 03.01.17..
 */

public class PagerFragment extends Fragment {

    private static final String TYPE = "type";

    @BindView(R.id.viewpager_main)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_main)
    TabLayout tabLayout;

    public static PagerFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        PagerFragment fragment = new PagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pager, container, false);
        ButterKnife.bind(this, v);

        int type = getArguments().getInt(TYPE);

        viewPager.setAdapter(new MainFragmentPagerAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(type - 1); // type num equals page+1

        return v;
    }


}
