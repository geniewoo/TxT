package com.sungwoo.boostcamp.widgetgame.find_game;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.ImageUtility;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.DownloadGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.FullGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.Page;
import com.sungwoo.boostcamp.widgetgame.Repositories.PlayGameRepo;
import com.sungwoo.boostcamp.widgetgame.RetrofitRequests.GameInformationRetrofit;
import com.sungwoo.boostcamp.widgetgame.widget.TxTWidget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility.getServerGameImageFolderPathStringBuffer;

public class DownloadGameActivity extends AppCompatActivity {

    private static final int GET_GAME_SUCCESS = 100;
    private static final int GET_GAME_NO_RESULT = 200;
    private static final int GET_GAME_FAILED = 500;
    @BindView(R.id.download_game_image_iv)
    protected ImageView mDownloadGameImageIv;
    @BindView(R.id.download_game_title_tv)
    protected TextView mDownloadGameTitleTv;
    @BindView(R.id.download_game_nickname_tv)
    protected TextView mDownloadGameNicknameTv;
    @BindView(R.id.download_game_description_tv)
    protected TextView mDownloadGameDescriptionTv;
    @BindView(R.id.download_game_stars_tv)
    protected TextView mDownloadGameStarsTv;
    @BindView(R.id.download_game_maker_image_iv)
    protected ImageView mDownloadGameMakerImageIv;

    private String mNickname;
    private String mGameTitle;
    private Boolean mIsDownloadSuccess;
    private PlayGameRepo mPlayGameRepo;
    private List<String> mGamePageImagePaths;
    private int imagePathIndex;
    private ProgressDialog mProgressDialog;

    private Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ImageUtility.saveImageInFilesDirectory(getApplicationContext(), bitmap, getString(R.string.LOCAL_STORAGE_PLAY_GAME_DIR), mGamePageImagePaths.get(imagePathIndex));
            if (++imagePathIndex >= mGamePageImagePaths.size()) {
                downloadFinnish();
            } else {
                StringBuffer stringBuffer = getServerGameImageFolderPathStringBuffer(getApplicationContext(), mNickname, mGameTitle, mGamePageImagePaths.get(imagePathIndex));
                Picasso.with(getApplicationContext()).load(stringBuffer.toString()).resize(300, 300).centerCrop().into(mTarget);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mIsDownloadSuccess = false;
            downloadFinnish();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_game);
        ButterKnife.bind(this);

