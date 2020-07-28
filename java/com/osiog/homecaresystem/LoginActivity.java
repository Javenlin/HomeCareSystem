package com.osiog.homecaresystem;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.osiog.homecaresystem.Model.UserCheck;
import com.osiog.homecaresystem.Model.UserAcData;
import com.osiog.homecaresystem.Model.UserLogin;
import com.osiog.homecaresystem.Retrofit.IHomeCareAPI;
import com.osiog.homecaresystem.Utils.Common;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


//@@@ _MENU
//        INIT_COMPONENT();
//        onRequestPermissionsResult();
//        READ_PHONE_STATE_API();
//
//        GOOGLE_SIGN_OUT_API();
//        GOOGLE_SIGN_IN_API();
//        GOOGLE_HANDLERESULT();
//        GOOGLE_UPDATEUI();
//        GOOGLE_IN();
//        GOOGLE_OUT();
//
//        clearEditor();
//
//        FB_SIGNIN_API();
//        FACEBOOK_IN();
//        FACEBOOK_OUT();


    //COMPONENT
    private LoginButton btn_FB_login;
    private ImageView btn_FB_login_skin;
    private ImageView btn_GOOGLE_login_skin;
    private ImageView btn_GOOGLE_logout;
    private ImageView btn_PHONE_login;
    private LinearLayout LL_loginInfo;
    private TextView txt_ID;
    private TextView txt_name;
    private TextView txt_email;
    private ImageView img_Avatar;

    //FUNCTION
    private Context context = LoginActivity.this;   //INITIAL_CONTEXT;
    private IHomeCareAPI mService;
    private ProgressDialog mDialog;
    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;
    private Auth mAuth;
    private AccessToken accessToken;

    private static final int REQ_CODE_GG = 9001;
    private static final int REQ_CODE_PH = 1000;
    private static final int REQUEST_PERMISSION = 1001;

    private boolean _isGLLogin = false;
    private boolean _isFBLogin = false;
    private boolean _willGLLogin = false;
    private boolean _willFBLogin = false;

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
        //FACEBOOK
        btn_FB_login = (LoginButton) findViewById(R.id.btn_FB_login);
        btn_FB_login_skin = (ImageView) findViewById(R.id.btn_FB_login_skin);

        //GOOGLE
        //btn_GOOGLE_login = (SignInButton) findViewById(R.id.btn_GOOGLE_login);
        btn_GOOGLE_login_skin = (ImageView) findViewById(R.id.btn_GOOGLE_login_skin);
        btn_GOOGLE_logout = (ImageView) findViewById(R.id.btn_GOOGLE_logout);

        //PHONE
        btn_PHONE_login = (ImageView) findViewById(R.id.btn_PHONE_login);

        //LOGIN_INFO
        LL_loginInfo = (LinearLayout) findViewById(R.id.LL_loginInfo);
        img_Avatar = (ImageView) findViewById(R.id.img_Avatar);
        txt_ID = (TextView) findViewById(R.id.txt_ID);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_email = (TextView) findViewById(R.id.txt_email);


        /**
         * EVENT
         */

        btn_FB_login.setOnClickListener(FACEBOOK);
        btn_FB_login_skin.setOnClickListener(FACEBOOK);

        //btn_GOOGLE_login.setOnClickListener(GOOGLE);
        btn_GOOGLE_login_skin.setOnClickListener(GOOGLE);
        btn_GOOGLE_logout.setOnClickListener(GOOGLE);

        btn_PHONE_login.setOnClickListener(this);


    }

    // -- -- -- -- --
    //  請求授權結果
    // -- -- -- -- --
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //READ_PHONE_STATE_API();       //READ_PHONE_STATE
        INIT_COMPONENT();               //INITIAL_COMPONENT
        mService = Common.getAPI();     //INITIAL IHOMECAREAPI SERVICE
        FacebookSdk.sdkInitialize(context); //INITIAL_FACEBOOK
        AUTO_DATA_LOGIN();            //GET_USERINFO_DATA + SKIP_LOGINPROCESS
        TOTAL_STAGE_LOGOUT();            //第三方平台總登出

        //-- -- -- -- -- -- --
        // REQUEST_PERMISSION
        //-- -- -- -- -- -- --
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                    //android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_SMS,
            }, REQUEST_PERMISSION);


        //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
        // CallbackManager 建立一個用來管理登入/登出後的共用回呼程式
        //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
        callbackManager = CallbackManager.Factory.create(); //INITIAL_CALLBACKMANAGER

        //-- -- -- -- -- -- -- -- --
        // GOOGLE_SIGNIN_SECTION
        //-- -- -- -- -- -- -- -- --
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();

    }


    //== == == == == == ==
    //  BUTTON_CLICK_ZONE
    //== == == == == == ==

    //  PHONE_BUTTON
    //-- -- -- -- -- --
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_PHONE_login:
                //Phone登入按鈕

                //callbackManager = CallbackManager.Factory.create();

                //-- -- -- -- -- --
                //  PHONE_AUTO_LOGIN
                //-- -- -- -- -- --
                if (AccountKit.getCurrentAccessToken() != null && type == "Phone") {

                    //-- -- -- -- -- --
                    //若已有Token直接登入
                    //-- -- -- -- -- --

                    //產生視窗物件
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("您已經手機登入，是否直接進入?")//設定視窗標題
                            .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                            //.setMessage("Google+ 我要登出")//設定顯示的文字
                            .setNegativeButton("登出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    AccountKit.logOut();
                                }
                            })
                            .setPositiveButton("直接進入", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    GETPHONE_API();

                                }
                            })//設定結束的子視窗
                            .show();//呈現對話視窗
                } else {

                    //-- -- -- -- -- -- -- --
                    //若無Token進入手機登入程序
                    //-- -- -- -- -- -- -- --

                    clearEditor();  //清理其他登入資料
                    type = "Phone"; //設置登入type
                    startPhoneLoginPage(LoginType.PHONE);
                    break;
                }

        }
    }

    //  GOOGLE_BUTTON
    //-- -- -- -- -- --
    private View.OnClickListener GOOGLE = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.btn_GOOGLE_login_skin:
                    //GOOGLE_登入
                    if (_isGLLogin == false) {

                        //其餘登出
                        if (isFBLoggedIn() != false) {
                            //啟動中斷繼續登入
                            _willGLLogin = true;
                            btn_FB_login.performClick();
                        } else {
                            GOOGLE_SIGN_IN_API();
                        }
                    }

                    break;
                case R.id.btn_GOOGLE_logout:
                    //GOOGLE_登出
                    if (_isGLLogin == true) {

                        //產生視窗物件
                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setTitle("Google+ 登出")//設定視窗標題
                                .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                                //.setMessage("Google+ 我要登出")//設定顯示的文字
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("登出", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        //其餘登出
                                        if (isFBLoggedIn() != false) {
                                            //清除登入資料
                                            clearEditor();
                                            //登出臉書
                                            FACEBOOK_OUT();
                                        }

                                        //進入登出API
                                        GOOGLE_SIGN_OUT_API();

                                        //若中斷FACEBOOK登入，回頭自動登入
                                        if (_willFBLogin == true) {
                                            _willFBLogin = false;
                                            btn_FB_login.performClick();
                                        }
                                    }
                                })//設定結束的子視窗
                                .show();//呈現對話視窗
                    }
                    break;

            }

        }
    };


    //  FACEBOOK_BUTTON
    //-- -- -- -- -- -- -- --

    private View.OnClickListener FACEBOOK = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_FB_login:

                    FB_SIGNIN_API();


                    break;
                case R.id.btn_FB_login_skin:
                    //FB美化按鈕


                    if (isFBLoggedIn() == false) {
                        //FACEBOOK_未登入_其餘登出後再登入

                        //其餘強制登出
                        if (_isGLLogin != false) {

                            //啟動中斷繼續登入
                            _willFBLogin = true;
                            btn_GOOGLE_logout.performClick();

                        }

                        //其餘皆未登入_FACEBOOK直接登入
                        else {
                            //FB原生按鈕
                            btn_FB_login.performClick();
                        }

                    } else {
                        //FACEBOOK_已登入_執行登出
                        btn_FB_login.performClick();
                    }
                    break;

            }
        }
    };

    //====================
    // 判斷FACEBOOK是否已經登入
    //====================
    private boolean isFBLoggedIn() {
        accessToken = AccessToken.getCurrentAccessToken();
        return !(accessToken == null || accessToken.getPermissions().isEmpty());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        Log.e("PHONE請求回傳", "onActivityResult=>" +
                " request_code: " + requestCode +
                " resultCode: " + resultCode);

        //-- -- -- -- -- -- -- -- -- --
        //  取得GOOGLE請求回傳
        //-- -- -- -- -- -- -- -- -- --
        if (requestCode == REQ_CODE_GG) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GOOGLE_HANDLERESULT(result);
        }

        //-- -- -- -- -- -- -- -- -- --
        //  取得FACEBOOK_PHONE請求回傳
        //-- -- -- -- -- -- -- -- -- --
        if (requestCode == REQ_CODE_PH) {

            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (result.getError() != null) {
                Toast.makeText(this, "" + result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
            } else if (result.wasCancelled()) {
                Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
            } else {
                if (result.getAccessToken() != null) {
                    Log.e("getAccessToken", result.getAccessToken().toString());

                    //手機登入程序
                    GETPHONE_API();
                }
            }
        }
    }

