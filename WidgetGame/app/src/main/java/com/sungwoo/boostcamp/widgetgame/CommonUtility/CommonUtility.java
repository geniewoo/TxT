package com.sungwoo.boostcamp.widgetgame.CommonUtility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.sungwoo.boostcamp.widgetgame.R;

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
}