package mmss.musicco;

import javax.inject.Singleton;

import dagger.Component;
import mmss.musicco.ui.activities.AddPlaylistActivity;
import mmss.musicco.ui.activities.AddTrackToPlaylistActivity;
import mmss.musicco.ui.activities.MainActivity;
import mmss.musicco.ui.fragments.AlbumsFragment;
import mmss.musicco.ui.fragments.ArtistsFragment;
import mmss.musicco.ui.fragments.PlayerFragment;
import mmss.musicco.ui.fragments.PlaylistsFragment;
import mmss.musicco.ui.fragments.TracksFragment;
import mmss.musicco.ui.services.PlayerService;

/**
 * Created by User on 13.10.2016.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(ArtistsFragment mainActivity);

    void inject(AlbumsFragment mainActivity);

    void inject(PlayerFragment mainActivity);

    void inject(TracksFragment mainActivity);

    void inject(PlayerService playerService);

    void inject(PlaylistsFragment playlistsFragment);

    void inject(AddPlaylistActivity addPlaylistActivity);

    void inject(AddTrackToPlaylistActivity addTrackToPlaylistActivity);
}
