package mmss.musicco.core;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import mmss.musicco.ui.services.PlayerService;

/**
 * Created by vlad on 5/30/17.
 */

public class ServiceStarter {
    private Context mContext;
    private MusiccoPlayer mPlayer;

    public ServiceStarter(Context context, MusiccoPlayer musiccoPlayer) {
        this.mContext = context;
        this.mPlayer = musiccoPlayer;
        mPlayer.getStateObservable().subscribe((state) -> {
            Log.d("LOGI", "state changed");
            mContext.startService(new Intent(mContext, PlayerService.class));
        });
    }
}
