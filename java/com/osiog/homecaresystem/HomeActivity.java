package com.osiog.homecaresystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.osiog.homecaresystem.AlarmActivity.MainClock;
import com.osiog.homecaresystem.Retrofit.IHomeCareAPI;
import com.osiog.homecaresystem.Utils.Common;
import com.osiog.homecaresystem.Utils.ProgressRequestBody;
import com.osiog.homecaresystem.Utils.UploadCallBack;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UploadCallBack, View.OnClickListener {

    //COMPONENT
    private ImageView btn_goto_clock;

    private ImageView img_nav_avatar;
    private TextView txt_nav_name;
    private TextView txt_nav_email;
    private TextView txt_nav_state;

    //FUNCTION
    private Context context = HomeActivity.this;   //INITIAL_CONTEXT;
    private Uri selectedFileUri;
    private IHomeCareAPI mService;

    private NavigationView navigationView;
    private DrawerLayout drawer;

    private TabLayout MainTablayout;
    private ViewPager MainViewPager;


    //VAR
    private String id;
    private String link;
    private String time;
    private String name;
    private String email;
    private String gender;
    private String birthday;
    private String picture;
    private String type;
    private String phone;

    private void INIT_COMPONENT() {
        //用藥提醒 - 單機版
        btn_goto_clock = (ImageView) findViewById(R.id.btn_goto_clock);
        btn_goto_clock.setOnClickListener(this);

        //TAB滑動面
        //tab_mailbox = (RelativeLayout) findViewById(R.id.tab_mailbox);
        //tab_chatroom = (RelativeLayout) findViewById(R.id.tab_chatroom);
        //tab_recVideo = (RelativeLayout) view.findViewById(R.id.tab_recVideo);
        //tab_findUser = (RelativeLayout) findViewById(R.id.tab_findUser);
        //tab_aboutMe = (RelativeLayout) view.findViewById(R.id.tab_aboutMe);

        //切換列
        MainTablayout = (TabLayout) findViewById(R.id.MainTablayout);
        MainViewPager = (ViewPager) findViewById(R.id.MainViewPager);
    }

    //== == == == == == ==
    //  BUTTON_CLICK_ZONE
    //== == == == == == ==
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //-- -- -- -- -- --
            //  前往用藥提醒系統
            //-- -- -- -- -- --
            case R.id.btn_goto_clock:
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, MainClock.class);
                startActivity(intent);
                break;

