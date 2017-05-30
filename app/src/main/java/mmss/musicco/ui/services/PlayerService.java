package mmss.musicco.ui.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import mmss.musicco.core.MusiccoPlayer;
import mmss.musicco.dataobjects.Track;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by vlad on 5/30/17.
 */

public class PlayerService extends Service {
    @Inject
    private MusiccoPlayer mPlayer;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        mSubscription.add(mPlayer.getStateObservable().subscribe(this::onStateChanged));
        mSubscription.add(mPlayer.getTrackObservable().subscribe(this::onTrackChanged));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    private void onStateChanged(int state) {
        if (state != MusiccoPlayer.STATE_STOPPED) {

        } else {
            stopForeground(true);
        }
    }

    private void onTrackChanged(Track track) {

    }

    private void

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
