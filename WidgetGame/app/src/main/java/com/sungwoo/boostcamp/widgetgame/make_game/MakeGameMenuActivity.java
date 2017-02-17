package com.sungwoo.boostcamp.widgetgame.make_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.FullGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.GameInfo;
import com.sungwoo.boostcamp.widgetgame.Repositories.MakeGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Maker;
import com.sungwoo.boostcamp.widgetgame.Repositories.Page;
import com.sungwoo.boostcamp.widgetgame.Repositories.PlayInfo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Selection;
import com.sungwoo.boostcamp.widgetgame.upload.Upload;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

public class MakeGameMenuActivity extends AppCompatActivity {

    CommonRepo.MakeGamePreference mMakeGamePreference = null;
    Realm mRealm;

    private static final String TAG = "MakeGameMenuActivity";
    private static final int OVER_PAGE_INDEX = 100;
    private static final int NO_GAME_CLEAR_PAGE = 200;
    private static final int VALID_GAME = 300;

    @BindView(R.id.make_menu_continue_btn)
    Button mMakeMenuContinueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game_menu);

        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();
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
        if (mMakeGamePreference != null) {
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

    @OnClick(R.id.make_menu_share_btn)
    public void onMakeMenuShareBtnClicked() {
        switch (checkValidateGame()) {
            case OVER_PAGE_INDEX:
                Toast.makeText(this, "OVER_PAGE_INDEX", Toast.LENGTH_SHORT).show();
                return;
            case NO_GAME_CLEAR_PAGE:
                Toast.makeText(this, "NO_GAME_CLEAR_PAGE", Toast.LENGTH_SHORT).show();
                return;
            case VALID_GAME:
                Toast.makeText(this, "isValid", Toast.LENGTH_SHORT).show();
                postGameToServer();
        }
    }

    private int checkValidateGame() {
        MakeGameRepo makeGameRepo = mRealm.where(MakeGameRepo.class).findAll().get(0);
        RealmList<Page> pages = makeGameRepo.getGameInfo().getPages();
        boolean isAtLeastOneClearGamePage = false;
        int pageNum = pages.size();
        for (int i = 0 ; i < pageNum ; i ++) {
            Page page = pages.get(i);
            for (Selection selection : page.getSelections()) {
                if (pageNum < selection.getNextIndex()) {
                    return OVER_PAGE_INDEX;
                }
            }
            if (isAtLeastOneClearGamePage == false && page.getPage().equals(getString(R.string.SPINNER_PAGE_4))) {
                isAtLeastOneClearGamePage = true;
            }
        }

        if (!isAtLeastOneClearGamePage) {
            return NO_GAME_CLEAR_PAGE;
        }
        return VALID_GAME;
    }
    private void postGameToServer() {
        FullGameRepo fullGameRepo = makeFullGameRepoWithMakeGameRepo();
        Upload.uploadGameImages(getApplicationContext(), fullGameRepo);
    }

    private FullGameRepo makeFullGameRepoWithMakeGameRepo() {
        CommonRepo.UserRepo userRepo = CommonUtility.getUserRepoFromPreference(getApplicationContext());
        Maker maker = new Maker(userRepo.getEmail(), userRepo.getNickname());
        PlayInfo playInfo = new PlayInfo(0, 0, 0, 0, 0);
        GameInfo gameInfo = mRealm.where(MakeGameRepo.class).findAll().get(0).getGameInfo();

        return new FullGameRepo(gameInfo, playInfo, maker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}