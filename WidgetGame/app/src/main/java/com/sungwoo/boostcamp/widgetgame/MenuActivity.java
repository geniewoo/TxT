package com.sungwoo.boostcamp.widgetgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity {
    @BindView(R.id.menu_user_iv)
    protected CircleImageView mMenuUserIv;
    @BindView(R.id.menu_find_game_btn)
    protected Button mMenuFindGameBtn;
    @BindView(R.id.menu_make_game_btn)
    protected Button mMenuMakeGameBtn;
    @BindView(R.id.menu_played_btn)
    protected Button mMenuPlayedBtn;
    @BindView(R.id.menu_report_btn)
    protected Button menuReportBtn;
    @BindView(R.id.menu_logout_btn)
    protected Button menuLogoutBtn;
    @BindView(R.id.menu_user_tv)
    protected TextView mMenuUserTv;

    @BindDimen(R.dimen.user_circle_iv)
    protected int USER_CIRCLE_IV;

    long mDoubleTouch = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ButterKnife.bind(this);

        setUserInfo();

        mMenuUserIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploaduserImage();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mDoubleTouch < 700) {
            super.onBackPressed();
        } else {
            mDoubleTouch = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), getString(R.string.MENU_BACK_PRESSED), Toast.LENGTH_LONG).show();
        }
    }

    private void setUserInfo(){
        CommonRepo.UserRepo userRepo = CommonUtility.getUserRepoFromPreference(getApplicationContext());

        mMenuUserTv.setText(userRepo.getNickname());

        String imageUrl = userRepo.getImageUrl();
        if(imageUrl == getString(R.string.none) || imageUrl == ""){
            Picasso.with(getApplicationContext()).load(R.drawable.default_user_image).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMenuUserIv);
        }else{
            Picasso.with(getApplicationContext()).load(getString(R.string.URL_IMAGE_SERVER_FOLDER) + imageUrl).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMenuUserIv);
        }
    }

    private void uploaduserImage(){

    }
}
