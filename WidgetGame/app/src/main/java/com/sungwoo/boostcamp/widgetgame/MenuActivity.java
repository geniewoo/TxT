package com.sungwoo.boostcamp.widgetgame;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;
import com.sungwoo.boostcamp.widgetgame.RetrofitRequests.UserInformationRetrofit;
import com.sungwoo.boostcamp.widgetgame.find_game.FindGameActivity;
import com.sungwoo.boostcamp.widgetgame.make_game.MakeGameMenuActivity;
import com.sungwoo.boostcamp.widgetgame.upload.Upload;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility.REQ_CODE_SELECT_IMAGE;
import static com.sungwoo.boostcamp.widgetgame.upload.Upload.USER_INFORMATION;
import static com.sungwoo.boostcamp.widgetgame.upload.Upload.uploadUserImageToServer;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = MenuActivity.class.getSimpleName();

    @BindView(R.id.menu_user_tv)
    protected TextView mMenuUserTv;
    @BindView(R.id.menu_user_iv)
    protected CircleImageView mMenuUserIv;
    @BindDimen(R.dimen.user_circle_iv)
    protected int USER_CIRCLE_IV;

    long mFirstTapped = 0;

    CommonRepo.UserRepo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ButterKnife.bind(this);
        mUserInfo = CommonUtility.getUserRepoFromPreference(getApplicationContext());
        initUserInfo();
    }

    @OnClick(R.id.menu_find_game_btn)
    public void onMenuFindGameBtnClicked() {
        Intent intent = new Intent(getApplicationContext(), FindGameActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.menu_make_game_btn)
    public void onMenuMakeGameBtnClicked() {
        Intent intent = new Intent(getApplicationContext(), MakeGameMenuActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.menu_played_games_btn)
    public void onMenuPlayedGamesBtnClicked() {

    }

    @OnClick(R.id.menu_report_btn)
    public void onMenuReportBtnClicked() {

    }

    @OnClick(R.id.menu_logout_btn)
    public void onMenuLogoutBtnClicked() {
        CommonUtility.deleteAllUserPreference(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.menu_user_iv) // 사진, 닉네임 변경하기
    public void onMenuUserIvClicked() {
        ImageUtility.startSelectImageActivity(this);
    }

    private void initUserInfo() {

        mMenuUserTv.setText(mUserInfo.getNickname());

        String imageUrl = mUserInfo.getImageUrl();

        if (imageUrl.equals(getString(R.string.SERVER_NO_IMAGE_FILE)) || imageUrl.equals("")) {
            Picasso.with(getApplicationContext()).load(R.drawable.default_user_image).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMenuUserIv);
        } else {
            Picasso.with(getApplicationContext()).load(getString(R.string.URL_PROFILE_IMAGE_SERVER_FOLDER) + imageUrl).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMenuUserIv);
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
                Uri imageUri = data.getData();
                Picasso.with(getApplicationContext()).load(imageUri).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMenuUserIv);
                ImageUtility.saveImageInFilesDirectory(this, imageUri, getString(R.string.LOCAL_STORAGE_USER_DIR), getString(R.string.LOCAL_USER_IMAGE_FILE_NAME));
                Upload.uploadUserImageToServer(getApplicationContext(), getString(R.string.LOCAL_STORAGE_USER_DIR), getString(R.string.LOCAL_USER_IMAGE_FILE_NAME), mUserInfo.getNickname() + getString(R.string.FILE_EXPANDER_PNG), USER_INFORMATION);
                updateUserImageUrlToServer();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mFirstTapped < 700) {
            super.onBackPressed();
        } else {
            mFirstTapped = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), R.string.MENU_BACK_PRESSED, Toast.LENGTH_LONG).show();
        }
    }

    public void updateUserImageUrlToServer() {
        String email = mUserInfo.getEmail();
        String imageUrl = mUserInfo.getNickname() + getString(R.string.FILE_EXPANDER_PNG);
        String password = mUserInfo.getPassword();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserInformationRetrofit userInformationRetrofit = retrofit.create(UserInformationRetrofit.class);
        Call<CommonRepo.ResultCodeRepo> codeRepoCall = userInformationRetrofit.updateUserImage(email, password, imageUrl);
        codeRepoCall.enqueue(new Callback<CommonRepo.ResultCodeRepo>() {
            @Override
            public void onResponse(Call<CommonRepo.ResultCodeRepo> call, Response<CommonRepo.ResultCodeRepo> response) {
                CommonRepo.ResultCodeRepo resultCodeRepo = response.body();
                switch (resultCodeRepo.getCode()) {
                    case Upload.UPLOAD_SUCCESS:
                        break;
                    case Upload.UPLOAD_FAIL:
                        Toast.makeText(getApplicationContext(), R.string.COMMON_SERVER_ERROR, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<CommonRepo.ResultCodeRepo> call, Throwable t) {
                CommonUtility.displayNetworkError(getApplicationContext());
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }
}
