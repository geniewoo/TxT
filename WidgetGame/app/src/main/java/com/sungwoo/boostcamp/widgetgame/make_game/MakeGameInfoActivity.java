package com.sungwoo.boostcamp.widgetgame.make_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.GameInfo;
import com.sungwoo.boostcamp.widgetgame.Repositories.MakeGameRepo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class MakeGameInfoActivity extends AppCompatActivity {

    Realm mRealm;
    @BindView(R.id.make_game_info_title_et)
    EditText mMakeGameInfoTitleEt;
    @BindView(R.id.make_game_info_description_et)
    EditText mMakeGameInfoDescriptionEt;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_make_game_info);

        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();
    }
    @OnClick(R.id.make_game_start_btn)
    public void makeGameStartBtn() {
        setMakeGamePreference();
        GameInfo gameInfo = getNewMakeGameInfo();
        saveNewMakeGameRepo(gameInfo);

        Intent intent = new Intent(getApplicationContext(), MakeGamePageActivity.class);
        startActivity(intent);
    }

    private void saveNewMakeGameRepo(GameInfo gameRepo) {
        MakeGameRepo makeGameRepo = new MakeGameRepo(gameRepo);
        mRealm.beginTransaction();
        mRealm.delete(MakeGameRepo.class);
        mRealm.insert(makeGameRepo);
        mRealm.commitTransaction();
    }

    private GameInfo getNewMakeGameInfo() {
        String titleStr = mMakeGameInfoTitleEt.getText().toString();
        String descriptionStr = mMakeGameInfoDescriptionEt.getText().toString();
        GameInfo.Page[] pages = {};
        return new GameInfo(titleStr, getString(R.string.GAME_GENRE_SELECTIONS), descriptionStr, 0, pages);
    }

    private void setMakeGamePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_MAKE_GAME), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.PREF_MAKE_GAME_MAX_INDEX), 1);
        editor.putBoolean(getString(R.string.PREF_MAKE_GAME_IS_EXISTS), true);
        editor.apply();
    }
}
