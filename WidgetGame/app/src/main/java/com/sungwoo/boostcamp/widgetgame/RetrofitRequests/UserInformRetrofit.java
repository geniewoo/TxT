package com.sungwoo.boostcamp.widgetgame.RetrofitRequests;

import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by SungWoo on 2017-02-08.
 */

public interface UserInformRetrofit {
    @GET("join/test")
    Call<CommonRepo.ResultCodeRepo> testJoinServer(@Query("email") String email, @Query("password") String password, @Query("nickname") String nickname);

    @GET("login/test")
    Call<CommonRepo.ResultCodeRepo> testLoginServer(@Query("email") String email, @Query("password") String password);
}
