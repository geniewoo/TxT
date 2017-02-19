package com.sungwoo.boostcamp.widgetgame.Repositories;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by SungWoo on 2017-02-15.
 */
@RealmClass
public class GameInfo extends RealmObject {
    private String gameTitle;
    private String gameTheme;
    private String gameDescription;
    private String gameImagePath;
    private int pagesNum;
    private RealmList<Page> pages;

    public GameInfo() {
    }

    public GameInfo(String gameTitle, String gameTheme, String gameDescription, String gameImagePath, int pagesNum, RealmList<Page> pages) {
        this.gameTitle = gameTitle;
        this.gameTheme = gameTheme;
        this.gameDescription = gameDescription;
        this.gameImagePath = gameImagePath;
        this.pagesNum = pagesNum;
        this.pages = pages;
    }


    public String getGameTitle() {
        return gameTitle;
    }

    public String getGameTheme() {
        return gameTheme;
    }

    public String getGameDescription() {
        return gameDescription;
    }

    public String getGameImagePath(){
        return gameImagePath;
    }

    public int getPagesNum() {
        return pagesNum;
    }

    public RealmList<Page> getPages() {
        return pages;
    }

    public void setPages(RealmList<Page> pages) {
        this.pages = pages;
    }

    public void setPagesNum(int pagesNum) {
        this.pagesNum = pagesNum;
    }
}
