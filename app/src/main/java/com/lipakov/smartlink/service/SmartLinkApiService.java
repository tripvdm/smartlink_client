package com.lipakov.smartlink.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lipakov.smartlink.R;
import com.lipakov.smartlink.service.api.Crud;
import com.lipakov.smartlink.service.api.CrudApi;
import com.lipakov.smartlink.service.api.RestApi;

import java.util.Arrays;

import io.reactivex.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmartLinkApiService {
    private static final String TAG = SmartLinkApiService.class.getSimpleName();
    private final Context context;
    private final CrudApi crudApi;
    public SmartLinkApiService(Context context, CrudApi crudApi) {
        this.context = context;
        this.crudApi = crudApi;
    }

    public void callResponse(final Emitter emitter, Crud crud) {
        RestApi restApi = RetrofitService.getInterface();
        Call<ResponseBody> call = crudApi.crudData(restApi, crud);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.i(TAG, response.message());
                emitter.onComplete();
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                String stackTrace = Arrays.toString(t.getStackTrace());
                Log.e(TAG, stackTrace);
                emitter.onError(new Throwable(context.getString(R.string.error_of_connection)));
            }
        });
    }

}
