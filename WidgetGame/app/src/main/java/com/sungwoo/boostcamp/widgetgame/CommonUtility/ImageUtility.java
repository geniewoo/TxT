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
import com.sungwoo.boostcamp.widgetgame.Repositories.FullGameRepo;
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

    public static void startSelectImageActivity(AppCompatActivity appCompatActivity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        appCompatActivity.startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    public static void saveImageInFilesDirectory(Context context, Uri uri, String dirPath, String fileName) {//TODO Picasso를 이용하여 Bitmap을 인자로 받기
        Bitmap userImageBitmap = null;
        try {
            userImageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (userImageBitmap != null) {
            File dir = new File(context.getFilesDir() + File.separator + dirPath);
            saveBitmapToFile(dir, fileName, userImageBitmap, Bitmap.CompressFormat.PNG, SAVE_BITMAP_TO_FILE_QUALITY);
        }
    }

    private static void saveBitmapToFile(File dir, String fileName, Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        if(!dir.getParentFile().isDirectory()){
            dir.getParentFile().mkdir();
        }

        if(!dir.isDirectory()){
            dir.mkdir();
        }

        File imageFile = new File(dir, fileName);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(format, quality, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    public static File getPageImageFromLocal(Context context, int index) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(File.separator);
        stringBuffer.append(context.getString(R.string.LOCAL_STORAGE_MAKE_GAME_DIR));
        stringBuffer.append(File.separator);
        stringBuffer.append(context.getString(R.string.LOCAL_MAKE_GAME_PAGE_IMAGE_FILE_NAME));
        stringBuffer.append(index);
        stringBuffer.append(context.getString(R.string.FILE_EXPANDER_PNG));

        return new File(context.getFilesDir().toString(), stringBuffer.toString());
    }

    public static File getInfoImageFromLocal(Context context) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(File.separator);
        stringBuffer.append(context.getString(R.string.LOCAL_STORAGE_MAKE_GAME_DIR));
        stringBuffer.append(File.separator);
        stringBuffer.append(context.getString(R.string.LOCAL_MAKE_GAME_INFO_IMAGE_FILE_NAME));

        return new File(context.getFilesDir().toString(), stringBuffer.toString());
    }
}