//            case R.id.SOMETHING:
//                //DO SOMETHING
//                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = HomeActivity.this;   //INITIAL_CONTEXT

        GET_USER_INFO();
        INIT_COMPONENT();        //INITIAL_COMPONENT
        INIT_LAYOUT_DRAWER();    //INITIAL_DRAWER
        INIT_LAYOUT_TAB();       //INITIAL_DRAWER

        Log.e("THIS_USERINFO_DATA", "【 id: " + id + " 】 / 【 time: " + time + " 】 / 【 name: " + name + " 】 / 【 email: " + email + " 】 / 【 picture: " + picture + " 】 / 【 type: " + type + " 】");

        if(name!= null && email!= null && type!= null) {
            if (!picture.isEmpty()) {
                Glide.with(context).load("").into(img_nav_avatar);
            }
            txt_nav_name.setText(name);
            txt_nav_email.setText(email);
            txt_nav_state.setText(type);
        }

    }

    private void GET_USER_INFO() {
        id = getSharedPreferences("myLogin", MODE_PRIVATE).getString("id", "");
        time = getSharedPreferences("myLogin", MODE_PRIVATE).getString("time", "");
        name = getSharedPreferences("myLogin", MODE_PRIVATE).getString("name", "");
        email = getSharedPreferences("myLogin", MODE_PRIVATE).getString("email", "");
        picture = getSharedPreferences("myLogin", MODE_PRIVATE).getString("picture", "");
        type = getSharedPreferences("myLogin", MODE_PRIVATE).getString("type", "");
        phone =  getSharedPreferences("myLogin", MODE_PRIVATE).getString("phone", "");
        Log.e("USERINFO_DATA", "【 id: " + id + " 】 / 【 time: " + time + " 】 / 【 name: " + name + " 】 / 【 email: " + email + " 】 / 【 picture: " + picture + " 】 / 【 type: " + type + " 】" + " 】 / 【 phone: " + phone + " 】");


        Log.d("GET_HOME_Login",
                "Account: " + Common.currentUserlogin.getAccount() + " \n " +
                     "Phone: " + Common.currentUserlogin.getPhone() + " \n " +
                     "Type: " + Common.currentUserlogin.getType() );

        Log.d("GET_HOME_AcData",
                "Account: " + Common.currentUserAcData.getAccount() + " \n " +
                     "Time: " + Common.currentUserAcData.getTime() + " \n " +
                     "Name: " + Common.currentUserAcData.getName() + " \n " +
                     "Email: " + Common.currentUserAcData.getEmail() + " \n " +
                     "Picture: " + Common.currentUserAcData.getPicture() + " \n " +
                     "Type: " + Common.currentUserAcData.getType());

    }

    //== == == == == == == == ==
    //  正左方 - 使用者隱藏頁籤
    //== == == == == == == == ==
    private void INIT_LAYOUT_DRAWER() {


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //隱藏左方 - 使用者頁籤訊息
        View headerView = navigationView.getHeaderView(0);
        img_nav_avatar = (ImageView) headerView.findViewById(R.id.img_nav_avatar);
        txt_nav_name = (TextView) headerView.findViewById(R.id.txt_nav_name);
        txt_nav_email = (TextView) headerView.findViewById(R.id.txt_nav_email);
        txt_nav_state = (TextView) headerView.findViewById(R.id.txt_nav_state);


    }

    //== == == == == == ==
    //  正下方-導覽滑動按鈕
    //== == == == == == ==
    private void INIT_LAYOUT_TAB() {
        MainTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { //看範例設定
            @Override
            public void onTabSelected(TabLayout.Tab tab) {  //若是滑動頁籤就把選擇到的位置，指定到對應位置的頁面
                Log.e("44", String.valueOf(tab.getPosition()));
                if (tab.getPosition() != 2)
                    MainViewPager.setCurrentItem(tab.getPosition()); //看範例設定
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //頁面新增事件監聽器，對應頁籤的監聽器
        MainViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(MainTablayout)); //看範例設定

    }

// == == == == == == == == == == == == == == == == == == == == == == == == == == == == == == == ==

    //== == == == == ==
    //  返回上一頁
    //== == == == == ==
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //== == == == == ==
    //  右上方 - 設定欄位
    //== == == == == ==
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //== == == == == == == ==
    //  正左方 - 隱藏使用者頁籤
    //== == == == == == == ==
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
            //-- -- --
            //  登出
            //-- -- --

            //清除公用數據
            Common.currentUserlogin = null;
            Common.currentUserAcData = null;

            SharedPreferences.Editor editor = getSharedPreferences("myLogin", Context.MODE_PRIVATE).edit();
            editor.putString("id", null);
            editor.putString("link", null);
            editor.putString("name", null);
            editor.putString("email", null);
            editor.putString("time", null);
            editor.putString("gender", null);
            editor.putString("birthday", null);
            editor.putString("picture", null);
            editor.putString("type", null);
            editor.commit();

            //GOTO_PAGE
            Intent intent = new Intent();
            intent.setClass(HomeActivity.this, LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("state", "sign_out");   //傳給登入頁要求第三方登出
            intent.putExtras(bundle);  // 記得put進去，不然資料不會帶過去
            startActivity(intent);


        } else if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //=====================
    // 手機夾帶資料-上傳頭像
    //=====================
    @Override
    public void onProgressUpdate(int partantage) {
    }

    private void uploadFile() {
        if (selectedFileUri != null) {
            File file = FileUtils.getFile(this, selectedFileUri);

            String fileName = new StringBuilder(Common.currentUserlogin.getPhone())
                    .append(FileUtils.getExtension(file.toString()))
                    .toString();

            ProgressRequestBody requestFile = new ProgressRequestBody(file, this);

            final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", fileName, requestFile);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mService.uploadFile(Common.currentUserlogin.getPhone(), body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Toast.makeText(HomeActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();
        }
    }


}
