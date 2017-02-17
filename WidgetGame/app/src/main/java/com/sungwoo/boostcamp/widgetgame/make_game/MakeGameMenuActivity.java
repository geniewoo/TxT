package com.sungwoo.boostcamp.widgetgame.make_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class MakeGameMenuActivity extends AppCompatActivity {


    CommonRepo.MakeGamePreference mMakeGamePreference = null;

    private static final String TAG = "MakeGameMenuActivity";

    @BindView(R.id.make_menu_continue_btn)
    Button mMakeMenuContinueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game_menu);

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initMakeGamePreference();

        if (mMakeGamePreference == null) {
            setUnClickableMakeGameContinueBtn();
        } else {
            setClickableMakeGameContinueBtn();
        }
    }

    @OnClick(R.id.make_menu_new_btn)
    public void onMakeNewGameBtnClicked() {
        if (mMakeGamePreference != null) {  //TODO alert다이얼로그 만들어서 여부 묻기
            Toast.makeText(this, "제작중인 게임이 있습니다", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "New game button clicked");
        Intent intent = new Intent(getApplicationContext(), MakeGameInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.make_menu_continue_btn)
    public void onMakeMenuContinueBtnClicked() {
        if(mMakeGamePreference != null){
            Log.d(TAG, "here right?");
            Intent intent = new Intent(getApplicationContext(), MakeGamePageActivity.class);
            intent.putExtra(getString(R.string.INTENT_MAKE_NEW_GAME_PAGE), true);
            intent.putExtra(getString(R.string.INTENT_MAKE_GAME_PAGE_INDEX), mMakeGamePreference.getMaxIndex() + 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void initMakeGamePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_MAKE_GAME), MODE_PRIVATE);
        if (!sharedPreferences.contains(getString(R.string.PREF_MAKE_GAME_IS_EXISTS)) || !sharedPreferences.getBoolean(getString(R.string.PREF_MAKE_GAME_IS_EXISTS), false)) {
            return;
        } else {
            mMakeGamePreference = new CommonRepo.MakeGamePreference(sharedPreferences.getBoolean(getString(R.string.PREF_MAKE_GAME_IS_EXISTS), false),
                    sharedPreferences.getInt(getString(R.string.PREF_MAKE_GAME_MAX_INDEX), 0));
        }
    }

    private void setUnClickableMakeGameContinueBtn() {
        mMakeMenuContinueBtn.setEnabled(false);
    }

    private void setClickableMakeGameContinueBtn() {
        mMakeMenuContinueBtn.setEnabled(true);
    }
}
