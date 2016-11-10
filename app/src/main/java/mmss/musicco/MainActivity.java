package mmss.musicco;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmss.musicco.dataobjects.Album;
import mmss.musicco.dataobjects.Artist;
import mmss.musicco.dataobjects.Track;
import mmss.musicco.fragments.ActorsFragment;
import mmss.musicco.fragments.AlbumsFragment;
import mmss.musicco.fragments.TracksFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Fragment f = new TracksFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_main_container, f).commit();
        navigationView.setCheckedItem(R.id.nav_tracks);

        Log.d(TAG, "--- getAllTracks ---");
        for (Track i : DatabaseHelper.getAllTracks(this)) {
            Log.d(TAG, i.name + ", " + i.album + ", " + i.artist);
        }

        Log.d(TAG, "--- getArtistTracks ---");
        for (Track i : DatabaseHelper.getArtistTracks(this, "artist1")) {
            Log.d(TAG, i.name + ", " + i.album + ", " + i.artist);
        }

        Log.d(TAG, "--- getAlbumTracks ---");
        for (Track i : DatabaseHelper.getAlbumTracks(this, "artist1", "album1")) {
            Log.d(TAG, i.name + ", " + i.album + ", " + i.artist);
        }

        Log.d(TAG, "--- getAllArtists ---");
        for (Artist i : DatabaseHelper.getAllArtist(this)) {
            Log.d(TAG, i.name + ", " + i.tracksCount);
        }

        Log.d(TAG, "--- getAllAlbums ---");
        for (Album i : DatabaseHelper.getAllAlbums(this, "artist2")) {
            Log.d(TAG, i.artist + ", " + i.name + ", " + i.tracksCount);
        }
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_tracks) {
            Fragment f = new TracksFragment();
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_main_container, f).commit();
        } else if (id == R.id.nav_albums) {
            Fragment f = new AlbumsFragment();
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_main_container, f).commit();
        } else if (id == R.id.nav_actors) {
            Fragment f = new ActorsFragment();
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_main_container, f).commit();
        } else if (id == R.id.nav_play_lists) {
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
