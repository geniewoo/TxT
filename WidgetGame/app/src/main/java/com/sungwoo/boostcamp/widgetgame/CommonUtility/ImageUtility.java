package com.sungwoo.boostcamp.widgetgame.CommonUtility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;
import com.sungwoo.boostcamp.widgetgame.RetrofitRequests.UserInformationRetrofit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by psw10 on 2017-02-16.
 */

public class ImageUtility {
    private static final String TAG = ImageUtility.class.getSimpleName();
    private static final int SAVE_BITMAP_TO_FILE_QUALITY = 100;
    public static final int REQ_CODE_SELECT_IMAGE = 100;

    public static final int USER_INFORMATION = 100;
    public static final int UPLOAD_IMAGE_SUCCESS = 100;
    public static final int UPLOAD_IMAGE_FAIL = 500;

    public static void startSelectImageActivity(AppCompatActivity appCompatActivity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        appCompatActivity.startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    public static void saveImageInFilesDirectory(Context context, Uri uri, String dirPath, String fileName) {
        Bitmap userImageBitmap = null;
        try {
            userImageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (userImageBitmap != null) {
            File dir = new File(context.getFilesDir() + File.separator + dirPath);
            Log.d(TAG, context.getFilesDir() + File.separator + dirPath + fileName);
            saveBitmapToFile(dir, fileName, userImageBitmap, Bitmap.CompressFormat.PNG, SAVE_BITMAP_TO_FILE_QUALITY);
        }
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
                    case UPLOAD_IMAGE_SUCCESS:
                        break;
                    case UPLOAD_IMAGE_FAIL:
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
    private static void saveBitmapToFile(File dir, String fileName, Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        Log.d(TAG, "mkdir1");
        if(!dir.getParentFile().isDirectory()){
            dir.getParentFile().mkdir();
        }
        Log.d(TAG, "mkdir2");
        if(!dir.isDirectory()){
            dir.mkdir();
        }
        Log.d(TAG, "mkdir3");
        File imageFile = new File(dir, fileName);
        Log.d(TAG, "imageFile : " + imageFile.getName());
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(format, quality, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "error : " + e.getMessage());
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
