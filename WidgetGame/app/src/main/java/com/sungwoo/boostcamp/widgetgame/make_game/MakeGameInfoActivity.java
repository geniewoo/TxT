package com.sungwoo.boostcamp.widgetgame.make_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.GameInfo;
import com.sungwoo.boostcamp.widgetgame.Repositories.MakeGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Page;

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

        if (!checkValuesAreValidateAndShowMessage()) {
            return;
        }

        if (mGameInfoImageUri != null) {
            Picasso.with(this).load(mGameInfoImageUri).resize(300,200).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    ImageUtility.saveImageInFilesDirectory(getApplicationContext(), bitmap, getString(R.string.LOCAL_STORAGE_MAKE_GAME_DIR), getString(R.string.LOCAL_MAKE_GAME_INFO_IMAGE_FILE_NAME));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }


        GameInfo gameInfo = getNewMakeGameInfo();
        deleteOldAndSaveNewMakeGameRepo(gameInfo);

        Intent intent = new Intent(getApplicationContext(), MakeGamePageActivity.class);
        intent.putExtra(getString(R.string.INTENT_MAKE_NEW_GAME_PAGE), true);
        intent.putExtra(getString(R.string.INTENT_MAKE_GAME_PAGE_INDEX), 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        setMakeGamePreference();
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
        if( 10 <= descriptionStr.length() && descriptionStr.length() <= 200 ) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkValuesAreValidateAndShowMessage() {
        if (!isValidateTitle()) {
            CommonUtility.showNeutralDialog(MakeGameInfoActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_MAKE_GAME_INFO_INVALID_TITLE, R.string.DIALOG_CONFIRM);
            return false;
        } else if (!isValidateDescription()){
            CommonUtility.showNeutralDialog(MakeGameInfoActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_MAKE_GAME_INFO_INVALID_DESCRIPTION, R.string.DIALOG_CONFIRM);
            return false;
        }
        return true;
    }
}