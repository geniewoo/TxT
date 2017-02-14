package com.sungwoo.boostcamp.widgetgame.make_game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sungwoo.boostcamp.widgetgame.R;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeGamePageActivity extends AppCompatActivity {
    @BindView(R.id.make_page_image_iv)
    protected ImageView makeImageIv;
    @BindView(R.id.make_page_image_tv)
    protected TextView makeImageTv;
    @BindView(R.id.make_page_description_et)
    protected EditText makeDescriptionEt;
    @BindView(R.id.make_page_confirm_btn)
    protected Button makeConfirmBtn;
    @BindView(R.id.make_page_cancel_btn)
    protected Button makeCancelBtn;
    @BindView(R.id.make_index_sp)
    protected Spinner makeIndexSp;
    @BindView(R.id.make_page_sp)
    protected Spinner makePageSp;
    @BindView(R.id.make_sound_sp)
    protected Spinner makeSoundSp;
    @BindView(R.id.make_vibrate_sp)
    protected Spinner makeVibrateSp;
    @BindViews({R.id.make_choice_cb1, R.id.make_choice_cb2, R.id.make_choice_cb3, R.id.make_choice_cb4})
    protected List<CheckBox> makeChoiceCbs;
    @BindViews({R.id.make_choice_et1, R.id.make_choice_et2, R.id.make_choice_et3, R.id.make_choice_et4})
    protected List<EditText> makeChoiceEts;

    private static final int REQ_CODE_SELECT_IMAGE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game_page);

        ButterKnife.bind(this);
    }
    @OnClick(R.id.make_page_image_fl)
    public void onMakeImageFlClick(){
        startImageSelectForSelectPageImage();
    }

    public void startImageSelectForSelectPageImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }
}
