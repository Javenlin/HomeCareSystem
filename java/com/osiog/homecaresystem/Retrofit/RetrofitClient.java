package com.osiog.homecaresystem.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by OSIOG on 2018/7/12.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())         //加入-converter-gson
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  //加入-adapter-rxjava2
                    .build();
        }
        return retrofit;
    }
}