// == == == == == == == == == == == == == == == == == == == == == == == == == == ==

    //===========================
    //  TODO PHONE手機登入程序API
    //===========================

    //-------------------
    // 呼叫手機程序_前置設定
    //-------------------
    private void startPhoneLoginPage(LoginType loginType) {
        Intent intent = new Intent(this, AccountKitActivity.class);

        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder
                        (loginType, AccountKitActivity.ResponseType.TOKEN);

        Log.e("REQ_CODE_PH", String.valueOf(REQ_CODE_PH));

        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, builder.build());
        startActivityForResult(intent, REQ_CODE_PH);

    }

    private void GETPHONE_API() {

        final SpotsDialog waitingDialog = new SpotsDialog(context);
        waitingDialog.show();
        waitingDialog.setMessage("請稍後...");

        //------------------------------
        //獲取用戶手機號碼 檢查是否已經存在
        //------------------------------
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {

                //----------------
                // 獲取用戶手機號碼
                //----------------
                phone = account.getPhoneNumber().toString();    // 區域變數-手機

                //-- -- -- -- -- --
                // 若為手機登入 (PH)
                //-- -- -- -- -- --
                if (type.equals("Phone")) {

                    id = phone; //將手機ID - 設為電話號碼
                    time = new Date(System.currentTimeMillis()).toString();

                    Log.e("AccountKit", "phone: " + phone + "id: " + id + "time" + time + "type: " + type);

                    //存入快取
                    SharedPreferences.Editor editor = getSharedPreferences("myLogin", Context.MODE_PRIVATE).edit();
                    editor.putString("id", id);
                    editor.putString("time", time);
//                    editor.putString("name", name);
//                    editor.putString("email", email);
//                    editor.putString("picture", picture);
                    editor.putString("type", type);
                    editor.putString("phone", phone);
                    editor.commit();

                    REGISTER_API();

                    //註冊用戶帳號夾帶資料
                    REGISTER_NEWACDATA();
                } else {

                    //-- -- -- -- -- --
                    // 若為其他第三方登入(FB/GG)
                    //-- -- -- -- -- --

                    Log.e("AccountKit", "phone: " + phone + "id: " + id + "time" + time + "type: " + type);

                    //手機補進緩存
                    SharedPreferences.Editor editor = getSharedPreferences("myLogin", Context.MODE_PRIVATE).edit();
                    editor.putString("phone", phone);
                    editor.commit();


                    //-------------
                    // 註冊用戶手機
                    //-------------
                    mService.registerNewUser(id, phone, type)
                            .enqueue(new Callback<UserLogin>() {
                                @Override
                                public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {

                                    //公用變數 - 用戶登入資料
                                    UserLogin userLogin = response.body();
                                    Common.currentUserlogin = response.body();

                                    Log.e("GETPHONE_API", "PHONE_CODE: " + userLogin.getResult_code() + " " + userLogin.getResult_msg());

                                    //------------------
                                    //  手機已經存在
                                    //------------------
                                    if (userLogin.getResult_code().equals("0")) {

                                        //-- -- -- -- -- --
                                        //更新完畢進入主頁
                                        //-- -- -- -- -- --
                                        Log.d("GETPHONE_API", Common.currentUserlogin.getResult_msg() +
                                                "Account:" + Common.currentUserlogin.getAccount() + " \n " +
                                                "Phone:" + Common.currentUserlogin.getPhone() + " \n " +
                                                "Type:" + Common.currentUserlogin.getType());
                                        waitingDialog.dismiss();
                                        GOTO_HomeActivity();
                                    }


                                    //------------------
                                    //  不匹配 -更新手機
                                    //------------------
                                    if (userLogin.getResult_code().equals("1")) {

                                        //查看用戶登入資料
                                        mService.getUserLoginInfo(id)
                                                .enqueue(new Callback<UserLogin>() {
                                                    @Override
                                                    public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {

                                                        //-- -- -- -- -- --
                                                        //更新完畢進入主頁
                                                        //-- -- -- -- -- --
                                                        Log.d("GETPHONE_API", Common.currentUserlogin.getResult_msg() +
                                                                "Account:" + Common.currentUserlogin.getAccount() + " \n " +
                                                                "Phone:" + Common.currentUserlogin.getPhone() + " \n " +
                                                                "Type:" + Common.currentUserlogin.getType());
                                                        waitingDialog.dismiss();
                                                        GOTO_HomeActivity();

                                                    }

                                                    @Override
                                                    public void onFailure(Call<UserLogin> call, Throwable t) {
                                                        Log.e("GETPHONE_API_FAIL", "getUserLoginInfo: " + t.getMessage());
                                                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT);
                                                    }
                                                });
                                    }


                                    //------------------
                                    //  帳號不存在 -註冊新用戶
                                    //------------------
                                    if (userLogin.getResult_code().equals("2")) {

                                        //-- -- -- -- -- --
                                        //更新完畢進入主頁
                                        //-- -- -- -- -- --
                                        Log.d("GETPHONE_API", Common.currentUserlogin.getResult_msg() +
                                                "Account:" + Common.currentUserlogin.getAccount() + " \n " +
                                                "Phone:" + Common.currentUserlogin.getPhone() + " \n " +
                                                "Type:" + Common.currentUserlogin.getType());
                                        waitingDialog.dismiss();
                                        GOTO_HomeActivity();
                                    }

                                    //-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

                                    //-- -- -- -- -- --
                                    //  手機夾帶資料對話框
                                    //-- -- -- -- -- --
                                    //showRegisterDialog();

                                }

                                @Override
                                public void onFailure(Call<UserLogin> call, Throwable t) {
                                    waitingDialog.dismiss();
                                    Log.e("GETPHONE_API_FAIL", "registerNewUser:" + t);
                                }
                            });
                }
            }//else if (!type.equals("Phone"))

            @Override
            public void onError(AccountKitError accountKitError) {
                Log.e("ERROR", accountKitError.getErrorType().getMessage());
                Log.e("AccountKit", "onError");
            }


        });


    }


