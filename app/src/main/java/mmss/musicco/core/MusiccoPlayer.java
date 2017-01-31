package mmss.musicco.core;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mmss.musicco.dataobjects.Track;

/**
 * Created by User on 27.01.2017.
 */

public class MusiccoPlayer implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {
    public static final int STATE_STOPPED = 0;
    public static final int STATE_PAUSED = 1;
    public static final int STATE_PLAYING = 2;
    private static final String TAG = "MusiccoPlayer";
    private Context mContext;
    private MediaPlayer mPlayer;
    private Track mCurrentTrack;
    private int mState = STATE_STOPPED;
    private final List<OnStateChangedListener> mStateChangedListeners = new ArrayList<>();
    private final List<OnPosChangedListener> mPosChangedListeners = new ArrayList<>();
    private final List<OnTrackChangedListener> mTrackChangedListeners = new ArrayList<>();

    public MusiccoPlayer(Context context) {
        this.mContext = context;
        this.mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    public void playTrack(Track track) {
        Log.d(TAG, "playTrack");
        mPlayer.reset();
        mState = STATE_STOPPED;

        if (track == null || track.url == null) {
            mCurrentTrack = null;
            return;
        }

        mCurrentTrack = track;
        notifyTrackChanged();

        try {
            mPlayer.setDataSource(track.url);
            Log.d(TAG, "playTrack set");
            mPlayer.prepareAsync();
            Log.d(TAG, "playTrack prepareAsync");
        } catch (IOException e) {
            e.printStackTrace();
            mCurrentTrack = null;
        }
    }

    public void pause() {
        Log.d(TAG, "pause");

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mState = STATE_PAUSED;
        }
    }

    public void resume() {
        Log.d(TAG, "resume");
        mPlayer.start();
        mState = STATE_PLAYING;
    }

    public void stop() {
        Log.d(TAG, "stop");
        mPlayer.stop();
        mState = STATE_STOPPED;
    }

    public int getState() {
        return mState;
    }

    public Track getCurrentTrack() {
        return mCurrentTrack;
    }

    public void addOnStateChangedListener(OnStateChangedListener listener) {
        if (!mStateChangedListeners.contains(listener)) {
            mStateChangedListeners.add(listener);
        }
    }

    public void removeOnStateChangedListener(OnStateChangedListener listener) {
        if (mStateChangedListeners.contains(listener)) {
            mStateChangedListeners.remove(listener);
        }
    }

    public void addOnPosChangedListener(OnPosChangedListener listener) {
        if (!mPosChangedListeners.contains(listener)) {
            mPosChangedListeners.add(listener);
        }
    }

    public void removeOnPosChangedListener(OnPosChangedListener listener) {
        if (mPosChangedListeners.contains(listener)) {
            mPosChangedListeners.remove(listener);
        }
    }

    public void addOnTrackChangedListener(OnTrackChangedListener listener) {
        if (!mTrackChangedListeners.contains(listener)) {
            mTrackChangedListeners.add(listener);
        }
    }

    public void removeOnTrackChangedListener(OnTrackChangedListener listener) {
        if (mTrackChangedListeners.contains(listener)) {
            mTrackChangedListeners.remove(listener);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError");
        mPlayer.reset();
        mCurrentTrack = null;
        mState = STATE_STOPPED;
        notifyStateChanged();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared");
        mPlayer.start();
        mState = STATE_PLAYING;
        notifyStateChanged();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete");
    }

    private void notifyStateChanged() {
        for (OnStateChangedListener listener : mStateChangedListeners) {
            listener.onStateChangedListener(mState);
        }
    }

    private void notifyTrackChanged() {
        for (OnTrackChangedListener listener : mTrackChangedListeners) {
            listener.onTrackChangedListener(mCurrentTrack);
        }
    }

    public interface OnStateChangedListener {
        void onStateChangedListener(int state);
    }

    public interface OnPosChangedListener {
        void onPosChandedListener(float pos);
    }

    public interface OnTrackChangedListener {
        void onTrackChangedListener(Track track);
    }
}
