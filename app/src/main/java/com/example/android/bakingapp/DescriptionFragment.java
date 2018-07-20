package com.example.android.bakingapp;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DescriptionFragment extends Fragment implements ExoPlayer.EventListener {

    private static final String SHORT_TAG = "short_description";
    private static final String LONG_TAG = "long_description";
    private static final String URL_TAG = "URL_description";
    private static final String TURL_TAG = "tURL_description";
    private static final String POSITION = "position";

    @Nullable
    @BindView(R.id.playerView)
    SimpleExoPlayerView mExoPlayerView;
    @Nullable
    @BindView(R.id.tv_current_step_description)
    TextView currentStepDescription;
    @Nullable
    @BindView(R.id.description_layout_container)
    LinearLayout layout;
    @Nullable
    @BindView(R.id.thumbnail_image)
    ImageView imageView;

    private static final String IS_FULLSCREEN = "is_fullscreen";
    private static final String EXO_CURRENT_POSITION = "current_position";
    private static final String TWO_PANE = "twoPane";

    private ArrayList<String> shortDescription;
    private ArrayList<String> longDescription;
    private ArrayList<String> url;
    private ArrayList<String> tUrl;
    private String videoUrl;
    private String thumbnailUrl;
    private String lDescription;
    private int position;
    private long exo_current_position = 0;
    private SimpleExoPlayer exoPlayer;
    private Unbinder unbinder;
    private boolean isFullScreen = false;
    private boolean mTwoPane;
    private boolean playerStopped = false;
    private long playerStopPosition;
    private Dialog mFullScreenDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle arguments = getArguments();
        shortDescription = arguments.getStringArrayList(SHORT_TAG);
        longDescription = arguments.getStringArrayList(LONG_TAG);
        url = arguments.getStringArrayList(URL_TAG);
        tUrl = arguments.getStringArrayList(TURL_TAG);
        position = arguments.getInt(POSITION);
        mTwoPane = arguments.getBoolean(TWO_PANE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            isFullScreen = false;
        } else {
            isFullScreen = savedInstanceState.getBoolean(IS_FULLSCREEN);
            exo_current_position = savedInstanceState.getLong(EXO_CURRENT_POSITION);
        }
        if (!mTwoPane)
            checkFullScreen();

        videoUrl = url.get(position);
        lDescription = longDescription.get(position);
        thumbnailUrl = tUrl.get(position);

        currentStepDescription.setText(lDescription);
        initFullscreenDialog();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mExoPlayerView.getLayoutParams();

        if (mTwoPane) {
            params.width = params.MATCH_PARENT;
            if (getActivity().getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                params.height = 600;
            else
                params.height = 900;
        } else {
            configureScreen(getActivity().getResources().getConfiguration());
        }

        // Check to see if video url is not empty, and hide player if empty
        if (!videoUrl.equals("")) {
            initializePlayer(Uri.parse(videoUrl));
        } else {
            view.findViewById(R.id.fragment_media_frame).setVisibility(View.GONE);
            if (!thumbnailUrl.equals("")) {
                imageView.setVisibility(View.VISIBLE);
                Picasso.with(getContext())
                        .load(thumbnailUrl)
                        .error(R.drawable.baking)
                        .into(imageView);
            }
        }

        view.findViewById(R.id.exo_fullscreen_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mTwoPane) {
                    if (!isFullScreen) {
                        isFullScreen = true;
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mExoPlayerView.getLayoutParams();
                        params.width = params.MATCH_PARENT;
                        params.height = params.MATCH_PARENT;
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                        mExoPlayerView.setLayoutParams(params);
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        view.findViewById(R.id.exo_fullscreen_icon).setBackgroundResource(R.drawable.ic_fullscreen_skrink);
                    } else {
                        isFullScreen = false;
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mExoPlayerView.getLayoutParams();
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        params.width = params.MATCH_PARENT;
                        params.height = 900;
                        mExoPlayerView.setLayoutParams(params);
                        if (getActivity().getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                        view.findViewById(R.id.exo_fullscreen_icon).setBackgroundResource(R.drawable.ic_fullscreen_expand);
                    }
                } else {
                    if (!isFullScreen) {
                        openFullscreenDialog();
                    } else {
                        closeFullscreenDialog();
                    }
                }
            }
        });


        return view;
    }

    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (isFullScreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {
        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mExoPlayerView.findViewById(R.id.exo_fullscreen_icon).setBackgroundResource(R.drawable.ic_fullscreen_skrink);
        isFullScreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {
        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        FrameLayout frameLayout = getView().findViewById(R.id.fragment_media_frame);
        frameLayout.addView(mExoPlayerView);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mExoPlayerView.getLayoutParams();
        params.width = params.MATCH_PARENT;
        if (getActivity().getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            params.height = 600;
        else
            params.height = 900;
        isFullScreen = false;
        mFullScreenDialog.dismiss();
        mExoPlayerView.findViewById(R.id.exo_fullscreen_icon).setBackgroundResource(R.drawable.ic_fullscreen_expand);
    }

    private void initializePlayer(Uri parse) {
        if (exoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mExoPlayerView.setPlayer(exoPlayer);
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(parse, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            exoPlayer.prepare(mediaSource);
            if (exo_current_position != 0 && !playerStopped) {
                exoPlayer.seekTo(exo_current_position);
            } else {
                exoPlayer.seekTo(playerStopPosition);
            }
        }
    }

    private void checkFullScreen() {
        Configuration newConfig = new Configuration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isFullScreen = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isFullScreen = false;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    public static DescriptionFragment setValues(ArrayList<String> shortDescriptions, ArrayList<String> longDescriptions,
                                                ArrayList<String> URLs, ArrayList<String> tURLs, int stepPosition, boolean mTwoPane) {
        DescriptionFragment fragment = new DescriptionFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(SHORT_TAG, shortDescriptions);
        bundle.putStringArrayList(LONG_TAG, longDescriptions);
        bundle.putStringArrayList(URL_TAG, URLs);
        bundle.putStringArrayList(TURL_TAG, tURLs);
        bundle.putInt(POSITION, stepPosition);
        bundle.putBoolean(TWO_PANE, mTwoPane);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_FULLSCREEN, isFullScreen);
        outState.putLong(EXO_CURRENT_POSITION, exoPlayer.getCurrentPosition());
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer(Uri.parse(videoUrl));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            playerStopPosition = exoPlayer.getCurrentPosition();
            playerStopped = true;
            releasePlayer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        unbinder.unbind();
    }

    public void configureScreen(Configuration newConfig) {
        // Checking the orientation of the screen
        if (!mTwoPane) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mExoPlayerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                mExoPlayerView.setLayoutParams(params);
                DescriptionActivity.hideRelativeLayout();
                isFullScreen = true;
                mExoPlayerView.findViewById(R.id.exo_fullscreen_icon).setBackgroundResource(R.drawable.ic_fullscreen_skrink);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mExoPlayerView.getLayoutParams();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                params.width = params.MATCH_PARENT;
                params.height = 900;
                mExoPlayerView.setLayoutParams(params);
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                DescriptionActivity.showRelativeLayout();
            }
        }
    }

    /**
     * Method to release exoPlayer
     */
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
