package com.sungwoo.boostcamp.widgetgame.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.LoginActivity;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.FullGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Page;
import com.sungwoo.boostcamp.widgetgame.Repositories.PlayGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Selection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by SungWoo on 2017-02-20.
 */

public class TxTWidget extends AppWidgetProvider {
    private static final int IN_NO_GAME = 0;
    private static final int IN_INFO = IN_NO_GAME + 1;
    private static final int IN_SELECTIONS = IN_INFO + 1;
    private static final int IN_STORY = IN_SELECTIONS + 1;
    private static final int IN_GAME_OVER = IN_STORY + 1;
    private static final int IN_GAME_CLEAR = IN_GAME_OVER + 1;
    private static final int IN_MENU = IN_GAME_CLEAR + 1;

    private static int mNowStage;
    private static List<Integer> mAppWidgetIds = new ArrayList<>();
    private static final String TAG = TxTWidget.class.getSimpleName();
    private static Realm mRealm = null;
    private static FullGameRepo mFullGameRepo = null;
    private static final String ACTION_WIDGET_FLIPPER_BTN_CLICKED =
            "com.sungwoo.boostcamp.widgetgame.action.FLIPPER_LEFT_BTN_CLICKED";
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

    private static final int[] SELECTION_IDS = {R.id.widget_playing_game_selection1, R.id.widget_playing_game_selection2, R.id.widget_playing_game_selection3, R.id.widget_playing_game_selection4};
    private static int[] mSelectedTargetIndex = new int[4];
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (mRealm == null) {
            mRealm = Realm.getDefaultInstance();
        }

        String action = intent.getAction();
        Log.d(TAG, "size : " + mRealm.where(PlayGameRepo.class).findAll().size() + " playable : "
                + mRealm.where(PlayGameRepo.class).findAll().get(0).getPlayable());
        if (mFullGameRepo == null && mRealm.where(PlayGameRepo.class).findAll().size() == 1
                && mRealm.where(PlayGameRepo.class).findAll().get(0).getPlayable()) {
            Log.d(TAG, "setFullGameRepoMemberField");
            setFullGameRepoMemberField();
        }

        switch (action) {
            case ACTION_WIDGET_DISPLAY_NEW_GAME:
                setFullGameRepoMemberField();
                showGameInfoLayout(context);
                break;
            case ACTION_WIDGET_FLIPPER_BTN_CLICKED:
                flip(context);
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
                        flip(context);
                        break;
                    case IN_GAME_OVER:
                        flip(context);
                        break;
                    case IN_STORY:
                        showGamePageLayout(context, getPlayGameIndexPreference(context) + 1);
                        break;
                }
                break;
            case ACTION_WIDGET_MENU_START_GAME_BTN:
                flip(context);
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

