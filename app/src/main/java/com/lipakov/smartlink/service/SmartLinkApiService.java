package com.lipakov.smartlink.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.service.api.SmartLinkApi;

import java.util.concurrent.TimeUnit;

import io.reactivex.Emitter;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SmartLinkApiService {
    private static final String TAG = SmartLinkApiService.class.getSimpleName();
    public static final String BASE_URL = "https://localhost:8080";

    private final Retrofit retrofit;

    public SmartLinkApiService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build();
    }

    public void callResponse(final Emitter emitter, final SmartLink smartLink) {
        SmartLinkApi smartLinkApi = retrofit.create(SmartLinkApi.class);
        Call<ResponseBody> call = smartLinkApi.addSmartLink(smartLink);
        call.enqueue(new Callback<  >() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.i(TAG, response.message());
                emitter.onComplete();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
                emitter.onComplete();
            }
        });
    }

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

}
