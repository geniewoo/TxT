package com.sungwoo.boostcamp.widgetgame.Repositories;

import io.realm.RealmObject;

/**
 * Created by psw10 on 2017-02-27.
 */

public class SelectionHistory extends RealmObject {
    private int history;

    public SelectionHistory() {
    }

    public SelectionHistory(int history) {
        this.history = history;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }
}
