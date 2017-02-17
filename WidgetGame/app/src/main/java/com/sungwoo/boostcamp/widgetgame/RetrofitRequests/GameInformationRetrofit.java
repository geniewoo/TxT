package com.sungwoo.boostcamp.widgetgame.RetrofitRequests;

import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.FullGameRepo;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by SungWoo on 2017-02-17.
 */

public interface GameInformationRetrofit {
    @Multipart
    @POST("upload/game")
    Call<CommonRepo.ResultCodeRepo> uploadGame(@PartMap Map<String, RequestBody> map, @Part("FullGameRepo")
            RequestBody fullGameRepo);
}
