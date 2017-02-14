package com.sungwoo.boostcamp.widgetgame.MakeGame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sungwoo.boostcamp.widgetgame.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeGameMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game_menu);

        ButterKnife.bind(this);
    }
    @OnClick(R.id.make_new_game_btn)
    public void onMakeNewGameBtnClicked(){
        Intent intent = new Intent(getApplicationContext(), MakeGameInfoActivity.class);
        startActivity(intent);
    }
}
