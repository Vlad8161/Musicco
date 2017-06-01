package mmss.musicco.core;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mmss.musicco.dataobjects.Track;
import rx.Observable;
import rx.subjects.BehaviorSubject;

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
    private final BehaviorSubject<Integer> mStateSubject = BehaviorSubject.create();
    private final BehaviorSubject<Integer> mPositionSubject = BehaviorSubject.create();
    private final BehaviorSubject<Integer> mDurationSubject = BehaviorSubject.create();
    private final BehaviorSubject<Track> mTrackSubject = BehaviorSubject.create();
    private MediaPlayer mPlayer;
    private Track mCurrentTrack;
    private Integer mCurrentTrackIndex;
    private List<Track> mCurrentTrackList;
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
        this.mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setLooping(false);
        mPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);

        HandlerThread thread = new HandlerThread("MusiccoPlayerThread");
        thread.start();
        while (thread.getLooper() == null) ;
        mBackgroundHandler = new Handler(thread.getLooper());
        mUiHandler = new Handler(Looper.getMainLooper());

        mStateSubject.onNext(getState());
        mPositionSubject.onNext(getCurrentPosition());
        mDurationSubject.onNext(getDuration());
        mTrackSubject.onNext(getCurrentTrack());
    }

    public Observable<Integer> getStateObservable() {
        return mStateSubject;
    }

    public Observable<Integer> getPositionObservable() {
        return mPositionSubject;
    }

    public Observable<Integer> getDurationObservable() {
        return mDurationSubject;
    }

    public Observable<Track> getTrackObservable() {
        return mTrackSubject;
    }

    public void playTrack(Integer trackIndex) {
        mPlayer.reset();
        setInternalState(INTERNAL_STATE_IDLE);

        setCurrentTrack(trackIndex);
        if (mCurrentTrack == null) {
            return;
        }

        try {
            mPlayer.setDataSource(mCurrentTrack.url);
            setInternalState(INTERNAL_STATE_INITIALIZED);
            mPlayer.prepareAsync();
            setInternalState(INTERNAL_STATE_PREPARING);
        } catch (IOException e) {
            e.printStackTrace();
            setCurrentTrack(null);
        }
    }

    public List<Track> getTracks() {
        if (mCurrentTrackList != null) {
            return new ArrayList<>(mCurrentTrackList);
        } else {
            return null;
        }
    }

    public void setTracks(List<Track> tracks) {
        mPlayer.reset();
        setInternalState(INTERNAL_STATE_IDLE);

        mCurrentTrackList = tracks;

        if (mCurrentTrackList == null) {
            setCurrentTrack(null);
        } else {
            setCurrentTrack(0);
        }
    }

    public int getTracksCount() {
        if (mCurrentTrackList != null) {
            return mCurrentTrackList.size();
        } else {
            return 0;
        }
    }

    public Integer getCurrentTrackIndex() {
        return mCurrentTrackIndex;
    }

    public void nextTrack() {
        if (mCurrentTrackIndex != null) {
            setCurrentTrack(mCurrentTrackIndex + 1);
            playTrack(mCurrentTrackIndex);
        }
    }

    public void prevTrack() {
        if (mCurrentTrackIndex != null) {
            setCurrentTrack(mCurrentTrackIndex - 1);
            playTrack(mCurrentTrackIndex);
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
        } else if (mInternalState == INTERNAL_STATE_IDLE) {
            playTrack(mCurrentTrackIndex);
        } else if (mInternalState == INTERNAL_STATE_PLAYBACK_COMPLETED) {
            mPlayer.start();
            setInternalState(INTERNAL_STATE_STARTED);
        }
    }

    public void stop() {
        if (mInternalState == INTERNAL_STATE_STARTED ||
                mInternalState == INTERNAL_STATE_PAUSED ||
                mInternalState == INTERNAL_STATE_PREPARED ||
                mInternalState == INTERNAL_STATE_PLAYBACK_COMPLETED) {
            mPlayer.seekTo(0);
            mPlayer.reset();
            setInternalState(INTERNAL_STATE_IDLE);
        }
    }

    public void seek(int pos) {
        if (mInternalState == INTERNAL_STATE_PAUSED ||
                mInternalState == INTERNAL_STATE_STARTED) {
            if (pos >= 0 && pos < getDuration()) {
                mPlayer.seekTo(pos);
            }
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

            if (prevState != nextState) {
                mStateSubject.onNext(getState());
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

    private void setCurrentTrack(Integer i) {
        Track track;
        if (mCurrentTrackList != null && i != null && i >= 0 && i < mCurrentTrackList.size()) {
            mCurrentTrackIndex = i;
            track = mCurrentTrackList.get(i);
            if (track.url == null) {
                track = null;
            }
        } else {
            mCurrentTrackIndex = null;
            track = null;
        }

        if (track != mCurrentTrack) {
            mCurrentTrack = track;
            mTrackSubject.onNext(mCurrentTrack);
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

    private void notifyPosChanged() {
        mPositionSubject.onNext(getCurrentPosition());
        mDurationSubject.onNext(getDuration());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setInternalState(INTERNAL_STATE_PLAYBACK_COMPLETED);
        notifyPosChanged();
        nextTrack();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        setInternalState(INTERNAL_STATE_ERROR);
        mPlayer.reset();
        setInternalState(INTERNAL_STATE_IDLE);
        nextTrack();
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

    }
}
