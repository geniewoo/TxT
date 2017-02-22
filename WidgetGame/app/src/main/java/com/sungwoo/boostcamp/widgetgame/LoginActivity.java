package com.sungwoo.boostcamp.widgetgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;
import com.sungwoo.boostcamp.widgetgame.RetrofitRequests.UserInformationRetrofit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.login_email_et)
    protected EditText mLoginEmailEt;
    @BindView(R.id.login_password_et)
    protected EditText mLoginPasswordEt;
    @BindView(R.id.activity_login_lo)
    protected LinearLayout mActivityLoginLo;

    public static final String ACTION_LOGIN_SUCCESS = "ACTION_LOGIN_SUCCESS";
    private final CommonRepo.UserRepo mUserRepo = new CommonRepo.UserRepo();

    private  static final int JOIN_REQUEST_CODE = 100;
    private static final int LOGIN_SUCCESS = 100;
    private static final int LOGIN_CAN_NOT_FIND_USER = 200;
    private static final int LOGIN_SERVER_ERROR = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

//        CommonUtility.showNeutralDialog(getApplicationContext(), R.string.DIALOG_ERR_TITLE, R.string.DIALOG_JOIN_EMAIL_EXISTS_ITEM, R.string.DIALOG_CONFIRM);
        testLoginPreference();

    }

    private void testLoginServer(String email, String password) {
        mUserRepo.setEmail(email);
        mUserRepo.setPassword(password);
        if (!CommonUtility.isNetworkAvailableShowErrorMessageIfNeeded(LoginActivity.this)) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserInformationRetrofit userInformationRetrofit = retrofit.create(UserInformationRetrofit.class);
        Call<CommonRepo.ResultNicknameRepo> testJoinServerCall = userInformationRetrofit.testLoginServer(mUserRepo.getEmail(), mUserRepo.getPassword());
        testJoinServerCall.enqueue(new Callback<CommonRepo.ResultNicknameRepo>() {
            @Override
            public void onResponse(Call<CommonRepo.ResultNicknameRepo> call, Response<CommonRepo.ResultNicknameRepo> response) {
                CommonRepo.ResultNicknameRepo resultCodeRepo = response.body();
                switch (resultCodeRepo.getCode()) {
                    case LOGIN_SUCCESS:
                        mUserRepo.setNickname(resultCodeRepo.getNickname());
                        mUserRepo.setImageUrl(resultCodeRepo.getImageUrl());
                        loginSuccess();
                        break;
                    case LOGIN_CAN_NOT_FIND_USER:
                        CommonUtility.showNeutralDialog(LoginActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_LOGIN_FAIL_CONTENT, R.string.DIALOG_CONFIRM);
                        break;
                    case LOGIN_SERVER_ERROR:
                        CommonUtility.showNeutralDialog(LoginActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_COMMON_SERVER_ERROR_CONTENT, R.string.DIALOG_CONFIRM);
                        break;
                }
            }

            @Override
            public void onFailure(Call<CommonRepo.ResultNicknameRepo> call, Throwable t) {
                CommonUtility.showNeutralDialog(LoginActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_COMMON_SERVER_ERROR_CONTENT, R.string.DIALOG_CONFIRM);
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.login_join_btn)
    public void onLoginJoinBtnClick() {
        Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, JOIN_REQUEST_CODE);
    }

    @OnClick(R.id.login_login_btn)
    public void onLoginLoginBtnClick() {
        testLoginServer(mLoginEmailEt.getText().toString(), mLoginPasswordEt.getText().toString());
    }

    private void loginSuccess() {

        updateLoginPreference();

        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.setAction(ACTION_LOGIN_SUCCESS);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateLoginPreference() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_USER), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.PREF_USER_EMAIL), mUserRepo.getEmail());
        editor.putString(getString(R.string.PREF_USER_PASSWORD), mUserRepo.getPassword());
        editor.putString(getString(R.string.PREF_USER_NICKNAME), mUserRepo.getNickname());
        editor.putString(getString(R.string.PREF_USER_IMAGE_URL), mUserRepo.getImageUrl());
        editor.apply();
    }

    private void testLoginPreference() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_USER), MODE_PRIVATE);
        if (preferences.contains(getString(R.string.PREF_USER_EMAIL)) && preferences.contains(getString(R.string.PREF_USER_PASSWORD))) {
            String email = preferences.getString(getString(R.string.PREF_USER_EMAIL), "");
            String password = preferences.getString(getString(R.string.PREF_USER_PASSWORD), "");
            testLoginServer(email, password);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case JOIN_REQUEST_CODE:
                if (resultCode == JoinActivity.JOIN_SUCCESS_RESULT_CODE){
                    Snackbar.make(mActivityLoginLo, R.string.DIALOG_JOIN_SUCCESS_CONTENT, Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }
}