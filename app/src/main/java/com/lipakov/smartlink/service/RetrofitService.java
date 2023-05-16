package com.lipakov.smartlink.service;

import com.lipakov.smartlink.service.api.RestApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final String BASE_URL = "http://192.168.43.254:8080";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).build())
            .build();

    public static RestApi getInterface() {
        return retrofit.create(RestApi.class);
    }
}
