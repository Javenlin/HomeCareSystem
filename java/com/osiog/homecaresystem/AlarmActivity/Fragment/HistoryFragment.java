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

import com.osiog.homecaresystem.AlarmActivity.AlarmModel.History;
import com.osiog.homecaresystem.AlarmActivity.AlarmModel.PillBox;
import com.osiog.homecaresystem.R;

/**
 * This fragment is based on the code at
 * http://www.feelzdroid.com/2014/10/android-action-bar-tabs-swipe-views.html
 *
 * This fragment handles the view and controller of the history tab on home screen
 */

public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.z_fragment_history, container, false);

        TableLayout stk = (TableLayout) rootView.findViewById(R.id.table_history);

        TableRow tbrow0 = new TableRow(container.getContext());

        int color = getResources().getColor(R.color.blue600);

        TextView tt1 = new TextView(container.getContext());
        tt1.setText("名稱");
        tt1.setTextColor(Color.WHITE);
        tt1.setGravity(Gravity.CENTER);
        tt1.setTypeface(null, Typeface.BOLD);
        tt1.setTextSize(25);
        tt1.setBackgroundColor(color);
        tbrow0.addView(tt1);

        TextView tt2 = new TextView(container.getContext());
        tt2.setText("日期");
        tt2.setTextColor(Color.WHITE);
        tt2.setGravity(Gravity.CENTER);
        tt2.setTypeface(null, Typeface.BOLD);
        tt2.setTextSize(25);
        tt2.setBackgroundColor(color);
        tbrow0.addView(tt2);

        TextView tt3 = new TextView(container.getContext());
        tt3.setText("時間");
        tt3.setTextColor(Color.WHITE);
        tt3.setGravity(Gravity.CENTER);
        tt3.setTypeface(null, Typeface.BOLD);
        tt3.setTextSize(25);
        tt3.setBackgroundColor(color);
        tbrow0.addView(tt3);

        stk.addView(tbrow0);

        PillBox pillBox = new PillBox();

        for (History history: pillBox.getHistory(container.getContext())){
            TableRow tbrow = new TableRow(container.getContext());

            TextView t1v = new TextView(container.getContext());
            t1v.setText(history.getPillName());
            t1v.setTextColor(Color.DKGRAY);
            t1v.setGravity(Gravity.CENTER);
            t1v.setPadding(30, 30, 30, 30);
            t1v.setMaxEms(4);
            t1v.setTypeface(null, Typeface.BOLD);
            t1v.setTextSize(28);
            tbrow.addView(t1v);

            TextView t2v = new TextView(container.getContext());
            String date = history.getDateString();
            t2v.setText(date);
            t2v.setTextColor(Color.DKGRAY);
            t2v.setGravity(Gravity.CENTER);
            t2v.setPadding(30, 30, 30, 30);
            t2v.setTextSize(20);
            tbrow.addView(t2v);

            TextView t3v = new TextView(container.getContext());

            int nonMilitaryHour = history.getHourTaken() % 12;
            if (nonMilitaryHour == 0)
                nonMilitaryHour = 12;

            String minute;
            if (history.getMinuteTaken() < 10)
                minute = "0" + history.getMinuteTaken();
            else
                minute = "" + history.getMinuteTaken();

            String time = nonMilitaryHour + ":" + minute + " " + history.getAm_pmTaken();
            t3v.setText(time);
            //t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.CENTER);
            t3v.setTextSize(20);
            tbrow.addView(t3v);

            stk.addView(tbrow);
         }
        return rootView;
    }
}