// == == == == == == == == == == == == == == == == == == == == == == == == == == ==

    //=======================
    // TODO GOOGLE 登入程序API
    //=======================
    //Android 實現 google 登入 (1) 準備 & 版面


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //---------------
    // GOOGLE登出
    //---------------
    private void GOOGLE_SIGN_OUT_API() {


        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        Log.e("", String.valueOf(googleApiClient.isConnected()));
                        if (googleApiClient.isConnected()) {
                            //SIGN OUT HERE
                            Auth.GoogleSignInApi.signOut(googleApiClient).

                                    setResultCallback(new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            Log.e("GOOGLE_SIGN_OUT_API", "GOOGLE_SIGN_OUT_API");

                                            //清除登入資料
                                            clearEditor();

                                            //登入判別切換
                                            GOOGLE_OUT();

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {/*ignored*/}
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            /*ignored*/
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API) //IMPORTANT!!!
                .build();

        googleApiClient.connect();

    }


    //-------------------------
    // GOOGLE登入回傳資料
    //-------------------------
    private void GOOGLE_SIGN_IN_API() {
        //清除登入資料
        clearEditor();
        //callbackManager = CallbackManager.Factory.create();

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE_GG);
    }

    private void GOOGLE_HANDLERESULT(GoogleSignInResult result) {

        if (result.isSuccess()) {

            //顯示登入資料
            LL_loginInfo.setVisibility(View.VISIBLE);

            GoogleSignInAccount account = result.getSignInAccount();
            id = account.getId();
            time = new Date(System.currentTimeMillis()).toString();
            name = account.getDisplayName();
            email = account.getEmail();
            type = "Google";


            //顯示大頭照 (注意 - 尚未作無圖防呆)
            if (account.getPhotoUrl() != null) {
                picture = account.getPhotoUrl().toString();
                Glide.with(context).load(picture).into(img_Avatar);
            }


            //顯示使用者資料
            txt_ID.setText(id);
            txt_name.setText(name);
            txt_email.setText(email);
            Log.d("Google_INFO", " id: " + id + " time: " + time + " name: " + name + " email: " + email + " picture: " + picture + " type: " + type);


            //存入快取
            SharedPreferences.Editor editor = getSharedPreferences("myLogin", Context.MODE_PRIVATE).edit();
            editor.putString("id", id);
            editor.putString("time", time);
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("picture", picture);
            editor.putString("type", type);
            editor.commit();

            //檢查使用者
            CHECK_USER(id);

            //登入判別切換
            GOOGLE_IN();

        } else {

            //登入判別切換
            GOOGLE_OUT();
        }
    }

    //-------------------------
    // GOOGLE UI更新
    //-------------------------
    private void GOOGLE_UPDATEUI() {

        if (_isGLLogin) {
            //已登入
            LL_loginInfo.setVisibility(View.VISIBLE);
            btn_GOOGLE_login_skin.setVisibility(View.GONE); //登入不可
            btn_GOOGLE_logout.setVisibility(View.VISIBLE);
        } else {
            //未登入
            LL_loginInfo.setVisibility(View.GONE);
            btn_GOOGLE_login_skin.setVisibility(View.VISIBLE);  //登入可
            btn_GOOGLE_logout.setVisibility(View.GONE);
        }
    }


