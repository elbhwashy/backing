package com.backing.backingapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import com.backing.backingapp.R;
import com.backing.backingapp.data.dto.StepDto;
import com.backing.backingapp.utils.ExoEventListener;
import com.backing.backingapp.utils.Playable;
import com.squareup.picasso.Picasso;

public class StepFragment extends Fragment {

    public static final String STEP_KEY = "STEP";
    public static final String POSITION_PLAYER_KEY = "POSITION_PLAYER";
    public static final String PLAY_KEY = "PLAY";
    private StepDto step;
    private SimpleExoPlayerView simpleExoPlayerView;
    private TextView description;
    private ImageView imageView;
    private SimpleExoPlayer simpleExoPlayer;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private long position = 0;

    public StepFragment() {
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    private void initPlayer(Context context) {
        if (simpleExoPlayer == null) {
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
            simpleExoPlayer.addListener(new ExoEventListener(stateBuilder, simpleExoPlayer, context));
        }
    }

    public void setStep(StepDto step) {
        this.step = step;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(STEP_KEY, step);
        outState.putLong(POSITION_PLAYER_KEY, simpleExoPlayer.getCurrentPosition());
        outState.putBoolean(PLAY_KEY, simpleExoPlayer.getPlayWhenReady());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mediaSession = ((Playable) context).getMediaSession();
            mediaSession.setCallback(new MySessionCallback());
            stateBuilder = ((Playable) context).getStatePlayback();
            initPlayer(context);
        } catch (ClassCastException e) {
            throw new ClassCastException("need to implement Playable");
        }
    }

    private void updateView() {
        if (step != null && getContext() != null && step.getVideoURL() != null && !step.getVideoURL().isEmpty()) {
            String backingApp = Util.getUserAgent(getContext(), "Backing");
            DefaultDataSourceFactory factory = new DefaultDataSourceFactory(getContext(), backingApp, new DefaultBandwidthMeter());
            MediaSource mediaSource = new ExtractorMediaSource.Factory(factory).createMediaSource(Uri.parse(step.getVideoURL()));
            simpleExoPlayer.seekTo(position);
            simpleExoPlayer.prepare(mediaSource);
        }
        if (simpleExoPlayerView != null && step != null) {
            if (!step.getVideoURL().isEmpty()) {
                simpleExoPlayerView.setVisibility(View.VISIBLE);
            } else {
                simpleExoPlayerView.setVisibility(View.GONE);
            }
        }
        if (description != null && step != null) {
            description.setText(step.getDescription());
        }
        if (imageView != null) {
            if (step != null && step.getThumbnailURL() != null &&
                    !step.getThumbnailURL().isEmpty() &&
                    !step.getThumbnailURL().contains(".mp4")) {
                imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(step.getThumbnailURL()).into(imageView);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_step, container, false);
        description = (TextView)view.findViewById(R.id.textView_step);
        simpleExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.player_exo);
        imageView = (ImageView)view.findViewById(R.id.imageView_step);
        simpleExoPlayerView.setPlayer(simpleExoPlayer);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STEP_KEY)) {
                step = ((StepDto) savedInstanceState.getSerializable(STEP_KEY));
            }
            if (savedInstanceState.containsKey(POSITION_PLAYER_KEY)) {
                position = savedInstanceState.getLong(POSITION_PLAYER_KEY);
            }
            if (savedInstanceState.containsKey(PLAY_KEY)) {
                simpleExoPlayer.setPlayWhenReady(savedInstanceState.getBoolean(PLAY_KEY));
            }
        }
        updateView();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            releasePlayer();
        }
        mediaSession.setActive(false);
        super.onDestroy();
    }

    @Override
    public void onStop() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            releasePlayer();
        }
        super.onStop();
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            simpleExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            simpleExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            simpleExoPlayer.seekTo(0);
        }
    }
}
