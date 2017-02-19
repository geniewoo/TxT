package com.sungwoo.boostcamp.widgetgame.RetrofitRequests;

import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.FindGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.FullGameRepo;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by SungWoo on 2017-02-17.
 */

public interface GameInformationRetrofit {

    public static final String NICKNAME = "nickname";
    public static final String GAME_TITLE = "gameTitle";

    @Multipart
    @POST("upload/game/images")
    Call<CommonRepo.ResultCodeRepo> uploadGameImages(@PartMap Map<String, RequestBody> map, @Query(NICKNAME) String nickname, @Query(GAME_TITLE) String gameTitle);

    @POST("upload/game/fullGameRepo")
    Call<CommonRepo.ResultCodeRepo> uploadGameRepo(@Body FullGameRepo fullGameRepo);

    @GET("game/get/gameList")
    Call<FindGameRepo> getGameList(@Query("skip") int skip, @Query("num") int num, @Query("sort") String sort);
}