// == == == == == == == == == == == == == == == == == == == == == == == == == == ==

    //==========================
    // TODO FACEBOOK 登入程序API
    //==========================
    //第16課 Facebook登入登出

    private void FB_SIGNIN_API() {

        context = this;

        //-------------------------
        // ##FB登出後, 偏好內容即清除
        //-------------------------
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {

            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                SharedPreferences.Editor editor = getSharedPreferences("myLogin", Context.MODE_PRIVATE).edit();

                //清除登入資料
                clearEditor();

                //登出臉書
                FACEBOOK_OUT();

                //若中斷GOOGLE登入，回頭自動登入
                if (_willGLLogin == true) {
                    _willGLLogin = false;
                    btn_GOOGLE_login_skin.performClick();
                }

            }
        };


        //--------------------------------
        // setReadPermissions 要求讀取權限
        //--------------------------------
        btn_FB_login.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));

        //--------------------------------
        // ##Facebook登入回傳資料
        //--------------------------------
        btn_FB_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                //TODO SpotsDialog 範例
                final SpotsDialog waitingDialog = new SpotsDialog(context);
                waitingDialog.show();
                waitingDialog.setMessage("請稍後...");

                //TODO ProgressDialog 範例
//                mDialog = new ProgressDialog(context);
//                mDialog.setMessage("Retrieving data...");
//                mDialog.show();

                //QUICK_FB_ENTRANCE
//                AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                if (accessToken != null) {
//                    waitingDialog.dismiss();
//                    Log.e("FB_accessToken", "" + accessToken);
//                    GOTO_HomeActivity();
//                } else {

                String accessToken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        //結束讀取跑馬燈
                        waitingDialog.dismiss();
                        // mDialog.dismiss();

                        //----------------
                        //  登入前重置
                        //----------------

                        //清除登入資料
                        clearEditor();

                        //其餘登出
                        GOOGLE_OUT();
                        //----------------

                        ////////
                        try {

                            //開啟顯示框
                            LL_loginInfo.setVisibility(View.VISIBLE);

                            //---------------
                            //預覽 JSONObject
                            //---------------
                            Log.d("JSON_all", object.toString());

                            //-- -- -- -- --
                            // 獲取頭像並顯示
                            //-- -- -- -- --
                            picture = "";
                            JSONObject DATA = response.getJSONObject();
                            if (DATA.has("picture")) {
                                picture = DATA.getJSONObject("picture").getJSONObject("data").getString("url");
                                Glide.with(context).load(picture).into(img_Avatar);
                            }
                            Log.d("JSON_picture", picture);

                            //---------------------------------------------------
                            // 將Facebook回傳的id及name, 另系統時間加入偏好內容中
                            //---------------------------------------------------

                            //登入基本歸檔資料
                            id = object.optString("id");
                            time = new Date(System.currentTimeMillis()).toString();
                            name = object.optString("name");
                            email = object.optString("email");
                            type = "Facebook";
//                            link = object.optString("link");
//                            gender = object.optString("gender");
//                            birthday = object.optString("birthday");

                            //顯示使用者資料
                            txt_ID.setText(id);
                            txt_name.setText(name);
                            txt_email.setText(email);
                            Log.d("FACEBOOK_INFO", " id: " + id + " time: " + time + " name: " + name + " email: " + email + " picture: " + picture + " type: " + type);


                            //存入快取
                            SharedPreferences.Editor editor = getSharedPreferences("myLogin", Context.MODE_PRIVATE).edit();
                            editor.putString("id", id);
                            editor.putString("time", time);
                            editor.putString("name", name);
                            editor.putString("email", email);
                            editor.putString("picture", picture);
                            editor.putString("type", type);
                            editor.commit();


                            //檢查使用者
                            CHECK_USER(id);


                            //登入判別
                            FACEBOOK_IN();


                        } catch (Exception e) {
                            Toast.makeText(context, "Exception! " + e, Toast.LENGTH_SHORT).show();
                            Log.e("Exception!", "" + e);

                            //登出判別
                            FACEBOOK_OUT();
                        }
                        ////////


                    }


                });

                //---------------------------------
                // 要求Facebook回覆的使用者資料項目
                //---------------------------------
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, link, name, email, gender, birthday, picture.width(700).height(700)");
                request.setParameters(parameters);


                //----------------------
                // 向Facebook發出請求
                //----------------------
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, "登入取消", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(context, "登入錯誤", Toast.LENGTH_SHORT).show();
            }


        });


    }


    //-- -- -- -- --
    //用戶登入主要程序
    //-- -- -- -- --
    private void CHECK_USER(final String ID) {

        mService.checkExistsAccount(ID)
                .enqueue(new Callback<UserCheck>() {
                    @Override
                    public void onResponse(Call<UserCheck> call, Response<UserCheck> response) {

                        UserCheck userResponse = response.body();
                        Log.e("response.body", "body : " + response.body());

                        if (userResponse.isAccount_exists()) {

                            Log.d("CHECK_USER", "使用者帳號_已經註冊!");

                            //取得用戶資料
                            USERINFO_API();


                        } else {
                            Log.d("CHECK_USER", "使用者帳號_尚未註冊!");

                            //註冊用戶資料
                            REGISTER_API();

//                            waitingDialog.dismiss();
//                            showRegisterDialog(account.getPhoneNumber().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserCheck> call, Throwable t) {
//                        waitingDialog.dismiss();

                        Log.e("CHECK_USER_FAIL", "onFailure: " + t.getMessage());
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT);
                    }

                });
    }


