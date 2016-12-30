package ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.englishforkid_3.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import broadcast.NetworkReceiver;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import data.local.SQLiteCommon;
import data.model.DataModel;
import ui.adapter.DataModelAdapter;
import util.Constant;
import util.JsoupParserHtml;
import util.Util;

/**
 * Created by tuanbg on 12/21/2016.
 */
public class DisplayVideoActivity extends AppCompatActivity implements SurfaceHolder.Callback,
    DataModelAdapter.OnItemClick, NetworkReceiver.NetworkReceiverListener {
    private static final String EXTRA_KEY_POSITION = "key_position";
    private List<DataModel> mListDataModel = new ArrayList<>();
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.linear_play_option)
    LinearLayout mLinearOption;
    @BindView(R.id.surfaceview)
    SurfaceView mSurfaceView;
    @BindView(R.id.seekbar_time)
    SeekBar mSeekBar;
    @BindView(R.id.image_play)
    ImageView mImagePlayVideo;
    @BindView(R.id.image_fullscreen)
    ImageView mImageFullScreen;
    @BindView(R.id.text_duration_time)
    TextView mTextDurationVideo;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.image_pause_lage)
    ImageView mImagePauseLage;
    @BindView(R.id.text_curation_time)
    TextView mTextCurentTime;
    @BindView(R.id.rcl_random_video)
    RecyclerView mRecyclerRandomVideo;
    private ProgressDialog mProgressDialog;
    private boolean mIsPause;
    private Runnable mRunnable;
    private Handler mHandler;
    private MediaPlayer mMediaPlayer;
    private String mVideoUrl;
    private int mTypeWatch = SQLiteCommon.TYPE_TABLE_SONGS;
    private SQLiteCommon mSqLiteCommon;
    private DataModelAdapter mAdapterRandomVideo;
    private DataModel mDataModel;
    private int mPosition;
    private Snackbar mSnackbar;

    public static Intent getProfileIntent(Context context, DataModel dataModel, int type) {
        Intent intent = new Intent(context, DisplayVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.BUNDLE_DATA_MODEL, dataModel);
        bundle.putInt(Constant.BUNDLE_TYPE_WATCH, type);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_video);
        ButterKnife.bind(this);
        mHandler = new Handler();
        mSqLiteCommon = new SQLiteCommon(this);
        getVideoUrl();
        initView();
        if (savedInstanceState != null) disPlayVideo(mVideoUrl);
        randomDataModel(Constant.NUMBER_RANDOM);
    }

    private void disPlayVideo(String linkVideo) {
        if (linkVideo == null) return;
        setVideoUri(linkVideo);
        playVideo(linkVideo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void initView() {
        setUpActionbar();
        mMediaPlayer = new MediaPlayer();
        showProgressDialog();
        mSeekBar.setBackgroundColor(Color.CYAN);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) mMediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mLinearOption.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mLinearOption.setVisibility(View.VISIBLE);
            }
        });
        mRecyclerRandomVideo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapterRandomVideo =
            new DataModelAdapter(getApplicationContext(), mListDataModel, mTypeWatch,
                R.layout.item_data_model_linear);
        mRecyclerRandomVideo
            .setAdapter(mAdapterRandomVideo);
        mAdapterRandomVideo.setOnClickItem(this);
        NetworkReceiver.setNetworkReceiver(this);
    }

    @OnTouch({R.id.seekbar_time, R.id.surfaceview})
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.surfaceview:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mLinearOption.setVisibility(View.VISIBLE);
                    mImagePauseLage.setVisibility(View.VISIBLE);
                    hideDisplayLayoutControl();
                    hidePauseCenterVideo();
                    return false;
                }
            case R.id.seekbar_time:
                mLinearOption.setVisibility(View.VISIBLE);
                return false;
            default:
                break;
        }
        return false;
    }

    private void hidePauseCenterVideo() {
        mImagePauseLage.postDelayed(new Runnable() {
            public void run() {
                mImagePauseLage.setVisibility(View.INVISIBLE);
            }
        }, Constant.TIME_DELAY_PAUSE);
    }

    private void hideDisplayLayoutControl() {
        mLinearOption.postDelayed(new Runnable() {
            public void run() {
                mLinearOption.setVisibility(View.INVISIBLE);
            }
        }, Constant.TIME_DELAY_CONTROL);
    }

    private void getVideoUrl() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDataModel = (DataModel) bundle.getSerializable(Constant.BUNDLE_DATA_MODEL);
            if (mDataModel != null) mVideoUrl = mDataModel.getUrlMp4();
        }
    }

    private void setUpActionbar() {
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null) {
            setTitle(mDataModel.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.msg_loading));
            mProgressDialog.setCancelable(false);
        }
        if (!mProgressDialog.isShowing()) mProgressDialog.show();
    }

    private void setTtDurationTime() {
        if (mMediaPlayer != null) {
            int duration = mMediaPlayer.getDuration() / 1000;
            mTextDurationVideo.setText(Util.convertToTime(duration));
        }
    }

    private void setVideoUri(String videoUrl) {
        try {
            Uri video = Uri.parse(videoUrl);
            mMediaPlayer.setDataSource(this, video);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playVideo(final String videoUrl) {
        if (videoUrl != null) {
            SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
            surfaceHolder.addCallback(this);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    hideDialog();
                    mMediaPlayer.start();
                    hidePauseCenterVideo();
                    hideDisplayLayoutControl();
                    setTtDurationTime();
                    mSeekBar.setProgress(0);
                    mSeekBar.setMax(mMediaPlayer.getDuration());
                    if (mMediaPlayer.isPlaying()) runTimeSeekbar();
                }
            });
            mMediaPlayer
                .setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                        mSeekBar.setSecondaryProgress(i * mMediaPlayer.getDuration() / 100);
                    }
                });
            mMediaPlayer.prepareAsync();
        }
    }

    public void runTimeSeekbar() {
        if (mMediaPlayer == null) return;
        int pos = mMediaPlayer.getCurrentPosition();
        mSeekBar.setProgress(pos);
        mTextCurentTime.setText(String.valueOf(Util.convertToTime(pos / 1000) + " / "));
        if (mMediaPlayer.isPlaying()) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    runTimeSeekbar();
                }
            };
            mHandler.postDelayed(mRunnable, Constant.TIME_DELAY_SEEKBAR);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isFullScreen =
            getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        configSizeScreenVideo(isFullScreen);
    }

    private void configSizeScreenVideo(boolean isFullScreen) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = isFullScreen ? metrics.heightPixels : getResources().getDimensionPixelSize(R
            .dimen.dp_300);
        params.leftMargin = 0;
        mSurfaceView.setLayoutParams(params);
        mToolBar.setVisibility(isFullScreen ? View.GONE : View.VISIBLE);
        mRecyclerRandomVideo.setVisibility(isFullScreen ? View.GONE : View.VISIBLE);
    }

    private void savePosition() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                .putInt(Constant.PRE_POSITION_VIDEO, mMediaPlayer.getCurrentPosition())
                .putInt(Constant.PRE_KEY_ID, mDataModel.getId())
                .apply();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_KEY_POSITION, mMediaPlayer.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            final int pos = savedInstanceState.getInt(EXTRA_KEY_POSITION);
            mMediaPlayer.start();
            mMediaPlayer.seekTo(pos);
        }
    }

    private void restoreLinkUrl() {
        mVideoUrl = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
            .getString(Constant.PRE_CURENT_VIDEO_LINK, "");
        disPlayVideo(mVideoUrl);
    }

    private void restorePosition() {
        mPosition = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
            .getInt(Constant.PRE_POSITION_VIDEO, 0);
        mMediaPlayer.start();
        mMediaPlayer.reset();
        if (mVideoUrl == null) return;
        disPlayVideo(mVideoUrl);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.seekTo(mPosition);
            }
        });
    }

    @OnClick({R.id.image_play, R.id.image_fullscreen, R.id.image_pause_lage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_play:
                int resource = mIsPause ? R.drawable.ic_play_video : R.drawable.ic_pause_video;
                setImagePlayPause(R.drawable.ic_play, R.drawable.ic_pause, mImagePauseLage);
                toggleMedia(resource, mImagePlayVideo);
                break;
            case R.id.image_fullscreen:
                setFullScreen();
                break;
            case R.id.image_pause_lage:
                int res = mIsPause ? R.drawable.ic_play : R.drawable.ic_pause;
                setImagePlayPause(R.drawable.ic_play_video, R.drawable.ic_pause_video,
                    mImagePlayVideo);
                toggleMedia(res, mImagePauseLage);
                break;
            default:
                break;
        }
    }

    private void setImagePlayPause(int play, int pause, ImageView image) {
        int resImage = mIsPause ? play : pause;
        image.setImageResource(resImage);
    }

    private void toggleMedia(int resource, ImageView image) {
        image.setImageResource(resource);
        if (mIsPause) {
            mMediaPlayer.start();
            runTimeSeekbar();
        } else mMediaPlayer.pause();
        mIsPause = !mIsPause;
    }

    public void setFullScreen() {
        int typeScreen = getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ?
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        setRequestedOrientation(typeScreen);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.pause();
        mImagePauseLage.setImageResource(R.drawable.ic_pause);
        mImagePlayVideo.setImageResource(R.drawable.ic_pause_video);
        savePosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer == null) return;
        mMediaPlayer.pause();
        int idModel = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
            .getInt(Constant.PRE_KEY_ID, 0);
        restoreLinkUrl();
        if (mDataModel.getId() == idModel) restorePosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mMediaPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    private void randomDataModel(int numberRandom) {
        List<DataModel> dataModels =
            mSqLiteCommon.getDataModelRandom(mTypeWatch, mDataModel.getId(), numberRandom);
        mListDataModel.addAll(dataModels);
        mAdapterRandomVideo.notifyDataSetChanged();
    }

    @Override
    public void onClickItem(DataModel dataModel, int type) {
        mMediaPlayer.pause();
        if (dataModel == null) return;
        if (dataModel.getUrlMp4() == null) new JsoupAsyncUrlMp4().execute(dataModel);
        else {
            mMediaPlayer.reset();
            disPlayVideo(dataModel.getUrlMp4());
            saveLinkToSharef(dataModel);
        }
        mToolBar.setTitle(dataModel.getName());
        mListDataModel.remove(dataModel);
        randomDataModel(1);
    }

    private void hideDialog() {
        if (!isFinishing() && mProgressDialog != null) mProgressDialog.dismiss();
    }

    private void saveLinkToSharef(DataModel dataModel) {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
            .putString(Constant.PRE_CURENT_VIDEO_LINK, dataModel.getUrlMp4())
            .apply();
    }

    @Override
    public void onNetworkConnectChange(boolean isConnect) {
        if (!isConnect) {
            if (mSnackbar == null) {
                mSnackbar =
                    Snackbar.make(mLinearLayout, R.string.error_check_network,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok", null);
                View view = mSnackbar.getView();
                TextView textSnackbar =
                    (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                textSnackbar.setGravity(Gravity.CENTER);
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.color_grey_300));
            }
            mSnackbar.show();
        } else {
            if (mSnackbar != null && mSnackbar.isShown()) {
                mSnackbar.dismiss();
            }
        }
    }

    private class JsoupAsyncUrlMp4 extends AsyncTask<DataModel, Void, DataModel> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected DataModel doInBackground(DataModel... dataModels) {
            DataModel dataModel = dataModels[0];
            try {
                String urlMp4 = JsoupParserHtml.parseUrlVideo(dataModel.getPathRender());
                dataModel.setUrlMp4(getString(R.string.url_video, urlMp4));
                mSqLiteCommon.updateDataModel(dataModel, mTypeWatch);
                return dataModel;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DataModel dataModel) {
            super.onPostExecute(dataModel);
            if (dataModel != null) {
                mDataModel = dataModel;
                mMediaPlayer.reset();
                disPlayVideo(dataModel.getUrlMp4());
                mImagePlayVideo.setImageResource(R.drawable.ic_play_video);
                mImagePauseLage.setImageResource(R.drawable.ic_play);
                saveLinkToSharef(dataModel);
            } else {
                Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.data_updating),
                    Toast.LENGTH_LONG).show();
            }
        }
    }
}
