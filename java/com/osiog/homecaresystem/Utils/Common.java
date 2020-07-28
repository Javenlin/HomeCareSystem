package com.osiog.homecaresystem.Utils;

import com.osiog.homecaresystem.Model.UserAcData;
import com.osiog.homecaresystem.Model.UserLogin;
import com.osiog.homecaresystem.Retrofit.IHomeCareAPI;
import com.osiog.homecaresystem.Retrofit.RetrofitClient;

/**
 * Created by OSIOG on 2018/7/12.
 */

public class Common {

    //CURRENT_OBJ
    public static UserLogin currentUserlogin = null;
    public static UserAcData currentUserAcData = null;


    //BASE_URL
    public static final String BASE_URL = "http://192.168.43.112:80/HomeCarePHP/";

    //RetrofitClient
    public static IHomeCareAPI getAPI() {
        return RetrofitClient.getClient(BASE_URL).create(IHomeCareAPI.class);
    }
}
