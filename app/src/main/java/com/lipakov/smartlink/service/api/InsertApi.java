package com.lipakov.smartlink.service.api;

import okhttp3.ResponseBody;
import retrofit2.Call;

public interface InsertApi {
    Call<ResponseBody> addData(RestApi restApi);
}
