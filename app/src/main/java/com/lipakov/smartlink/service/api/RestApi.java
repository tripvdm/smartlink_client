package com.lipakov.smartlink.service.api;

import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.model.UserSl;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface RestApi {
    @POST("/api/addLink")
    Call<ResponseBody> addSmartLink(@Body SmartLink smartLink);

    @POST("/api/addUser")
    Call<ResponseBody> addUserSl(@Body UserSl userSl);

    @GET("/api/{email}")
    Call<List<SmartLink>> getSmartLinkListByEmail(@Path("email") String email);

    @PUT("/api/{userId}/{smartLinkId}")
    Call<List<SmartLink>> updateSmartLink(@Body SmartLink smartLink,
                                          @Path("userId") Long userId, @Path("smartLinkId") Long smartLinkId);
}
