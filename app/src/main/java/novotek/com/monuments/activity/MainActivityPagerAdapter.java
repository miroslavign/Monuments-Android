
/*
 * MainActivityPagerAdapter.java
 * Monuments-Android
 *
 * Created by Miroslav Ignjatovic on 1/3/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package novotek.com.monuments.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import novotek.com.monuments.Monuments;
import novotek.com.monuments.fragment.MonumentListFragment;

public class MainActivityPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 2;
    public static final int MY_MONUMENTS_FRAGEMENT = 0;
    public static final int OTHER_MONUMENTS_FRAGMENT = 1;

    private MainActivity myActivity;

    public MainActivityPagerAdapter(FragmentManager fragmentManager, MainActivity activity) {
        super(fragmentManager);
        myActivity = activity;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MonumentListFragment otherMonumentsFragment = MonumentListFragment.newInstance(
                        OTHER_MONUMENTS_FRAGMENT, "page 1", Monuments.instance.getLoggedUser().getUserId());
                myActivity.setOtherMonumentsFragment(otherMonumentsFragment);
                return otherMonumentsFragment;
            case 1:
                MonumentListFragment myMonumentsFragment = MonumentListFragment.newInstance(
                        MY_MONUMENTS_FRAGEMENT, "page 0", null);
                myActivity.setMyMonumentsFragment(myMonumentsFragment);
                return myMonumentsFragment;
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "My Monuments";
            case 1:
                return "All Monuments";
            default:
                return "";
        }
    }

}

