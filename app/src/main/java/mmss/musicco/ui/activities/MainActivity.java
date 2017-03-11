package mmss.musicco.ui.activities;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
import mmss.musicco.ui.customviews.MeasurableRelativeLayout;
import mmss.musicco.ui.fragments.AlbumsFragment;
import mmss.musicco.ui.fragments.ArtistsFragment;
import mmss.musicco.ui.fragments.TracksFragment;
import rx.Observable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MusiccoPlayer.OnTrackChangedListener, OnShowTracksListener, View.OnTouchListener, ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    private static final String TAG = "MainActivity";
    private static final int BOTTOM_SHEET_STATE_HIDDEN = 0;
    private static final int BOTTOM_SHEET_STATE_SHOWN = 1;

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
    MeasurableRelativeLayout rootLayout;

    private int mDraggerHeight;
    private int mPlayerHeight;
    private int mStartBottomSheetHeight;
    private int mBottomSheetState = BOTTOM_SHEET_STATE_HIDDEN;
    private int mStartDragY;
    private boolean isDraggingNow = false;
    private ValueAnimator mBottomSheetAnimator = null;

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

        bottomSheetView.setOnTouchListener(this);
        rootLayout.addOnSizeChangedListener((w, h, oldw, oldh) -> {
            mPlayerHeight = bottomSheetView.getMeasuredHeight();
            mDraggerHeight = dragger.getMeasuredHeight();
            if (mBottomSheetState == BOTTOM_SHEET_STATE_SHOWN) {
                setPlayerPeekHeight(mPlayerHeight);
            } else if (mBottomSheetState == BOTTOM_SHEET_STATE_HIDDEN) {
                setPlayerPeekHeight(0);
            }
            Log.d("LOGI", "playerHeight : " + mPlayerHeight);
            Log.d("LOGI", "draggerHeight : " + mDraggerHeight);
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
        if (track != null) {
            showPlayer();
        } else {
            hidePlayer();
        }
    }

    @Override
    public void onShowTracks(Observable<List<Track>> tracksObservable) {
        Fragment f = TracksFragment.create(tracksObservable);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_main_container, f).commit();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mBottomSheetAnimator != null) {
            return true;
        }

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
            setPlayerPeekHeight(newBottomSheetHeight);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isDraggingNow = false;
            if (mBottomSheetState == BOTTOM_SHEET_STATE_SHOWN) {
                if (getPlayerPeekHeight() < mPlayerHeight / 2) {
                    animatePlayerToHeight(mDraggerHeight);
                } else {
                    animatePlayerToHeight(mPlayerHeight);
                }
            } else if (mBottomSheetState == BOTTOM_SHEET_STATE_HIDDEN) {
                animatePlayerToHeight(0);
            }
        }

        return true;
    }

    private int getPlayerPeekHeight() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bottomSheetView.getLayoutParams();
        if (lp != null) {
            return mPlayerHeight + lp.bottomMargin;
        } else {
            return 0;
        }
    }

    private void setPlayerPeekHeight(int height) {
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

    private void hidePlayer() {
        mBottomSheetState = BOTTOM_SHEET_STATE_HIDDEN;
        animatePlayerToHeight(0);
    }

    private void showPlayer() {
        mBottomSheetState = BOTTOM_SHEET_STATE_SHOWN;
        animatePlayerToHeight(mPlayerHeight);
    }

    private void animatePlayerToHeight(int height) {
        if (isDraggingNow || mBottomSheetAnimator != null) {
            return;
        }

        int currHeight = getPlayerPeekHeight();
        mBottomSheetAnimator = ValueAnimator.ofInt(currHeight, height);
        mBottomSheetAnimator.addUpdateListener(this);
        mBottomSheetAnimator.addListener(this);
        mBottomSheetAnimator.setDuration((long) (Math.abs(currHeight - height) * 0.9));
        mBottomSheetAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        setPlayerPeekHeight((int) mBottomSheetAnimator.getAnimatedValue());
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mBottomSheetAnimator = null;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        mBottomSheetAnimator = null;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
