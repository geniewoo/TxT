package com.sungwoo.boostcamp.widgetgame.Repositories;

import java.util.List;

import io.realm.RealmObject;

/**
 * Created by psw10 on 2017-02-27.
 */

public class AppWidgetIds extends RealmObject {
    private int AppWidgetIds;

    public AppWidgetIds() {
    }

    public AppWidgetIds(int appWidgetIds) {

        AppWidgetIds = appWidgetIds;
    }

    public int getAppWidgetIds() {
        return AppWidgetIds;
    }

    public void setAppWidgetIds(int appWidgetIds) {
        AppWidgetIds = appWidgetIds;
    }
}
