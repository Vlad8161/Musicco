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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.core.MusiccoPlayer;
import mmss.musicco.dataobjects.Track;
import mmss.musicco.models.TracksRepo;
import mmss.musicco.ui.fragments.AlbumsFragment;
import mmss.musicco.ui.fragments.ArtistsFragment;
import mmss.musicco.ui.fragments.TracksFragment;
import rx.Observable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MusiccoPlayer.OnTrackChangedListener, OnShowTracksListener, View.OnTouchListener {
    public static final int BOTTOM_SHEET_STATE_HIDDEN = 0;
    public static final int BOTTOM_SHEET_STATE_SHOWN = 1;
    private static final String TAG = "MainActivity";
    @Inject
    TracksRepo tracksRepo;

    @Inject
    MusiccoPlayer musiccoPlayer;

    @BindView(R.id.content_main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.content_main_dragger)
    View dragger;

    @BindView(R.id.content_main_bottom_sheet_view)
    View bottomSheetView;

    @BindView(R.id.content_main_root)
    View rootLayout;

    private int mDraggerHeight;
    private int mPlayerHeight;
    private int mStartBottomSheetHeight;
    private int mBottomSheetState;
    private int mStartDragY;
    private boolean isDraggingNow = false;

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

        musiccoPlayer.addOnTrackChangedListener(this);

        dragger.setOnTouchListener(this);
        bottomSheetView.addOnLayoutChangeListener((v, l, t, r, b, oL, oT, oR, oB) -> {
            if (!isDraggingNow) {
                mPlayerHeight = v.getMeasuredHeight();
            }
        });
        dragger.addOnLayoutChangeListener((v, l, t, r, b, oL, oT, oR, oB) -> {
            if (!isDraggingNow) {
                mDraggerHeight = v.getHeight();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musiccoPlayer.removeOnTrackChangedListener(this);
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
            Fragment f = TracksFragment.create(tracksRepo.getAllTracks());
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_main_container, f).commit();
        } else if (id == R.id.nav_albums) {
            Fragment f = AlbumsFragment.create(this);
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_main_container, f).commit();
        } else if (id == R.id.nav_actors) {
            Fragment f = ArtistsFragment.create(this);
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_main_container, f).commit();
        } else if (id == R.id.nav_play_lists) {
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTrackChangedListener(Track track) {

    }

    @Override
    public void onShowTracks(Observable<List<Track>> tracksObservable) {
        Fragment f = TracksFragment.create(tracksObservable);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_main_container, f).commit();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bottomSheetView.getLayoutParams();
            mStartBottomSheetHeight = mPlayerHeight + lp.bottomMargin;
            mStartDragY = yFromDraggerToRootLayout((int) event.getY());
            isDraggingNow = true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int currY = yFromDraggerToRootLayout((int) event.getY());
            int deltaY = currY - mStartDragY;
            int newBottomSheetHeight = mStartBottomSheetHeight - deltaY;
            newBottomSheetHeight = Math.min(newBottomSheetHeight, mPlayerHeight);
            newBottomSheetHeight = Math.max(newBottomSheetHeight, mDraggerHeight);
            setPlayerHeight(newBottomSheetHeight);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isDraggingNow = false;
        }
        return true;
    }

    private void setPlayerHeight(int height) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.setMargins(0, 0, 0, -(mPlayerHeight - height));
        bottomSheetView.setLayoutParams(lp);
    }

    private int yFromDraggerToRootLayout(int y) {
        int[] draggerLoc = new int[2];
        int[] rootLoc = new int[2];
        dragger.getLocationOnScreen(draggerLoc);
        rootLayout.getLocationOnScreen(rootLoc);
        return draggerLoc[1] - rootLoc[1] + y;
    }
}
