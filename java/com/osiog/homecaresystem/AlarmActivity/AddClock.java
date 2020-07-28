package com.osiog.homecaresystem.AlarmActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class AddClock extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private final String PERMISSION_WRITE_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.z_addclock);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //開啟回復上一頁功能
//        getSupportActionBar().setTitle("用藥提醒");

       InitialComponent();//基本初值
        //StartCamera();//開啟相機
    }


    /**
     * Button_時間選擇按鍵
     */
    private View.OnClickListener btn_AddTimeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
            new TimePickerDialog(AddClock.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            tv_AddTime.setText((hourOfDay > 12 ? hourOfDay - 12 : hourOfDay) + ":"
                                    + minute + " " + (hourOfDay > 12 ? "PM" : "AM"));
                        }
                    }, hour, minute, true).show();
        }
    };


    /**
     * METHOD_實作按鈕_(拍攝項目)開啟相機功能
     */
    ImageButton.OnClickListener btn_GoCameraListener =
            new ImageButton.OnClickListener() {
                public void onClick(View v) {

                    picOK = 1;
                    Intent intent = getPackageManager().getLaunchIntentForPackage("net.sourceforge.opencamera");
                    startActivity(intent);
                }
            };

//    /**
//     * METHOD_實作按鈕_(拍攝項目)開啟相機功能
//     */
//    public void StartCamera() {
//        btn_GoCamera.setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View v) {
//
//                        picOK = 1;
//                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.android.camera2");
//                        startActivity(intent);
//                    }
//                });
//    }


    /**
     * METHOD_按鈕方法_瀏覽圖片
     */
    ImageButton.OnClickListener btn_GoPicListener =
            new ImageButton.OnClickListener() {
                public void onClick(View v) {

                    picOK = 2;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);

                }
            };

    /**
     * METHOD_按鈕方法_網路圖片
     */
    ImageButton.OnClickListener btn_GoWebListener =
            new ImageButton.OnClickListener() {
                public void onClick(View v) {

                    picOK = 3;
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                    startActivity(intent);

                }
            };


    /**
     * 展示照片/圖片方法
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if (resultCode == RESULT_OK) {

//            /**
//             * METHOD_置放相機照片-> 拍照結果
//             */
//            if (picOK == 1) {
//                uri = data.getData();
//                Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");
//                imageView.setImageBitmap(bitmap1);
//                super.onActivityResult(requestCode, resultCode, data);
//            }

            /**
             * METHOD_瀏覽圖片-> 瀏覽圖結果
             */
            if (picOK == 2) {
                uri = data.getData();
                Log.e("uri", uri.toString());
                ContentResolver cr = this.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    encodedImage = encodeImageToString(bitmap);

                    String pillcode = encodedImage.toString();
                    String pilluri = uri.toString();
//                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(), e);
                }
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * 將圖片轉換成bitmap並編碼
     */
    private String encodeImageToString(Bitmap bitmap) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        int quality = 100; //100: compress nothing
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bao);

        byte[] imgbytes = bao.toByteArray();
        String encodedImage = Base64.encodeToString(imgbytes, Base64.NO_PADDING);
        return encodedImage;
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
//        Intent returnHome = new Intent(getBaseContext(), MedicationRemindFragment.class);
//        startActivity(returnHome);
        finish();
        return super.onOptionsItemSelected(item);
    }


    /**
     * BUTTON_實作按鈕_(設定取消)回主選單功能
     */
    private Button.OnClickListener btn_cancel_alarmListner = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


    /**
     * BUTTON_實作按鈕_(創建鬧鈴)回主選單功能
     */
    private Button.OnClickListener btn_set_alarmListner = new Button.OnClickListener() {
        public Context context;

        @Override
        public void onClick(View v) {

            // pic_code = encodedImage.toString();

            //pic_uri = uri.toString();
            pic_uri="";
            pic_code="";


            String time = tv_AddTime.getText().toString();
            //String days = et_days.getText().toString();
            String weeks = dayOfWeekList.toString();
            pill_name = et_Item.getText().toString();


            if (et_Item.getText().toString().matches("")) {
                Toast message = Toast.makeText(
                        AddClock.this,
                        "欄位不能有空值",
                        Toast.LENGTH_SHORT);
                message.show();
            } else {
                if (pic_uri == "" && pic_code == "") {

                    /** Updating model */
                    Alarm alarm = new Alarm();

                    /** If Pill does not already exist */
                    if (!pillBox.pillExist(getApplicationContext(), pill_name)) {
                        Pill pill = new Pill();
                        pill.setPillName(pill_name);
                        pill.setPillUri("");
                        pill.setPillCode("");
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

                            /** Input form is not completely filled out */
                            if (checkBoxCounter == 0)
                                Toast.makeText(getBaseContext(), "星期請至少勾選一天", Toast.LENGTH_SHORT).show();
                            else {

                                // This intent invokes the activity AlertActivity, which in turn opens the AlertAlarm window
                                Intent intent = new Intent(getBaseContext(), AlertActivity.class);
                                intent.putExtra("pill_name", pill_name);

                                literal_operation = PendingIntent.getActivity(getBaseContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
                                        alarmManager.INTERVAL_DAY * 7, literal_operation);
                            }
                        }
                    }

                    if (checkBoxCounter == 0 || et_Item.getText().toString().matches("")) {
                        /** Input form is not completely filled out */
                        if (checkBoxCounter == 0)
                            Toast.makeText(getBaseContext(), "星期請至少勾選一天", Toast.LENGTH_SHORT).show();
                        else if (et_Item.getText().toString().matches("")) {
                            Toast message = Toast.makeText(
                                    AddClock.this,
                                    "欄位不能有空值",
                                    Toast.LENGTH_SHORT);
                            message.show();
                        }
                    } else {


//                        //活動建立成功吐司訊息
//                        Toast message = Toast.makeText(
//                                AddClock.this,
//                                pill_name + " 鬧鈴設置成功!",
//                                Toast.LENGTH_SHORT);
//                        message.show();

                        Intent intent = new Intent();
                        intent.setClass(AddClock.this, MainClock.class);
                        startActivity(intent);
                        AddClock.this.finish();
                        finish();

                    }
                } else
                    Toast.makeText(getBaseContext(),
                            "請放上一張提醒圖片", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private void InitialComponent() {

        helper = new AlarmDBHelper(this, "expense.db", null, 1);

//        btn_GoPic = (ImageButton) findViewById(R.id.btn_GoPic);
//        btn_GoPic.setOnClickListener(btn_GoPicListener);

//        btn_GoCamera = (ImageButton) findViewById(R.id.btn_GoCamera);
//        btn_GoCamera.setOnClickListener(btn_GoCameraListener);

//        btn_GoWeb = (ImageButton) findViewById(R.id.btn_GoWeb);
//        btn_GoWeb.setOnClickListener(btn_GoWebListener);

        btn_set_alarm = (Button) findViewById(R.id.btn_set_alarm);
        btn_set_alarm.setOnClickListener(btn_set_alarmListner);

        btn_cancel_alarm = (Button) findViewById(R.id.btn_cancel_alarm);
        btn_cancel_alarm.setOnClickListener(btn_cancel_alarmListner);

        tv_AddTime = (TextView) findViewById(R.id.tv_AddTime);
        et_Item = (EditText) findViewById(R.id.et_Item);
        //et_days = (EditText) findViewById(R.id.et_days);
        pill_name = et_Item.getText().toString();
        this.context = this;


        // 將時間字串設為當前時間
        Calendar rightNow = Calendar.getInstance();
        hour = rightNow.get(Calendar.HOUR_OF_DAY);
        minute = rightNow.get(Calendar.MINUTE);
        tv_AddTime.setText(setTime(hour, minute));

        tv_AddTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(AddClock.this,
                        //R.style.Theme_AppCompat_Dialog,
                        t,
                        hour,
                        minute,
                        false).show();
            }
        });
        tv_AddTime.setText(setTime(hour, minute));

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
    ImageButton btn_GoCamera;
//    Uri outputFileUri;
//    private ImageView mImg;
//    private DisplayMetrics mPhone;
//    private final static int CAMERA = 66;
//    private final static int PHOTO = 99;
    Uri uri = Uri.parse("");
    String encodeString;
    String encodedImage;
    String pic_uri;
    String pic_code;


    //ImageView檢查變數
    int picOK = 0;

    //鬧鈴功能
    private AlarmDBHelper helper;
    private AlarmManager alarmManager;
    private PendingIntent literal_operation;
    private PendingIntent sound_operation;
    PillBox pillBox = new PillBox();
    final Calendar c = Calendar.getInstance();
    int hour;
    int minute;
    private TextView tv_AddTime;
    private EditText et_Item;
    private EditText et_days;
    private Button btn_set_alarm;
    private Button btn_cancel_alarm;
    private ImageButton btn_GoPic;
    private ImageButton btn_GoWeb;
    String pill_name;
    Context context;


}

