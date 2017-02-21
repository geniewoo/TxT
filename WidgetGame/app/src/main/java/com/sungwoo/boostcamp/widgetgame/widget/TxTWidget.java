package com.sungwoo.boostcamp.widgetgame.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.LoginActivity;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.FullGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Page;
import com.sungwoo.boostcamp.widgetgame.Repositories.PlayGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Selection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

import static com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility.getSoundMap;

/**
 * Created by SungWoo on 2017-02-20.
 */

public class TxTWidget extends AppWidgetProvider {
    private static MediaPlayer mMediaPlayer;
    private static Vibrator mVibrator;
    private static final int IN_NO_GAME = 0;
    private static final int IN_INFO = IN_NO_GAME + 1;
    private static final int IN_SELECTIONS = IN_INFO + 1;
    private static final int IN_STORY = IN_SELECTIONS + 1;
    private static final int IN_GAME_OVER = IN_STORY + 1;
    private static final int IN_GAME_CLEAR = IN_GAME_OVER + 1;
    private static final int IN_MENU = IN_GAME_CLEAR + 1;

    private static boolean mIsMenuStage = false;
    private static int mNowStage;
    private static boolean mIsGamePlayingFlipper1;
    private static List<Integer> mAppWidgetIds = new ArrayList<>();
    private static final String TAG = TxTWidget.class.getSimpleName();
    private static Realm mRealm = null;
    private static FullGameRepo mFullGameRepo = null;
    private static final String ACTION_WIDGET_MENU_FLIPPER_BTN_CLICKED =
            "com.sungwoo.boostcamp.widgetgame.action.MENU_FLIPPER_BTN_CLICKED";
    public static final String ACTION_WIDGET_DISPLAY_NEW_GAME =
            "com.sungwoo.boostcamp.widgetgame.action.DISPLAY_NEW_GAME";
    private static final String ACTION_WIDGET_GAME_INFO_START_GAME_BTN =
            "com.sungwoo.boostcamp.widgetgame.action.GAME_INFO_START_BTN";
    private static final String ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN =
            "com.sungwoo.boostcamp.widgetgame.action.GAME_PLAYING_SELECTION_BTN";
    private static final String ACTION_WIDGET_GAME_PLAYING_NO_SELECTION_NEXT_BTN =
            "com.sungwoo.boostcamp.widgetgame.action.GAME_PLAYING_NO_SELECTION_NEXT_BTN";
    private static final String ACTION_WIDGET_MENU_START_GAME_BTN =
            "com.sungwoo.boostcamp.widgetgame.action.MENU_START_GAME_BTN";
    public static final String ACTION_WIDGET_NO_GAME_START_APP_BTN =
            "com.sungwoo.boostcamp.widgetgame.action.NO_GAME_START_APP_BTN";
    public static final String ACTION_WIDGET_MENU_START_APP_BTN =
            "com.sungwoo.boostcamp.widgetgame.action.MENU_START_APP_BTN";
    private static final String PAGE_CHOICE = "Choice";
    private static final String PAGE_STORY = "Story";
    private static final String PAGE_GAME_OVER = "Game Over";
    private static final String PAGE_GAME_CLEAR = "Game Clear";

    private static HashMap<String, Integer> mSoundMap;
    private static final int[] SELECTION_IDS1 = {R.id.widget_playing_game1_selection1, R.id.widget_playing_game1_selection2, R.id.widget_playing_game1_selection3, R.id.widget_playing_game1_selection4};
    private static final int[] SELECTION_IDS2 = {R.id.widget_playing_game2_selection1, R.id.widget_playing_game2_selection2, R.id.widget_playing_game2_selection3, R.id.widget_playing_game2_selection4};
    private static int[] mSelectedTargetIndex = new int[4];