// == == == == == == == == == == == == == == == == == == == == == == == == == == == == ==

    //-- -- -- -- --
    //確認完畢進入主頁
    //-- -- -- -- --
    private void GOTO_HomeActivity() {

        //開啟新頁面
        startActivity(new Intent(context, HomeActivity.class));
        //關閉登入頁面
        finish();
    }

    //=====================
    //  第三方平台總登出
    //=====================
    private void TOTAL_STAGE_LOGOUT() {
        //HomeActivity GET_BUNDLE
        Bundle bundle = getIntent().getExtras();
        String state = null;
        if (bundle != null) {
            state = bundle.getString("state");
            if (state != null) {
                Log.e("bundle_state", state);
                if (state.equals("sign_out")) {

                    GOOGLE_SIGN_OUT_API();  //GG_LOGOUT
                    AccountKit.logOut();    //PH_LOGOUT
                    LoginManager.getInstance().logOut();  //FB_LOGOUT
                }
            }
        }
    }


    //-- -- -- -- -- --
    // 註冊用戶新資料
    //-- -- -- -- -- --
    private void REGISTER_API() {

        /**
         * 註冊新資料
         * */

        //若尚未註冊手機 則指定空值
        if (TextUtils.isEmpty(phone)) phone = "";

        Log.e("註冊用戶登入資料", id + "/" + phone + "/" + type);

        //註冊用戶登入資料
        REGISTER_NEWUSER();

        //註冊用戶帳號夾帶資料
        REGISTER_NEWACDATA();


    }


    //-- -- -- -- -- --
    //  註冊用戶登入資料
    //-- -- -- -- -- --
    private void REGISTER_NEWUSER() {

        mService.registerNewUser(id, phone, type)
                .enqueue(new Callback<UserLogin>() {
                    @Override
                    public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {

                        //註冊資料物件化
                        UserLogin userLogin = response.body();

                        //公用變數-用戶登入資料
                        Common.currentUserlogin = response.body();
                        Log.d("REGISTER_API", Common.currentUserlogin.getResult_code() + " / " + Common.currentUserlogin.getResult_msg());

                        //-- -- -- -- -- --
                        // 檢查是否進行手機註冊
                        //-- -- -- -- -- --
                        if (!phone.equals("")) {

                            //手機存在- 不做事
                            if (userLogin.getResult_code().equals("0")) {
                                Toast.makeText(context, userLogin.getResult_msg(), Toast.LENGTH_SHORT).show();
                                Log.d("REGISTER_API", userLogin.getResult_msg());
                                GOTO_HomeActivity();
                            }
                            //手機不匹配- 更新手機
                            else if (userLogin.getResult_code().equals("1")) {
                                Toast.makeText(context, userLogin.getResult_msg(), Toast.LENGTH_SHORT).show();
                                Log.d("REGISTER_API", userLogin.getResult_msg());
                                GOTO_HomeActivity();
                            }

                        } else {

                            //手機不存在- 進入手機註冊程序
                            if (userLogin.getResult_code().equals("3")) {
                                Toast.makeText(context, userLogin.getResult_msg(), Toast.LENGTH_SHORT).show();
                                Log.d("REGISTER_API", userLogin.getResult_msg());

                                startPhoneLoginPage(LoginType.PHONE);
                            }

                            //全都不存在- 建立新帳號+進入手機註冊程序
                            else if (userLogin.getResult_code().equals("2")) {
                                Toast.makeText(context, userLogin.getResult_msg(), Toast.LENGTH_SHORT).show();
                                Log.d("REGISTER_API", userLogin.getResult_msg());

                                //-- -- -- -- -- -- --
                                //呼叫手機程序前置設定
                                //-- -- -- -- -- -- --
                                startPhoneLoginPage(LoginType.PHONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserLogin> call, Throwable t) {
                        Log.e("REGISTER_API_FAIL", "onFailure: " + t.getMessage());
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT);
                    }

                });

    }


    //-- -- -- -- -- -- --
    //註冊用戶帳號夾帶資料
    //-- -- -- -- -- -- --
    private void REGISTER_NEWACDATA() {

        mService.registerNewAcdata(id, time, name, email, picture, type)
                .

                        enqueue(new Callback<UserAcData>() {
                            @Override
                            public void onResponse
                                    (Call<UserAcData> call, Response<UserAcData> response) {

                                //註冊資料物件化
                                UserAcData userAcData = response.body();

                                Log.d("REGISTER_API", userAcData.getResult_code());


                                //更新資料
                                if (userAcData.getResult_code().equals("0")) {
                                    Toast.makeText(context, userAcData.getResult_msg(), Toast.LENGTH_SHORT).show();
                                    Log.d("REGISTER_API", userAcData.getResult_msg());

                                    //公用變數-用戶帳號夾帶資料
                                    Common.currentUserAcData = response.body();
                                }
                                //建立新資料
                                else if (userAcData.getResult_code().equals("1")) {
                                    Toast.makeText(context, userAcData.getResult_msg(), Toast.LENGTH_SHORT).show();
                                    Log.d("REGISTER_API", userAcData.getResult_msg());
                                }


                            }

                            @Override
                            public void onFailure(Call<UserAcData> call, Throwable t) {
                                Log.e("REGISTER_API_FAIL", "onFailure: " + t.getMessage());
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT);
                            }
                        });

    }


    //-- -- -- -- -- --
    // 檢查用戶新資料
    //-- -- -- -- -- --
    private void USERINFO_API() {

        /**
         * 檢查獲取資料
         * */

        //獲取用戶登入資料
        mService.getUserLoginInfo(id)
                .enqueue(new Callback<UserLogin>() {
                    @Override
                    public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {

                        //公用變數-用戶登入資料
                        Common.currentUserlogin = response.body();
                        phone = Common.currentUserlogin.getPhone();
                        type = Common.currentUserlogin.getType();

                        Log.d("USERINFO_API", Common.currentUserlogin.getResult_msg() +
                                "Account:" + Common.currentUserlogin.getAccount() + " \n " +
                                "Phone:" + Common.currentUserlogin.getPhone() + " \n " +
                                "Type:" + Common.currentUserlogin.getType());

                        if (TextUtils.isEmpty(Common.currentUserlogin.getPhone())) {
                            //註冊手機
                            Log.d("USERINFO_API", "您尚未註冊手機!");
                            Toast.makeText(context, "您尚未註冊手機!", Toast.LENGTH_SHORT).show();

                        }

                        //更新用戶資料
                        REGISTER_API();


                    }

                    @Override
                    public void onFailure(Call<UserLogin> call, Throwable t) {
                        Log.e("USERINFO_API_FAIL", "onFailure: " + t.getMessage());
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT);
                    }

                });

        //獲取用戶帳號夾帶資料
        mService.getUserAcData(id)
                .enqueue(new Callback<UserAcData>() {
                    @Override
                    public void onResponse(Call<UserAcData> call, Response<UserAcData> response) {

                        //公用變數-用戶帳號夾帶資料
                        Common.currentUserAcData = response.body();

                        Log.d("USERINFO_API", Common.currentUserAcData.getResult_msg() +
                                "Account: " + Common.currentUserAcData.getAccount() + " \n " +
                                "Time: " + Common.currentUserAcData.getTime() + " \n " +
                                "Name: " + Common.currentUserAcData.getName() + " \n " +
                                "Email: " + Common.currentUserAcData.getEmail() + " \n " +
                                "Picture: " + Common.currentUserAcData.getPicture() + " \n " +
                                "Type: " + Common.currentUserAcData.getType());
                    }

                    @Override
                    public void onFailure(Call<UserAcData> call, Throwable t) {
                        Log.e("USERINFO_API_FAIL", "onFailure: " + t.getMessage());
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT);
                    }

                });

    }

    //============================
    // 取得過去登入資料 - 直接進入
    //============================
    private void AUTO_DATA_LOGIN() {

        id = getSharedPreferences("myLogin", MODE_PRIVATE).getString("id", "");
        time = getSharedPreferences("myLogin", MODE_PRIVATE).getString("time", "");
        name = getSharedPreferences("myLogin", MODE_PRIVATE).getString("name", "");
        email = getSharedPreferences("myLogin", MODE_PRIVATE).getString("email", "");
        picture = getSharedPreferences("myLogin", MODE_PRIVATE).getString("picture", "");
        type = getSharedPreferences("myLogin", MODE_PRIVATE).getString("type", "");
        phone = getSharedPreferences("myLogin", MODE_PRIVATE).getString("phone", "");
        Log.e("USERINFO_DATA", "【 id: " + id + " 】 / 【 time: " + time + " 】 / 【 name: " + name + " 】 / 【 email: " + email + " 】 / 【 picture: " + picture + " 】 / 【 type: " + type + " 】 / 【 phone: " + phone + " 】");

        UserLogin userLogin = new UserLogin();
        userLogin.setAccount(id);
        userLogin.setPhone(phone);
        userLogin.setType(type);

        UserAcData userAcData = new UserAcData();
        userAcData.setAccount(id);
        userAcData.setTime(time);
        userAcData.setName(name);
        userAcData.setEmail(email);
        userAcData.setPicture(picture);
        userAcData.setType(type);


        Common.currentUserlogin.setAccount(id);
        Common.currentUserlogin.setPhone(phone);
        Common.currentUserlogin.setType(type);

        Common.currentUserAcData.setAccount(id);
        Common.currentUserAcData.setTime(time);
        Common.currentUserAcData.setName(name);
        Common.currentUserAcData.setEmail(email);
        Common.currentUserAcData.setPicture(picture);
        Common.currentUserAcData.setType(type);

        //-- -- -- -- -- --
        //  詢問是否直接登入
        //-- -- -- -- -- --
        if (!id.isEmpty()) {
            //產生視窗物件
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("您曾經登入，是否直接進入?")//設定視窗標題
                    .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                    //.setMessage("Google+ 我要登出")//設定顯示的文字
                    .setNegativeButton("重新選擇", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            clearEditor();
                        }
                    })
                    .setPositiveButton("直接進入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            GOTO_HomeActivity();

                        }
                    })//設定結束的子視窗
                    .show();//呈現對話視窗
        }
    }


    //-- -- -- -- -- -- --
    // 手機夾帶資料對話框
    //-- -- -- -- -- -- --
    private void showRegisterDialog() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("註冊");
        //
        final LayoutInflater inflater = this.getLayoutInflater();
        final View register_layout = inflater.inflate(R.layout.tb_register_layout, null);
        //
        final MaterialEditText edt_name = (MaterialEditText) register_layout.findViewById(R.id.edt_name);
        final MaterialEditText edt_address = (MaterialEditText) register_layout.findViewById(R.id.edt_address);
        final MaterialEditText edt_birthdate = (MaterialEditText) register_layout.findViewById(R.id.edt_birthdate);
        //
        edt_birthdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));
        //
        TextView btn_register = (TextView) register_layout.findViewById(R.id.btn_register);

        //---------------
        //設定並顯示對話框
        //---------------
        alertDialog.setView(register_layout);
        final AlertDialog dialog = alertDialog.create();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //---------
                //關閉對話框
                //---------
                dialog.dismiss();

