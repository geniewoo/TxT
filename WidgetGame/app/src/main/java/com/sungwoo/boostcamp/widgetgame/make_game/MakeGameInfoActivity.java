package com.sungwoo.boostcamp.widgetgame.make_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.GameInfo;
import com.sungwoo.boostcamp.widgetgame.Repositories.MakeGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Page;

import java.util.regex.Pattern;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

import static com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility.REQ_CODE_SELECT_IMAGE;

public class MakeGameInfoActivity extends AppCompatActivity {
    private static final String TAG = MakeGameInfoActivity.class.getSimpleName();

    Realm mRealm;
    Uri mGameInfoImageUri = null;

    @BindView(R.id.make_game_info_title_et)
    EditText mMakeGameInfoTitleEt;
    @BindView(R.id.make_game_info_description_et)
    EditText mMakeGameInfoDescriptionEt;
    @BindView(R.id.make_game_info_image_iv)
    ImageView mMakeGameInfoImageIv;
    @BindView(R.id.make_game_info_image_tv)
    TextView mMakeGameInfoImageTv;
    @BindDimen(R.dimen.user_circle_iv)
    protected int USER_CIRCLE_IV;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game_info);

        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();
    }
    @OnClick(R.id.make_game_info_start_btn)
    public void onMakeGameInfoStartBtnClicked() {
        setMakeGamePreference();

        if (mGameInfoImageUri != null) {
            ImageUtility.saveImageInFilesDirectory(this, mGameInfoImageUri, getString(R.string.LOCAL_STORAGE_MAKE_GAME_DIR), getString(R.string.LOCAL_MAKE_GAME_INFO_IMAGE_FILE_NAME));
        }

        if (!checkValuesAreValidateAndShowMessage()){
            return;
        }

        GameInfo gameInfo = getNewMakeGameInfo();
        deleteOldAndSaveNewMakeGameRepo(gameInfo);

        Intent intent = new Intent(getApplicationContext(), MakeGamePageActivity.class);
        intent.putExtra(getString(R.string.INTENT_MAKE_NEW_GAME_PAGE), true);
        intent.putExtra(getString(R.string.INTENT_MAKE_GAME_PAGE_INDEX), 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.make_game_info_image_fl)
    public void onMakeGameInfoIfClicked(){
        ImageUtility.startSelectImageActivity(this);
    }

    private void deleteOldAndSaveNewMakeGameRepo(GameInfo gameRepo) {
        MakeGameRepo makeGameRepo = new MakeGameRepo(gameRepo);
        mRealm.beginTransaction();
        mRealm.where(MakeGameRepo.class).findAll().deleteAllFromRealm();
        mRealm.insert(makeGameRepo);
        mRealm.commitTransaction();
    }

    private GameInfo getNewMakeGameInfo() {
        String titleStr = mMakeGameInfoTitleEt.getText().toString();
        String descriptionStr = mMakeGameInfoDescriptionEt.getText().toString();
        String imagePath = getString(R.string.LOCAL_NO_IMAGE_FILE);
        if(mGameInfoImageUri != null){
            imagePath = getString(R.string.LOCAL_MAKE_GAME_INFO_IMAGE_FILE_NAME);
        }
        RealmList<Page> pages = new RealmList<>();
        return new GameInfo(titleStr, getString(R.string.GAME_GENRE_SELECTIONS), descriptionStr, imagePath, 0, pages);
    }

    private void setMakeGamePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_MAKE_GAME), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.PREF_MAKE_GAME_MAX_INDEX), 0);
        editor.putBoolean(getString(R.string.PREF_MAKE_GAME_IS_EXISTS), true);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) {
            mRealm.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQ_CODE_SELECT_IMAGE:
                mGameInfoImageUri = data.getData();
                Picasso.with(getApplicationContext()).load(mGameInfoImageUri).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMakeGameInfoImageIv);
                mMakeGameInfoImageTv.setVisibility(View.GONE);
                break;
        }
    }

    private boolean isValidateTitle() {
        String titleStr = mMakeGameInfoTitleEt.getText().toString();
        if ( 0 < titleStr.length() && titleStr.length() < 16 ){
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidateDescription() {
        String descriptionStr = mMakeGameInfoDescriptionEt.getText().toString();
        if( 5 < descriptionStr.length() && descriptionStr.length() <= 400 ) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkValuesAreValidateAndShowMessage() {
        if (!isValidateTitle()) {
            Toast.makeText(this, R.string.INVALID_MAKE_GAME_INFO_TITLE, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidateDescription()){
            Toast.makeText(this, R.string.INVALID_MAKE_GAME_INFO_DESCRIPTION, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}