    private static final long[] VIBRATOR_PATTERN = {0, 300, 150, 400};

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (mRealm == null) {
            mRealm = Realm.getDefaultInstance();
        }
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        if (mVibrator == null) {
            mVibrator = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (mSoundMap == null) {
            Log.d(TAG, "getSoundMap");
            mSoundMap = getSoundMap(context);
        }

        String action = intent.getAction();
        if (mFullGameRepo == null && mRealm.where(PlayGameRepo.class).findAll().size() == 1
                && mRealm.where(PlayGameRepo.class).findAll().get(0).getPlayable()) {
            setFullGameRepoMemberField();
        }

        switch (action) {
            case ACTION_WIDGET_DISPLAY_NEW_GAME:
                setFullGameRepoMemberField();
                showGameInfoLayout(context);
                if (mIsMenuStage) {
                    flipMenu(context);
                }
                break;
            case ACTION_WIDGET_MENU_FLIPPER_BTN_CLICKED:
                flipMenu(context);
                break;
            case ACTION_WIDGET_GAME_INFO_START_GAME_BTN:
                showGamePageLayout(context, 1);
                break;
            case ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + "0":
                showGamePageLayout(context, mSelectedTargetIndex[0]);
                break;
            case ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + "1":
                showGamePageLayout(context, mSelectedTargetIndex[1]);
                break;
            case ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + "2":
                showGamePageLayout(context, mSelectedTargetIndex[2]);
                break;
            case ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + "3":
                showGamePageLayout(context, mSelectedTargetIndex[3]);
                break;
            case ACTION_WIDGET_GAME_PLAYING_NO_SELECTION_NEXT_BTN:
                switch (mNowStage) {
                    case IN_GAME_CLEAR:
                        flipMenu(context);
                        break;
                    case IN_GAME_OVER:
                        flipMenu(context);
                        break;
                    case IN_STORY:
                        showGamePageLayout(context, getPlayGameIndexPreference(context) + 1);
                        break;
                }
                break;
            case ACTION_WIDGET_MENU_START_GAME_BTN:
                flipMenu(context);
                showGameInfoLayout(context);
                break;
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        if (mRealm == null) {
            mRealm = Realm.getDefaultInstance();
        }
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        if (mVibrator == null) {
            mVibrator = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (mSoundMap == null) {
            Log.d(TAG, "getSoundMap");
            mSoundMap = getSoundMap(context);
        }

        for (int i : appWidgetIds) {
            mAppWidgetIds.add(i);
        }
        mNowStage = IN_NO_GAME;
        mIsGamePlayingFlipper1 = true;
        setPendingIntents(context);
        if (mRealm.where(PlayGameRepo.class).findAll().size() == 1
                && mRealm.where(PlayGameRepo.class).findAll().get(0).getPlayable()) {
            if (mFullGameRepo == null) {
                setFullGameRepoMemberField();
            }
            int index = getPlayGameIndexPreference(context);
            if (index == 0) {
                showGameInfoLayout(context);
            } else {
                showGamePageLayout(context, index);
            }
        }
    }

    private void setPendingIntents(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.setOnClickPendingIntent(R.id.widget_playing_game1_info_start_game_btn,
                getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_INFO_START_GAME_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_playing_game2_info_start_game_btn,
                getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_INFO_START_GAME_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_flipper_btn,
                getTxTWidgetPendingIntent(context, ACTION_WIDGET_MENU_FLIPPER_BTN_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.widget_playing_game1_no_selections_next_btn, getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_NO_SELECTION_NEXT_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_playing_game2_no_selections_next_btn, getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_NO_SELECTION_NEXT_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_menu_start_game, getTxTWidgetPendingIntent(context, ACTION_WIDGET_MENU_START_GAME_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_menu_start_app, getTxTAppPendingIntent(context, ACTION_WIDGET_MENU_START_APP_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_no_game_start_app, getTxTAppPendingIntent(context, ACTION_WIDGET_NO_GAME_START_APP_BTN));
        for (int i = 0; i < 4; i++) {
            remoteViews.setOnClickPendingIntent(SELECTION_IDS1[i], getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + i));
            remoteViews.setOnClickPendingIntent(SELECTION_IDS2[i], getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + i));
        }
        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds), remoteViews);
    }

    private int getPlayGameIndexPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE);
        return preferences.getInt(context.getString(R.string.PREF_PLAY_GAME_INDEX), 0);
    }

    private void setPlayGameIndexPreference(Context context, int index) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).edit();
        editor.putInt(context.getString(R.string.PREF_PLAY_GAME_INDEX), index);
        editor.apply();
    }

    private void changeToPlayLayout(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.setViewVisibility(R.id.widget_no_game_lo, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_play_lo, View.VISIBLE);
        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds),
                remoteViews);
    }

    private void changeToNoGameLayout(Context context) {
        mNowStage = IN_NO_GAME;
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.setViewVisibility(R.id.widget_play_lo, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_no_game_lo, View.VISIBLE);

        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds),
                remoteViews);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int i : appWidgetIds) {
            mAppWidgetIds.remove(new Integer(i));
        }
        if (mAppWidgetIds.size() == 0 && mRealm != null) {
            mRealm.close();
            mRealm = null;
            setPlayGameIndexPreference(context, 0);
        }
    }

    private int[] intListToIntArray(List<Integer> intList) {
        int[] returnIntArray = new int[intList.size()];
        for (int i = 0; i < intList.size(); i++) {
            returnIntArray[i] = intList.get(i);
        }
        return returnIntArray;
    }

    private void flipMenu(Context context) {
        if (mIsMenuStage) {
            mIsMenuStage = false;
        } else {
            mIsMenuStage = true;
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.showPrevious(R.id.widget_menu_flipper);
        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds),
                remoteViews);
    }

    private void setFullGameRepoMemberField() {
        mFullGameRepo = mRealm.copyFromRealm(mRealm.where(PlayGameRepo.class).findAll().get(0).getFullGameRepo());
    }

    private void showGameInfoLayout(Context context) {
        setPlayGameIndexPreference(context, 0);

        switch (mNowStage) {
            case IN_NO_GAME:
                changeToPlayLayout(context);
                mNowStage = IN_INFO;
                break;
            default:
                mNowStage = IN_INFO;
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        String title = mFullGameRepo.getGameInfo().getGameTitle();
        String description = mFullGameRepo.getGameInfo().getGameDescription();
        String imagePath = mFullGameRepo.getGameInfo().getGameImagePath();
        String maker = mFullGameRepo.getMaker().getNickName();
        String email = mFullGameRepo.getMaker().getEmail();

        if (mIsGamePlayingFlipper1) {
            remoteViews.setViewVisibility(R.id.widget_playing_game2_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_playing_game2_no_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game2_info_lo, View.VISIBLE);

            remoteViews.setTextViewText(R.id.widget_playing_game2_info_title_tv, title);
            remoteViews.setTextViewText(R.id.widget_playing_game2_info_description_tv, description);
            remoteViews.setTextViewText(R.id.widget_playing_game2_info_maker_tv, maker);
            remoteViews.setTextViewText(R.id.widget_playing_game2_info_email_tv, email);

            remoteViews.setTextViewText(R.id.widget_game2_image_tv, "");

            if (imagePath != context.getString(R.string.LOCAL_NO_IMAGE_FILE)) {
                File file = ImageUtility.getPlayGameInfoImageFromLocal(context);
                Picasso.with(context).load(file).resize(200, 150).centerCrop().into(remoteViews,
                        R.id.widget_game2_image_iv, intListToIntArray(mAppWidgetIds));
            } else {
                Picasso.with(context).load(R.drawable.default_user_image).resize(200,
                        150).centerCrop().into(remoteViews, R.id.widget_game2_image_iv,
                        intListToIntArray(mAppWidgetIds));
            }
        } else {
            remoteViews.setViewVisibility(R.id.widget_playing_game1_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_playing_game1_no_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game1_info_lo, View.VISIBLE);

            remoteViews.setTextViewText(R.id.widget_playing_game1_info_title_tv, title);
            remoteViews.setTextViewText(R.id.widget_playing_game1_info_description_tv, description);
            remoteViews.setTextViewText(R.id.widget_playing_game1_info_maker_tv, maker);
            remoteViews.setTextViewText(R.id.widget_playing_game1_info_email_tv, email);

            remoteViews.setTextViewText(R.id.widget_game1_image_tv, "");

            if (imagePath != context.getString(R.string.LOCAL_NO_IMAGE_FILE)) {
                File file = ImageUtility.getPlayGameInfoImageFromLocal(context);
                Picasso.with(context).load(file).resize(200, 150).centerCrop().into(remoteViews,
                        R.id.widget_game1_image_iv, intListToIntArray(mAppWidgetIds));
            } else {
                Picasso.with(context).load(R.drawable.default_user_image).resize(200,
                        150).centerCrop().into(remoteViews, R.id.widget_game1_image_iv,
                        intListToIntArray(mAppWidgetIds));
            }
        }
        flipGame(context);

        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds),
                remoteViews);
    }

    private void showGamePageLayout(Context context, int index) {
        setPlayGameIndexPreference(context, index);

        switch (mNowStage) {
            case IN_NO_GAME:
                changeToPlayLayout(context);
                break;
        }
        Page page = mFullGameRepo.getGameInfo().getPages().get(index - 1);
        switch (page.getPage()) {
            case PAGE_CHOICE:
                mNowStage = IN_SELECTIONS;
                showGameSelectionPageLayout(context, page);
                break;
            case PAGE_STORY:
                mNowStage = IN_STORY;
                showGameStoryPageLayout(context, page);
                break;
            case PAGE_GAME_OVER:
                mNowStage = IN_GAME_OVER;
                showGameStoryPageLayout(context, page);
                break;
            case PAGE_GAME_CLEAR:
                mNowStage = IN_GAME_CLEAR;
                showGameStoryPageLayout(context, page);
                break;
        }
    }

    private PendingIntent getTxTAppPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setAction(action);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getTxTWidgetPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, TxTWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void showGameSelectionPageLayout(Context context, Page page) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        String title = page.getTitle();
        String description = page.getDescription();
        String sound = page.getSound();
        String imagePath = page.getImagePath();
        List<Selection> selections = page.getSelections();
        Boolean isVibrateOn = page.isVibrateOn();


        turnOnVibrator(isVibrateOn);
        playSound(context, sound);

        if (mIsGamePlayingFlipper1) {
            remoteViews.setTextViewText(R.id.widget_game2_title_tv, title);
            remoteViews.setTextViewText(R.id.widget_game2_image_tv, description);

            remoteViews.setViewVisibility(R.id.widget_game2_info_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_playing_game2_no_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_playing_game2_selections_lo, View.VISIBLE);
            for (int i = 0; i < selections.size(); i++) {
                remoteViews.setTextViewText(SELECTION_IDS2[i], selections.get(i).getSelectionText());
                mSelectedTargetIndex[i] = selections.get(i).getNextIndex();
            }
            if (imagePath != context.getString(R.string.LOCAL_NO_IMAGE_FILE)) {
                File file = ImageUtility.getPlayGamePageImageFromLocal(context, page.getIndex());
                Picasso.with(context).load(file).resize(200, 150).centerCrop().into(remoteViews,
                        R.id.widget_game2_image_iv, intListToIntArray(mAppWidgetIds));
            } else {
                Picasso.with(context).load(R.drawable.default_user_image).resize(200,
                        150).centerCrop().into(remoteViews, R.id.widget_game2_image_iv,
                        intListToIntArray(mAppWidgetIds));
            }
        } else {
            remoteViews.setTextViewText(R.id.widget_game1_title_tv, title);
            remoteViews.setTextViewText(R.id.widget_game1_image_tv, description);

            remoteViews.setViewVisibility(R.id.widget_game1_info_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_playing_game1_no_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_playing_game1_selections_lo, View.VISIBLE);
            for (int i = 0; i < selections.size(); i++) {
                remoteViews.setTextViewText(SELECTION_IDS1[i], selections.get(i).getSelectionText());
                mSelectedTargetIndex[i] = selections.get(i).getNextIndex();
            }
            if (imagePath != context.getString(R.string.LOCAL_NO_IMAGE_FILE)) {
                File file = ImageUtility.getPlayGamePageImageFromLocal(context, page.getIndex());
                Picasso.with(context).load(file).resize(200, 150).centerCrop().into(remoteViews,
                        R.id.widget_game1_image_iv, intListToIntArray(mAppWidgetIds));
            } else {
                Picasso.with(context).load(R.drawable.default_user_image).resize(200,
                        150).centerCrop().into(remoteViews, R.id.widget_game1_image_iv,
                        intListToIntArray(mAppWidgetIds));
            }
        }
        flipGame(context);

        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds), remoteViews);
    }

    private void showGameStoryPageLayout(Context context, Page page) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        String title = page.getTitle();
        String description = page.getDescription();
        String sound = page.getSound();
        String imagePath = page.getImagePath();
        Boolean isVibrateOn = page.isVibrateOn();

        if (mIsGamePlayingFlipper1) {
            remoteViews.setViewVisibility(R.id.widget_game2_info_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_playing_game2_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_playing_game2_no_selections_lo, View.VISIBLE);

            remoteViews.setTextViewText(R.id.widget_game2_title_tv, title);
            remoteViews.setTextViewText(R.id.widget_game2_image_tv, description);

            if (imagePath != context.getString(R.string.LOCAL_NO_IMAGE_FILE)) {
                File file = ImageUtility.getPlayGamePageImageFromLocal(context, page.getIndex());
                Picasso.with(context).load(file).resize(200, 150).centerCrop().into(remoteViews,
                        R.id.widget_game2_image_iv, intListToIntArray(mAppWidgetIds));
            } else {
                Picasso.with(context).load(R.drawable.default_user_image).resize(200,
                        150).centerCrop().into(remoteViews, R.id.widget_game2_image_iv,
                        intListToIntArray(mAppWidgetIds));
            }
        } else {
            remoteViews.setViewVisibility(R.id.widget_game1_info_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_playing_game1_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_playing_game1_no_selections_lo, View.VISIBLE);

            remoteViews.setTextViewText(R.id.widget_game1_title_tv, title);
            remoteViews.setTextViewText(R.id.widget_game1_image_tv, description);

            if (imagePath != context.getString(R.string.LOCAL_NO_IMAGE_FILE)) {
                File file = ImageUtility.getPlayGamePageImageFromLocal(context, page.getIndex());
                Picasso.with(context).load(file).resize(200, 150).centerCrop().into(remoteViews,
                        R.id.widget_game1_image_iv, intListToIntArray(mAppWidgetIds));
            } else {
                Picasso.with(context).load(R.drawable.default_user_image).resize(200,
                        150).centerCrop().into(remoteViews, R.id.widget_game1_image_iv,
                        intListToIntArray(mAppWidgetIds));
            }
        }
        flipGame(context);

        turnOnVibrator(isVibrateOn);
        playSound(context, sound);

        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds), remoteViews);
    }

    private void playSound(Context context, String sound) {
        if (sound.equals(context.getString(R.string.SPINNER_SOUND_DEFAULT))) {
            return;
        }
        int soundId = mSoundMap.get(sound);
        try {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(context, Uri.parse("android.resource://com.sungwoo.boostcamp.widgetgame/" + soundId));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void turnOnVibrator(boolean isVibratorOn) {
        if (!isVibratorOn) {
            return;
        }

        if (mVibrator.hasVibrator()) {
            mVibrator.cancel();
            mVibrator.vibrate(VIBRATOR_PATTERN, -1);
        }
    }

    private void flipGame(Context context) {
        if (mIsGamePlayingFlipper1) {
            mIsGamePlayingFlipper1 = false;
        } else {
            mIsGamePlayingFlipper1 = true;
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.showPrevious(R.id.widget_game_flipper);
        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds),
                remoteViews);
    }
}
