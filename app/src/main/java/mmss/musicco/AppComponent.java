package mmss.musicco;

import javax.inject.Singleton;

import dagger.Component;
import mmss.musicco.ui.activities.MainActivity;
import mmss.musicco.ui.fragments.ActorsFragment;
import mmss.musicco.ui.fragments.AlbumsFragment;
import mmss.musicco.ui.fragments.PlayerFragment;
import mmss.musicco.ui.fragments.TracksFragment;

/**
 * Created by User on 13.10.2016.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);
    void inject(ActorsFragment mainActivity);
    void inject(AlbumsFragment mainActivity);
    void inject(PlayerFragment mainActivity);
    void inject(TracksFragment mainActivity);
}