//                alertDialog.create().dismiss();

                final SpotsDialog waitingDialog = new SpotsDialog(context);
                waitingDialog.show();
                waitingDialog.setMessage("請稍後...");

                if (TextUtils.isEmpty(edt_name.getFloatingLabelText().toString())) {
                    Toast.makeText(context, "請輸入你的名字", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(edt_birthdate.getFloatingLabelText().toString())) {
                    Toast.makeText(context, "請輸入你的生日", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(edt_address.getFloatingLabelText().toString())) {
                    Toast.makeText(context, "請輸入你的地址", Toast.LENGTH_SHORT).show();
                    return;
                }

                //註冊用戶登入手機
                mService.registerNewUser(id, phone, type)
                        .enqueue(new Callback<UserLogin>() {
                            @Override
                            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                                waitingDialog.dismiss();
                                UserLogin userLogin = response.body();
                                if (userLogin.getResult_code().equals("2")) {
                                    Toast.makeText(context, "註冊成功!!!", Toast.LENGTH_SHORT).show();

                                    //公用變數-用戶登入資料
                                    Common.currentUserlogin = response.body();


                                    //-- -- -- -- --
                                    //確認完畢進入主頁
                                    //-- -- -- -- --
                                    GOTO_HomeActivity();
                                    finish();

                                }
                            }

                            @Override
                            public void onFailure(Call<UserLogin> call, Throwable t) {
                                waitingDialog.dismiss();
                            }
                        });

            }
        });

