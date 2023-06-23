package com.lipakov.smartlink.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.model.UserSl;

import java.util.List;

public class SmartLinkViewModel extends ViewModel {
    private static final String TAG = SmartLinkViewModel.class.getSimpleName();
    private static final String SMART_DATA_KEY = "smartLinkData";
    private final SmartLinkRepository repository;
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<List<SmartLink>> smartLinkMutableLiveData;
    private UserSl userSl;

    public SmartLinkViewModel(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
        repository = new SmartLinkRepository();
        smartLinkMutableLiveData = savedStateHandle.get(SMART_DATA_KEY);
    }

    public MutableLiveData<List<SmartLink>> getSmartLinkMutableLiveData(Context context) {
        if (smartLinkMutableLiveData == null) {
            smartLinkMutableLiveData = getRefreshingSmartLinkMutableLiveData(context);
        }
        return smartLinkMutableLiveData;
    }

    public MutableLiveData<List<SmartLink>> getRefreshingSmartLinkMutableLiveData(Context context) {
        userSl = getUserSl(context);
        smartLinkMutableLiveData = loadMoviesData(userSl);
        return smartLinkMutableLiveData;
    }
    private UserSl getUserSl(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonUserSl = sharedPreferences.getString("usersl", "");
        return gson.fromJson(jsonUserSl, UserSl.class);
    }
    private MutableLiveData<List<SmartLink>> loadMoviesData(UserSl userSl) {
        return repository.getSmartLinkMutableLiveData(userSl);
    }
    public void saveState() {
        savedStateHandle.set(SMART_DATA_KEY, smartLinkMutableLiveData.getValue());
    }
}
