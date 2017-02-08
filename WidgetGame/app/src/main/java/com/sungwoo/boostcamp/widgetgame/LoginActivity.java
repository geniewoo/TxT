package com.sungwoo.boostcamp.widgetgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    }

}
