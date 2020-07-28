package com.osiog.homecaresystem.AlarmActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.osiog.homecaresystem.AlarmActivity.adapter.TabsAdapter;
import com.osiog.homecaresystem.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This activity is based on the code at
 * http://www.feelzdroid.com/2014/10/android-action-bar-tabs-swipe-views.html
 * <p>
 * This fragment handles the view and controller of the home screen
 * It allows the user to swipe between the today, tomorrow and history tabs
 * It also contains the action bar buttons to the add, schedule, and pill box pages
 */

public class PagerClock extends AppCompatActivity implements ActionBar.TabListener {
    private ViewPager tabsviewPager;
    private TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_pagerclock);
//        getSupportActionBar().setTitle("用藥紀錄");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabsviewPager = (ViewPager) findViewById(R.id.tabspager);

        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());

        tabsviewPager.setAdapter(mTabsAdapter);

         getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        String todayString = new SimpleDateFormat("M月d日").format(today);
// Date tomorrow = calendar.getTime();
//        String tomorrowString = new SimpleDateFormat("EEE, MMM d").format(tomorrow);

        Tab historytab = getSupportActionBar().newTab().setTabListener(this);
        Tab todaytab = getSupportActionBar().newTab().setTabListener(this);
        Tab scheduletab = getSupportActionBar().newTab().setTabListener(this);

//        TextView tt1 = new TextView(this);
//        tt1.setText(Html.fromHtml("<b>達成紀錄</b><br>"));
//        tt1.setTextColor(Color.WHITE);
//        tt1.setGravity(Gravity.CENTER);
//        tt1.setTextSize(25);
//        tt1.setHeight(200);
//        historytab.setCustomView(tt1);

        TextView tt2 = new TextView(this);
        tt2.setText(Html.fromHtml("<b>今日用藥</b><br><small>" + todayString + "</small>"));
        tt2.setTextColor(Color.WHITE);
        tt2.setGravity(Gravity.CENTER);
        tt2.setTextSize(22);
        tt2.setHeight(200);
        todaytab.setCustomView(tt2);

        TextView tt3 = new TextView(this);
        tt3.setText(Html.fromHtml("<b>全周預覽</b><br>"));
        tt3.setTextColor(Color.WHITE);
        tt3.setGravity(Gravity.CENTER);
        tt3.setTextSize(25);
        tt3.setHeight(200);
        scheduletab.setCustomView(tt3);

        getSupportActionBar().addTab(historytab);
        getSupportActionBar().addTab(todaytab);
        getSupportActionBar().addTab(scheduletab);

        getSupportActionBar().setSelectedNavigationItem(1);

        /** This helps in providing swiping effect for v7 compat library */
        tabsviewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
//                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }


    @Override
    /** Inflate the menu; this adds items to the action bar if it is present */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menupager, menu);
        return true;
    }

    @Override
    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        Intent intent = new Intent();
        intent.setClass(PagerClock.this, MainClock.class);
        startActivity(intent);
        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
    }

    @Override
    public void onTabSelected(Tab selectedtab, FragmentTransaction arg1) {
        /** Update tab position on tap */
        tabsviewPager.setCurrentItem(selectedtab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

    }


    @Override
    public void onBackPressed() {
        finish();
    }
    //return
}