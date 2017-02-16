package com.sungwoo.boostcamp.widgetgame.make_game;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.Page;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility.REQ_CODE_SELECT_IMAGE;

public class MakeGamePageActivity extends AppCompatActivity {
    private static final String TAG = MakeGamePageActivity.class.getSimpleName();

    @BindView(R.id.make_page_selections_lo)
    protected LinearLayout makePageSelectionsLo;
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
    @BindView(R.id.make_page_cancel_btn)
    protected Button mMakePageCancelBtn;
    @BindView(R.id.make_index_sp)
    protected Spinner mMakeIndexSp;
    @BindView(R.id.make_page_sp)
    protected Spinner mMakePageSp;
    @BindView(R.id.make_sound_sp)
    protected Spinner mMakeSoundSp;
    @BindView(R.id.make_vibrate_cb)
    protected CheckBox mMakeVibrateCb;
    @BindViews({R.id.make_choice_cb1, R.id.make_choice_cb2, R.id.make_choice_cb3, R.id.make_choice_cb4})
    protected List<CheckBox> mMakeChoiceCbs;
    @BindViews({R.id.make_choice_et1, R.id.make_choice_et2, R.id.make_choice_et3, R.id.make_choice_et4})
    protected List<EditText> mMakeChoiceEts;
    @BindDimen(R.dimen.user_circle_iv)
    protected int USER_CIRCLE_IV;
    private int mPageIndex;
    Uri mGamePageImageUri = null;
    private Boolean mIsNewPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game_page);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mPageIndex = intent.getIntExtra(getString(R.string.INTENT_MAKE_GAME_PAGE_INDEX), -1);
        if (mPageIndex == -1) {
            Log.e(TAG, "index error");
            finish();
        }
        String[] indexStringArr = getIndexStringArr();
        setIndexView(indexStringArr);
        if (intent.getBooleanExtra(getString(R.string.INTENT_MAKE_NEW_GAME_PAGE), false)) {
            mIsNewPage = true;
        } else {
            mIsNewPage = false;
            setViewValues();
        }
    }

    @OnClick(R.id.make_page_confirm_btn)
    public void onMakePageConfirmBtn(){
        if (mGamePageImageUri != null) {
            ImageUtility.saveImageInFilesDirectory(this, mGamePageImageUri, getString(R.string.LOCAL_STORAGE_MAKE_GAME_DIR), getString(R.string.LOCAL_MAKE_GAME_PAGE_IMAGE_FILE_NAME) + mPageIndex + getString(R.string.FILE_EXPANDER_PNG));
        }

        countUpPreferenceMaxIndex();

        if (!checkValuesAreValidateAndShowMessage()) {
            return;
        }

        insertThisPageInLocalDataBase();

        Intent intent = new Intent(this, MakeGamePageActivity.class);
        intent.putExtra(getString(R.string.INTENT_MAKE_NEW_GAME_PAGE), true);
        intent.putExtra(getString(R.string.INTENT_MAKE_GAME_PAGE_INDEX), mPageIndex + 1);
        intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
                Picasso.with(getApplicationContext()).load(mGamePageImageUri).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMakePageImageIv);
                mMakePageImageTv.setVisibility(View.GONE);
                break;
        }
    }

    private void setViewValues() {

    }

    private void setIndexView(String[] indexStringArr) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.layout_string_spinner, indexStringArr);
        mMakeIndexSp.setAdapter(arrayAdapter);
        mMakeIndexSp.setSelection(mPageIndex - 1);
    }

    private String[] getIndexStringArr() {
        String[] indexStringArr = new String[mPageIndex];
        for (int i = 1 ; i <= mPageIndex ; i ++) {
            indexStringArr[i - 1] = String.valueOf(i);
        }
        return indexStringArr;
    }

    private void countUpPreferenceMaxIndex(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_MAKE_GAME), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.PREF_MAKE_GAME_MAX_INDEX), mPageIndex + 1);
        editor.apply();
    }
    private boolean checkValuesAreValidateAndShowMessage() {
        if (!isValidateTitle()) {
            Toast.makeText(this, R.string.MAKE_GAME_PAGE_TITLE_IS_WRONG, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidateDescription()){
            Toast.makeText(this, R.string.MAKE_GAME_PAGE_DESCRIPTION_IS_WRONG, Toast.LENGTH_SHORT).show();
            return false;
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
            for (int i = 0 ; i < makePageSelectionsLo.getChildCount() ; i ++) {
                LinearLayout layout = (LinearLayout)makePageSelectionsLo.getChildAt(i);
                for (int j = 0 ; j < layout.getChildCount() ; j ++) {
                    layout.getChildAt(j).setEnabled(true);
                }
            }
        } else {
            for (int i = 0 ; i < makePageSelectionsLo.getChildCount() ; i ++) {
                LinearLayout layout = (LinearLayout)makePageSelectionsLo.getChildAt(i);
                for (int j = 0 ; j < layout.getChildCount() ; j ++) {
                    layout.getChildAt(j).setEnabled(false);
                }
            }
        }
        Log.d(TAG, mMakePageSp.getSelectedItem().toString());
    }

    private void insertThisPageInLocalDataBase() {
        String titleStr = mMakePageTitleEt.getText().toString();
        String descriptionStr = mMakePageDescriptionEt.getText().toString();
        String pageStr = mMakePageSp.getSelectedItem().toString();
        String soundStr = mMakeSoundSp.getSelectedItem().toString();
        Boolean isVibrateOn = mMakeVibrateCb.isChecked();

        //Page page = new Page(mPageIndex, titleStr, descriptionStr, pageStr, "", soundStr, isVibrateOn, )
    }
}