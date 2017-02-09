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
    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()){
            return true;
        }else{
            Toast.makeText(context, context.getString(R.string.COMMON_NETWORK_IS_NOT_AVAILABLE), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public static void networkError(Context context){
        Toast.makeText(context, context.getString(R.string.COMMON_NETWORK_ERROR), Toast.LENGTH_SHORT).show();
    }

    public static CommonRepo.UserRepo getUserRepoFromPreference(Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREF_USER), MODE_PRIVATE);
        String email = preferences.getString(context.getString(R.string.PREF_EMAIL), "");
        String password = preferences.getString(context.getString(R.string.PREF_PASSWORD), "");
        String nickname = preferences.getString(context.getString(R.string.PREF_NICKNAME), "");
        String imageUrl = preferences.getString(context.getString(R.string.PREF_IMAGE_URL), "");

        return new CommonRepo.UserRepo(email, password, nickname, imageUrl);
    }
}