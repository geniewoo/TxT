package com.sungwoo.boostcamp.widgetgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;
import com.sungwoo.boostcamp.widgetgame.RetrofitRequests.UserInformRetrofit;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JoinActivity extends AppCompatActivity {
    @BindView(R.id.join_email_et)
    protected EditText mJoinEmailEt;
    @BindView(R.id.join_password_et)
    protected EditText mJoinPasswordEt;
    @BindView(R.id.join_nickname_et)
    protected EditText mJoinNickname_et;
    @BindView(R.id.join_join_btn)
    protected Button mJoinJoinBtn;

    //회원가입 스트링 유효성 파악을 위한 패턴들
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^[A-Z0-9!@#$%]{8,20}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_NICKNAME_REGES = Pattern.compile("^[A-z0-9가-힣_]{2,16}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        ButterKnife.bind(this);

        mJoinJoinBtn.setOnClickListener(new View.OnClickListener() {// 회원가입 버튼을 누를시
            @Override
            public void onClick(View v) {
                String email = mJoinEmailEt.getText().toString();
                String password = mJoinPasswordEt.getText().toString();
                String nickname = mJoinNickname_et.getText().toString();
                if(!checkLocally(email, password, nickname)){   //각 값들을 로컬에서 테스트하고 이상이 있을 시 서버로 넘기지 않는다
                    return;
                }
                checkJoinServer(email, password, nickname);     //각 값들을 서버에서 중복확인을 한다
            }
        });
    }

    private boolean checkLocally(String email, String password, String nickname){   //각 값들을 확인해 이상이 있을 시 토스트알림을 띄워준다

        if(!checkEmail(email)){
            Toast.makeText(this, getString(R.string.JOIN_EMAIL_IS_WRONG), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!checkPassword(password)){
            Toast.makeText(this, getString(R.string.JOIN_PASSWORD_IS_WRONG), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!checkNickname(nickname)){
            Toast.makeText(this, getString(R.string.JOIN_NICKNAME_IS_WRONG), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean checkEmail(String email){
        if(!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find())
            return false;
        return true;
    }

    private boolean checkPassword(String password){
        if(!VALID_PASSWORD_REGEX.matcher(password).find())
            return false;
        return true;
    }

    private boolean checkNickname(String nickname){
        if(!VALID_NICKNAME_REGES.matcher(nickname).find())
            return false;
        return true;
    }

    private void checkJoinServer(String email, String password, String nickname){   //서버에 값들을 보내 중복이 없을 시 회원가입까지, 있으면 오류코드를 받아온다.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserInformRetrofit userInformRetrofit = retrofit.create(UserInformRetrofit.class);
        Call<CommonRepo.ResultCodeRepo> testJoinServerCall = userInformRetrofit.testJoinServer(email, password, nickname);
        testJoinServerCall.enqueue(new Callback<CommonRepo.ResultCodeRepo>() {
            @Override
            public void onResponse(Call<CommonRepo.ResultCodeRepo> call, Response<CommonRepo.ResultCodeRepo> response) {
                CommonRepo.ResultCodeRepo weatherGson = response.body();
                if (weatherGson.getCode() == 100) {     //성공
                    Toast.makeText(JoinActivity.this, "유효", Toast.LENGTH_SHORT).show();//TODO 후에 "가입성공", finish()로 바꿈
                }else if (weatherGson.getCode() == 200){    //이메일 중복
                    Toast.makeText(JoinActivity.this, getString(R.string.JOIN_EMAIL_EXISTS), Toast.LENGTH_SHORT).show();
                }else if (weatherGson.getCode() == 300){    //닉네임 중복
                    Toast.makeText(JoinActivity.this, getString(R.string.JOIN_NICKNAME_EXISTS), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CommonRepo.ResultCodeRepo> call, Throwable t) {
                Toast.makeText(JoinActivity.this, getString(R.string.COMMON_ERROR), Toast.LENGTH_SHORT).show();
            }
        });
    }

}