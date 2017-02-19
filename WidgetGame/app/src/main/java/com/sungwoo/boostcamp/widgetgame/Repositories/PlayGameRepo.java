package com.sungwoo.boostcamp.widgetgame.Repositories;

import io.realm.RealmObject;

/**
 * Created by psw10 on 2017-02-19.
 */

public class PlayGameRepo extends RealmObject {
    FullGameRepo fullGameRepo;
    Boolean isPlayable;

    public PlayGameRepo() {
    }

    public PlayGameRepo(FullGameRepo fullGameRepo, Boolean isPlayable) {
        this.fullGameRepo = fullGameRepo;
        this.isPlayable = isPlayable;
    }

    public FullGameRepo getFullGameRepo() {
        return fullGameRepo;
    }

    public Boolean getPlayable() {
        return isPlayable;
    }

    public void setPlayable(Boolean playable) {
        isPlayable = playable;
    }
}
