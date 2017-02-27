package com.sungwoo.boostcamp.widgetgame.Repositories;

/**
 * Created by SungWoo on 2017-02-17.
 */

public class Maker {
    private String email;
    private String nickName;

    public Maker(String email, String nickName) {
        this.email = email;
        this.nickName = nickName;
    }

    public Maker() {
    }

    public String getEmail() {
        return email;
    }

    public String getNickName() {
        return nickName;
    }
}
