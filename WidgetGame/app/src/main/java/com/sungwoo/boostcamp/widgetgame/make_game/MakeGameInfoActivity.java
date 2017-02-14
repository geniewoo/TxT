package com.sungwoo.boostcamp.widgetgame.make_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.GameInfo;
import com.sungwoo.boostcamp.widgetgame.Repositories.MakeGameRepo;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class MakeGameInfoActivity extends AppCompatActivity {
    Realm mRealm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game_info);
        mRealm = Realm.getDefaultInstance();

        ButterKnife.bind(this);
    }

    @OnClick(R.id.make_game_start_btn)
    public void makeGameStartBtn(){

        setMakeGamePreference();
        makeGameInfo();
        setMakeGameRepo();

        Intent intent = new Intent(getApplicationContext(), MakeGamePageActivity.class);
        startActivity(intent);
    }

    private void setMakeGameRepo(){
        MakeGameRepo makeGameRepo = new MakeGameRepo();
    }

    private GameInfo makeGameInfo(){

        return new GameInfo();
    }

    private void setMakeGamePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_MAKE_GAME), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.PREF_MAKE_GAME_MAX_INDEX), 1);
        editor.putBoolean(getString(R.string.PREF_MAKE_GAME_IS_EXISTS), true);
        editor.apply();
    }
}
