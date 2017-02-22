package com.sungwoo.boostcamp.widgetgame.CommonUtility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;

import java.io.File;
import java.util.HashMap;

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

    public static StringBuffer getServerGameImageFolderPathStringBuffer(Context context, String nickname, String gameTitle, String fileName) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(context.getString(R.string.URL_GAME_IMAGE_SERVER_FOLDER));
        stringBuffer.append(File.separator);
        stringBuffer.append(nickname);
        stringBuffer.append(File.separator);
        stringBuffer.append(gameTitle);
        stringBuffer.append(File.separator);
        stringBuffer.append(fileName);
        return stringBuffer;
    }

    public static HashMap<String, Integer> getSoundMap(Context context) {
        HashMap<String, Integer> soundMap = new HashMap<>();
        soundMap.put(context.getString(R.string.SPINNER_SOUND_1), R.raw.coins);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_2), R.raw.crash);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_3), R.raw.doorbell);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_4), R.raw.glass);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_5), R.raw.gun);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_6), R.raw.laugh);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_7), R.raw.open);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_8), R.raw.opendoor);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_9), R.raw.ring);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_10), R.raw.shotgun);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_11), R.raw.sword);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_12), R.raw.wind);
        soundMap.put(context.getString(R.string.SPINNER_SOUND_13), R.raw.witchlaugh);
        return soundMap;
    }

    public static void showNeutralDialog(Context context, int titleResId, int itemResId, int neutralBtnResId) {
        new MaterialDialog.Builder(context)
                .title(titleResId)
                .items(itemResId)
                .neutralText(neutralBtnResId)
                .show();
    }
}