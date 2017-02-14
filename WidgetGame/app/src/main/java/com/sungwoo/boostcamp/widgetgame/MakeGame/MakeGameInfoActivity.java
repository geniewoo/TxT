package com.sungwoo.boostcamp.widgetgame.MakeGame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sungwoo.boostcamp.widgetgame.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeGameInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game_info);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.make_game_start_btn)
    public void makeGameStartBtn(){
        Intent intent = new Intent(getApplicationContext(), MakeGamePageActivity.class);
        startActivity(intent);
    }
}
