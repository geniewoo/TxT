package com.sungwoo.boostcamp.widgetgame.make_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import io.realm.Realm;
import io.realm.RealmList;

public class MakeGameMenuActivity extends AppCompatActivity {

    CommonRepo.MakeGamePreference mMakeGamePreference = null;
    Realm mRealm;

    private static final int OVER_PAGE_INDEX = 100;
    private static final int NO_GAME_CLEAR_PAGE = 200;
    private static final int VALID_GAME = 300;

    @BindView(R.id.make_menu_lv)
    ListView mMakeMenuLv;

    private boolean mIsGameExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game_menu);

        ButterKnife.bind(this);

        constructMakeMenuLv();

        mRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initMakeGamePreference();

        if (mMakeGamePreference == null) {
            mIsGameExist = false;
        } else {
            mIsGameExist = true;
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

    private void constructMakeMenuLv() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.menu_lv_item, getResources().getStringArray(R.array.MAKE_MENU_LV_ITEMS));
        mMakeMenuLv.setAdapter(arrayAdapter);
        mMakeMenuLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        if (mMakeGamePreference != null) {
                            new MaterialDialog.Builder(MakeGameMenuActivity.this)
                                    .title(R.string.DIALOG_ALERT_TITLE)
                                    .content(R.string.DIALOG_MAKE_GAME_MENU_IS_ALREADY_EXIST)
                                    .positiveText(R.string.DIALOG_POSITIVE)
                                    .negativeText(R.string.DIALOG_NEGATIVE)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Intent intent = new Intent(getApplicationContext(), MakeGameInfoActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                        } else {
                            intent = new Intent(getApplicationContext(), MakeGameInfoActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 1:
                        if (!mIsGameExist) {
                            intent = new Intent(getApplicationContext(), MakeGameInfoActivity.class);
                            startActivity(intent);
                        }
                        if (mMakeGamePreference != null) {
                            intent = new Intent(getApplicationContext(), MakeGamePageActivity.class);
                            intent.putExtra(getString(R.string.INTENT_MAKE_NEW_GAME_PAGE), true);
                            intent.putExtra(getString(R.string.INTENT_MAKE_GAME_PAGE_INDEX), mMakeGamePreference.getMaxIndex() + 1);
                            startActivity(intent);
                        }
                        break;
                    case 2:
                        switch (checkValidateGame()) {
                            case OVER_PAGE_INDEX:
                                CommonUtility.showNeutralDialog(MakeGameMenuActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_MAKE_GAME_MENU_INVALID_TARGET, R.string.DIALOG_CONFIRM);
                                return;
                            case NO_GAME_CLEAR_PAGE:
                                CommonUtility.showNeutralDialog(MakeGameMenuActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_MAKE_GAME_MENU_NO_GAME_CLEAR_PAGE, R.string.DIALOG_CONFIRM);
                                return;
                            case VALID_GAME:
                                postGameToServer();
                        }
                        break;
                }
            }
        });
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
        if (!CommonUtility.isNetworkAvailableShowErrorMessageIfNeeded(MakeGameMenuActivity.this)) {
            return;
        }
        FullGameRepo fullGameRepo = makeFullGameRepoWithMakeGameRepo();
        Upload.uploadGameImages(MakeGameMenuActivity.this, fullGameRepo);
    }

    private FullGameRepo makeFullGameRepoWithMakeGameRepo() {
        CommonRepo.UserRepo userRepo = CommonUtility.getUserRepoFromPreference(getApplicationContext());
        Maker maker = new Maker(userRepo.getEmail(), userRepo.getNickname(), userRepo.getImageUrl());
        PlayInfo playInfo = new PlayInfo(0, 0, 0, 0, 0);
        GameInfo gameInfo = mRealm.copyFromRealm(mRealm.where(MakeGameRepo.class).findAll().get(0).getGameInfo());

        return new FullGameRepo(gameInfo, playInfo, maker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) {
            mRealm.close();
        }
    }
}