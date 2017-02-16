package com.sungwoo.boostcamp.widgetgame.make_game;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.R;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility.REQ_CODE_SELECT_IMAGE;

public class MakeGamePageActivity extends AppCompatActivity {
    @BindView(R.id.make_page_image_iv)
    protected ImageView mMakePageImageIv;
    @BindView(R.id.make_page_image_tv)
    protected TextView mMakePageImageTv;
    @BindView(R.id.make_page_description_et)
    protected EditText mMakePageDescriptionEt;
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
    @BindView(R.id.make_vibrate_sp)
    protected Spinner mMakeVibrateSp;
    @BindViews({R.id.make_choice_cb1, R.id.make_choice_cb2, R.id.make_choice_cb3, R.id.make_choice_cb4})
    protected List<CheckBox> mMakeChoiceCbs;
    @BindViews({R.id.make_choice_et1, R.id.make_choice_et2, R.id.make_choice_et3, R.id.make_choice_et4})
    protected List<EditText> mMakeChoiceEts;
    @BindDimen(R.dimen.user_circle_iv)
    protected int USER_CIRCLE_IV;

    Uri mGamePageImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game_page);

        ButterKnife.bind(this);

        setViewValues();
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

    public void setViewValues() {

    }
}
