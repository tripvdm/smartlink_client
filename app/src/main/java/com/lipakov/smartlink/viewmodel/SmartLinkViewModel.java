package com.lipakov.smartlink.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.model.UserSl;

import java.util.List;

public class SmartLinkViewModel extends ViewModel {
    private static final String TAG = SmartLinkViewModel.class.getSimpleName();
    private final SmartLinkRepository repository;
    public SmartLinkViewModel() {
        repository = new SmartLinkRepository();
    }
    public MutableLiveData<List<SmartLink>> getSmartLinkMutableLiveData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonUserSl = sharedPreferences.getString("usersl", "");
        UserSl userSl = gson.fromJson(jsonUserSl, UserSl.class);
        return loadMoviesData(userSl);
    }

    private MutableLiveData<List<SmartLink>> loadMoviesData(UserSl userSl) {
        return repository.getSmartLinkMutableLiveData(userSl);
    }

}
