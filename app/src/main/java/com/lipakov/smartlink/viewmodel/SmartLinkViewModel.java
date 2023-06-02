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
    private static final String VISIBILITY_OF_WARNING = "visibilityOfWarning";
    private final SmartLinkRepository repository;
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<List<SmartLink>> smartLinkMutableLiveData;
    private final Integer visibilityOfWarning;
    public SmartLinkViewModel(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
        repository = new SmartLinkRepository();
        smartLinkMutableLiveData = savedStateHandle.get(SMART_DATA_KEY);
        visibilityOfWarning = savedStateHandle.get(VISIBILITY_OF_WARNING);
    }

    public MutableLiveData<List<SmartLink>> getSmartLinkMutableLiveData(Context context, boolean refreshed) {
        if (smartLinkMutableLiveData == null || refreshed) {
            UserSl userSl = getUserSl(context);
            smartLinkMutableLiveData = loadMoviesData(userSl);
        }
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
    public void saveState(int visibility) {
        savedStateHandle.set(SMART_DATA_KEY, smartLinkMutableLiveData.getValue());
        savedStateHandle.set(VISIBILITY_OF_WARNING, visibility);
    }
}
