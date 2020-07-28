package com.osiog.homecaresystem.AlarmActivity.Fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.osiog.homecaresystem.AlarmActivity.AlarmModel.Alarm;
import com.osiog.homecaresystem.AlarmActivity.AlarmModel.PillBox;
import com.osiog.homecaresystem.R;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

//import android.support.v7.app.ActionBarActivity;


/**
 * This activity handles the view and controller of the week-at-a-glance page, where
 * the user can view the schedule of alarms for all 7 days. The logic is the same as
 * the logic in TodayFragment and TomorrowFragment.
 */

public class ScheduleFragment extends Fragment {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.z_fragment_schedule);
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.z_fragment_schedule, container, false);

            TableLayout stk = (TableLayout) rootView.findViewById(R.id.table_calendar);

//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle("Week at a Glance");


            PillBox pillBox = new PillBox();

            List<Alarm> alarms = null;

            List<String> days = Arrays.asList("星期日", "星期一", "星期二",
                    "星期三", "星期四", "星期五", "星期六");

            int color = getResources().getColor(R.color.blue600);

            for (int i = 1; i < 8; i++) {

                String day = days.get(i-1);
                TableRow headerRow = new TableRow(container.getContext());
                TextView headerText = new TextView(container.getContext());

                headerText.setText(day);
                headerText.setTextColor(Color.WHITE);
                headerText.setPadding(30, 0, 0, 0);
                headerText.setTypeface(null, Typeface.BOLD);
                headerText.setGravity(Gravity.CENTER);
                headerText.setTextSize(20);
                headerRow.addView(headerText);
                headerRow.setBackgroundColor(color);
                stk.addView(headerRow);

                //Let headerText span two columns
                TableRow.LayoutParams params = (TableRow.LayoutParams)headerText.getLayoutParams();
                params.span = 2;
                headerText.setLayoutParams(params);

                try {
                    alarms = pillBox.getAlarms(container.getContext(), i);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                if(alarms.size() != 0) {
                    for(Alarm alarm: alarms) {
                        TableRow tbrow = new TableRow(container.getContext());

                        TextView t1v = new TextView(container.getContext());
                        t1v.setText(alarm.getPillName());
                        t1v.setMaxEms(6);
                        t1v.setGravity(Gravity.CENTER);
                        t1v.setTextColor(Color.DKGRAY);
                        t1v.setPadding(10, 10, 10, 10);
                        t1v.setTypeface(null, Typeface.BOLD);
                        t1v.setTextSize(20);
                        tbrow.addView(t1v);

                        TextView t2v = new TextView(container.getContext());

                        String time = alarm.getStringTime();
                        t2v.setText(time);
                        t2v.setGravity(Gravity.CENTER);
                        t2v.setTextSize(20);
                        tbrow.addView(t2v);

                        stk.addView(tbrow);
                    }
                } else {
                    TableRow tbrow = new TableRow(container.getContext());
                    TextView tv = new TextView(container.getContext());
                    tv.setGravity(Gravity.CENTER);
                    tv.setText(day + "　尚無創建任何資料");
                    tv.setTextSize(20);
                    tbrow.addView(tv);
                    stk.addView(tbrow);

                    //Let tv span two columns
                    TableRow.LayoutParams params2 = (TableRow.LayoutParams)tv.getLayoutParams();
                    params2.span = 2;
                    tv.setLayoutParams(params2);
                }
            }


            return rootView;
        }



//    @Override
//    /** Inflate the menu; this adds items to the action bar if it is present */
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_schedule, menu);
//        return true;
//    }


    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent returnHome = new Intent(getBaseContext(), MainActivity.class);
//        startActivity(returnHome);
//        finish();
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public void onBackPressed() {
//        Intent returnHome = new Intent(getBaseContext(), MainActivity.class);
//        startActivity(returnHome);
//        finish();
//    }
}