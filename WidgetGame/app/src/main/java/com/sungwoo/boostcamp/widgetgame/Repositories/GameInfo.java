package com.sungwoo.boostcamp.widgetgame.Repositories;

import io.realm.RealmObject;

/**
 * Created by SungWoo on 2017-02-14.
 */


public class GameInfo {
    private String gameTitle; // 게임제목
    private String gameTheme; // 게임 장르
    private String gameDescription; // 게임 설명
    private int pagesNum; // 페이지 총 수
    private Page[] pages; // 각 페이지 정보

    public GameInfo(String gameTitle, String gameTheme, String gameDescription, int pagesNum, Page[] pages) {
        this.gameTitle = gameTitle;
        this.gameTheme = gameTheme;
        this.gameDescription = gameDescription;
        this.pagesNum = pagesNum;
        this.pages = pages;
    }

    public class Page {
        private int index; // 페이지 번호
        private String title; // 페이지 제목
        private String description; // 페이지 내용
        private String page; // GAMEOVER, GAMECLEAR, SELECTION, SORY
        private int selectionNum; //선택지 개수
        private Select[] selects; // 페이지 선택지

        public Page(int index, String title, String description, String page, int selectionNum, Select[] selects) {
            this.index = index;
            this.title = title;
            this.description = description;
            this.page = page;
            this.selectionNum = selectionNum;
            this.selects = selects;
        }

        public class Select {
            private boolean isSelected; // 클릭된 선택지인지 아닌지
            private int NextIndex; // 선택했을 때 갈 페이지번호
            private String selectionText; // 선택지 내용
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

        public int getSelectionNum() {
            return selectionNum;
        }

        public Select[] getSelects() {
            return selects;
        }

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

    public int getPagesNum() {
        return pagesNum;
    }

    public Page[] getPages() {
        return pages;
    }

    public void setPages(Page[] pages) {
        this.pages = pages;
    }

    public void setPagesNum(int pagesNum) {
        this.pagesNum = pagesNum;
    }
}
