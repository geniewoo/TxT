package com.sungwoo.boostcamp.widgetgame.Repositories;

/**
 * Created by SungWoo on 2017-02-17.
 */

public class FullGameRepo {
    GameInfo gameInfo;
    Maker maker;
    PlayInfo playInfo;

    public FullGameRepo(GameInfo gameInfo, PlayInfo playInfo, Maker maker) {
        this.playInfo = playInfo;
        this.gameInfo = gameInfo;
        this.maker = maker;
    }

    public FullGameRepo() {
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public Maker getMaker() {
        return maker;
    }

    public PlayInfo getPlayInfo() {
        return playInfo;
    }
}
