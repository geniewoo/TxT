package com.sungwoo.boostcamp.widgetgame.make_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.MakeGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Page;
import com.sungwoo.boostcamp.widgetgame.Repositories.Selection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import io.realm.Realm;
import io.realm.RealmList;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility.REQ_CODE_SELECT_IMAGE;

public class MakeGamePageActivity extends AppCompatActivity {
    private static final String TAG = MakeGamePageActivity.class.getSimpleName();

    @BindView(R.id.make_page_selections_lo)
    protected LinearLayout mMakePageSelectionsLo;
    @BindView(R.id.make_page_image_iv)
    protected ImageView mMakePageImageIv;
    @BindView(R.id.make_page_image_tv)
    protected TextView mMakePageImageTv;
    @BindView(R.id.make_page_description_et)
    protected EditText mMakePageDescriptionEt;
    @BindView(R.id.make_page_title_et)
    protected EditText mMakePageTitleEt;
    @BindView(R.id.make_page_confirm_btn)
    protected Button mMakePageConfirmBtn;
    @BindView(R.id.make_index_sp)
    protected Spinner mMakeIndexSp;
    @BindView(R.id.make_page_sp)
    protected Spinner mMakePageSp;
    @BindView(R.id.make_sound_sp)
    protected Spinner mMakeSoundSp;
    @BindView(R.id.make_vibrate_cb)
    protected CheckBox mMakeVibrateCb;
    @BindViews({R.id.make_selections_cb1, R.id.make_selections_cb2, R.id.make_selections_cb3, R.id.make_selections_cb4})
    protected List<CheckBox> mMakeSelectionsCbs;
    @BindViews({R.id.make_selections_et1, R.id.make_selections_et2, R.id.make_selections_et3, R.id.make_selections_et4})
    protected List<EditText> mMakeSelectionsEts;
    @BindViews({R.id.make_target_et1, R.id.make_target_et2, R.id.make_target_et3, R.id.make_target_et4})
    protected List<EditText> mMakeTargetEts;
    @BindDimen(R.dimen.user_circle_iv)
    protected int USER_CIRCLE_IV;

