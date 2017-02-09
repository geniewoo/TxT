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
    @BindView(R.id.login_login_btn)
    protected Button mLoginLoginBtn;
    @BindView(R.id.login_join_btn)
    protected Button mLoginJoinBtn;

    private String mEmail;
    private String mPassword;
    private String mNickname;
    private String mImageUrl;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        testLoginPreference();

        ButterKnife.bind(this);

        mLoginJoinBtn.setOnClickListener(new View.OnClickListener() { //회원가입 버튼 클릭시 회원가입 activity로 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

        mLoginLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLoginServer(mLoginEmailEt.getText().toString(), mLoginPasswordEt.getText().toString());
            }
        });

    }

    private void testLoginServer(String email, String password) {
        mEmail = email;
        mPassword = password;
        if (!CommonUtility.isNetworkAvailable(getApplicationContext())) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserInformRetrofit userInformRetrofit = retrofit.create(UserInformRetrofit.class);
        Call<CommonRepo.ResultNicknameRepo> testJoinServerCall = userInformRetrofit.testLoginServer(mEmail, mPassword);
        testJoinServerCall.enqueue(new Callback<CommonRepo.ResultNicknameRepo>() {
            @Override
            public void onResponse(Call<CommonRepo.ResultNicknameRepo> call, Response<CommonRepo.ResultNicknameRepo> response) {
                CommonRepo.ResultNicknameRepo resultCodeRepo = response.body();
                if (resultCodeRepo.getCode() == 100) {     //성공
                    Toast.makeText(LoginActivity.this, getString(R.string.LOGIN_SUCCESS), Toast.LENGTH_SHORT).show();
                    mNickname = resultCodeRepo.getNickname();
                    mImageUrl = resultCodeRepo.getImageUrl();
                    loginSuccess();
                } else if (resultCodeRepo.getCode() == 200) {    //이메일 or nickname 없음
                    Toast.makeText(LoginActivity.this, getString(R.string.LOGIN_FAIL), Toast.LENGTH_SHORT).show();
                } else if (resultCodeRepo.getCode() == 500) { // 서버 내 오류
                    Toast.makeText(LoginActivity.this, getString(R.string.COMMON_SERVER_ERROR), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommonRepo.ResultNicknameRepo> call, Throwable t) {
                CommonUtility.networkError(getApplicationContext());
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    private void loginSuccess() {//TODO sharedPreference에 정보 넣기, 이미지 들고있는 방법 구상하기
        updateLoginPreference();

        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateLoginPreference() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_USER), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.PREF_EMAIL), mEmail);
        editor.putString(getString(R.string.PREF_NICKNAME), mNickname);
        editor.putString(getString(R.string.PREF_PASSWORD), mPassword);
        editor.putString(getString(R.string.PREF_IMAGE_URL), mImageUrl);
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