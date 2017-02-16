package com.sungwoo.boostcamp.widgetgame.Repositories;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by SungWoo on 2017-02-15.
 */


public class Page extends RealmObject {
    private int index; // 페이지 번호
    private String title; // 페이지 제목
    private String description; // 페이지 내용
    private String page; // GAMEOVER, GAMECLEAR, SELECTION, SORY
    private String imagePath;
    private String sound;
    private boolean isVibrateOn;
    private int selectionNum; //선택지 개수
    private RealmList<Selection> selections; // 페이지 선택지

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

}