//        //---------------
//        //簡易顯示對話框
//        //---------------
//        alertDialog.setView(register_layout);
//        alertDialog.show();

        dialog.show();
    }

    //-- -- -- -- -- --
    //  獲取手機狀態資料
    //-- -- -- -- -- --
    private void READ_PHONE_STATE_API() {

        String deviceid = "";
        String tel = "";
        String imei = "";
        String imsi = "";

        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (tm != null) {

            //取得手機狀態PHONE_STATE與簡訊SMS權限
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            //deviceid = tm.getDeviceId();
            //tel = tm.getLine1Number();
            //imei = tm.getSimSerialNumber();
            //imsi = tm.getSubscriberId();

            Log.d("__tm5", " deviceid: " + deviceid + " tel: " + tel + " imei: " + imei + " imsi: " + imsi);

            if (TextUtils.isEmpty(tel)) {

            }

        }

    }


    //-- -- -- -- --
    // 清除登入資料
    //-- -- -- -- --
    private void clearEditor() {

        //清除公用數據
        Common.currentUserlogin = null;
        Common.currentUserAcData = null;

        Log.e("clearEditor", "UserAcData_Cleaned :" + Common.currentUserlogin + "UserAcData_Cleaned :" + Common.currentUserAcData);


        SharedPreferences.Editor editor = getSharedPreferences("myLogin", Context.MODE_PRIVATE).edit();
        LL_loginInfo.setVisibility(View.INVISIBLE); //關閉顯示框
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
        img_Avatar.setImageDrawable(null); //頭像清空

    }


    //谷哥登入判別
    private void GOOGLE_IN() {
        _isGLLogin = true;
        GOOGLE_UPDATEUI();
    }

    //谷哥登出判別
    private void GOOGLE_OUT() {
        _isGLLogin = false;
        GOOGLE_UPDATEUI();
    }

    //臉書登入判別
    private void FACEBOOK_IN() {
        _isFBLogin = true;
    }

    //臉書登出判別
    private void FACEBOOK_OUT() {
        _isFBLogin = false;
    }


    public void layoutMenu() {
        //TODO LAYOUT_MENU
        LayoutInflater inflater = this.getLayoutInflater();
        View 登入 = inflater.inflate(R.layout.activity_login, null);
        View 主頁_主要內容 = inflater.inflate(R.layout.home_content, null);
        View 主頁_工具欄 = inflater.inflate(R.layout.home_app_bar, null);
        View 主頁_隱藏頁簽 = inflater.inflate(R.layout.home_nav_header, null);

    }

}


//@SharedPreferences的四种模式
//
//======================================================================================
//开发应用需要保存一些配置参数，如果是Android应用，我们最适合采用SharedPreferences保存数据，
//它是一个轻量级的存储类，特别适合用于保存软件配置参数。
//使用SharedPreferences保存数据，其背后是用xml文件存放数据，
//文件存放在/data/data/<packagename>/shared_prefs目录下。
//
//<?xml version='1.0'encoding='utf-8'standalone='yes'?>
//<map>
//<stringname="name">四种模式</string>
//<int name="age"value="4"/>
//</map>
//
//因为SharedPreferences背后是使用xml文件保存数据，getSharedPreferences(name,mode)方法的第一个参数用于指定
//该文件的名称，名称不用带后缀，后缀会由Android自动加上。方法的第二个参数指定文件的操作模式，共有四种操作模式，
//
//这四种模式代表的含义为：
//
//Context.MODE_PRIVATE=0
//Context.MODE_APPEND=32768
//Context.MODE_WORLD_READABLE=1
//Context.MODE_WORLD_WRITEABLE=2
//
//Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
//Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
//Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
//MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
//======================================================================================


