package com.sungwoo.boostcamp.widgetgame.CommonUtility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by psw10 on 2017-02-09.
 */

public class CommonUtility {

    private static final String TAG = CommonUtility.class.getSimpleName();

    public static boolean isNetworkAvailableShowErrorMessageIfNeeded(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivityManager.getActiveNetworkInfo() == null || !connectivityManager.getActiveNetworkInfo().isConnected()) {
            Toast.makeText(context, R.string.COMMON_NETWORK_IS_NOT_AVAILABLE, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static void displayNetworkError(Context context) {
        Toast.makeText(context, R.string.COMMON_NETWORK_ERROR, Toast.LENGTH_SHORT).show();
    }

    public static CommonRepo.UserRepo getUserRepoFromPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREF_USER), MODE_PRIVATE);

        String email = preferences.getString(context.getString(R.string.PREF_USER_EMAIL), "");
        String password = preferences.getString(context.getString(R.string.PREF_USER_PASSWORD), "");
        String nickname = preferences.getString(context.getString(R.string.PREF_USER_NICKNAME), "");
        String imageUrl = preferences.getString(context.getString(R.string.PREF_USER_IMAGE_URL), "");

        return new CommonRepo.UserRepo(email, password, nickname, imageUrl);
    }

    public static void deleteAllUserPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREF_USER), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}