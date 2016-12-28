package ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
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

import com.framgia.englishforkid_3.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import data.model.DataModel;
import util.Constant;
import util.Util;

/**
 * Created by tuanbg on 12/21/2016.
 */
public class DisplayVideoActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String EXTRA_KEY_POSITION = "key_position";
    private static final String EXTRA_CHECK_LAUNCHED = "check_launched";
    private static ProgressDialog mProgressDialog;
    private final String TAG = DisplayVideoActivity.class.getSimpleName();
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
    private boolean mIsPause;
    private Runnable mRunnable;
    private Handler mHandler;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;

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
        initView();
        playVideo(getVideoUrl());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_disaplay_videlo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void initView() {
        showProgressDialog();
        setUpActionbar();
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               /* if (fromUser) mMediaPlayer.seekTo(progress);*/
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
    }

    @OnTouch({R.id.seekbar_time, R.id.surfaceview})
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.surfaceview:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mLinearOption.setVisibility(View.VISIBLE);
                    mImagePauseLage.setVisibility(View.VISIBLE);
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideDisplayLayoutControl();
                    hidePauseCenterVideo();
                    return true;
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

    private String getVideoUrl() {
        String videoUrl = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            DataModel mDataModel = (DataModel) bundle.getSerializable(Constant.BUNDLE_DATA_MODEL);
            if (mDataModel != null) videoUrl = mDataModel.getUrlMp4();
            return videoUrl;
        }
        return "";
    }

    private void setUpActionbar() {
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            //Hiện nút back
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
        int duration = mMediaPlayer.getDuration() / 1000;
        mTextDurationVideo.setText(Util.convertToTime(duration));
    }

    private void playVideo(final String videoUrl) {
        if (videoUrl != null) {
            Uri video = Uri.parse(videoUrl);
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(this);
            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(this, video);
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mProgressDialog.dismiss();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void runTimeSeekbar() {
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
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            configFullScreenVideo();
        else {
            mToolBar.setVisibility(View.VISIBLE);
            mRecyclerRandomVideo.setVisibility(View.VISIBLE);
        }
    }

    private void configFullScreenVideo() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        FrameLayout.LayoutParams params =
            (FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels /*- heightLayoutOption*/;
        params.leftMargin = 0;
        mSurfaceView.setLayoutParams(params);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        mRecyclerRandomVideo.setVisibility(View.INVISIBLE);
    }

    private void savePosition() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
            .edit()
            .putInt(Constant.PRE_POSITION_VIDEO, mMediaPlayer.getCurrentPosition())
            .apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_KEY_POSITION, mMediaPlayer.getCurrentPosition());
        outState.putBoolean(EXTRA_CHECK_LAUNCHED, true);
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

    private void restorePosition() {
        final int pos = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
            .getInt(Constant.PRE_POSITION_VIDEO, 0);
        // check null
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.seekTo(pos);
            }
        });
    }

    @OnClick({R.id.image_play, R.id.image_fullscreen, R.id
        .image_pause_lage})
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
        savePosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restorePosition();
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
}
