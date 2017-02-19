package com.sungwoo.boostcamp.widgetgame.Repositories;

import com.google.gson.annotations.SerializedName;

/**
 * Created by psw10 on 2017-02-19.
 */

public class DownloadGameRepo {
    private int code;
    @SerializedName("FullGameRepo")
    private FullGameRepo fullGameRepo;

    public int getCode() {
        return code;
    }

    public FullGameRepo getFullGameRepo() {
        return fullGameRepo;
    }
}
