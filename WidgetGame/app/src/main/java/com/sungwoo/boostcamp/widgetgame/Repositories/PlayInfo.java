package com.sungwoo.boostcamp.widgetgame.Repositories;

/**
 * Created by SungWoo on 2017-02-17.
 */

public class PlayInfo{
    private float stars;
    private int ratedNum;
    private int downloadNum;
    private int tryNum;
    private int clearNum;

    public PlayInfo() {
    }

    public PlayInfo(float stars, int ratedNum, int downloadNum, int tryNum, int clearNum) {
        this.stars = stars;
        this.ratedNum = ratedNum;
        this.downloadNum = downloadNum;
        this.tryNum = tryNum;
        this.clearNum = clearNum;

    }

    public float getStars() {
        return stars;
    }

    public int getRatedNum() {
        return ratedNum;
    }

    public int getDownloadNum() {
        return downloadNum;
    }

    public int getTryNum() {
        return tryNum;
    }

    public int getClearNum() {
        return clearNum;
    }
}
