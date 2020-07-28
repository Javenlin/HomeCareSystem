package com.osiog.homecaresystem.AlarmActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.osiog.homecaresystem.AlarmActivity.AlarmModel.History;
import com.osiog.homecaresystem.AlarmActivity.AlarmModel.Pill;
import com.osiog.homecaresystem.AlarmActivity.AlarmModel.PillBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*import pillapp.Model.History;
import pillapp.Model.Pill;
import pillapp.Model.PillBox;*/

/**
 * Utilized the link below as a reference guide:
 * http://wptrafficanalyzer.in/blog/setting-up-alarm-using-alarmmanager-and-waking-up-screen-and-unlocking-keypad-on-alarm-goes-off-in-android/
 *
 * This activity handles the view and controller of the alert page, which contains
 * a dialog fragment AlertAlarm that shows the dialog box to let the user respond to an alarm.
 * This is the "notification" we are using right now. But it only contains a dialog box so it is
 * not a real notification. We can change this to a real notification that has a ringtone or a
 * vibrating function in the future.
 */

public class AlertActivity extends FragmentActivity {

    private AlarmManager alarmManager;
    private PendingIntent literal_operation;
    private PendingIntent sound_operation;
    Context context;

    public void onAttach(Activity activity){
       // this.activity = (MainActivity) activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Creating an Alert Dialog Window */
        AlertAlarm alert = new AlertAlarm();

        /** Opening the Alert Dialog Window. This will be opened when the alarm goes off */
        alert.show(getSupportFragmentManager(), "AlertAlarm");

     }

    // Snooze
    public void doNeutralClick(String pillName){
       // STOP();
        final int _id = (int) System.currentTimeMillis();
        final long minute = 1000;
        long snoozeLength = 3;
        long currTime = System.currentTimeMillis();
        long min = currTime + minute * snoozeLength;

        Intent intent = new Intent(getBaseContext(), AlertActivity.class);
        intent.putExtra("pill_name", pillName);

        literal_operation = PendingIntent.getActivity(getBaseContext(), _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /** Getting a reference to the System Service ALARM_SERVICE */
        alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, min, literal_operation);
        Toast.makeText(getBaseContext(), "鬧鈴 " + pillName + " 將於３秒鐘後提醒", Toast.LENGTH_LONG).show();

        finish();

    }

    // I took it
    public void doPositiveClick(String pillName){
       // STOP();
        PillBox pillBox = new PillBox();
        Pill pill = pillBox.getPillByName(this, pillName);
        History history = new History();

        Calendar takeTime = Calendar.getInstance();
        Date date = takeTime.getTime();
        String dateString = new SimpleDateFormat("M月d日Y年").format(date);

        int hour = takeTime.get(Calendar.HOUR_OF_DAY);
        int minute = takeTime.get(Calendar.MINUTE);
        String am_pm = (hour < 12) ? "am" : "pm";

        history.setHourTaken(hour);
        history.setMinuteTaken(minute);
        history.setDateString(dateString);
        history.setPillName(pillName);

        pillBox.addToHistory(this, history);

        String stringMinute;
        if (minute < 10)
            stringMinute = "0" + minute;
        else
            stringMinute = "" + minute;

        int nonMilitaryHour = hour % 12;
        if (nonMilitaryHour == 0)
            nonMilitaryHour = 12;

//        Toast.makeText(getBaseContext(),  pillName + "　已經服用完畢! \n\n時間為 "+ nonMilitaryHour + ":" + stringMinute + " " + am_pm + ".", Toast.LENGTH_LONG).show();


//        Intent returnHistory = new Intent(getBaseContext(), HealthActivity.class);
//        startActivity(returnHistory);
        finish();
    }

    // I won't take it
    public void doNegativeClick(){
        //STOP();
        Toast.makeText(getBaseContext(),"請盡量準時服藥!!", Toast.LENGTH_LONG).show();
        finish();
    }

//    private void STOP() {
//        this.context  = context;
//
//        //final Intent Intent2 = new Intent(context, AlarmReceiver.class);
//
//        final Intent Intent2 = new Intent(activity, AlarmReceiver.class);
//
//
//        int min = 1;
//        int max = 9;
//
//        Random r = new Random();
//        int random_number = r.nextInt(max - min + 1) + min;
//        Log.e("random number is ", String.valueOf(random_number));
//
//        Intent2.putExtra("extra", "no");
//        sendBroadcast(Intent2);
//
//        alarmManager.cancel(sound_operation);
//        //setAlarmText("Alarm canceled");
//        //setAlarmText("You clicked a " + " canceled");}
//
//    }
//    public MainActivity activity;

}