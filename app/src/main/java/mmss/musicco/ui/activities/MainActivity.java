package mmss.musicco.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.dataobjects.Track;
import mmss.musicco.ui.fragments.ActorsFragment;
import mmss.musicco.ui.fragments.AlbumsFragment;
import mmss.musicco.ui.fragments.TracksFragment;
import mmss.musicco.models.TracksRepo;
import rx.Observable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    @Inject
    TracksRepo tracksRepo;

    @BindView(R.id.content_main_toolbar)
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
        App.getApp().inject(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getFragmentManager();
        Observable<List<Track>> obsTracks = tracksRepo.getAllTracks();
        fm.beginTransaction()
                .replace(R.id.content_main_container, TracksFragment.create(obsTracks))
                .commit();
        navigationView.setCheckedItem(R.id.nav_tracks);
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
