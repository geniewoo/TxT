package com.sungwoo.boostcamp.widgetgame.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.PlayGameRepo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by SungWoo on 2017-02-20.
 */

public class TxTWidget extends AppWidgetProvider {
    private static List<Integer> mAppWidgetIds = new ArrayList<>();
    private static final String TAG = TxTWidget.class.getSimpleName();
    private static Realm mRealm = Realm.getDefaultInstance();
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

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();

        switch (action) {
            case ACTION_WIDGET_DISPLAY_NEW_GAME:
                changeLayout(context, CHANGE_CODE_PLAY_LAYOUT);
                break;
            case ACTION_WIDGET_FLIPPER_BTN_CLICKED:
                flip(context);
                break;
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int i : appWidgetIds) {
            mAppWidgetIds.add(i);
            Log.d(TAG, "update : " + i);
        }
        PlayGameRepo playGameRepo = mRealm.where(PlayGameRepo.class).findAll().get(0);
        if (playGameRepo != null && playGameRepo.getPlayable()) {
            Log.d(TAG, "is playable");
            changeToPlayLayout(context);
        }
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
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        remoteViews.setViewVisibility(R.id.widget_no_game_lo, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_play_lo, View.VISIBLE);
        setFlipperBtn(context, remoteViews);
        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds),
                remoteViews);
    }

    private void setFlipperBtn(Context context, RemoteViews remoteViews) {
        Intent intent = new Intent(context, TxTWidget.class);
        intent.setAction(ACTION_WIDGET_FLIPPER_BTN_CLICKED);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_flipper_btn, pendingIntent);
    }

    private void changeToNoGameLayout(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
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
    }

    private int[] intListToIntArray(List<Integer> intList) {
        int[] returnIntArray = new int[intList.size()];
        for (int i = 0; i < intList.size(); i++) {
            returnIntArray[i] = intList.get(i);
        }
        return returnIntArray;
    }

    private void flip(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        remoteViews.showPrevious(R.id.widget_flipper);
        AppWidgetManager.getInstance(context).updateAppWidget(intListToIntArray(mAppWidgetIds),
                remoteViews);
    }
}
