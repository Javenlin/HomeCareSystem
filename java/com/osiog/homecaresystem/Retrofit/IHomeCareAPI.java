package com.osiog.homecaresystem.Retrofit;

import com.osiog.homecaresystem.Model.UserCheck;
import com.osiog.homecaresystem.Model.UserAcData;
import com.osiog.homecaresystem.Model.UserLogin;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by OSIOG on 2018/7/12.
 */

public interface IHomeCareAPI {

    //檢

    //檢查用戶帳號存在
    @FormUrlEncoded
    @POST("check_user.php")
    Call<UserCheck> checkExistsAccount(@Field("account")  String account);

    //檢查用戶登入資料存在
    @FormUrlEncoded
    @POST("check_user.php")
    Call<UserCheck> checkExistsUser(@Field("account")  String account,
                                    @Field("phone")    String phone,
                                    @Field("type")     String type);

    //取

    //取得用戶登入資料
    @FormUrlEncoded
    @POST("get_userlogin.php")
    Call<UserLogin> getUserLoginInfo(@Field("account") String account);

    //取得用戶帳號夾帶資料
    @FormUrlEncoded
    @POST("get_acdata.php")
    Call<UserAcData> getUserAcData(@Field("account")   String account);

    //註

    //註冊新用戶
    @FormUrlEncoded
    @POST("register_user.php")
    Call<UserLogin> registerNewUser(@Field("account")  String account,
                                    @Field("phone")    String phone,
                                    @Field("type")     String type);

    //註冊新帳號夾帶資料
    @FormUrlEncoded
    @POST("register_acdata.php")
    Call<UserAcData> registerNewAcdata(@Field("account")   String account,
                                      @Field("time")      String time,
                                      @Field("name")      String name,
                                      @Field("email")     String email,
                                      @Field("picture")   String picture,
                                      @Field("type")      String type);

    //註冊新手機夾帶資料
    @FormUrlEncoded
    @POST("register_phdata.php")
    Call<UserAcData> registerNewPhdata(@Field("account")   String account,
                                       @Field("time")      String time,
                                       @Field("name")      String name,
                                       @Field("email")     String email,
                                       @Field("picture")   String picture,
                                       @Field("type")      String type);


    //上傳照片
    @Multipart
    @POST("upload.php")
    Call<String> uploadFile(@Part("phone") String phone, @Part MultipartBody.Part file);








}
