package mmss.musicco.ui.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import javax.inject.Inject;

import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.core.MusiccoPlayer;
import mmss.musicco.dataobjects.Track;
import mmss.musicco.ui.activities.MainActivity;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by vlad on 5/30/17.
 */

public class PlayerService extends Service {
    private static String TAG = "PlayerService";
    private static int FOREGROUND_ID = 123;
    private static String ACTION_PLAY = "service_action_play";
    private static String ACTION_PAUSE = "service_action_pause";
    private static String ACTION_STOP = "service_action_stop";
    private static String ACTION_NEXT = "service_action_next";
    private static String ACTION_PREV = "service_action_prev";

    @Inject
    public MusiccoPlayer mPlayer;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    @Override
    public void onCreate() {
        super.onCreate();
        App.getApp().inject(this);
        mSubscription.add(mPlayer.getStateObservable().subscribe(this::onStateChanged));
        mSubscription.add(mPlayer.getTrackObservable().subscribe(this::onTrackChanged));
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
        Log.d(TAG, "onDestroy");
    }

    private void onStateChanged(int state) {
        showStatus();
    }

    private void onTrackChanged(Track track) {
        showStatus();
    }

    private void showStatus() {
        int state = mPlayer.getState();
        if (state != MusiccoPlayer.STATE_STOPPED) {
            Notification.Builder nb = new Notification.Builder(this)
                    .setTicker(getResources().getString(R.string.foreground_notification_ticker))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_service_small_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_service_large_icon));

            Track track = mPlayer.getCurrentTrack();
            String trackName;
            String artistName;
            if (track != null && track.name != null) {
                trackName = track.name;
            } else {
                trackName = getResources().getString(R.string.tracks_repo_track_unknown);
            }
            if (track != null && track.artist != null) {
                artistName = track.artist;
            } else {
                artistName = getResources().getString(R.string.tracks_repo_artist_unknown);
            }
            nb.setContentTitle(trackName);
            nb.setContentText(artistName);

            Intent prevIntent = new Intent(this, PlayerService.class);
            prevIntent.setAction(ACTION_PREV);
            PendingIntent prevPendingIntent = PendingIntent.getService(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            nb.addAction(R.drawable.ic_service_prev, "", prevPendingIntent);

            if (state == MusiccoPlayer.STATE_PLAYING) {
                Intent pauseIntent = new Intent(this, PlayerService.class);
                pauseIntent.setAction(ACTION_PAUSE);
                PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                nb.addAction(R.drawable.ic_service_pause, "", pausePendingIntent);
            } else {
                Intent playIntent = new Intent(this, PlayerService.class);
                playIntent.setAction(ACTION_PLAY);
                PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                nb.addAction(R.drawable.ic_service_play, "", playPendingIntent);
            }

            Intent nextIntent = new Intent(this, PlayerService.class);
            nextIntent.setAction(ACTION_NEXT);
            PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            nb.addAction(R.drawable.ic_service_next, "", nextPendingIntent);

/*
            //TODO: Полная остановка воспроизведения. Пока не реализована т.к. в Notification не может быть больше 3х действий
            Intent stopIntent = new Intent(this, PlayerService.class);
            stopIntent.setAction(ACTION_STOP);
            PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            nb.addAction(R.drawable.ic_service_stop, "", stopPendingIntent);
*/

            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            nb.setContentIntent(mainActivityPendingIntent);

            startForeground(FOREGROUND_ID, nb.build());
        } else {
            stopForeground(true);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(ACTION_PLAY)) {
                    mPlayer.play();
                } else if (action.equals(ACTION_PAUSE)) {
                    mPlayer.pause();
                } else if (action.equals(ACTION_STOP)) {
                    mPlayer.stop();
                } else if (action.equals(ACTION_NEXT)) {
                    mPlayer.nextTrack();
                } else if (action.equals(ACTION_PREV)) {
                    mPlayer.prevTrack();
                }
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
