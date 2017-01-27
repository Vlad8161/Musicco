package mmss.musicco.core;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

import mmss.musicco.dataobjects.Track;

/**
 * Created by User on 27.01.2017.
 */

public class MusiccoPlayer implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {
    private static final String TAG = "MusiccoPlayer";
    private Context mContext;
    private MediaPlayer mPlayer;
    private Track mCurrentTrack;

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

        if (track == null || track.url == null) {
            mCurrentTrack = null;
            return;
        }

        mCurrentTrack = track;

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
        }
    }

    public void resume() {
        Log.d(TAG, "resume");
        mPlayer.start();
    }

    public void stop() {
        Log.d(TAG, "stop");
        mPlayer.stop();
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public Track getCurrentTrack() {
        return mCurrentTrack;
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
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared");
        mPlayer.start();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete");
    }
}
