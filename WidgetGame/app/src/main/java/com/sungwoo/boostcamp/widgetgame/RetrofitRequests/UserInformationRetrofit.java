package com.sungwoo.boostcamp.widgetgame.RetrofitRequests;

import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;

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

public interface UserInformationRetrofit {
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String IMAGE_URL = "imageUrl";
    public static final String NICKNAME = "nickname";
    public static final String RETROFIT_FILE_DESCRIPTION = "file\"; filename=\"name.png\" ";
    public static final String FILE_NAMES = "fileNames";

    @GET("user/join/test")
    Call<CommonRepo.ResultCodeRepo> testJoinServer(@Query(EMAIL) String email, @Query(PASSWORD) String password, @Query(NICKNAME) String nickname);

    @GET("user/login/test")
    Call<CommonRepo.ResultNicknameRepo> testLoginServer(@Query(EMAIL) String email, @Query(PASSWORD) String password);

    @GET("user/update/imageUrl")
    Call<CommonRepo.ResultCodeRepo> updateUserImage(@Query(EMAIL) String email, @Query(PASSWORD) String password, @Query(IMAGE_URL) String imageUrl);

    @Multipart
    @POST("upload/userImageFile")
    Call<CommonRepo.ResultCodeRepo> uploadUserImage(@Part(RETROFIT_FILE_DESCRIPTION) RequestBody file, @Part(FILE_NAMES) RequestBody name);
}
