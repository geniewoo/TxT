package com.sungwoo.boostcamp.widgetgame.Repositories;

import io.realm.RealmObject;

/**
 * Created by SungWoo on 2017-02-17.
 */

public class Maker extends RealmObject{
    private String email;
    private String nickName;
    private String imagePath;

    public Maker(String email, String nickName, String imagePath) {
        this.email = email;
        this.nickName = nickName;
        this.imagePath = imagePath;
    }

    public Maker() {
    }

    public String getEmail() {
        return email;
    }

    public String getNickName() {
        return nickName;
    }

    public String getImagePath() {
        return imagePath;
    }
}
