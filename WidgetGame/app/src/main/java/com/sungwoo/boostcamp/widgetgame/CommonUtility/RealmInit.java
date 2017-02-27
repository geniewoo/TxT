package com.sungwoo.boostcamp.widgetgame.CommonUtility;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by SungWoo on 2017-02-14.
 */

public class RealmInit extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
