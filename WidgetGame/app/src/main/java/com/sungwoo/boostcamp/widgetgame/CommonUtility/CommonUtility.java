package com.sungwoo.boostcamp.widgetgame.CommonUtility;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by psw10 on 2017-02-09.
 */

public class CommonUtility {
    public static final int SAVE_BITMAP_TO_FILE_QUALITY = 100;

    private static final String TAG = "CommonUtility";

    public static boolean isNetworkAvailableShowErrorMessageIfNeeded(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            Toast.makeText(context, R.string.COMMON_NETWORK_IS_NOT_AVAILABLE, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static void displayNetworkError(Context context) {
        Toast.makeText(context, R.string.COMMON_NETWORK_ERROR, Toast.LENGTH_SHORT).show();
    }

    public static CommonRepo.UserRepo getUserRepoFromPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREF_USER), MODE_PRIVATE);
        String email = preferences.getString(context.getString(R.string.PREF_EMAIL), "");
        String password = preferences.getString(context.getString(R.string.PREF_PASSWORD), "");
        String nickname = preferences.getString(context.getString(R.string.PREF_NICKNAME), "");
        String imageUrl = preferences.getString(context.getString(R.string.PREF_IMAGE_URL), "");

        return new CommonRepo.UserRepo(email, password, nickname, imageUrl);
    }

    public static void deleteAllUserPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREF_USER), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static boolean saveBitmapToFile(File dir, String fileName, Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        dir.getParentFile().mkdir();
        dir.mkdir();
        File imageFile = new File(dir, fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(format, quality, fileOutputStream);
            fileOutputStream.close();
            return true;
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
        return false;
    }
}