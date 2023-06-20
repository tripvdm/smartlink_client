package com.lipakov.smartlink.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.model.UserSl;
import com.lipakov.smartlink.service.RetrofitService;
import com.lipakov.smartlink.service.api.RestApi;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmartLinkRepository {
    private static final String TAG = SmartLinkRepository.class.getSimpleName();
    private static RestApi restApi;
    private final MutableLiveData<List<SmartLink>> smartLinkMutableLiveData = new MutableLiveData<>();

    public SmartLinkRepository() {
        restApi = RetrofitService.getInterface();
    }

    public MutableLiveData<List<SmartLink>> getSmartLinkMutableLiveData(UserSl userSl) {
        Call<List<SmartLink>> smartLinkCall = restApi.getSmartLinkListByEmail(userSl.getEmail());
        smartLinkCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<SmartLink>> call, @NonNull Response<List<SmartLink>> response) {
                List<SmartLink> smartLinkList = response.body();
                Log.i(TAG, "Size of list: " + Objects.requireNonNull(smartLinkList).size());
                smartLinkMutableLiveData.setValue(smartLinkList);
            }

            @Override
            public void onFailure(@NonNull Call<List<SmartLink>> call, Throwable t) {
                smartLinkMutableLiveData.setValue(null);
            }
        });
        return smartLinkMutableLiveData;
    }
}
