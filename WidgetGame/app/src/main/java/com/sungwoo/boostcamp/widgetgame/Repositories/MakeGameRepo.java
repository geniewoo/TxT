package com.sungwoo.boostcamp.widgetgame.Repositories;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by SungWoo on 2017-02-14.
 */

public class MakeGameRepo extends RealmObject {
    private GameInfo gameInfo;

    public MakeGameRepo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public MakeGameRepo() {
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

}
