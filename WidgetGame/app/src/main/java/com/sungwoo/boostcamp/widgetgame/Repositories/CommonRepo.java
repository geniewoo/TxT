package com.sungwoo.boostcamp.widgetgame.Repositories;

/**
 * Created by SungWoo on 2017-02-08.
 */

public class CommonRepo {

    public class ResultCodeRepo {

        private int code;
        private String err_msg;

        public int getCode() {
            return code;
        }

        public String getErr_msg() {
            return err_msg;
        }
    }

    public class ResultNicknameRepo {

        private int code;
        private String err_msg;
        private String nickname;
        private String imageUrl;

        public String getImageUrl() {
            return imageUrl;
        }

        public int getCode() {
            return code;
        }

        public String getErr_msg() {
            return err_msg;
        }

        public String getNickname() {
            return nickname;
        }
    }

    static public class UserRepo {
        private String email;
        private String nickname;
        private String password;
        private String imageUrl;

        public UserRepo(String email, String nickname, String password, String imageUrl) {
            this.email = email;
            this.nickname = nickname;
            this.password = password;
            this.imageUrl = imageUrl;
        }

        public String getEmail() {
            return email;
        }

        public String getNickname() {
            return nickname;
        }

        public String getPassword() {
            return password;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
