package com.osiog.homecaresystem.AlarmActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.osiog.homecaresystem.AlarmActivity.AlarmModel.Alarm;
import com.osiog.homecaresystem.AlarmActivity.AlarmModel.AlarmDBHelper;
import com.osiog.homecaresystem.AlarmActivity.AlarmModel.Pill;
import com.osiog.homecaresystem.AlarmActivity.AlarmModel.PillBox;
import com.osiog.homecaresystem.R;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class EditClock extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_editclock);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //開啟回復上一頁功能
//        getSupportActionBar().setTitle("用藥鬧鈴");

        InitialComponent();//基本初值
//        onDecodeClicked();//還原初設圖片
    }


    //標頭_刪除按鈕
    @Override
    /** Inflate the menu; this adds items to the action bar if it is present */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menumain, menu);
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

        //刪除按鈕實作
        if (id == R.id.action_delete) {
            //pillBox.destory(getApplicationContext());


            AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle("我要刪除一筆鬧鈴");
            isExit.setMessage("確定要刪除嗎");
            isExit.setButton("確定", quitlistener);
            isExit.setButton2("取消", quitlistener);
            isExit.show();
            return true;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //過兩秒後要做的事情
                Log.d("tag", "我要離開了");

                Intent intent = new Intent();
                intent.setClass(EditClock.this, MainClock.class);
                startActivity(intent);
                finish();

            }
        }, 0);
        return super.onOptionsItemSelected(item);

    }


    DialogInterface.OnClickListener quitlistener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {

                case AlertDialog.BUTTON_POSITIVE:
                    for (long alarmID : tempIds) {
                        pillBox.deleteAlarm(getApplicationContext(), alarmID);

                        Intent intent = new Intent(getBaseContext(), AlertActivity.class);
                        PendingIntent operation = PendingIntent.getActivity(getBaseContext(), (int) alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(operation);
                    }

                    // Delete the pill if there is no alarm for it
                    try {
                        List<Alarm> tempTracker = pillBox.getAlarmByPill(getBaseContext(), tempPill_name);
                        if (tempTracker.size() == 0)
                            pillBox.deletePill(getBaseContext(), tempPill_name);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getBaseContext(), "鬧鈴：" + tempPill_name + "_已刪除成功！！", Toast.LENGTH_SHORT).show();

                    Intent returnPillBox = new Intent(getBaseContext(), MainClock.class);
                    startActivity(returnPillBox);
                    finish();

                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Button_時間選擇按鍵
     */
    private View.OnClickListener btn_AddTimeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
            new TimePickerDialog(EditClock.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            tv_AddTime.setText((hourOfDay > 12 ? hourOfDay - 12 : hourOfDay) + ":"
                                    + minute + " " + (hourOfDay > 12 ? "PM" : "AM"));
                        }
                    }, hour, minute, true).show();
        }
    };



    /**
     * 展示照片/圖片方法
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * METHOD_置放相機照片-> 顯示拍照結果方法
         */
        if (resultCode == RESULT_OK) {

            if (picOK == 1) {
                super.onActivityResult(requestCode, resultCode, data);
                Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap1);
            }

        }

    }


    private void encode(String path) {
        //decode to bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Log.d("TAG", "bitmap width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
        //convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        //base64 encode
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        String encodeString = new String(encode);
    }

    /**
     * 3.將大串還原成圖片
     */

    public void onDecodeClicked() {


        //顯示初設圖片編碼

        Pill firstPill = pillBox.getPillByName(getApplicationContext(), tempPill_name);
        String pillcode = firstPill.getPillCode();

//        imageView = (ImageView) findViewById(R.id.imageView);
        final String pureBase64Encoded = pillcode.substring(pillcode.indexOf(",") + 1);
        byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

//        imageView.setImageBitmap(bitmap);


    }

    private void saveBitmap(Bitmap bitmap) {
        try {
            String path = Environment.getExternalStorageDirectory().getPath()
                    + "/decodeImage.jpg";
            Log.d("linc", "path is " + path);
            OutputStream stream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            stream.close();
            Log.e("linc", "jpg okay!");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("linc", "failed: " + e.getMessage());
        }
    }


    /**
     * CHECKBOX_勾選框實作_服藥時間(星期)
     */
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        /** Checking which checkbox was clicked */
        switch (view.getId()) {
            case R.id.checkbox_monday:
                if (checked)
                    dayOfWeekList[1] = true;
                else
                    dayOfWeekList[1] = false;
                break;
            case R.id.checkbox_tuesday:
                if (checked)
                    dayOfWeekList[2] = true;
                else
                    dayOfWeekList[2] = false;
                break;
            case R.id.checkbox_wednesday:
                if (checked)
                    dayOfWeekList[3] = true;
                else
                    dayOfWeekList[3] = false;
                break;
            case R.id.checkbox_thursday:
                if (checked)
                    dayOfWeekList[4] = true;
                else
                    dayOfWeekList[4] = false;
                break;
            case R.id.checkbox_friday:
                if (checked)
                    dayOfWeekList[5] = true;
                else
                    dayOfWeekList[5] = false;
                break;
            case R.id.checkbox_saturday:
                if (checked)
                    dayOfWeekList[6] = true;
                else
                    dayOfWeekList[6] = false;
                break;
            case R.id.checkbox_sunday:
                if (checked)
                    dayOfWeekList[0] = true;
                else
                    dayOfWeekList[0] = false;
                break;
            case R.id.every_day:
                LinearLayout ll = (LinearLayout) findViewById(R.id.checkbox_layout);
                for (int i = 0; i < ll.getChildCount(); i++) {
                    View v = ll.getChildAt(i);
                    ((CheckBox) v).setChecked(checked);
                    onCheckboxClicked(v);
                }
                break;
        }
    }



    /**
     * BUTTON_實作按鈕_(追加鬧鈴)回主選單功能
     */
    private Button.OnClickListener btn_cancel_alarmListner = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            Pill firstPill = pillBox.getPillByName(getApplicationContext(), tempPill_name);
            String PillUri = firstPill.getPillUri();
            String Pillcode = firstPill.getPillCode();

            pill_name = et_Item.getText().toString();

            /** Updating model */
            Alarm alarm = new Alarm();

            /** If Pill does not already exist */
            if (!pillBox.pillExist(getApplicationContext(), pill_name)) {
                Pill pill = new Pill();
                pill.setPillName(pill_name);
                pill.setPillUri(PillUri);
                pill.setPillCode(Pillcode);
                alarm.setHour(hour);
                alarm.setMinute(minute);
                alarm.setPillName(pill_name);
                alarm.setDayOfWeek(dayOfWeekList);
                pill.addAlarm(alarm);
                long pillId = pillBox.addPill(getApplicationContext(), pill);
                pill.setPillId(pillId);
                pillBox.addAlarm(getApplicationContext(), alarm, pill);
            } else { // If Pill already exists
                Pill pill = pillBox.getPillByName(getApplicationContext(), pill_name);
                alarm.setHour(hour);
                alarm.setMinute(minute);
                alarm.setPillName(pill_name);
                alarm.setDayOfWeek(dayOfWeekList);
                pill.addAlarm(alarm);
                pillBox.addAlarm(getApplicationContext(), alarm, pill);
            }


            List<Long> ids = new LinkedList<Long>();
            try {
                List<Alarm> alarms = pillBox.getAlarmByPill(getApplicationContext(), pill_name);
                for (Alarm tempAlarm : alarms) {
                    if (tempAlarm.getHour() == hour && tempAlarm.getMinute() == minute) {
                        ids = tempAlarm.getIds();
                        break;
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }



            int checkBoxCounter = 0;
            for (int i = 0; i < 7; i++) {
                if (dayOfWeekList[i] && et_Item.length() != 0) {

                    int dayOfWeek = i + 1;

                    long _id = ids.get(checkBoxCounter);
                    int id = (int) _id;
                    checkBoxCounter++;

                    // This intent invokes the activity AlertActivity, which in turn opens the AlertAlarm window
                    Intent intent = new Intent(getBaseContext(), AlertActivity.class);
                    intent.putExtra("pill_name", pill_name);

                    operation = PendingIntent.getActivity(getBaseContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Getting a reference to the System Service ALARM_SERVICE
                    alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

                    // Creating a calendar object corresponding to the date and time set by the user
                    Calendar calendar = Calendar.getInstance();

                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                    // Converting the date and time in to milliseconds elapsed since epoch
                    long alarm_time = calendar.getTimeInMillis();

                    if (calendar.before(Calendar.getInstance()))
                        alarm_time += AlarmManager.INTERVAL_DAY * 7;

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm_time,
                            alarmManager.INTERVAL_DAY * 7, operation);
                }
            }
            /** Input form is not completely filled out */
            if (checkBoxCounter == 0)
                Toast.makeText(getBaseContext(), "星期請至少勾選一天", Toast.LENGTH_SHORT).show();
            else if (et_Item.getText().toString().matches("")) {
                Toast message = Toast.makeText(
                        EditClock.this,
                        "欄位不能有空值",
                        Toast.LENGTH_SHORT);
                message.show();
            } else { // Input form is completely filled out



                Toast message = Toast.makeText(
                        EditClock.this,
                        pill_name + " 鬧鈴設置成功!",
                        Toast.LENGTH_SHORT);
                message.show();

                Intent intent = new Intent();
                intent.setClass(EditClock.this, MainClock.class);
                startActivity(intent);
                EditClock.this.finish();

            }
        }
    };


    /**
     * BUTTON_實作按鈕_(修改鬧鈴)回主選單功能
     */
    private Button.OnClickListener btn_set_alarmListner = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            Pill firstPill = pillBox.getPillByName(getApplicationContext(), tempPill_name);
            String PillUri = firstPill.getPillUri();
            String Pillcode = firstPill.getPillCode();

            pill_name = et_Item.getText().toString();

            /** Updating model */
            Alarm alarm = new Alarm();

            /** If Pill does not already exist */
            if (!pillBox.pillExist(getApplicationContext(), pill_name)) {
                Pill pill = new Pill();
                pill.setPillName(pill_name);
                pill.setPillUri(PillUri);
                pill.setPillCode(Pillcode);
                alarm.setHour(hour);
                alarm.setMinute(minute);
                alarm.setPillName(pill_name);
                alarm.setDayOfWeek(dayOfWeekList);
                pill.addAlarm(alarm);
                long pillId = pillBox.addPill(getApplicationContext(), pill);
                pill.setPillId(pillId);
                pillBox.addAlarm(getApplicationContext(), alarm, pill);
            } else { // If Pill already exists
                Pill pill = pillBox.getPillByName(getApplicationContext(), pill_name);
                alarm.setHour(hour);
                alarm.setMinute(minute);
                alarm.setPillName(pill_name);
                alarm.setDayOfWeek(dayOfWeekList);
                pill.addAlarm(alarm);
                pillBox.addAlarm(getApplicationContext(), alarm, pill);
            }

            for (long alarmID : tempIds) {
                pillBox.deleteAlarm(getApplicationContext(), alarmID);

                Intent intent = new Intent(getBaseContext(), AlertActivity.class);
                PendingIntent operation = PendingIntent.getActivity(getBaseContext(), (int) alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);
                alarmManager.cancel(operation);
            }

            // Delete the pill if there is no alarm for it
            try {
                List<Alarm> tempTracker = pillBox.getAlarmByPill(getBaseContext(), tempPill_name);
                if (tempTracker.size() == 0)
                    pillBox.deletePill(getBaseContext(), tempPill_name);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            List<Long> ids = new LinkedList<Long>();
            try {
                List<Alarm> alarms = pillBox.getAlarmByPill(getApplicationContext(), pill_name);
                for (Alarm tempAlarm : alarms) {
                    if (tempAlarm.getHour() == hour && tempAlarm.getMinute() == minute) {
                        ids = tempAlarm.getIds();
                        break;
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }



            int checkBoxCounter = 0;
            for (int i = 0; i < 7; i++) {
                if (dayOfWeekList[i] && et_Item.length() != 0) {

                    int dayOfWeek = i + 1;

                    long _id = ids.get(checkBoxCounter);
                    int id = (int) _id;
                    checkBoxCounter++;

                    // This intent invokes the activity AlertActivity, which in turn opens the AlertAlarm window
                    Intent intent = new Intent(getBaseContext(), AlertActivity.class);
                    intent.putExtra("pill_name", pill_name);

                    operation = PendingIntent.getActivity(getBaseContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Getting a reference to the System Service ALARM_SERVICE
                    alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

                    // Creating a calendar object corresponding to the date and time set by the user
                    Calendar calendar = Calendar.getInstance();

                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                    // Converting the date and time in to milliseconds elapsed since epoch
                    long alarm_time = calendar.getTimeInMillis();

                    if (calendar.before(Calendar.getInstance()))
                        alarm_time += AlarmManager.INTERVAL_DAY * 7;

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm_time,
                            alarmManager.INTERVAL_DAY * 7, operation);
                }
            }
            /** Input form is not completely filled out */
            if (checkBoxCounter == 0)
                Toast.makeText(getBaseContext(), "星期請至少勾選一天", Toast.LENGTH_SHORT).show();
            else if (et_Item.getText().toString().matches("")) {
                Toast message = Toast.makeText(
                        EditClock.this,
                        "欄位不能有空值",
                        Toast.LENGTH_SHORT);
                message.show();
            } else { // Input form is completely filled out




                //活動建立成功吐司訊息
//                Toast message = Toast.makeText(
//                        EditClock.this,
//                        pill_name + " 鬧鈴設置成功!",
//                        Toast.LENGTH_SHORT);
//                message.show();

                Intent intent = new Intent();
                intent.setClass(EditClock.this, MainClock.class);
                startActivity(intent);
                EditClock.this.finish();

            }

        }

    };


    private void InitialComponent() {

        helper = new AlarmDBHelper(this, "expense.db", null, 1);
        btn_set_alarm = (Button) findViewById(R.id.btn_set_alarm);
        btn_set_alarm.setOnClickListener(btn_set_alarmListner);
        btn_cancel_alarm = (Button) findViewById(R.id.btn_cancel_alarm);
        btn_cancel_alarm.setOnClickListener(btn_cancel_alarmListner);
        tv_AddTime = (TextView) findViewById(R.id.tv_AddTime);
        et_Item = (EditText) findViewById(R.id.et_Item);
         pill_name = et_Item.getText().toString();
//        imageView = (ImageView) findViewById(R.id.imageView);


        Alarm firstAlarm = null;
        try {

            //同步初設時間
            firstAlarm = pillBox.getAlarmById(getApplicationContext(), tempIds.get(0));
            hour = firstAlarm.getHour();
            minute = firstAlarm.getMinute();
            tv_AddTime.setText(setTime(hour, minute));

            //將本項目註冊為暫時藥名
            pillBox.setTempName(firstAlarm.getPillName());

            tv_AddTime.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new TimePickerDialog(EditClock.this,
                            //R.style.Theme_AppCompat_Dialog,
                            t,
                            hour,
                            minute,
                            false).show();
                }
            });
            tv_AddTime.setText(setTime(hour, minute));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //顯示初設藥名
        EditText editText = (EditText) findViewById(R.id.et_Item);
        tempPill_name = pillBox.getTempName();
        editText.setText(tempPill_name);

        //顯示初設星期
        for (Long id : tempIds) {
            try {
                int day = pillBox.getDayOfWeek(getApplicationContext(), id);
                CheckBox checkBoxMon = (CheckBox) findViewById(R.id.checkbox_monday);
                CheckBox checkBoxTues = (CheckBox) findViewById(R.id.checkbox_tuesday);
                CheckBox checkBoxWed = (CheckBox) findViewById(R.id.checkbox_wednesday);
                CheckBox checkBoxThur = (CheckBox) findViewById(R.id.checkbox_thursday);
                CheckBox checkBoxFri = (CheckBox) findViewById(R.id.checkbox_friday);
                CheckBox checkBoxSat = (CheckBox) findViewById(R.id.checkbox_saturday);
                CheckBox checkBoxSun = (CheckBox) findViewById(R.id.checkbox_sunday);
                if (day == 2) {
                    checkBoxMon.setChecked(true);
                    dayOfWeekList[1] = true;
                } else if (day == 3) {
                    checkBoxTues.setChecked(true);
                    dayOfWeekList[2] = true;
                } else if (day == 4) {
                    checkBoxWed.setChecked(true);
                    dayOfWeekList[3] = true;
                } else if (day == 5) {
                    checkBoxThur.setChecked(true);
                    dayOfWeekList[4] = true;
                } else if (day == 6) {
                    checkBoxFri.setChecked(true);
                    dayOfWeekList[5] = true;
                } else if (day == 7) {
                    checkBoxSat.setChecked(true);
                    dayOfWeekList[6] = true;
                } else if (day == 1) {
                    checkBoxSun.setChecked(true);
                    dayOfWeekList[0] = true;
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 時間設定觸發事件
     */
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minuteOfHour) {
            hour = hourOfDay;
            minute = minuteOfHour;
            tv_AddTime.setText(setTime(hour, minute));
        }
    };


    /**
     * 將所選時間以00:00 AM/PM 回傳於字串
     */
    public String setTime(int hour, int minute) {
        String am_pm = (hour < 12) ? " AM" : " PM";
        int nonMilitaryHour = hour % 12;
        if (nonMilitaryHour == 0)
            nonMilitaryHour = 12;
        String minuteWithZero;
        if (minute < 10)
            minuteWithZero = "0" + minute;
        else
            minuteWithZero = "" + minute;
        return nonMilitaryHour + ":" + minuteWithZero + am_pm;
    }


    //星期勾選框
    private boolean dayOfWeekList[] = new boolean[7];

    //相機功能
    private ImageView mImg;
    private DisplayMetrics mPhone;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    Uri uri;

    //ImageView檢查變數
    int picOK = 0;

    //鬧鈴功能
    private AlarmDBHelper helper;
    private AlarmManager alarmManager;
    private PendingIntent operation;
    PillBox pillBox = new PillBox();
    List<Long> tempIds = pillBox.getTempIds();
    String tempPill_name;
    String pill_name;
    ImageView imageView;

    final Calendar c = Calendar.getInstance();
    int hour;
    int minute;
    private TextView tv_AddTime;
    private EditText et_Item;
    private EditText et_days;
    private Button btn_set_alarm;
    private Button btn_cancel_alarm;


}


