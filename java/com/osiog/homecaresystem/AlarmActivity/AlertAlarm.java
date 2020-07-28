package com.osiog.homecaresystem.AlarmActivity;

/**
 * Created by CharlesPK3 on 3/21/15.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager.LayoutParams;

/**
 * Utilized the link below as a reference guide:
 * http://wptrafficanalyzer.in/blog/setting-up-alarm-using-alarmmanager-and-waking-up-screen-and-unlocking-keypad-on-alarm-goes-off-in-android/
 *
 * This is a dialog box that AlertActivity called when it is triggered.
 * It contains three buttons to let the user respond to an alarm.
 */

public class AlertAlarm extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /** Turn Screen On and Unlock the keypad when this alert dialog is displayed */
        getActivity().getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD);

        /** Creating a alert dialog builder */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        /** Setting title for the alert dialog */
        builder.setTitle("用藥提醒");

        /** Making it so notification can only go away by pressing the buttons */
        setCancelable(false);

        final String pill_name = getActivity().getIntent().getStringExtra("pill_name");

        builder.setMessage("請記得服用 "+ pill_name +" !!!");

        builder.setPositiveButton("確認", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                AlertActivity act = (AlertActivity)getActivity();
//                act.doPositiveClick(pill_name);
                getActivity().finish();
            }
        });

//        builder.setNeutralButton("稍等!", new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                /** Exit application on click OK */
//                AlertActivity act = (AlertActivity)getActivity();
//                act.doNeutralClick(pill_name);
//                getActivity().finish();
//            }
//        });
//
//        builder.setNegativeButton("不服用!", new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                /** Exit application on click OK */
//                AlertActivity act = (AlertActivity)getActivity();
//                act.doNegativeClick();
//                getActivity().finish();
//            }
//        });

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}