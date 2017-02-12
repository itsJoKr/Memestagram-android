package dev.jokr.memestagram.misc;

import android.support.v4.widget.DrawerLayout;
import android.view.View;

import dev.jokr.memestagram.views.AnimatedLogoView;

/**
 * Created by jokr on 12.02.17..
 */

public class LogoAnimatorDrawerListener implements DrawerLayout.DrawerListener {

    private final AnimatedLogoView animatedLogoView;

    public LogoAnimatorDrawerListener(AnimatedLogoView view) {
        this.animatedLogoView = view;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) { }

    @Override
    public void onDrawerOpened(View drawerView) {
        animatedLogoView.startLoading();
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        animatedLogoView.stopLoading();
    }

    @Override
    public void onDrawerStateChanged(int newState) { }
}
