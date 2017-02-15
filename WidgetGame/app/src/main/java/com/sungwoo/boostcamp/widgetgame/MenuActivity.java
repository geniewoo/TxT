package com.sungwoo.boostcamp.widgetgame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.make_game.MakeGameMenuActivity;
import com.sungwoo.boostcamp.widgetgame.Repositories.CommonRepo;
import com.sungwoo.boostcamp.widgetgame.RetrofitRequests.UserInformRetrofit;

import java.io.File;
import java.io.IOException;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity";
    private static final int REQ_CODE_SELECT_IMAGE = 100;

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
        startSelectImageForUploadUserImage();
    }

    private void initUserInfo() {

        mMenuUserTv.setText(mUserInfo.getNickname());

        String imageUrl = mUserInfo.getImageUrl();
        if (imageUrl.equals(getString(R.string.none)) || imageUrl.equals("")) {
            Picasso.with(getApplicationContext()).load(R.drawable.default_user_image).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMenuUserIv);
        } else {
            Picasso.with(getApplicationContext()).load(getString(R.string.URL_IMAGE_SERVER_FOLDER) + imageUrl).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMenuUserIv);
        }
    }

    private void startSelectImageForUploadUserImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode){
            case REQ_CODE_SELECT_IMAGE :
                Uri imageUri = data.getData();
                Picasso.with(getApplicationContext()).load(imageUri).resize(USER_CIRCLE_IV, USER_CIRCLE_IV).centerCrop().into(mMenuUserIv);
                saveUserImageInLocalStorage(imageUri);
                uploadUserImageToServer();
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

    private void saveUserImageInLocalStorage(Uri uri) {
        Bitmap userImageBitmap = null;
        try {
            userImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (userImageBitmap != null) {
            File dir = new File(getApplicationContext().getFilesDir() + File.separator + getString(R.string.LOCAL_STORAGE_USER_DIR));
            Log.d(TAG, getApplicationContext().getFilesDir() + File.separator + getString(R.string.LOCAL_STORAGE_USER_DIR));
            CommonUtility.saveBitmapToFile(dir, getString(R.string.LOCAL_USER_IMAGE_FILE_NAME), userImageBitmap, Bitmap.CompressFormat.PNG, CommonUtility.SAVE_BITMAP_TO_FILE_QUALITY);
        }
    }

    private void uploadUserImageToServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        File file = new File(getApplicationContext().getFilesDir() + File.separator + getString(R.string.LOCAL_STORAGE_USER_DIR) + File.separator + getString(R.string.LOCAL_USER_IMAGE_FILE_NAME));
        if (!file.canRead()) {
            Log.e(TAG, getString(R.string.ERROR_IMAGE_IS_NOT_EXISTS));
            return;
        }
        RequestBody fileBody = RequestBody.create(MediaType.parse(getString(R.string.RETROFIT_FILE_FORMAT_IMAGE)), file);
        RequestBody fileNameBody = RequestBody.create(MediaType.parse(getString(R.string.RETROFIT_FILE_FORMAT_STRING)), mUserInfo.getNickname() + getString(R.string.FILE_EXPANDER_PNG));

        UserInformRetrofit userInformRetrofit = retrofit.create(UserInformRetrofit.class);
        Call<CommonRepo.ResultCodeRepo> codeRepoCall = userInformRetrofit.uploadUserImage(fileBody, fileNameBody);
        codeRepoCall.enqueue(new Callback<CommonRepo.ResultCodeRepo>() {
            @Override
            public void onResponse(Call<CommonRepo.ResultCodeRepo> call, Response<CommonRepo.ResultCodeRepo> response) {
                CommonRepo.ResultCodeRepo resultCodeRepo = response.body();
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
