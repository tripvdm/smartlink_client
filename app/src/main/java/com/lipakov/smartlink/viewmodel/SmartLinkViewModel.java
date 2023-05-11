package com.lipakov.smartlink.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.service.api.RestApi;

import java.util.ArrayList;
import java.util.List;

public class SmartLinkViewModel extends ViewModel {
    private static final String TAG = SmartLinkViewModel.class.getSimpleName();

    private RestApi restApi;
    private final MutableLiveData<List<SmartLink>> smartLinkLiveData;
    private final List<SmartLink> smartLinkList = new ArrayList<>();

    public SmartLinkViewModel() {
        smartLinkLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<SmartLink>> getSmartLinkLiveData() {
        return smartLinkLiveData;
    }

    public void init() {
        smartLinkLiveData.setValue(smartLinkList);
    }

}
