package mmss.musicco;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mmss.musicco.core.MusiccoPlayer;
import mmss.musicco.core.ServiceStarter;
import mmss.musicco.models.TracksRepo;

/**
 * Created by User on 13.10.2016.
 */

@Module
public class AppModule {

    private Context mContext;
    private ServiceStarter mStarter;

    public AppModule(Context context) {
        this.mContext = context;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    public TracksRepo provideTracksRepo() {
        return new TracksRepo();
    }

    @Provides
    @Singleton
    public MusiccoPlayer provideMusiccoPlayer(Context context) {
        MusiccoPlayer player = new MusiccoPlayer(context);
        mStarter = new ServiceStarter(context, player);
        return player;
    }
}
