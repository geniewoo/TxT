package com.sungwoo.boostcamp.widgetgame.upload;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.FullGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.GameInfo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Page;
import com.sungwoo.boostcamp.widgetgame.RetrofitRequests.GameInformationRetrofit;
import com.sungwoo.boostcamp.widgetgame.RetrofitRequests.UserInformationRetrofit;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SungWoo on 2017-02-17.
 */

public class Upload {
    private static final String TAG = Upload.class.getSimpleName();
    public static final int UPLOAD_SUCCESS = 100;
    public static final int UPLOAD_FAIL = 500;

    public static final int USER_INFORMATION = 100;

    public static void uploadGameRepo(final Context context, FullGameRepo fullGameRepo) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static void uploadGameImages(final Context context, FullGameRepo fullGameRepo) {
        HashMap<String, RequestBody> uploadGameMap = new HashMap<>();
        GameInfo gameInfo = fullGameRepo.getGameInfo();
        if (!gameInfo.getGameImagePath().equals(context.getString(R.string.LOCAL_NO_IMAGE_FILE))) {
            File file = ImageUtility.getInfoImageFromLocal(context);
            RequestBody requestBody = RequestBody.create(MediaType.parse(context.getString(R.string.RETROFIT_FILE_FORMAT_IMAGE)), file);
            uploadGameMap.put("file\"; filename=\"" + gameInfo.getGameImagePath() + "\" ", requestBody);
        }

        RealmList<Page> pages = gameInfo.getPages();
        Log.d(TAG, "here?");
        for (Page page : pages) {
            if (!page.getImagePath().equals(context.getString(R.string.LOCAL_NO_IMAGE_FILE))) {
                File file = ImageUtility.getPageImageFromLocal(context, page.getIndex());
                RequestBody requestBody = RequestBody.create(MediaType.parse(context.getString(R.string.RETROFIT_FILE_FORMAT_IMAGE)), file);
                uploadGameMap.put("file\"; filename=\"" + page.getImagePath() + "\" ", requestBody);
            }
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GameInformationRetrofit gameInformationRetrofit = retrofit.create(GameInformationRetrofit.class);
        Call<CommonRepo.ResultCodeRepo> uploadGameRepoCodeRepoCall = gameInformationRetrofit.uploadGameRepo(fullGameRepo);
        uploadGameRepoCodeRepoCall.enqueue(new Callback<CommonRepo.ResultCodeRepo>() {
            @Override
            public void onResponse(Call<CommonRepo.ResultCodeRepo> call,
                                   Response<CommonRepo.ResultCodeRepo> response) {
                CommonRepo.ResultCodeRepo resultCodeRepo = response.body();
                switch (resultCodeRepo.getCode()) {
                    case UPLOAD_SUCCESS:
                        Toast.makeText(context, "업로드 성공!!", Toast.LENGTH_LONG).show();
                        break;
                    case UPLOAD_FAIL:
                        Toast.makeText(context, R.string.COMMON_SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<CommonRepo.ResultCodeRepo> call, Throwable t) {
                CommonUtility.displayNetworkError(context);
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

        String nickname = fullGameRepo.getMaker().getNickName();
        String gameTitle = gameInfo.getGameTitle();
        Call<CommonRepo.ResultCodeRepo> uploadGameImagesCodeRepoCall = gameInformationRetrofit.uploadGameImages(uploadGameMap, nickname, gameTitle);
        uploadGameImagesCodeRepoCall.enqueue(new Callback<CommonRepo.ResultCodeRepo>() {
            @Override
            public void onResponse(Call<CommonRepo.ResultCodeRepo> call,
                                   Response<CommonRepo.ResultCodeRepo> response) {
                CommonRepo.ResultCodeRepo resultCodeRepo = response.body();
                switch (resultCodeRepo.getCode()) {
                    case UPLOAD_SUCCESS:
                        Toast.makeText(context, "업로드 성공!!", Toast.LENGTH_LONG).show();
                        break;
                    case UPLOAD_FAIL:
                        Toast.makeText(context, R.string.COMMON_SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<CommonRepo.ResultCodeRepo> call, Throwable t) {
                CommonUtility.displayNetworkError(context);
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }
    public static void uploadUserImageToServer(final Context context, String dirPath, String localFileName, String serverFileName, int uploadCode) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        File file = new File(context.getFilesDir() + File.separator + dirPath + File.separator + localFileName);
        if (!file.canRead()) {
            Log.e(TAG, context.getString(R.string.ERROR_IMAGE_IS_NOT_EXISTS));
            return;
        }
        Log.d(TAG, file.getName());
        RequestBody fileBody = RequestBody.create(MediaType.parse(context.getString(R.string.RETROFIT_FILE_FORMAT_IMAGE)), file);
        RequestBody fileNameBody = RequestBody.create(MediaType.parse(context.getString(R.string.RETROFIT_FILE_FORMAT_STRING)), serverFileName);

        Call<CommonRepo.ResultCodeRepo> codeRepoCall = null;

        switch (uploadCode){
            case USER_INFORMATION :
                UserInformationRetrofit userInformationRetrofit = retrofit.create(UserInformationRetrofit.class);
                codeRepoCall = userInformationRetrofit.uploadUserImage(fileBody, fileNameBody);
                break;
            default:
                Log.e(TAG, "there is wrong uploadCode");
                return;
        }
        codeRepoCall.enqueue(new Callback<CommonRepo.ResultCodeRepo>() {
            @Override
            public void onResponse(Call<CommonRepo.ResultCodeRepo> call, Response<CommonRepo.ResultCodeRepo> response) {
                CommonRepo.ResultCodeRepo resultCodeRepo = response.body();
                switch (resultCodeRepo.getCode()) {
                    case UPLOAD_SUCCESS:
                        break;
                    case UPLOAD_FAIL:
                        Toast.makeText(context, R.string.COMMON_SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<CommonRepo.ResultCodeRepo> call, Throwable t) {
                CommonUtility.displayNetworkError(context);
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }
}
