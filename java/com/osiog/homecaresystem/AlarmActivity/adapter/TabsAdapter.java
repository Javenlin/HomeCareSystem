package com.osiog.homecaresystem.AlarmActivity.adapter;

/**
 * Created by Qinghao on 3/11/2015.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.osiog.homecaresystem.AlarmActivity.Fragment.ScheduleFragment;
import com.osiog.homecaresystem.AlarmActivity.Fragment.TodayFragment;


/**
 * This fragment is based on the code at
 * http://www.feelzdroid.com/2014/10/android-action-bar-tabs-swipe-views.html
 * <p>
 * This is a customized fragment pager adapter that handles the controller of
 * the swipe tabs we use in the main page/activity.
 */

public class TabsAdapter extends FragmentPagerAdapter {

     private int TOTAL_TABS = 2;


    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
//            case 0:
//                return new HistoryFragment();
            case 0:
                return new TodayFragment();
            case 1:
                return new ScheduleFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TOTAL_TABS;
    }
}
