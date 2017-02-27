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

import com.squareup.picasso.Picasso;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.LoginActivity;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.FullGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Page;
import com.sungwoo.boostcamp.widgetgame.Repositories.PlayGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Selection;
import com.sungwoo.boostcamp.widgetgame.Repositories.SelectionHistory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

import static com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility.getSoundMap;

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

    private static final int INFO_IMAGE_X = 360;
    private static final int INFO_IMAGE_Y = 480;
    private static final int PAGE_IMAGE_X = 480;
    private static final int PAGE_IMAGE_Y = 360;
    private static final int PAGE_STORY_IMAGE_X = 520;
    private static final int PAGE_STORY_IMAGE_Y = 390;
    private static final String TAG = TxTWidget.class.getSimpleName();
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
    public static final String ACTION_WIDGET_MENU_BACK_TO_BEFORE_SELECTED_BTN =
            "com.sungwoo.boostcamp.widgetgame.action.MENU_BACK_TO_BEFORE_SELECTED_BTN";

    private static final String PAGE_CHOICE = "Choice";
    private static final String PAGE_STORY = "Story";
    private static final String PAGE_GAME_OVER = "Game Over";
    private static final String PAGE_GAME_CLEAR = "Game Clear";

    private static final int[] SELECTION_IDS1 = {R.id.widget_game1_selection1, R.id.widget_game1_selection2, R.id.widget_game1_selection3, R.id.widget_game1_selection4};
    private static final int[] SELECTION_IDS2 = {R.id.widget_game2_selection1, R.id.widget_game2_selection2, R.id.widget_game2_selection3, R.id.widget_game2_selection4};
    private static final int[] SELECTION_VFS1 = {R.id.widget_game1_selection1_vf, R.id.widget_game1_selection2_vf, R.id.widget_game1_selection3_vf, R.id.widget_game1_selection4_vf};
    private static final int[] SELECTION_VFS2 = {R.id.widget_game2_selection1_vf, R.id.widget_game2_selection2_vf, R.id.widget_game2_selection3_vf, R.id.widget_game2_selection4_vf};
    private static final int[] SELECTION_LOS1 = {R.id.widget_game1_selection1_lo, R.id.widget_game1_selection2_lo, R.id.widget_game1_selection3_lo, R.id.widget_game1_selection4_lo};
    private static final int[] SELECTION_LOS2 = {R.id.widget_game2_selection1_lo, R.id.widget_game2_selection2_lo, R.id.widget_game2_selection3_lo, R.id.widget_game2_selection4_lo};
    private static final int[] SELECTION_SEL_IDS1 = {R.id.widget_game1_selection1_selected, R.id.widget_game1_selection2_selected, R.id.widget_game1_selection3_selected, R.id.widget_game1_selection4_selected};
    private static final int[] SELECTION_SEL_IDS2 = {R.id.widget_game2_selection1_selected, R.id.widget_game2_selection2_selected, R.id.widget_game2_selection3_selected, R.id.widget_game2_selection4_selected};

    private static final long[] VIBRATOR_PATTERN = {0, 300, 150, 400};

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "OnReceive1");
        super.onReceive(context, intent);
        Log.d(TAG, "OnReceive2");
        String action = intent.getAction();
        Realm realm = Realm.getDefaultInstance();
        FullGameRepo fullGameRepo = getFullGameRepo(realm);
        realm.close();

        if (fullGameRepo == null) {
            return;
        }

        boolean isMenuStage = context.getSharedPreferences(context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).getBoolean(context.getString(R.string.PREF_PLAY_GAME_IS_MENU_STAGE), false);
        boolean isGamePlayingFlipper1 = context.getSharedPreferences(context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).getBoolean(context.getString(R.string.PREF_PLAY_GAME_IS_PLAYING_GAME_FLIPPER1), false);
        boolean isOnUpdate = context.getSharedPreferences(context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).getBoolean(context.getString(R.string.PREF_PLAY_GAME_IS_ON_UPDATE), false);
        int appWidgetId = context.getSharedPreferences(context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).getInt(context.getString(R.string.PREF_PLAY_GAME_APP_WIDGET_ID), -1);
        int index = context.getSharedPreferences(context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).getInt(context.getString(R.string.PREF_PLAY_GAME_INDEX), 0);
        int nowStage = context.getSharedPreferences(context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).getInt(context.getString(R.string.PREF_PLAY_GAME_NOW_STAGE), IN_NO_GAME);

        Log.d(TAG, "index : " + index + " nowStage : " + nowStage + " action : " + action);
        if (isOnUpdate) {
            SharedPreferences.Editor editor = context.getSharedPreferences(
                    context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).edit();
            editor.putBoolean(context.getString(R.string.PREF_PLAY_GAME_IS_ON_UPDATE), false);
            editor.apply();
            if (index == 0) {
                showGameInfoLayout(context, fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
            } else {
                showGamePageLayout(context, index, fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
            }
        }

        switch (action) {
            case ACTION_WIDGET_DISPLAY_NEW_GAME:
                showGameInfoLayout(context, fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
                if (isMenuStage) {
                    flipMenu(context, isMenuStage, appWidgetId);
                }
                break;
            case ACTION_WIDGET_MENU_FLIPPER_BTN_CLICKED:
                flipMenu(context, isMenuStage, appWidgetId);
                break;
            case ACTION_WIDGET_GAME_INFO_START_GAME_BTN:
                showGamePageLayout(context, 1, fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
                break;
            case ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + "0":
                saveSelectionsAndHistory(index, 0);
                showGamePageLayout(context, fullGameRepo.getGameInfo().getPages().get(index - 1).getSelections().get(0).getNextIndex(), fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
                break;
            case ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + "1":
                saveSelectionsAndHistory(index, 1);
                showGamePageLayout(context, fullGameRepo.getGameInfo().getPages().get(index - 1).getSelections().get(1).getNextIndex(), fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
                break;
            case ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + "2":
                saveSelectionsAndHistory(index, 2);
                showGamePageLayout(context, fullGameRepo.getGameInfo().getPages().get(index - 1).getSelections().get(2).getNextIndex(), fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
                break;
            case ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + "3":
                saveSelectionsAndHistory(index, 3);
                showGamePageLayout(context, fullGameRepo.getGameInfo().getPages().get(index - 1).getSelections().get(3).getNextIndex(), fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
                break;
            case ACTION_WIDGET_GAME_PLAYING_NO_SELECTION_NEXT_BTN:
                switch (nowStage) {
                    case IN_GAME_CLEAR:
                        flipMenu(context, isMenuStage, appWidgetId);
                        break;
                    case IN_GAME_OVER:
                        flipMenu(context, isMenuStage, appWidgetId);
                        break;
                    case IN_STORY:
                        showGamePageLayout(context, index + 1, fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
                        break;
                }
                break;
            case ACTION_WIDGET_MENU_START_GAME_BTN:
                flipMenu(context, isMenuStage, appWidgetId);
                showGameInfoLayout(context, fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
                break;
            case ACTION_WIDGET_MENU_BACK_TO_BEFORE_SELECTED_BTN:
                int beforeIndex = backToBeforeSelectionAndGetIndex();
                flipMenu(context, isMenuStage, appWidgetId);
                if (beforeIndex == 0) {
                    showGameInfoLayout(context, fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
                } else {
                    showGamePageLayout(context, beforeIndex, fullGameRepo, isGamePlayingFlipper1, nowStage, appWidgetId);
                }
                break;
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "OnUpdate");

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.PREF_PLAY_GAME_IS_PLAYING_GAME_FLIPPER1), true);
        editor.putInt(context.getString(R.string.PREF_PLAY_GAME_NOW_STAGE), IN_NO_GAME);
        editor.putBoolean(context.getString(R.string.PREF_PLAY_GAME_IS_ON_UPDATE), true);
            editor.putInt(context.getString(R.string.PREF_PLAY_GAME_APP_WIDGET_ID),appWidgetIds[0]);
        editor.apply();
        Log.d(TAG, "id : " + sharedPreferences.getInt(context.getString(R.string.PREF_PLAY_GAME_APP_WIDGET_ID), -1) + " " + appWidgetIds[0]);
        setPendingIntents(context, sharedPreferences.getInt(context.getString(R.string.PREF_PLAY_GAME_APP_WIDGET_ID), -1));
    }

    private int backToBeforeSelectionAndGetIndex() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmList<SelectionHistory> selections = realm.where(PlayGameRepo.class).findAll().get(0).getSelectionHistories();
        int returnInt;
        if (selections.size() < 1) {
            returnInt = 0;
        } else {
            returnInt = selections.get(selections.size()-1).getHistory();
        }
        selections.deleteLastFromRealm();
        realm.commitTransaction();
        realm.close();
        return returnInt;
    }

    private void saveSelectionsAndHistory(int index, int selected) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(PlayGameRepo.class).findAll().get(0).getFullGameRepo().getGameInfo().getPages().get(index - 1).getSelections().get(selected).setSelected(true);
        realm.where(PlayGameRepo.class).findAll().get(0).getSelectionHistories().add(new SelectionHistory(index));
        realm.commitTransaction();
        realm.close();
    }
    private void setPendingIntents(Context context, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.setOnClickPendingIntent(R.id.widget_game1_info_start_game_btn,
                getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_INFO_START_GAME_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_game2_info_start_game_btn,
                getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_INFO_START_GAME_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_flipper_btn,
                getTxTWidgetPendingIntent(context, ACTION_WIDGET_MENU_FLIPPER_BTN_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.widget_game1_story_next_btn, getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_NO_SELECTION_NEXT_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_game2_story_next_btn, getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_NO_SELECTION_NEXT_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_menu_start_game, getTxTWidgetPendingIntent(context, ACTION_WIDGET_MENU_START_GAME_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_menu_start_app, getTxTAppPendingIntent(context, ACTION_WIDGET_MENU_START_APP_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_no_game_start_app, getTxTAppPendingIntent(context, ACTION_WIDGET_NO_GAME_START_APP_BTN));
        remoteViews.setOnClickPendingIntent(R.id.widget_menu_back_to_before_selected_btn, getTxTWidgetPendingIntent(context, ACTION_WIDGET_MENU_BACK_TO_BEFORE_SELECTED_BTN));
        for (int i = 0; i < 4; i++) {
            remoteViews.setOnClickPendingIntent(SELECTION_IDS1[i], getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + i));
            remoteViews.setOnClickPendingIntent(SELECTION_IDS2[i], getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + i));
            remoteViews.setOnClickPendingIntent(SELECTION_SEL_IDS1[i], getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + i));
            remoteViews.setOnClickPendingIntent(SELECTION_SEL_IDS2[i], getTxTWidgetPendingIntent(context, ACTION_WIDGET_GAME_PLAYING_SELECTION_BTN + i));
        }
        Log.d(TAG, "why ? : " + appWidgetId);
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
    }

    private void setPlayGameIndexPreference(Context context, int index) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).edit();
        editor.putInt(context.getString(R.string.PREF_PLAY_GAME_INDEX), index);
        editor.apply();
    }

    private void changeToPlayLayout(Context context, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.setViewVisibility(R.id.widget_no_game_lo, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_play_lo, View.VISIBLE);
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId,
                remoteViews);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(TAG, "OnDelete");
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE);
        for (int i : appWidgetIds) {
            if (sharedPreferences.getInt(context.getString(R.string.PREF_PLAY_GAME_APP_WIDGET_ID), -1) == i) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(context.getString(R.string.PREF_PLAY_GAME_APP_WIDGET_ID), -1);
                editor.apply();
            }
        }
    }

    private void flipMenu(Context context, boolean isMenuStage, int appWidgetId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).edit();
        if (isMenuStage) {
            editor.putBoolean(context.getString(R.string.PREF_PLAY_GAME_IS_MENU_STAGE), false);
        } else {
            editor.putBoolean(context.getString(R.string.PREF_PLAY_GAME_IS_MENU_STAGE), true);
        }
        editor.apply();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.showPrevious(R.id.widget_menu_flipper);
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId,
                remoteViews);
    }

    private FullGameRepo getFullGameRepo(Realm realm) {
        if (realm.where(PlayGameRepo.class).findAll().size() == 1) {
            return realm.copyFromRealm(realm.where(PlayGameRepo.class).findAll().get(0).getFullGameRepo());
        } else {
            return null;
        }
    }

    private void showGameInfoLayout(Context context, FullGameRepo fullGameRepo, boolean isGamePlayingFlipper1, int nowStage, int appWidgetId) {
        int[] appWidgetIdArray = {appWidgetId};
        setPlayGameIndexPreference(context, 0);

        switch (nowStage) {
            case IN_NO_GAME:
                changeToPlayLayout(context, appWidgetId);
                break;
            default:
        }
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(PlayGameRepo.class).findAll().get(0).setSelectionHistories(new RealmList<SelectionHistory>());
        realm.commitTransaction();
        realm.close();
        SharedPreferences.Editor editor = context.getSharedPreferences(
                context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).edit();
        editor.putInt(context.getString(R.string.PREF_PLAY_GAME_NOW_STAGE), IN_INFO);
        editor.apply();

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        String title = fullGameRepo.getGameInfo().getGameTitle();
        String description = fullGameRepo.getGameInfo().getGameDescription();
        String imagePath = fullGameRepo.getGameInfo().getGameImagePath();
        String maker = fullGameRepo.getMaker().getNickName();

        if (isGamePlayingFlipper1) {
            remoteViews.setViewVisibility(R.id.widget_game2_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game2_story_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game2_info_lo, View.VISIBLE);

            remoteViews.setTextViewText(R.id.widget_game2_info_title_tv, title);

            String descriptionStr = "";
            for (int i = 0; i < description.length(); i++) {
                descriptionStr += description.charAt(i);
                remoteViews.setTextViewText(R.id.widget_game2_info_description_tv, descriptionStr);
            }

            remoteViews.setTextViewText(R.id.widget_game2_info_maker_tv, maker);

            if (!imagePath.equals(context.getString(R.string.LOCAL_NO_IMAGE_FILE))) {
                File file = ImageUtility.getPlayGameInfoImageFromLocal(context);
                Picasso.with(context).load(file).resize(INFO_IMAGE_X, INFO_IMAGE_Y).centerCrop().into(remoteViews,
                        R.id.widget_game2_info_image_iv, appWidgetIdArray);
            } else {
                Picasso.with(context).load(R.drawable.txt_logo6).resize(INFO_IMAGE_X, INFO_IMAGE_Y).centerCrop().into(remoteViews, R.id.widget_game2_info_image_iv,
                        appWidgetIdArray);
            }
        } else {
            remoteViews.setViewVisibility(R.id.widget_game1_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game1_story_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game1_info_lo, View.VISIBLE);

            remoteViews.setTextViewText(R.id.widget_game1_info_title_tv, title);
            remoteViews.setTextViewText(R.id.widget_game1_info_description_tv, description);
            remoteViews.setTextViewText(R.id.widget_game1_info_maker_tv, maker);

            if (!imagePath.equals(context.getString(R.string.LOCAL_NO_IMAGE_FILE))) {
                File file = ImageUtility.getPlayGameInfoImageFromLocal(context);
                Picasso.with(context).load(file).resize(INFO_IMAGE_X, INFO_IMAGE_Y).centerCrop().into(remoteViews,
                        R.id.widget_game1_info_image_iv, appWidgetIdArray);
            } else {
                Picasso.with(context).load(R.drawable.txt_logo6).resize(INFO_IMAGE_X, INFO_IMAGE_Y).centerCrop().into(remoteViews, R.id.widget_game1_info_image_iv,
                        appWidgetIdArray);
            }
        }
        flipGame(context, isGamePlayingFlipper1, appWidgetId);

        if (isGamePlayingFlipper1) {
            remoteViews.showNext(R.id.widget_game2_info_description_vf);
            remoteViews.showNext(R.id.widget_game2_info_start_game_vf);
        } else {
            remoteViews.showNext(R.id.widget_game1_info_description_vf);
            remoteViews.showNext(R.id.widget_game1_info_start_game_vf);
        }

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId,
                remoteViews);
    }

    private void showGamePageLayout(Context context, int index, FullGameRepo fullGameRepo, boolean isGamePlayingFlipper1, int nowStage, int appWidgetId) {
        setPlayGameIndexPreference(context, index);

        switch (nowStage) {
            case IN_NO_GAME:
                changeToPlayLayout(context, appWidgetId);
                break;
        }
        Page page = fullGameRepo.getGameInfo().getPages().get(index - 1);
        Log.d(TAG, "target page : " + page.getPage() + " index : " + index + " nowStage : " + nowStage);
        SharedPreferences.Editor editor = context.getSharedPreferences(
                context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).edit();
        switch (page.getPage()) {
            case PAGE_CHOICE:
                editor.putInt(context.getString(R.string.PREF_PLAY_GAME_NOW_STAGE), IN_SELECTIONS);
                showGameSelectionPageLayout(context, page, isGamePlayingFlipper1, appWidgetId);
                break;
            case PAGE_STORY:
                editor.putInt(context.getString(R.string.PREF_PLAY_GAME_NOW_STAGE), IN_STORY);
                showGameStoryPageLayout(context, page, context.getString(R.string.TxT_WIDGET_STORY_NEXT_PAGE), isGamePlayingFlipper1, appWidgetId);
                break;
            case PAGE_GAME_OVER:
                editor.putInt(context.getString(R.string.PREF_PLAY_GAME_NOW_STAGE), IN_GAME_OVER);
                showGameStoryPageLayout(context, page, context.getString(R.string.TxT_WIDGET_STORY_GAME_OVER), isGamePlayingFlipper1, appWidgetId);
                break;
            case PAGE_GAME_CLEAR:
                editor.putInt(context.getString(R.string.PREF_PLAY_GAME_NOW_STAGE), IN_GAME_CLEAR);
                showGameStoryPageLayout(context, page, context.getString(R.string.TxT_WIDGET_STORY_GAME_CLEAR), isGamePlayingFlipper1, appWidgetId);
                break;
        }
        editor.apply();
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

    private void showGameSelectionPageLayout(Context context, Page page, boolean isGamePlayingFlipper1, int appWidgetId) {
        int[] appWidgetIdArray = {appWidgetId};
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        String title = page.getTitle();
        String description = page.getDescription();
        String sound = page.getSound();
        String imagePath = page.getImagePath();
        List<Selection> selections = page.getSelections();
        Boolean isVibrateOn = page.isVibrateOn();


        turnOnVibrator(context, isVibrateOn);
        playSound(context, sound);

        if (isGamePlayingFlipper1) {

            remoteViews.setViewVisibility(R.id.widget_game2_info_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game2_story_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game2_selections_lo, View.VISIBLE);

            remoteViews.setTextViewText(R.id.widget_game2_selections_title_tv, title);
            remoteViews.setTextViewText(R.id.widget_game2_selections_description_tv, description);

            int i = 0;

            for (; i < selections.size(); i++) {
                remoteViews.setViewVisibility(SELECTION_LOS2[i], View.VISIBLE);
                if (selections.get(i).isSelected()) {
                    remoteViews.setViewVisibility(SELECTION_IDS2[i], View.GONE);
                    remoteViews.setViewVisibility(SELECTION_SEL_IDS2[i], View.VISIBLE);
                    remoteViews.setTextViewText(SELECTION_SEL_IDS2[i], selections.get(i).getSelectionText());
                } else {
                    remoteViews.setViewVisibility(SELECTION_SEL_IDS2[i], View.GONE);
                    remoteViews.setViewVisibility(SELECTION_IDS2[i], View.VISIBLE);
                    remoteViews.setTextViewText(SELECTION_IDS2[i], selections.get(i).getSelectionText());
                }
            }
            for (; i < 4; i++) {
                remoteViews.setViewVisibility(SELECTION_LOS2[i], View.INVISIBLE);
            }
            if (!imagePath.equals(context.getString(R.string.LOCAL_NO_IMAGE_FILE))) {
                File file = ImageUtility.getPlayGamePageImageFromLocal(context, page.getIndex());
                Picasso.with(context).load(file).resize(PAGE_IMAGE_X, PAGE_IMAGE_Y).centerCrop().into(remoteViews,
                        R.id.widget_game2_selections_image_iv, appWidgetIdArray);
            } else {
                Picasso.with(context).load(R.drawable.txt_logo6).resize(PAGE_IMAGE_X,
                        PAGE_IMAGE_Y).centerCrop().into(remoteViews, R.id.widget_game2_selections_image_iv,
                        appWidgetIdArray);
            }
        } else {
            remoteViews.setViewVisibility(R.id.widget_game1_info_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game1_story_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game1_selections_lo, View.VISIBLE);

            remoteViews.setTextViewText(R.id.widget_game1_selections_title_tv, title);
            remoteViews.setTextViewText(R.id.widget_game1_selections_description_tv, description);

            int i = 0;
            for (; i < selections.size(); i++) {
                remoteViews.setViewVisibility(SELECTION_LOS1[i], View.VISIBLE);
                if (selections.get(i).isSelected()) {
                    remoteViews.setViewVisibility(SELECTION_IDS1[i], View.GONE);
                    remoteViews.setViewVisibility(SELECTION_SEL_IDS1[i], View.VISIBLE);
                    remoteViews.setTextViewText(SELECTION_SEL_IDS1[i], selections.get(i).getSelectionText());
                } else {
                    remoteViews.setViewVisibility(SELECTION_SEL_IDS1[i], View.GONE);
                    remoteViews.setViewVisibility(SELECTION_IDS1[i], View.VISIBLE);
                    remoteViews.setTextViewText(SELECTION_IDS1[i], selections.get(i).getSelectionText());
                }
            }
            for (; i < 4; i++) {
                remoteViews.setViewVisibility(SELECTION_LOS1[i], View.INVISIBLE);
            }
            if (!imagePath.equals(context.getString(R.string.LOCAL_NO_IMAGE_FILE))) {
                File file = ImageUtility.getPlayGamePageImageFromLocal(context, page.getIndex());
                Picasso.with(context).load(file).resize(PAGE_IMAGE_X, PAGE_IMAGE_Y).centerCrop().into(remoteViews,
                        R.id.widget_game1_selections_image_iv, appWidgetIdArray);
            } else {
                Picasso.with(context).load(R.drawable.txt_logo6).resize(PAGE_IMAGE_X,
                        PAGE_IMAGE_Y).centerCrop().into(remoteViews, R.id.widget_game1_selections_image_iv,
                        appWidgetIdArray);
            }
        }
        flipGame(context, isGamePlayingFlipper1, appWidgetId);

        Log.d(TAG, "flipper1 : " + isGamePlayingFlipper1);
        if (isGamePlayingFlipper1) {
            remoteViews.showNext(R.id.widget_game2_selections_description_vf);
            for (int i = 0; i < selections.size(); i++) {
                remoteViews.showNext(SELECTION_VFS2[i]);
            }
        } else {
            remoteViews.showNext(R.id.widget_game1_selections_description_vf);
            for (int i = 0; i < selections.size(); i++) {
                remoteViews.showNext(SELECTION_VFS1[i]);
            }
        }
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
    }

    private void showGameStoryPageLayout(Context context, Page page, String nextBtnStr, boolean isGamePlayingFlipper1, int appWidgetId) {
        int[] appWidgetIdArray = {appWidgetId};
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        String title = page.getTitle();
        String description = page.getDescription();
        String sound = page.getSound();
        String imagePath = page.getImagePath();
        Boolean isVibrateOn = page.isVibrateOn();

        if (isGamePlayingFlipper1) {
            remoteViews.setViewVisibility(R.id.widget_game2_info_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game2_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game2_story_lo, View.VISIBLE);

            remoteViews.setTextViewText(R.id.widget_game2_story_title_tv, title);
            remoteViews.setTextViewText(R.id.widget_game2_story_description_tv, description);
            remoteViews.setTextViewText(R.id.widget_game2_story_next_btn, nextBtnStr);

            if (!imagePath.equals(context.getString(R.string.LOCAL_NO_IMAGE_FILE))) {
                File file = ImageUtility.getPlayGamePageImageFromLocal(context, page.getIndex());
                Picasso.with(context).load(file).resize(PAGE_STORY_IMAGE_X, PAGE_STORY_IMAGE_Y).centerCrop().into(remoteViews,
                        R.id.widget_game2_story_image_iv, appWidgetIdArray);
            } else {
                Picasso.with(context).load(R.drawable.txt_logo6).resize(PAGE_STORY_IMAGE_X,
                        PAGE_STORY_IMAGE_Y).centerCrop().into(remoteViews, R.id.widget_game2_story_image_iv,
                        appWidgetIdArray);
            }
        } else {
            remoteViews.setViewVisibility(R.id.widget_game1_info_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game1_selections_lo, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_game1_story_lo, View.VISIBLE);

            remoteViews.setTextViewText(R.id.widget_game1_story_title_tv, title);
            remoteViews.setTextViewText(R.id.widget_game1_story_description_tv, description);
            remoteViews.setTextViewText(R.id.widget_game1_story_next_btn, nextBtnStr);

            if (!imagePath.equals(context.getString(R.string.LOCAL_NO_IMAGE_FILE))) {
                File file = ImageUtility.getPlayGamePageImageFromLocal(context, page.getIndex());
                Picasso.with(context).load(file).resize(PAGE_STORY_IMAGE_X, PAGE_STORY_IMAGE_Y).centerCrop().into(remoteViews,
                        R.id.widget_game1_story_image_iv, appWidgetIdArray);
            } else {
                Picasso.with(context).load(R.drawable.txt_logo6).resize(PAGE_STORY_IMAGE_X,
                        PAGE_STORY_IMAGE_Y).centerCrop().into(remoteViews, R.id.widget_game1_story_image_iv,
                        appWidgetIdArray);
            }
        }
        flipGame(context, isGamePlayingFlipper1, appWidgetId);
        Log.d(TAG, "flipper1 : " + isGamePlayingFlipper1);
        if (isGamePlayingFlipper1) {
            remoteViews.showNext(R.id.widget_game2_story_description_vf);
            remoteViews.showNext(R.id.widget_game2_story_next_vf);
        } else {
            remoteViews.showNext(R.id.widget_game1_story_description_vf);
            remoteViews.showNext(R.id.widget_game1_story_next_vf);
        }
        turnOnVibrator(context, isVibrateOn);
        playSound(context, sound);

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews);
    }

    private void playSound(Context context, String sound) {
        if (sound.equals(context.getString(R.string.SPINNER_SOUND_DEFAULT))) {
            return;
        }
        HashMap<String, Integer> soundMap = getSoundMap(context);
        int soundId = soundMap.get(sound);
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, Uri.parse(context.getString(R.string.RAW_FILE_FOLDER_URI) + soundId));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void turnOnVibrator(Context context, boolean isVibratorOn) {
        if (!isVibratorOn) {
            return;
        }

        Vibrator vibrator = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.cancel();
            vibrator.vibrate(VIBRATOR_PATTERN, -1);
        }
    }

    private void flipGame(Context context, boolean isGamePlayingFlipper1, int appWidgetId) {
        if (isGamePlayingFlipper1) {
            SharedPreferences.Editor editor = context.getSharedPreferences(
                    context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).edit();
            editor.putBoolean(context.getString(R.string.PREF_PLAY_GAME_IS_PLAYING_GAME_FLIPPER1), false);
            editor.apply();
        } else {
            SharedPreferences.Editor editor = context.getSharedPreferences(
                    context.getString(R.string.PREF_PLAY_GAME), Context.MODE_PRIVATE).edit();
            editor.putBoolean(context.getString(R.string.PREF_PLAY_GAME_IS_PLAYING_GAME_FLIPPER1), true);
            editor.apply();
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        remoteViews.showPrevious(R.id.widget_game_flipper);
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId,
                remoteViews);
    }
}