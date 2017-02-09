package com.sungwoo.boostcamp.widgetgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    protected Button mLoginLoingBtn;
    @BindView(R.id.login_join_btn)
    protected Button mLoginJoinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mLoginJoinBtn.setOnClickListener(new View.OnClickListener() { //회원가입 버튼 클릭시 회원가입 activity로 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

        mLoginLoingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLoginServer();
            }
        });
    }
    private void testLoginServer(){
        String email = mLoginEmailEt.getText().toString();
        String password = mLoginPasswordEt.getText().toString();
        if(!CommonUtility.isNetworkAvailable(getApplicationContext())) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserInformRetrofit userInformRetrofit = retrofit.create(UserInformRetrofit.class);
        Call<CommonRepo.ResultCodeRepo> testJoinServerCall = userInformRetrofit.testLoginServer(email, password);
        testJoinServerCall.enqueue(new Callback<CommonRepo.ResultCodeRepo>() {
            @Override
            public void onResponse(Call<CommonRepo.ResultCodeRepo> call, Response<CommonRepo.ResultCodeRepo> response) {
                CommonRepo.ResultCodeRepo weatherGson = response.body();
                if (weatherGson.getCode() == 100) {     //성공
                    Toast.makeText(LoginActivity.this, getString(R.string.LOGIN_SUCCESS), Toast.LENGTH_SHORT).show();
                    loginSucess();
                }else if (weatherGson.getCode() == 200){    //이메일 or nickname 없음
                    Toast.makeText(LoginActivity.this, getString(R.string.LOGIN_FAIL), Toast.LENGTH_SHORT).show();
                }else if (weatherGson.getCode() == 500){ // 서버 내 오류
                    Toast.makeText(LoginActivity.this, getString(R.string.COMMON_SERVER_ERROR), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CommonRepo.ResultCodeRepo> call, Throwable t) {
                CommonUtility.networkError(getApplicationContext());
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    private void loginSucess(){//TODO sharedPreference에 정보 넣기, 이미지 들고있는 방법 구상하기
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);
    }
}
