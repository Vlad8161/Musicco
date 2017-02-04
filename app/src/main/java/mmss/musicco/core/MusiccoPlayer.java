package mmss.musicco.core;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
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

    private static final int INTERNAL_STATE_IDLE = 0;
    private static final int INTERNAL_STATE_INITIALIZED = 1;
    private static final int INTERNAL_STATE_PREPARED = 2;
    private static final int INTERNAL_STATE_PREPARING = 3;
    private static final int INTERNAL_STATE_STARTED = 4;
    private static final int INTERNAL_STATE_STOPPED = 5;
    private static final int INTERNAL_STATE_PAUSED = 6;
    private static final int INTERNAL_STATE_PLAYBACK_COMPLETED = 7;
    private static final int INTERNAL_STATE_ERROR = 8;
    private static final int INTERNAL_STATE_END = 9;

    private static final String TAG = "MusiccoPlayer";
    private final List<OnStateChangedListener> mStateChangedListeners = new ArrayList<>();
    private final List<OnPosChangedListener> mPosChangedListeners = new ArrayList<>();
    private final List<OnTrackChangedListener> mTrackChangedListeners = new ArrayList<>();
    private Context mContext;
    private MediaPlayer mPlayer;
    private Track mCurrentTrack;
    private int mInternalState = INTERNAL_STATE_IDLE;
    private Handler mBackgroundHandler;
    private Handler mUiHandler;

    private Runnable mUpdater = new Runnable() {
        @Override
        public void run() {
            mUiHandler.post(() -> notifyPosChanged());
            mBackgroundHandler.postDelayed(mUpdater, 500);
        }
    };

    public MusiccoPlayer(Context context) {
        this.mContext = context;
        this.mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setLooping(false);

        HandlerThread thread = new HandlerThread("MusiccoPlayerThread");
        thread.start();
        while (thread.getLooper() == null);
        mBackgroundHandler = new Handler(thread.getLooper());
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    public void playTrack(Track track) {
        mPlayer.reset();
        setInternalState(INTERNAL_STATE_IDLE);

        if (track == null || track.url == null) {
            setCurrentTrack(null);
            return;
        }

        setCurrentTrack(track);

        try {
            mPlayer.setDataSource(track.url);
            setInternalState(INTERNAL_STATE_INITIALIZED);
            mPlayer.prepareAsync();
            setInternalState(INTERNAL_STATE_PREPARING);
        } catch (IOException e) {
            e.printStackTrace();
            setCurrentTrack(null);
        }
    }

    public void pause() {
        if (mInternalState == INTERNAL_STATE_STARTED) {
            mPlayer.pause();
            setInternalState(INTERNAL_STATE_PAUSED);
        }
    }

    public void play() {
        if (mInternalState == INTERNAL_STATE_PAUSED) {
            mPlayer.start();
            setInternalState(INTERNAL_STATE_STARTED);
        } else if (mInternalState == INTERNAL_STATE_STOPPED) {
            mPlayer.prepareAsync();
            setInternalState(INTERNAL_STATE_PREPARING);
        }
    }

    public void stop() {
        if (mInternalState == INTERNAL_STATE_STARTED ||
                mInternalState == INTERNAL_STATE_PAUSED ||
                mInternalState == INTERNAL_STATE_PREPARED ||
                mInternalState == INTERNAL_STATE_PLAYBACK_COMPLETED) {
            mPlayer.stop();
            setInternalState(INTERNAL_STATE_STOPPED);
        }
    }

    public int getState() {
        switch (mInternalState) {
            case INTERNAL_STATE_STARTED:
                return STATE_PLAYING;
            case INTERNAL_STATE_PAUSED:
                return STATE_PAUSED;
            default:
                return STATE_STOPPED;
        }
    }

    private void setInternalState(int state) {
        if (state != mInternalState) {
            int prevState = getState();
            mInternalState = state;
            int nextState = getState();
            for (OnStateChangedListener listener : mStateChangedListeners) {
                listener.onStateChangedListener(getState());
            }

            if (prevState != nextState) {
                if (nextState == STATE_PLAYING) {
                    mBackgroundHandler.postDelayed(mUpdater, 500);
                    notifyPosChanged();
                } else {
                    mBackgroundHandler.removeCallbacks(mUpdater);
                    notifyPosChanged();
                }
            }
        }

        switch (state) {
            case INTERNAL_STATE_IDLE:
                Log.d(TAG, "State : INTERNAL_STATE_IDLE");
                break;
            case INTERNAL_STATE_INITIALIZED:
                Log.d(TAG, "State : INTERNAL_STATE_INITIALIZED");
                break;
            case INTERNAL_STATE_PREPARED:
                Log.d(TAG, "State : INTERNAL_STATE_PREPARED");
                break;
            case INTERNAL_STATE_PREPARING:
                Log.d(TAG, "State : INTERNAL_STATE_PREPARING");
                break;
            case INTERNAL_STATE_STARTED:
                Log.d(TAG, "State : INTERNAL_STATE_STARTED");
                break;
            case INTERNAL_STATE_STOPPED:
                Log.d(TAG, "State : INTERNAL_STATE_STOPPED");
                break;
            case INTERNAL_STATE_PAUSED:
                Log.d(TAG, "State : INTERNAL_STATE_PAUSED");
                break;
            case INTERNAL_STATE_PLAYBACK_COMPLETED:
                Log.d(TAG, "State : INTERNAL_STATE_PLAYBACK_COMPLETED");
                break;
            case INTERNAL_STATE_ERROR:
                Log.d(TAG, "State : INTERNAL_STATE_ERROR");
                break;
            case INTERNAL_STATE_END:
                Log.d(TAG, "State : INTERNAL_STATE_END");
                break;
        }
    }

    public Track getCurrentTrack() {
        return mCurrentTrack;
    }

    private void setCurrentTrack(Track track) {
        if (track != mCurrentTrack) {
            mCurrentTrack = track;
            for (OnTrackChangedListener listener : mTrackChangedListeners) {
                listener.onTrackChangedListener(mCurrentTrack);
            }
        }
    }

    public int getDuration() {
        if (mInternalState == INTERNAL_STATE_PREPARED ||
                mInternalState == INTERNAL_STATE_STARTED ||
                mInternalState == INTERNAL_STATE_STOPPED ||
                mInternalState == INTERNAL_STATE_PAUSED ||
                mInternalState == INTERNAL_STATE_PLAYBACK_COMPLETED) {
            return mPlayer.getDuration();
        } else if (mCurrentTrack != null && mCurrentTrack.length != null) {
            return mCurrentTrack.length;
        } else {
            return 0;
        }
    }

    public int getCurrentPosition() {
        if (mInternalState == INTERNAL_STATE_PREPARED ||
                mInternalState == INTERNAL_STATE_STARTED ||
                mInternalState == INTERNAL_STATE_STOPPED ||
                mInternalState == INTERNAL_STATE_PAUSED ||
                mInternalState == INTERNAL_STATE_PLAYBACK_COMPLETED) {
            return mPlayer.getCurrentPosition();
        } else {
            return 0;
        }
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

    public void notifyPosChanged() {
        int dur = getDuration();
        int pos = getCurrentPosition();
        for (OnPosChangedListener listener : mPosChangedListeners) {
            listener.onPosChangedListener(pos, dur);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setInternalState(INTERNAL_STATE_PLAYBACK_COMPLETED);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        setInternalState(INTERNAL_STATE_ERROR);
        mPlayer.reset();
        setInternalState(INTERNAL_STATE_IDLE);
        setCurrentTrack(null);
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        setInternalState(INTERNAL_STATE_PREPARED);
        mPlayer.start();
        setInternalState(INTERNAL_STATE_STARTED);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete");
    }

    public interface OnStateChangedListener {
        void onStateChangedListener(int state);
    }

    public interface OnPosChangedListener {
        void onPosChangedListener(int pos, int dur);
    }

    public interface OnTrackChangedListener {
        void onTrackChangedListener(Track track);
    }
}
