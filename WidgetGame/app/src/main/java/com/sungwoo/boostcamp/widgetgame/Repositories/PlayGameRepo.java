package com.sungwoo.boostcamp.widgetgame.Repositories;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by psw10 on 2017-02-19.
 */

public class PlayGameRepo extends RealmObject {
    private FullGameRepo fullGameRepo;
    private Boolean isPlayable;
    private RealmList<SelectionHistory> selectionHistories;

    public PlayGameRepo() {
    }

    public PlayGameRepo(FullGameRepo fullGameRepo, Boolean isPlayable, RealmList<SelectionHistory> selectionHistories) {
        this.fullGameRepo = fullGameRepo;
        this.isPlayable = isPlayable;
        this.selectionHistories = selectionHistories;
    }

    public RealmList<SelectionHistory> getSelectionHistories() {
        return selectionHistories;
    }

    public void setSelectionHistories(RealmList<SelectionHistory> selectionHistories) {
        this.selectionHistories = selectionHistories;
    }

    public FullGameRepo getFullGameRepo() {
        return fullGameRepo;
    }

    public Boolean isPlayable() {
        return isPlayable;
    }

    public void setPlayable(Boolean playable) {
        isPlayable = playable;
    }
}
