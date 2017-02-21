package com.sungwoo.boostcamp.widgetgame.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.FullGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.GameInfo;
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
    private static final int CHANGE_CODE_PLAY_LAYOUT = 1;
    private static final int CHANGE_CODE_NO_GAME = CHANGE_CODE_PLAY_LAYOUT + 1;
    private static final String ACTION_WIDGET_FLIPPER_BTN_CLICKED =
            "com.sungwoo.boostcamp.widgetgame.action.FLIPPER_LEFT_BTN_CLICKED";
    private static final String ACTION_WIDGET_SELECT1_BTN_CLICKED =
            "com.sungwoo.boostcamp.widgetgame.action.SELECT1_BTN_CLICKED";
    private static final String ACTION_WIDGET_SELECT2_BTN_CLICKED =
            "com.sungwoo.boostcamp.widgetgame.action.SELECT2_BTN_CLICKED";
    private static final String ACTION_WIDGET_SELECT3_BTN_CLICKED =
            "com.sungwoo.boostcamp.widgetgame.action.SELECT3_BTN_CLICKED";
    private static final String ACTION_WIDGET_SELECT4_BTN_CLICKED =
            "com.sungwoo.boostcamp.widgetgame.action.SELECT4_BTN_CLICKED";
    public static final String ACTION_WIDGET_DISPLAY_NEW_GAME =
            "com.sungwoo.boostcamp.widgetgame.action.DISPLAY_NEW_GAME";
    public static final String ACTION_WIDGET_GAME_INFO_START_GAME_BTN =
            "com.sungwoo.boostcamp.widgetgame.action.GAME_INFO_START_BTN";
    public static final String ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN =
            "com.sungwoo.boostcamp.widgetgame.action.GAME_PLAYING_SELECTION_BTN";

    public static final String PAGE_CHOICE = "Choice";
    public static final String PAGE_STORY = "Story";
    public static final String PAGE_GAME_OVER = "Game Over";
    public static final String PAGE_GAME_CLEAR = "Game Clear";

    private static final int[] SELECTION_IDS = {R.id.widget_playing_game_selection1, R.id.widget_playing_game_selection2, R.id.widget_playing_game_selection3, R.id.widget_playing_game_selection4};

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
                showGameInfoLayout(context);
                break;
            case ACTION_WIDGET_FLIPPER_BTN_CLICKED:
                flip(context);
                break;
            case ACTION_WIDGET_GAME_INFO_START_GAME_BTN:
                showGamePageLayout(context, 1);
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

        if (mFullGameRepo == null && mRealm.where(PlayGameRepo.class).findAll().size() == 1
                && mRealm.where(PlayGameRepo.class).findAll().get(0).getPlayable()) {
            Log.d(TAG, "setFullGameRepoMemberField");
            setFullGameRepoMemberField();
            int index = getPlayGameIndex(context);
            mNowStage = IN_NO_GAME;
            if (index == 0) {
                showGameInfoLayout(context);
            } else {
                changeToPlayLayout(context);
                showGamePageLayout(context, index);
            }
        }

    }

    private int getPlayGameIndex(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE);
        return preferences.getInt(context.getString(R.string.PREF_PLAY_GAME_INDEX), 0);
    }

    private void changeLayout(Context context, int changeCode) {
        switch (changeCode) {
            case CHANGE_CODE_PLAY_LAYOUT:
                changeToPlayLayout(context);
                break;
            case CHANGE_CODE_NO_GAME:
                changeToNoGameLayout(context);
                break;
        }
    }

    private void changeToPlayLayout(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.setViewVisibility(R.id.widget_no_game_lo, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_play_lo, View.VISIBLE);
        remoteViews.setOnClickPendingIntent(R.id.widget_flipper_btn,
                getTxTWidgetPendingIntent(context, ACTION_WIDGET_FLIPPER_BTN_CLICKED));
        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds),
                remoteViews);
    }

    private void setFlipperBtn(Context context, RemoteViews remoteViews) {
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
        switch (mNowStage) {
            case IN_NO_GAME:
                changeLayout(context, CHANGE_CODE_PLAY_LAYOUT);
                mNowStage = IN_INFO;
                break;
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

        remoteViews.setOnClickPendingIntent(R.id.widget_game_info_start_game_btn,
                getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_INFO_START_GAME_BTN));

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
        switch (mNowStage) {
            case IN_NO_GAME:
                changeLayout(context, CHANGE_CODE_PLAY_LAYOUT);
                break;
        }
        Page page = mFullGameRepo.getGameInfo().getPages().get(index);
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
            remoteViews.setOnClickPendingIntent(SELECTION_IDS[i], getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + i));
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
