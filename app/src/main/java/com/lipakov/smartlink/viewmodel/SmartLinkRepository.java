package com.lipakov.smartlink.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.model.UserSl;
import com.lipakov.smartlink.service.RetrofitService;
import com.lipakov.smartlink.service.api.RestApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmartLinkRepository {
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
                smartLinkMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<SmartLink>> call, Throwable t) {
                smartLinkMutableLiveData.postValue(null);
            }
        });
        return smartLinkMutableLiveData;
    }
}
