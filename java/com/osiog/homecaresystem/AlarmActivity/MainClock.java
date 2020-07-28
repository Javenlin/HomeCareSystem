package com.osiog.homecaresystem.AlarmActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.osiog.homecaresystem.AlarmActivity.AlarmModel.Alarm;
import com.osiog.homecaresystem.AlarmActivity.AlarmModel.Pill;
import com.osiog.homecaresystem.AlarmActivity.AlarmModel.PillBox;
import com.osiog.homecaresystem.AlarmActivity.AlarmModel.PillComparator;
import com.osiog.homecaresystem.AlarmActivity.adapter.ExpandableListAdapter;
import com.osiog.homecaresystem.LoginActivity;
import com.osiog.homecaresystem.R;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//import com.example.iii.healthandmaps.Actmain;

/**
 * Created by iii on 2018/1/9.
 */

public class MainClock extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    // This data structure allows us to get the ids of the alarms we want to edit
    // and store them in the tempId in the pill box model. The structure is similar
    // to the struture of listDataChild.
    List<List<List<Long>>> alarmIDData;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_mainclock);
//        getSupportActionBar().setTitle("用藥提醒");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        InitialComponent();

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        try {
            prepareListData();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        listDataHeader.get(groupPosition) + " 欄位展開",
//                        Toast.LENGTH_SHORT).show();
            }


//            public int getGroupCount() {
//                int size = listDataHeader.size();
//                return size;
//            }
//
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                for (int g = 0; g <  getGroupCount(); g++) {
//                    if (g != groupPosition) {
//                        expListView.collapseGroup(g);
//                    }
//                }
//            }
        });

        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        listDataHeader.get(groupPosition) + " 欄位摺疊",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                PillBox pillBox = new PillBox();
                pillBox.setTempIds(alarmIDData.get(groupPosition).get(childPosition));

                Intent intent = new Intent(getApplicationContext(), EditClock.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
    }



    private void prepareListData() throws URISyntaxException{
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        alarmIDData = new ArrayList<List<List<Long>>>();

        PillBox pillbox = new PillBox();
        List<Pill> pills = pillbox.getPills(this);
        Collections.sort(pills, new PillComparator());

        for (Pill pill: pills){
            String name = pill.getPillName();

            /*String tempPill_name = pillBox.getTempName();
            List<Alarm> tempTracker = pillBox.getAlarmByPill(getBaseContext(), tempPill_name);
            if(tempTracker.size()==0)
            listDataHeader.remove(tempPill_name);*/
             
            
            listDataHeader.add(name);
            List<String> times = new ArrayList<String>();
            List<Alarm> alarms = pillbox.getAlarmByPill(this.getBaseContext(), name);
            List<List<Long>> ids = new ArrayList<List<Long>>();

            for (Alarm alarm :alarms){
                String time = alarm.getStringTime() + daysList(alarm);
                times.add(time);
                ids.add(alarm.getIds());
            }

            alarmIDData.add(ids);
            listDataChild.put(name, times);
        }
    }


    /**
     * Helper function to obtain a string of the days of the week
     * that can be used as a boolean list
     */
    private String daysList(Alarm alarm) {
        String days = "#";
        for(int i=0; i<7; i++){
            if (alarm.getDayOfWeek()[i])
                days += "1";
            else
                days += "0";
        }
        return days;
    }



    /**
     * BUTTON_實作按鈕_新增鬧鈴功能
     */
    ImageButton.OnClickListener btn_newclockListener =
            new ImageButton.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(MainClock.this, AddClock.class);
                    startActivity(intent);
                }
            };

    /**
     * BUTTON_實作按鈕_查詢紀錄功能
     */
    ImageButton.OnClickListener btn_recordListener =
            new ImageButton.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(MainClock.this, PagerClock.class);
                    startActivity(intent);
                }
            };

    @Override
/**
 * BUTTON_實作按鈕_( <-- )回主選單功能
 */
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        //刪除按鈕實作
        if (id == R.id.action_delete) {
//  pillBox.destory(getApplicationContext());

            AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle("鬧鈴格式化");
            isExit.setMessage("注意!! 所有已創建資料將初始化\n\n執行後APP將關閉重啟");
            isExit.setButton("確定", quitlistener);
            isExit.setButton2("取消", quitlistener);
            isExit.show();

//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable(){
//
//                                    @Override
//                                    public void run() {
//
//                                        //過兩秒後要做的事情
//                                        Log.d("tag", "我要打掃了");
//
//                                    }}, 10000);
            return true;
        }


            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){

                                    @Override
                                    public void run() {

                                        //過兩秒後要做的事情
                                        Log.d("tag", "我要離開了");

                                        Intent intent = new Intent();
                                        intent.setClass(MainClock.this, LoginActivity.class);
                                        startActivity(intent);

                                    }}, 0);

        return super.onOptionsItemSelected(item);

    }

    //標頭_刪除按鈕
    @Override
    /** Inflate the menu; this adds items to the action bar if it is present */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuedit, menu);
        return true;
    }



        DialogInterface.OnClickListener quitlistener = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {

                    case AlertDialog.BUTTON_POSITIVE:
                        pillBox.destory(getApplicationContext());
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };

     private void InitialComponent() {
        btn_newclock = (ImageButton)findViewById(R.id.btn_newclock);
        btn_newclock.setOnClickListener(btn_newclockListener);

         btn_record = (ImageButton)findViewById(R.id.btn_record);
         btn_record.setOnClickListener(btn_recordListener);

    }
    ImageButton btn_newclock;
    ImageButton btn_record;
    PillBox pillBox = new PillBox();
    String tempPill_name;


}