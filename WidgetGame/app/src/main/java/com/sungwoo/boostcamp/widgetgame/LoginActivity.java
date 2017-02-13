package com.sungwoo.boostcamp.widgetgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;
import com.sungwoo.boostcamp.widgetgame.RetrofitRequests.UserInformRetrofit;

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

    private CommonRepo.UserRepo mUserRepo = new CommonRepo.UserRepo();

    private static final String TAG = "LoginActivity";

    private static final int LOGIN_SUCCESS = 100;
    private static final int LOGIN_CAN_NOT_FIND_USER = 200;
    private static final int LOGIN_SERVER_ERROR = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        testLoginPreference();

        ButterKnife.bind(this);
    }

    private void testLoginServer(String email, String password) {
        mUserRepo.setEmail(email);
        mUserRepo.setPassword(password);
        if (!CommonUtility.isNetworkAvailableShowErrorMessageIfNeeded(getApplicationContext())) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserInformRetrofit userInformRetrofit = retrofit.create(UserInformRetrofit.class);
        Call<CommonRepo.ResultNicknameRepo> testJoinServerCall = userInformRetrofit.testLoginServer(mUserRepo.getEmail(), mUserRepo.getPassword());
        testJoinServerCall.enqueue(new Callback<CommonRepo.ResultNicknameRepo>() {
            @Override
            public void onResponse(Call<CommonRepo.ResultNicknameRepo> call, Response<CommonRepo.ResultNicknameRepo> response) {
                CommonRepo.ResultNicknameRepo resultCodeRepo = response.body();
                switch (resultCodeRepo.getCode()) {
                    case LOGIN_SUCCESS:
                        Toast.makeText(LoginActivity.this, R.string.LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
                        mUserRepo.setNickname(resultCodeRepo.getNickname());
                        mUserRepo.setImageUrl(resultCodeRepo.getImageUrl());
                        loginSuccess();
                        break;
                    case LOGIN_CAN_NOT_FIND_USER:
                        Toast.makeText(LoginActivity.this, R.string.LOGIN_FAIL, Toast.LENGTH_SHORT).show();
                        break;
                    case LOGIN_SERVER_ERROR:
                        Toast.makeText(LoginActivity.this, R.string.COMMON_SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<CommonRepo.ResultNicknameRepo> call, Throwable t) {
                CommonUtility.displayNetworkError(getApplicationContext());
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
        startActivity(intent);
    }

    @OnClick(R.id.login_login_btn)
    public void onLoginLoginBtnClick() {
        testLoginServer(mLoginEmailEt.getText().toString(), mLoginPasswordEt.getText().toString());
    }

    private void loginSuccess() {

        updateLoginPreference();

        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateLoginPreference() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_USER), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.PREF_EMAIL), mUserRepo.getEmail());
        editor.putString(getString(R.string.PREF_PASSWORD), mUserRepo.getPassword());
        editor.putString(getString(R.string.PREF_NICKNAME), mUserRepo.getNickname());
        editor.putString(getString(R.string.PREF_IMAGE_URL), mUserRepo.getImageUrl());
        editor.apply();
    }

    private void testLoginPreference() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_USER), MODE_PRIVATE);
        if (preferences.contains(getString(R.string.PREF_EMAIL)) && preferences.contains(getString(R.string.PREF_PASSWORD))) {
            String email = preferences.getString(getString(R.string.PREF_EMAIL), "");
            String password = preferences.getString(getString(R.string.PREF_PASSWORD), "");
            testLoginServer(email, password);
        } else {
            return;
        }
    }
}