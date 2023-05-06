package com.lipakov.smartlink.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lipakov.smartlink.model.SmartLink;

import java.util.ArrayList;
import java.util.List;

public class SmartLinkViewModel extends ViewModel {
    private static final String TAG = SmartLinkViewModel.class.getSimpleName();

    private final MutableLiveData<List<SmartLink>> smartLinkLiveData;
    private final List<SmartLink> smartLinkList = new ArrayList<>();

    /**TODO Call Rest API**/
    public SmartLinkViewModel() {
        this.smartLinkLiveData = new MutableLiveData<>();

    }

    public MutableLiveData<List<SmartLink>> getSmartLinkLiveData() {
        return smartLinkLiveData;
    }

    public void init(){
        smartLinkLiveData.setValue(smartLinkList);
    }

}
