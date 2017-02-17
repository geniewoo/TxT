package com.sungwoo.boostcamp.widgetgame.Repositories;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by SungWoo on 2017-02-15.
 */


public class Page extends RealmObject {
    private int index;
    private String title;
    private String description;
    private String page;
    private String imagePath;
    private String sound;
    private boolean isVibrateOn;
    private int selectionNum;
    private RealmList<Selection> selections;

    public Page() {
    }

    public Page(int index, String title, String description, String page, String imagePath, String sound, Boolean isVibrateOn, int selectionNum, RealmList<Selection> selections) {
        this.index = index;
        this.title = title;
        this.description = description;
        this.page = page;
        this.imagePath = imagePath;
        this.sound = sound;
        this.isVibrateOn = isVibrateOn;
        this.selectionNum = selectionNum;
        this.selections = selections;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPage() {
        return page;
    }

    public String getImagePath(){
        return imagePath;
    }

    public int getSelectionNum() {
        return selectionNum;
    }

    public RealmList<Selection> getSelections() {
        return selections;
    }

    public String getSound() {
        return sound;
    }

    public boolean isVibrateOn() {
        return isVibrateOn;
    }
}
