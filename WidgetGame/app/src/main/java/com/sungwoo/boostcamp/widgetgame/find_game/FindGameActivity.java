package com.sungwoo.boostcamp.widgetgame.find_game;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sungwoo.boostcamp.widgetgame.CommonUtility.CommonUtility;
import com.sungwoo.boostcamp.widgetgame.R;
import com.sungwoo.boostcamp.widgetgame.Repositories.FindGameRepo;
import com.sungwoo.boostcamp.widgetgame.Repositories.PlayInfo;
import com.sungwoo.boostcamp.widgetgame.RetrofitRequests.GameInformationRetrofit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindGameActivity extends AppCompatActivity {

    private static final String TAG = FindGameActivity.class.getSimpleName();
    private static final String SORT_GAME_TITLE = "gameTitle";
    public static final int GET_LIST_SUCCESS = 100;
    public static final int GET_LIST_FAIL = 200;
    private static final int getItemNum = 20;
    private int skip;
    private int itemNum;

    @BindView(R.id.find_game_rv)
    protected RecyclerView mFindGameListRv;
    private FindGameRvAdapter mFindGameRvAdapter;
    private LinearLayoutManager mLayoutManager;
    boolean isMoreItemsAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);

        ButterKnife.bind(this);

        skip = 0;
        itemNum = 0;
        isMoreItemsAvailable = true;

        mLayoutManager = new LinearLayoutManager(this);
        mFindGameListRv.setLayoutManager(mLayoutManager);
        mFindGameListRv.hasFixedSize();
        ArrayList<FindGameRepo.FindGameList> findGameLists = new ArrayList<>();
        mFindGameRvAdapter = new FindGameRvAdapter(findGameLists);
        mFindGameListRv.setAdapter(mFindGameRvAdapter);
        getGameListFromServer(skip++, getItemNum, SORT_GAME_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFindGameListRv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (isMoreItemsAvailable && mLayoutManager.findLastCompletelyVisibleItemPosition() == itemNum - 1) {
                        getGameListFromServer(skip++, getItemNum, SORT_GAME_TITLE);
                    }
                }
            });
        } else {
            mFindGameListRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (isMoreItemsAvailable && mLayoutManager.findLastCompletelyVisibleItemPosition() == itemNum - 1) {
                        getGameListFromServer(skip++, getItemNum, SORT_GAME_TITLE);
                    }
                }
            });
        }
    }

    class FindGameRvAdapter extends RecyclerView.Adapter<FindGameRvAdapter.FindGameRvViewHolder> {
        ArrayList<FindGameRepo.FindGameList> findGameLists;

        public FindGameRvAdapter(ArrayList<FindGameRepo.FindGameList> findGameLists) {
            this.findGameLists = findGameLists;
        }

        @Override
        public FindGameRvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.layout_find_game_rv_item, parent, false);
            FindGameRvViewHolder findGameRvViewHolder = new FindGameRvViewHolder(view);;
            return findGameRvViewHolder;
        }

        @Override
        public void onBindViewHolder(FindGameRvViewHolder holder, int position) {
            FindGameRvViewHolder findGameRvViewHolder = (FindGameRvViewHolder) holder;
            findGameRvViewHolder.bind(position);
        }


        @Override
        public int getItemCount() {
            return findGameLists.size();
        }

        public ArrayList<FindGameRepo.FindGameList> getFindGameLists() {
            return findGameLists;
        }

        class FindGameRvViewHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.find_game_list_image_iv)
            protected ImageView findGameListImageIv;
            @BindView(R.id.find_game_list_title_tv)
            protected TextView findGameListTitleTv;
            @BindView(R.id.find_game_list_description_tv)
            protected TextView findGameListDescriptionTv;
            @BindView(R.id.find_game_list_nickname_tv)
            protected TextView findGameListNicknameTv;
            @BindView(R.id.find_game_list_maker_iv)
            protected ImageView findGameListMakerIv;
            @BindView(R.id.find_game_list_stars_tv)
            TextView findGameListStarsTv;

            public FindGameRvViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), DownloadGameActivity.class);
                        FindGameRepo.FindGameList findGameList = findGameLists.get(getAdapterPosition());
                        intent.putExtra(getString(R.string.INTENT_FIND_GAME_TITLE), findGameList.getGameTitle());
                        intent.putExtra(getString(R.string.INTENT_FIND_GAME_DESCRIPTION), findGameList.getGameDescription());
                        intent.putExtra(getString(R.string.INTENT_FIND_GAME_NICKNAME), findGameList.getNickName());
                        intent.putExtra(getString(R.string.INTENT_FIND_GAME_STARS), findGameList.getStars());
                        intent.putExtra(getString(R.string.INTENT_FIND_GAME_IMAGEPATH), findGameList.getGameImagePath());
                        intent.putExtra(getString(R.string.INTENT_FIND_GAME_MAKER_IMAGEPATH), findGameList.getMakerImagePath());
                        startActivity(intent);
                    }
                });
            }

            public void bind(int position) {
                FindGameRepo.FindGameList findGameList = findGameLists.get(position);
                String nickname = findGameList.getNickName();
                String gameTitle = findGameList.getGameTitle();
                String gameImagePath = findGameList.getGameImagePath();
                String makerImagePath = findGameList.getMakerImagePath();
                findGameListTitleTv.setText((position + 1) + " " + gameTitle);
                findGameListDescriptionTv.setText(findGameList.getGameDescription());
                findGameListNicknameTv.setText(nickname);
                findGameListStarsTv.setText(String.valueOf(findGameList.getStars()));
                if (gameImagePath != null && !gameImagePath.equals(getString(R.string.SERVER_NO_IMAGE_FILE))){
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(getString(R.string.URL_GAME_IMAGE_SERVER_FOLDER));
                    stringBuffer.append(File.separator);
                    stringBuffer.append(nickname);
                    stringBuffer.append(File.separator);
                    stringBuffer.append(gameTitle);
                    stringBuffer.append(File.separator);
                    stringBuffer.append(gameImagePath);
                    Picasso.with(getApplicationContext()).load(stringBuffer.toString()).resize(200, 270).centerCrop().into(findGameListImageIv);
                } else {
                    Picasso.with(getApplicationContext()).load(R.drawable.default_user_image).resize(200, 270).centerCrop().into(findGameListImageIv);
                }
                if (makerImagePath != null && !makerImagePath.equals(getString(R.string.SERVER_NO_IMAGE_FILE))) {
                    Picasso.with(getApplicationContext()).load(getString(R.string.URL_PROFILE_IMAGE_SERVER_FOLDER) + makerImagePath).resize(20, 20).centerCrop().into(findGameListMakerIv);
                } else {
                    Picasso.with(getApplicationContext()).load(R.drawable.default_user_image).resize(20, 20).centerCrop().into(findGameListMakerIv);
                }
            }
        }
    }


    private void getGameListFromServer(int skip, int num, String sort) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.URL_WIDGET_GAME_SERVER))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GameInformationRetrofit gameInformationRetrofit = retrofit.create(GameInformationRetrofit.class);
        Call<FindGameRepo> uploadGameRepoCodeRepoCall = gameInformationRetrofit.getGameList(skip, num, sort);
        uploadGameRepoCodeRepoCall.enqueue(new Callback<FindGameRepo>() {
            @Override
            public void onResponse(Call<FindGameRepo> call, Response<FindGameRepo> response) {
                if (response.body() != null && response.body().getCode() == GET_LIST_SUCCESS) {
                    List<FindGameRepo.FindGameList> findGameLists = response.body().getFindGameList();
                    if (findGameLists.size() == 0) {
                        isMoreItemsAvailable = false;
                        Log.e(TAG, getString(R.string.FIND_NO_GAME));
                        return;
                    } else if (findGameLists.size() < getItemNum) {
                        isMoreItemsAvailable = false;
                    }
                    ArrayList<FindGameRepo.FindGameList> adapterList = mFindGameRvAdapter.getFindGameLists();
                    adapterList.addAll(findGameLists);
                    itemNum = findGameLists.size();
                    mFindGameRvAdapter.notifyDataSetChanged();
                } else {
                    CommonUtility.displayNetworkError(getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<FindGameRepo> call, Throwable t) {
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
