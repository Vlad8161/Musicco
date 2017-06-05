package mmss.musicco;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mmss.musicco.core.MusiccoPlayer;
import mmss.musicco.core.ServiceStarter;
import mmss.musicco.core.TracksRepo;
import mmss.musicco.dao.DaoMaster;
import mmss.musicco.dao.DaoSession;

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
    public TracksRepo provideTracksRepo(DaoSession daoSession) {
        return new TracksRepo(daoSession);
    }

    @Provides
    @Singleton
    public MusiccoPlayer provideMusiccoPlayer(Context context) {
        MusiccoPlayer player = new MusiccoPlayer(context);
        mStarter = new ServiceStarter(context, player);
        return player;
    }

    @Provides
    @Singleton
    public DaoSession provideDaoSession(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "musicco.db");
        Database db = helper.getWritableDb();
        DaoMaster master = new DaoMaster(db);
        return master.newSession();
    }
}
