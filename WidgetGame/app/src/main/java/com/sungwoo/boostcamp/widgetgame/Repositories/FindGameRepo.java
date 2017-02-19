package com.sungwoo.boostcamp.widgetgame.Repositories;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by psw10 on 2017-02-19.
 */

public class FindGameRepo {
    @SerializedName("FindGameList")
    private ArrayList<FindGameList> findGameLists;
    private int code;

    public ArrayList<FindGameList> getFindGameList() {
        return findGameLists;
    }

    public int getCode() {
        return code;
    }

    public class FindGameList{
        private String gameTitle;
        private String gameDescription;
        private String gameImagePath;
        private String nickName;
        private float stars;
        public String getGameTitle() {
            return gameTitle;
        }

        public String getGameDescription() {
            return gameDescription;
        }

        public String getGameImagePath() {
            return gameImagePath;
        }

        public String getNickName() {
            return nickName;
        }

        public float getStars() {
            return stars;
        }
    }

}