        getIntentExtrasAndSetViews(getIntent());
    }

    @OnClick(R.id.download_game_down_btn)
    public void onDownloadGameDownBtnClicked() {
        downloadGameFromServer();
    }

    private void getIntentExtrasAndSetViews(Intent intent) {
        mGameTitle = intent.getStringExtra(getString(R.string.INTENT_FIND_GAME_TITLE));
        mNickname = intent.getStringExtra(getString(R.string.INTENT_FIND_GAME_NICKNAME));
        Float stars = intent.getFloatExtra(getString(R.string.INTENT_FIND_GAME_STARS), 0.0f);
        String description = intent.getStringExtra(getString(R.string.INTENT_FIND_GAME_DESCRIPTION));
        String gameImagePath = intent.getStringExtra(getString(R.string.INTENT_FIND_GAME_IMAGEPATH));
        String makerImagePath = intent.getStringExtra(getString(R.string.INTENT_FIND_GAME_MAKER_IMAGEPATH));

        if (gameImagePath != null && !gameImagePath.equals(getString(R.string.SERVER_NO_IMAGE_FILE))) {
            StringBuffer stringBuffer = getServerGameImageFolderPathStringBuffer(getApplicationContext(), mNickname, mGameTitle, gameImagePath);
            Picasso.with(getApplicationContext()).load(stringBuffer.toString()).resize(300, 400).centerCrop().into(mDownloadGameImageIv);
        } else {
            Picasso.with(getApplicationContext()).load(R.drawable.default_user_image).resize(300, 400).centerCrop().into(mDownloadGameImageIv);
        }

        if (makerImagePath != null && !makerImagePath.equals(getString(R.string.SERVER_NO_IMAGE_FILE))) {
            Picasso.with(getApplicationContext()).load(getString(R.string.URL_PROFILE_IMAGE_SERVER_FOLDER) + makerImagePath).resize(50, 50).centerCrop().into(mDownloadGameMakerImageIv);
        } else {
            Picasso.with(getApplicationContext()).load(R.drawable.default_user_image).resize(50, 50).centerCrop().into(mDownloadGameMakerImageIv);
        }
        mDownloadGameTitleTv.setText(mGameTitle);
        mDownloadGameStarsTv.setText(String.valueOf(stars));
        mDownloadGameNicknameTv.setText(mNickname);
        mDownloadGameDescriptionTv.setText(description);
    }

    private void downloadGameFromServer() {
        if (!CommonUtility.isNetworkAvailableShowErrorMessageIfNeeded(DownloadGameActivity.this)) {
            return;
        }

        mProgressDialog = CommonUtility.showProgressDialogAndReturnInself(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GameInformationRetrofit gameInformationRetrofit = retrofit.create(GameInformationRetrofit.class);
        Call<DownloadGameRepo> gameRepoCall = gameInformationRetrofit.downloadGame(mNickname, mGameTitle);
        gameRepoCall.enqueue(new Callback<DownloadGameRepo>() {
            @Override
            public void onResponse(Call<DownloadGameRepo> call, Response<DownloadGameRepo> response) {

                if (response.body() != null){
                    switch (response.body().getCode()) {
                        case GET_GAME_SUCCESS:
                            FullGameRepo fullGameRepo = response.body().getFullGameRepo();
                            mPlayGameRepo = new PlayGameRepo(fullGameRepo, false);
                            downloadGameImages();
                            break;
                        case GET_GAME_NO_RESULT:
                            mIsDownloadSuccess = false;
                            downloadFinnish();
                            break;
                        case GET_GAME_FAILED:
                            mIsDownloadSuccess = false;
                            downloadFinnish();
                            break;
                    }
                } else {
                    mIsDownloadSuccess = false;
                    downloadFinnish();
                }
            }

            @Override
            public void onFailure(Call<DownloadGameRepo> call, Throwable t) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                CommonUtility.showNeutralDialog(DownloadGameActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_COMMON_SERVER_ERROR_CONTENT, R.string.DIALOG_CONFIRM);
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    private void downloadGameImages() {
        mIsDownloadSuccess = true;
        getDownloadImagePaths();
        downloadGameImagesRecursively();
    }

    private void getDownloadImagePaths() {
        mGamePageImagePaths = new ArrayList<>();
        String gameInfoImagePath = mPlayGameRepo
                .getFullGameRepo()
                .getGameInfo()
                .getGameImagePath();
        if (gameInfoImagePath != null && !gameInfoImagePath.equals(getString(R.string.SERVER_NO_IMAGE_FILE))) {
            mGamePageImagePaths.add(gameInfoImagePath);
        }

        for (Page page : mPlayGameRepo.getFullGameRepo().getGameInfo().getPages()) {
            String pageImagePath = page.getImagePath();
            if (pageImagePath != null && !pageImagePath.equals(getString(R.string.SERVER_NO_IMAGE_FILE))) {
                mGamePageImagePaths.add(pageImagePath);
            }
        }
    }

    private void downloadGameImagesRecursively() {

        if (mGamePageImagePaths.size() == 0) {
            downloadFinnish();
            return;
        }

        imagePathIndex = 0;

        StringBuffer stringBuffer = getServerGameImageFolderPathStringBuffer(getApplicationContext(), mNickname, mGameTitle, mGamePageImagePaths.get(imagePathIndex));
        Picasso.with(getApplicationContext()).load(stringBuffer.toString()).resize(300, 300).centerCrop().into(mTarget);
    }

    private void downloadFinnish() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        if (mIsDownloadSuccess) {
            new MaterialDialog.Builder(DownloadGameActivity.this)
                    .title(R.string.DIALOG_SUCCESS_TITLE)
                    .content(R.string.DIALOG_DOWNLOAD_GAME_SUCCESS_CONTENT)
                    .positiveText(R.string.DIALOG_POSITIVE)
                    .negativeText(R.string.DIALOG_NEGATIVE)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
                        }
                    })
                    .show();
            mPlayGameRepo.setPlayable(true);
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(PlayGameRepo.class).findAll().deleteAllFromRealm();
            realm.insert(mPlayGameRepo);
            realm.commitTransaction();
            realm.close();

            Intent intent = new Intent(TxTWidget.ACTION_WIDGET_DISPLAY_NEW_GAME);
            sendBroadcast(intent);
        } else {
            CommonUtility.showNeutralDialog(DownloadGameActivity.this, R.string.DIALOG_ERR_TITLE, R.string.DIALOG_DOWNLOAD_GAME_FAILED_CONTENT, R.string.DIALOG_CONFIRM);
        }
    }
}
