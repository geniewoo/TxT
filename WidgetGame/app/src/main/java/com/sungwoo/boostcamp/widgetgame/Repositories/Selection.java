package com.sungwoo.boostcamp.widgetgame.Repositories;

import io.realm.RealmObject;

/**
 * Created by SungWoo on 2017-02-15.
 */


public class Selection extends RealmObject {
    private boolean isSelected; // 클릭된 선택지인지 아닌지
    private int NextIndex;
    private String selectionText;

    public Selection() {
    }

    public Selection(boolean isSelected, int nextIndex, String selectionText) {
        this.isSelected = isSelected;
        NextIndex = nextIndex;
        this.selectionText = selectionText;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getNextIndex() {
        return NextIndex;
    }

    public String getSelectionText() {
        return selectionText;
    }
}
