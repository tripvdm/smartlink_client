package com.lipakov.smartlink.service.api;

import okhttp3.ResponseBody;
import retrofit2.Call;

public interface CrudApi {
    Call<ResponseBody> crudData(RestApi restApi, Crud crud);
}
