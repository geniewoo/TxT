package com.sungwoo.boostcamp.widgetgame.Repositories;

/**
 * Created by SungWoo on 2017-02-08.
 */

public class CommonRepo {

    public class ResultCodeRepo {

        private int code;
        private String errorMessage;

        public int getCode() {
            return code;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public class ResultNicknameRepo {

        private int code;
        private String errorMessage;
        private String nickname;
        private String imageUrl;

        public String getImageUrl() {
            return imageUrl;
        }

        public int getCode() {
            return code;
        }

        public String getErrorMessage() {
            return errorMessage;
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

        public  UserRepo(){

        }

        public UserRepo(String email, String password, String nickname, String imageUrl) {
            this.email = email;
            this.password = password;
            this.nickname = nickname;
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

        public void setEmail(String email) {
            this.email = email;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    static public class MakeGamePreference{
        private boolean isExist;
        private int maxIndex;

        public MakeGamePreference() {
        }

        public MakeGamePreference(boolean isExist, int maxIndex) {
            this.isExist = isExist;
            this.maxIndex = maxIndex;
        }

        public boolean isExist() {
            return isExist;
        }

        public int getMaxIndex() {
            return maxIndex;
        }
    }
}
