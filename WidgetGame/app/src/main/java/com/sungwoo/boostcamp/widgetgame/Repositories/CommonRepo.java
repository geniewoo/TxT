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
        private String nickname;
        private String err_msg;

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
}