    private boolean isFirstMakeIndexSpItemSelected = true;
    private int mPageIndex;
    private int mMaxPageIndex;
    Uri mGamePageImageUri = null;
    private Boolean mIsNewPage;
    private HashMap<String, Integer> mSoundMap;
    private MediaPlayer mMediaPlayer;

    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game_page);

        ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        mMediaPlayer = new MediaPlayer();
        mSoundMap = CommonUtility.getSoundMap(this);
        Intent intent = getIntent();

        mPageIndex = intent.getIntExtra(getString(R.string.INTENT_MAKE_GAME_PAGE_INDEX), -1);
        mMaxPageIndex = getPreferenceMaxIndex();
        if (mPageIndex == -1) {
            Log.e(TAG, getString(R.string.PAGE_INDEX_ERROR));
            finish();
        } else if (mMaxPageIndex == -1){
            Log.e(TAG, getString(R.string.MAX_INDEX_ERROR));
            finish();
        }

        String[] indexStringArr = getIndexStringArr();
        setIndexView(indexStringArr);
        if (intent.getBooleanExtra(getString(R.string.INTENT_MAKE_NEW_GAME_PAGE), false)) {
            mIsNewPage = true;
        } else {
            mIsNewPage = false;
            setButtonsText();
            setViewValues();
        }
    }

    @OnClick(R.id.make_page_confirm_btn)
    public void onMakePageConfirmBtn(){
        if (mGamePageImageUri != null) {

            Picasso.with(this).load(mGamePageImageUri).resize(300, 200).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    ImageUtility.saveImageInFilesDirectory(getApplicationContext(), bitmap, getString(R.string.LOCAL_STORAGE_MAKE_GAME_DIR), getString(R.string.LOCAL_MAKE_GAME_PAGE_IMAGE_FILE_NAME) + mPageIndex + getString(R.string.FILE_EXPANDER_PNG));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }


        if (!checkValuesAreValidateAndShowMessage()) {
            return;
        }
        insertThisPageInLocalDataBase();

        if (mIsNewPage){
            countUpPreferenceMaxIndex();
        }
        Intent intent = new Intent(this, MakeGamePageActivity.class);
        intent.putExtra(getString(R.string.INTENT_MAKE_NEW_GAME_PAGE), true);
        if (mIsNewPage){
            intent.putExtra(getString(R.string.INTENT_MAKE_GAME_PAGE_INDEX), mPageIndex + 1);
        } else {
            intent.putExtra(getString(R.string.INTENT_MAKE_GAME_PAGE_INDEX), mMaxPageIndex + 1);
        }
        intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.make_page_image_fl)
    public void onMakeImageFlClick() {
        ImageUtility.startSelectImageActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQ_CODE_SELECT_IMAGE:
                mGamePageImageUri = data.getData();
                showMakePageImage();
                break;
        }
    }

    private void showMakePageImage() {
        Picasso.with(getApplicationContext()).load(mGamePageImageUri).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMakePageImageIv);
        mMakePageImageTv.setVisibility(View.GONE);
    }

    private void showMakePageImageWithFile(File file) {
        Picasso.with(getApplicationContext()).load(file).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMakePageImageIv);
        mMakePageImageTv.setVisibility(View.GONE);
    }
    private void setButtonsText(){
        mMakePageConfirmBtn.setText(R.string.MAKE_GAME_PAGE_CHANGE_BTN_TEXT);
    }

    private void setViewValues() {
        MakeGameRepo makeGameRepo = mRealm.where(MakeGameRepo.class).findAll().get(0);
        Page page = makeGameRepo.getGameInfo().getPages().get(mPageIndex - 1);

        mMakePageTitleEt.setText(page.getTitle());
        mMakePageDescriptionEt.setText(page.getDescription());
        mMakeIndexSp.setSelection(mPageIndex - 1);
        mMakeVibrateCb.setEnabled(page.isVibrateOn());

        for (int i = 0 ; i < mMakePageSp.getAdapter().getCount() ; i ++){
            if (mMakePageSp.getItemAtPosition(i).toString().equals(page.getPage())) {
                mMakePageSp.setSelection(i);
                break;
            }
        }

        for (int i = 0 ; i < mMakeSoundSp.getAdapter().getCount() ; i ++){
            if (mMakeSoundSp.getItemAtPosition(i).toString().equals(page.getSound())) {
                mMakeSoundSp.setSelection(i);
                break;
            }
        }

        if (page.getPage() == getString(R.string.SPINNER_PAGE_1)) {
            RealmList<Selection> selections = page.getSelections();
            for (int i = 0 ; i < selections.size() ; i ++){
                mMakeSelectionsCbs.get(i).setSelected(true);
                mMakeSelectionsEts.get(i).setText(selections.get(i).getSelectionText());
                mMakeTargetEts.get(i).setText(selections.get(i).getNextIndex());
            }
        }

        if (!page.getImagePath().equals(getString(R.string.LOCAL_NO_IMAGE_FILE))) {
            File file = ImageUtility.getPageImageFromLocal(getApplicationContext(), mPageIndex);
            mGamePageImageUri = Uri.parse(file.toString()); // Set this value to do not save "none" when click confirmBtn
            showMakePageImageWithFile(file);
        }
    }

    @OnClick(R.id.make_page_cancel_btn)
    public void onMakePageCancelBtnClicked(){
        onBackPressed();
    }

    private void setIndexView(String[] indexStringArr) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.layout_string_spinner, indexStringArr);
        mMakeIndexSp.setAdapter(arrayAdapter);
        mMakeIndexSp.setSelection(mPageIndex - 1);
    }

    private String[] getIndexStringArr() {
        String[] indexStringArr = new String[mMaxPageIndex + 1];
        for (int i = 1 ; i <= mMaxPageIndex + 1 ; i ++) {
            indexStringArr[i - 1] = String.valueOf(i);
        }
        return indexStringArr;
    }

    private void countUpPreferenceMaxIndex() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_MAKE_GAME), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.PREF_MAKE_GAME_MAX_INDEX), mPageIndex);
        editor.apply();
    }

    private int getPreferenceMaxIndex() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_MAKE_GAME), MODE_PRIVATE);
        return sharedPreferences.getInt(getString(R.string.PREF_MAKE_GAME_MAX_INDEX), -1);
    }

    private boolean checkValuesAreValidateAndShowMessage() {
        if (!isValidateTitle()) {
            CommonUtility.showNeutralDialog(MakeGamePageActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_MAKE_GAME_PAGE_TITLE_IS_INVALID, R.string.DIALOG_CONFIRM);
            return false;
        } else if (!isValidateDescription()){
            CommonUtility.showNeutralDialog(MakeGamePageActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_MAKE_GAME_PAGE_DESCRIPTION_IS_INVALID, R.string.DIALOG_CONFIRM);
            return false;
        }

        String pageStr = mMakePageSp.getSelectedItem().toString();

        if (pageStr.equals(getString(R.string.SPINNER_PAGE_1))) {
            int count = 0;
            for (int i = 0 ; i < 4 ; i ++) {
                if (mMakeSelectionsCbs.get(i).isChecked()) {
                    count ++;
                    String nextIndex = mMakeTargetEts.get(i).getText().toString();
                    String selectionText = mMakeSelectionsEts.get(i).getText().toString();

                    if (nextIndex.length() < 1) {
                        CommonUtility.showNeutralDialog(MakeGamePageActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_MAKE_GAME_PAGE_NO_SELECTIONS_INDEX, R.string.DIALOG_CONFIRM);
                        return false;
                    }
                    if (selectionText.length() < 1) {
                        CommonUtility.showNeutralDialog(MakeGamePageActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_MAKE_GAME_PAGE_NO_SELECTIONS_TEXT, R.string.DIALOG_CONFIRM);
                        return false;
                    }
                }
            }
            if (count < 1) {
                CommonUtility.showNeutralDialog(MakeGamePageActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_MAKE_GAME_PAGE_SELECTIONS_ARE_NOT_ENOUGH, R.string.DIALOG_CONFIRM);
                return false;
            }
        }
        return true;
    }

    private boolean isValidateTitle() {
        String titleStr = mMakePageTitleEt.getText().toString();
        if (0 < titleStr.length() && titleStr.length() < 16) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidateDescription() {
        String descriptionStr = mMakePageDescriptionEt.getText().toString();
        if (0 < descriptionStr.length() && descriptionStr.length() <= 400) {
            return true;
        } else {
            return false;
        }
    }

    @OnItemSelected(R.id.make_page_sp)
    public void onMakePageSpItemSelected(int position) {
        if(mMakePageSp.getSelectedItem().toString().equals(getString(R.string.SPINNER_PAGE_1))) {
            for (int i = 0 ; i < mMakePageSelectionsLo.getChildCount() ; i ++) {
                LinearLayout layout = (LinearLayout)mMakePageSelectionsLo.getChildAt(i);
                for (int j = 0 ; j < layout.getChildCount() ; j ++) {
                    layout.getChildAt(j).setEnabled(true);
                }
            }
        } else {
            for (int i = 0 ; i < mMakePageSelectionsLo.getChildCount() ; i ++) {
                LinearLayout layout = (LinearLayout)mMakePageSelectionsLo.getChildAt(i);
                for (int j = 0 ; j < layout.getChildCount() ; j ++) {
                    layout.getChildAt(j).setEnabled(false);
                }
            }
        }
    }
    @OnItemSelected(R.id.make_index_sp)
    public void onMakeIndexSpItemSelected(int position) {
        if (isFirstMakeIndexSpItemSelected) {
            isFirstMakeIndexSpItemSelected = false;
            return;
        }
        if (position < mMaxPageIndex) {
            Intent intent = new Intent(this, MakeGamePageActivity.class);
            intent.putExtra(getString(R.string.INTENT_MAKE_NEW_GAME_PAGE), false);
            intent.putExtra(getString(R.string.INTENT_MAKE_GAME_PAGE_INDEX), position + 1);
            intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (position + 1 != mPageIndex && position == mMaxPageIndex) {
            Intent intent = new Intent(this, MakeGamePageActivity.class);
            intent.putExtra(getString(R.string.INTENT_MAKE_NEW_GAME_PAGE), true);
            intent.putExtra(getString(R.string.INTENT_MAKE_GAME_PAGE_INDEX), mMaxPageIndex + 1);
            intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @OnItemSelected(R.id.make_sound_sp)
    public void onMakeSoundSpItemSelected() {
        String soundStr = mMakeSoundSp.getSelectedItem().toString();
        playSound(soundStr);
    }

    private void playSound(String sound) {
        if (sound.equals(getString(R.string.SPINNER_SOUND_DEFAULT))) {
            return;
        }
        int soundId = mSoundMap.get(sound);
        try {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(this, Uri.parse(getString(R.string.RAW_FILE_FOLDER_URI) + soundId));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertThisPageInLocalDataBase() {
        String titleStr = mMakePageTitleEt.getText().toString();
        String descriptionStr = mMakePageDescriptionEt.getText().toString();
        String pageStr = mMakePageSp.getSelectedItem().toString();
        String soundStr = mMakeSoundSp.getSelectedItem().toString();
        Boolean isVibrateOn = mMakeVibrateCb.isChecked();
        String imagePath = getString(R.string.LOCAL_NO_IMAGE_FILE);

        if(mGamePageImageUri != null){
            imagePath = getString(R.string.LOCAL_MAKE_GAME_PAGE_IMAGE_FILE_NAME) + mPageIndex + getString(R.string.FILE_EXPANDER_PNG);
        }

        int selectionNum = 0;
        RealmList<Selection> selections = new RealmList<>();
        if (pageStr.equals(getString(R.string.SPINNER_PAGE_1))) {
            for (int i = 0 ; i < 4 ; i ++) {
                if (mMakeSelectionsCbs.get(i).isChecked()) {
                    selectionNum ++;
                    int nextIndex = Integer.parseInt(mMakeTargetEts.get(i).getText().toString());
                    String selectionText = mMakeSelectionsEts.get(i).getText().toString();
                    Selection selection = new Selection(false, nextIndex, selectionText);
                    selections.add(selection);
                }
            }
        }

        Page page = new Page(mPageIndex, titleStr, descriptionStr, pageStr, imagePath, soundStr, isVibrateOn, selectionNum, selections);

        MakeGameRepo makeGameRepo = mRealm.where(MakeGameRepo.class).findAll().get(0);

        RealmList<Page> pages = makeGameRepo.getGameInfo().getPages();
        if (mIsNewPage) {
            mRealm.beginTransaction();
            pages.add(page);
            mRealm.commitTransaction();
        } else {
            mRealm.beginTransaction();
            pages.remove(mPageIndex - 1);
            pages.add(mPageIndex - 1, page);
            mRealm.commitTransaction();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MakeGameMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) {
            mRealm.close();
        }
    }
}