package com.sungwoo.boostcamp.widgetgame.RetrofitRequests;

import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by SungWoo on 2017-02-08.
 */

public interface UserInformRetrofit {
    @GET("join/test")
    Call<CommonRepo.ResultCodeRepo> testJoinServer(@Query("email") String email, @Query("password") String password, @Query("nickname") String nickname);

    @GET("login/test")
    Call<CommonRepo.ResultNicknameRepo> testLoginServer(@Query("email") String email, @Query("password") String password);

    @Multipart
    @POST("upload/userImageFile")
    Call<CommonRepo.ResultCodeRepo> uploadUserImage(@Part("file\"; filename=\"pp.png\" ") RequestBody file);
}
