package mmss.musicco.ui.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.core.TracksRepo;
import mmss.musicco.dataobjects.Track;
import mmss.musicco.ui.fragments.PlaylistsFragment;

/**
 * Created by vlad on 18.07.17.
 */

public class AddTrackToPlaylistActivity extends AppCompatActivity implements OnPlaylistSelectedListener {
    public static final String TRACK_URL_KEY = "trackUrl";

    String trackUrl = null;

    @Inject
    TracksRepo tracksRepo;

    @BindView(R.id.activity_add_track_to_playlist_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_add_track_to_playlist_container)
    FrameLayout mFragmentContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track_to_playlist);
        ButterKnife.bind(this);
        App.getApp().inject(this);
        setSupportActionBar(mToolbar);

        trackUrl = getIntent().getStringExtra(TRACK_URL_KEY);
        if (trackUrl == null) {
            finish();
        }

        Fragment f = PlaylistsFragment.create(this);
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_add_track_to_playlist_container, f)
                .commit();
    }

    @Override
    public void onPlaylistSelected(Long playlistId) {
        tracksRepo.addTrackToPlaylist(playlistId, trackUrl);
    }
}
