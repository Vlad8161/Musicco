package mmss.musicco;

import dagger.Component;
import mmss.musicco.fragments.ActorsFragment;
import mmss.musicco.fragments.AlbumsFragment;
import mmss.musicco.fragments.PlayerFragment;
import mmss.musicco.fragments.TracksFragment;

/**
 * Created by User on 13.10.2016.
 */

@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);
    void inject(ActorsFragment mainActivity);
    void inject(AlbumsFragment mainActivity);
    void inject(PlayerFragment mainActivity);
    void inject(TracksFragment mainActivity);
}