//@Android Thread 簡單運用
//
//======================================================================================
//
//
//  有時候程式想讓他暫停個幾秒在運作 但又不想用太難的技巧 可參考下面
// 在Method裡想暫停的時候
//        try {
//        Thread.sleep(3000);
//        } catch (InterruptedException e) {
//// TODO Auto-generated catch block
//        e.printStackTrace();
//        }//毫秒的意思 3000就是3秒 以此類推
//        -----如果想透過Thread傳值 或 改變TextView 的文字或跳出Toast 就必須要加入Handler----- 如果運算比較長或是不希望造成Activity暫停回應的狀況， 會將運算的部分另開一個Thread，而在Thread中並沒有辦法改變畫面上的任何UI，所以會使用Handler來完成UI更新的目的
//
//
//
//        可參考這
//
//private Handler handler_1 = null;//老闆
//private HandlerThread handlerThread_1 = null;//員工
//private String handlerThread_1_name = "我是1號員工";
//
//
//
//        onCreate裡
//
////新增一個員工 給他一個名字
//        handlerThread_1 = new HandlerThread(handlerThread_1_name);
//
////讓他開始上班
//        handlerThread_1.start();
//
////新增一個老闆 他是員工1號的老闆
//        handler_1 = new Handler(handlerThread_1.getLooper());
//
////老闆指派員工1號去做事(runnable_1)
//        handler_1.post(runnable_1);
//
//
//
//        onCreate外
//
//        新增工作1的內容
//
//private Runnable runnable_1 = new Runnable() {
//
//@Override
//public void run() {
//        //要做的事情寫在這
//
//
//        //老闆指定每隔幾秒要做一次工作1 (單位毫秒:1000等於1秒)
//        handler_1.postDelayed(this, 1000);
//
//        }
//        };
//======================================================================================


////取出 SharedPreferences 物件
//
//======================================================================================
//    //取出 SharedPreferences 物件，若 SharedPreference 檔案不存在就會建立一個新的
//    SharedPreferences spref = getPreferences(MODE_PRIVATE);
//
//    //透過 KEY_BOOL key 取出 boolean 型態的資料，若資料不存在則回傳 true
//    boolean booValue = spref.getBoolean("KEY_BOOL", true);
//
//    //透過 KEY_FLOAT key 取出 float 型態的資料，若資料不存在則回傳 0
//    float floatValue = spref.getFloat("KEY_FLOAT", 0);
//
//    //透過 KEY_LONG key 取出 long 型態的資料，若資料不存在則回傳 0
//    long longValue = spref.getLong("KEY_LONG", 0);
//
//    //透過 KEY_STRING key 取出字串型態的資料，若資料不存在則回傳 null
//    String strValue = spref.getString("KEY_STRING", null);
//
//    //透過 KEY_STR_SET key 取出集合型態的資料，若資料不存在則回傳 null
//    HashSet<String> hs = (HashSet<String>) spref.getStringSet("KEY_STR_SET", null);
//
//    //回傳 KEY_STRING 是否在在 SharedPreferences 檔案中
//    boolean exists = spref.contains("KEY_STRING")
//======================================================================================


//Android Facebook SDK：檢查用戶是否已登錄
//======================================================================================


//    private boolean isLoggedIn = false; // by default assume not logged in
//
//    private Session.StatusCallback callback = new Session.StatusCallback() {
//        @Override
//        public void call(Session session, SessionState state, Exception exception) {
//            if (state.isOpened()) { //note: I think session.isOpened() is the same
//                isLoggedIn = true;
//            } else if (state.isClosed()) {
//                isLoggedIn = false;
//            }
//        }
//    };
//
//    public boolean isLoggedIn() {
//        return isLoggedIn;
//    }

//======================================================================================

//TextUtils.isEmpty()
//
//======================================================================================
//            在字符串为null或者""的情况下，都是可以用TextUtils.isEmpty()来进行判断的
//            !TextUtils.isEmpty(account.getPhotoUrl().toString())
//              //顯示大頭照
//            if ("" != account.getPhotoUrl().toString() || null !=account.getPhotoUrl().toString()) {
//                    picture = account.getPhotoUrl().toString();
//                    Glide.with(context).load(picture).into(img_Avatar);
//             }
//======================================================================================


////透過 KEY_STRING key 取出字串型態的資料，若資料不存在則回傳 null
//
//======================================================================================
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        //透過 KEY_STRING key 取出字串型態的資料，若資料不存在則回傳 null
//        SharedPreferences spref = getPreferences(MODE_PRIVATE);
//        String id = spref.getString("id", null);
//        String time = spref.getString("time", null);
//        String name = spref.getString("name", null);
//        String email = spref.getString("email", null);
//        String picture = spref.getString("picture", null);
//
//        Log.e("onResume",id+time+name+email+picture);
//
//        //顯示使用者資料
//        txt_ID.setText(id);
//        txt_name.setText(name);
//        txt_email.setText(email);
//        Glide.with(context).load(picture).into(img_Avatar);
//
//    }
//======================================================================================


//======================================================================================
////        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        if (AccessToken.getCurrentAccessToken() != null) {
//
//            final SpotsDialog waitingDialog = new SpotsDialog(context);
//            waitingDialog.show();
//            waitingDialog.setMessage("請稍後...");
//
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//// TODO Auto-generated catch block
//                e.printStackTrace();
//            }//毫秒的意思 3000就是3秒 以此類推
//
//            Log.e("FB_accessToken", "" + AccessToken.getCurrentAccessToken());
//            GOTO_HomeActivity();
//        }
//======================================================================================


// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
//        //如果FB登入 - 自動登入
//        if (isFBLoggedIn()) {
//            Log.e("FB_LOGGED_IN", "True - AccessToken:" + accessToken);
//            Log.e("FB_LOGGED_IN", " id: " + id + " time: " + time + " name: " + name + " email: " + email + " picture: " + picture + " type: " + type);
//            FB_SIGNIN_API();
//            GOTO_HomeActivity();
//        } else {
//            Log.e("FB_LOGGED_IN", "FALSE");
//        }
// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --


// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
//            Log.e("Google_PHOTO",account.getPhotoUrl().toString());
//            account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null;
//            String picture;
//            if ( account.getPhotoUrl() != null)
//                picture = account.getPhotoUrl().toString();
//            else
//                picture = null;
//            !TextUtils.isEmpty()
// -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --