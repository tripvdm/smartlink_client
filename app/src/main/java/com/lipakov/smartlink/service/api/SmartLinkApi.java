package com.lipakov.smartlink.service.api;

import com.lipakov.smartlink.model.SmartLink;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SmartLinkApi {

    @POST("/amrtlink_api/add")
    Call<ResponseBody> addSmartLink(@Body  SmartLink smartLink);
}
