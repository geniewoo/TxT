package com.sungwoo.boostcamp.widgetgame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.LicnseAndReport.LicenseAndReport;
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

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = MenuActivity.class.getSimpleName();

    @BindView(R.id.menu_user_tv)
    protected TextView mMenuUserTv;
    @BindView(R.id.menu_user_iv)
    protected CircleImageView mMenuUserIv;
    @BindView(R.id.activity_menu_lo)
    protected LinearLayout mActivityMenuLo;
    @BindView(R.id.menu_lv)
    protected ListView mMenuLv;
    @BindDimen(R.dimen.user_circle_iv)
    protected int USER_CIRCLE_IV;

    long mFirstTapped = 0;

    CommonRepo.UserRepo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        constructMenuLv();

        if (getIntent().getAction() != null && getIntent().getAction().equals(LoginActivity.ACTION_LOGIN_SUCCESS)) {
            Snackbar.make(mActivityMenuLo, R.string.LOGIN_SUCCESS, Snackbar.LENGTH_LONG).show();
        }

        mUserInfo = CommonUtility.getUserRepoFromPreference(getApplicationContext());
        initUserInfo();
    }

    private void constructMenuLv() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.menu_lv_item, getResources().getStringArray(R.array.MENU_LV_ITEMS));
        mMenuLv.setAdapter(arrayAdapter);
        mMenuLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(getApplicationContext(), FindGameActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getApplicationContext(), MakeGameMenuActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getApplicationContext(), LicenseAndReport.class);
                        startActivity(intent);
                        break;
                    case 3:
                        CommonUtility.deleteAllUserPreference(getApplicationContext());
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
            }
        });
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
            Picasso.with(getApplicationContext()).load((getString(R.string.URL_PROFILE_IMAGE_SERVER_FOLDER) + imageUrl).replace(" ", "%20")).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMenuUserIv);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (!CommonUtility.isNetworkAvailableShowErrorMessageIfNeeded(MenuActivity.this)) {
            return;
        }
        switch (requestCode) {
            case REQ_CODE_SELECT_IMAGE:
                Uri imageUri = data.getData();
                Picasso.with(this).load(imageUri).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMenuUserIv);
                Picasso.with(this).load(imageUri).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        ImageUtility.saveImageInFilesDirectory(getApplicationContext(), bitmap, getString(R.string.LOCAL_STORAGE_USER_DIR), getString(R.string.LOCAL_USER_IMAGE_FILE_NAME));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
                Upload.uploadUserImageToServer(MenuActivity.this, getString(R.string.LOCAL_STORAGE_USER_DIR), getString(R.string.LOCAL_USER_IMAGE_FILE_NAME), mUserInfo.getNickname() + getString(R.string.FILE_EXPANDER_PNG), USER_INFORMATION);
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
            Snackbar.make(mActivityMenuLo, R.string.MENU_BACK_PRESSED, Snackbar.LENGTH_SHORT).show();
        }
    }

    public void updateUserImageUrlToServer() {
        if (!CommonUtility.isNetworkAvailableShowErrorMessageIfNeeded(MenuActivity.this)) {
            return;
        }
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
                        CommonUtility.showNeutralDialog(MenuActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_SERVER_ERROR_CONTENT, R.string.DIALOG_CONFIRM);
                        break;
                }
            }

            @Override
            public void onFailure(Call<CommonRepo.ResultCodeRepo> call, Throwable t) {
                CommonUtility.showNeutralDialog(MenuActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_COMMON_SERVER_ERROR_CONTENT, R.string.DIALOG_CONFIRM);
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }
}