        for (int i : appWidgetIds) {
            mAppWidgetIds.add(i);
            Log.d(TAG, "update : " + i);
        }
        mNowStage = IN_NO_GAME;
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
        } else {
        }
    }
    private void setPendingIntents(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.setOnClickPendingIntent(R.id.widget_game_info_start_game_btn,
                getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_INFO_START_GAME_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_flipper_btn,
                getTxTWidgetPendingIntent(context, ACTION_WIDGET_FLIPPER_BTN_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.widget_playing_game_no_selections_next_btn,  getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_NO_SELECTION_NEXT_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_menu_start_game, getTxTWidgetPendingIntent(context, ACTION_WIDGET_MENU_START_GAME_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_menu_start_app, getTxTAppPendingIntent(context, ACTION_WIDGET_MENU_START_APP_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_no_game_start_app, getTxTAppPendingIntent(context, ACTION_WIDGET_NO_GAME_START_APP_BTN));
        for (int i = 0 ; i < 4 ; i++)
            remoteViews.setOnClickPendingIntent(SELECTION_IDS[i], getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + i));
        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds),
                remoteViews);
    }
    private int getPlayGameIndexPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE);
        return preferences.getInt(context.getString(R.string.PREF_PLAY_GAME_INDEX), 0);
    }

    private void setPlayGameIndexPreference(Context context, int index) {
        SharedPreferences.Editor editor= context.getSharedPreferences(
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
            Log.d(TAG, "delete : " + i);
        }
        if (mAppWidgetIds.size() == 0 && mRealm != null) {
            mRealm.close();
            mRealm = null;
        }
    }

    private int[] intListToIntArray(List<Integer> intList) {
        int[] returnIntArray = new int[intList.size()];
        for (int i = 0; i < intList.size(); i++) {
            returnIntArray[i] = intList.get(i);
        }
        return returnIntArray;
    }

    private void flip(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.showPrevious(R.id.widget_flipper);
        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds),
                remoteViews);
    }

    private void setFullGameRepoMemberField() {
        mFullGameRepo = mRealm.where(PlayGameRepo.class).findAll().get(0).getFullGameRepo();
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
        remoteViews.setViewVisibility(R.id.widget_playing_game_selections_lo, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_playing_game_no_selections_lo, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_game_info_lo, View.VISIBLE);

        String title = mFullGameRepo.getGameInfo().getGameTitle();
        String description = mFullGameRepo.getGameInfo().getGameDescription();
        String imagePath = mFullGameRepo.getGameInfo().getGameImagePath();
        String maker = mFullGameRepo.getMaker().getNickName();
        String email = mFullGameRepo.getMaker().getEmail();

        remoteViews.setTextViewText(R.id.widget_game_info_title_tv, title);
        remoteViews.setTextViewText(R.id.widget_game_info_description_tv, description);
        remoteViews.setTextViewText(R.id.widget_game_info_maker_tv, maker);
        remoteViews.setTextViewText(R.id.widget_game_info_email_tv, email);
        remoteViews.setTextViewText(R.id.widget_image_tv, "");


        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds),
                remoteViews);
        Log.d(TAG, "infoImage1");
        if (imagePath != context.getString(R.string.LOCAL_NO_IMAGE_FILE)) {
            Log.d(TAG, "infoImage2");
            File file = ImageUtility.getPlayGameInfoImageFromLocal(context);
            Picasso.with(context).load(file).resize(200, 150).centerCrop().into(remoteViews,
                    R.id.widget_image_iv, intListToIntArray(mAppWidgetIds));
        } else {
            Log.d(TAG, "infoImage3");
            Picasso.with(context).load(R.drawable.default_user_image).resize(200,
                    150).centerCrop().into(remoteViews, R.id.widget_image_iv,
                    intListToIntArray(mAppWidgetIds));
        }
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
        remoteViews.setViewVisibility(R.id.widget_game_info_lo, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_playing_game_no_selections_lo, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_playing_game_selections_lo, View.VISIBLE);

        String title = page.getTitle();
        String description = page.getDescription();
        String sound = page.getSound();
        String imagePath = page.getImagePath();
        List<Selection> selections = page.getSelections();

        remoteViews.setTextViewText(R.id.widget_title_tv, title);
        remoteViews.setTextViewText(R.id.widget_image_tv, description);

        playSound(sound);

        for (int i = 0 ; i < selections.size() ; i++) {
            remoteViews.setTextViewText(SELECTION_IDS[i], selections.get(i).getSelectionText());
            mSelectedTargetIndex[i] = selections.get(i).getNextIndex();
        }

        if (imagePath != context.getString(R.string.LOCAL_NO_IMAGE_FILE)) {
            Log.d(TAG, "infoImage2");
            File file = ImageUtility.getPlayGamePageImageFromLocal(context, page.getIndex());
            Picasso.with(context).load(file).resize(200, 150).centerCrop().into(remoteViews,
                    R.id.widget_image_iv, intListToIntArray(mAppWidgetIds));
        } else {
            Log.d(TAG, "infoImage3");
            Picasso.with(context).load(R.drawable.default_user_image).resize(200,
                    150).centerCrop().into(remoteViews, R.id.widget_image_iv,
                    intListToIntArray(mAppWidgetIds));
        }

        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds), remoteViews);
    }
    private void showGameStoryPageLayout(Context context, Page page) { //TODO 완성하기
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.setViewVisibility(R.id.widget_game_info_lo, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_playing_game_selections_lo, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_playing_game_no_selections_lo, View.VISIBLE);

        String title = page.getTitle();
        String description = page.getDescription();
        String sound = page.getSound();
        String imagePath = page.getImagePath();

        remoteViews.setTextViewText(R.id.widget_title_tv, title);
        remoteViews.setTextViewText(R.id.widget_image_tv, description);

        playSound(sound);

        if (imagePath != context.getString(R.string.LOCAL_NO_IMAGE_FILE)) {
            Log.d(TAG, "infoImage2");
            File file = ImageUtility.getPlayGamePageImageFromLocal(context, page.getIndex());
            Picasso.with(context).load(file).resize(200, 150).centerCrop().into(remoteViews,
                    R.id.widget_image_iv, intListToIntArray(mAppWidgetIds));
        } else {
            Log.d(TAG, "infoImage3");
            Picasso.with(context).load(R.drawable.default_user_image).resize(200,
                    150).centerCrop().into(remoteViews, R.id.widget_image_iv,
                    intListToIntArray(mAppWidgetIds));
        }

        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds), remoteViews);
    }

    private void playSound(String sound) {

    }
}
