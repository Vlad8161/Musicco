package mmss.musicco.ui.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
import mmss.musicco.ui.customviews.NotOverlappingBottomSheetLayout;
import mmss.musicco.ui.fragments.AlbumsFragment;
import mmss.musicco.ui.fragments.ArtistsFragment;
import mmss.musicco.ui.fragments.TracksFragment;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnShowTracksListener, View.OnTouchListener, ValueAnimator.AnimatorUpdateListener,
        Animator.AnimatorListener, View.OnLayoutChangeListener {
    private static final String TAG = "MainActivity";
    private static final int BOTTOM_SHEET_STATE_HIDDEN = 0;
    private static final int BOTTOM_SHEET_STATE_SHOWN = 1;
    private static final int MIN_DRAG_VELOCITY = 4;
    private static final int PERMISSION_REQUEST_CODE = 10;

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
    RelativeLayout rootLayout;

    @BindView(R.id.content_main_bottom_sheet_layout)
    NotOverlappingBottomSheetLayout bottomSheetLayout;

    private int mDraggerHeight;
    private int mPlayerHeight;
    private int mStartBottomSheetHeight;
    private int mBottomSheetState = BOTTOM_SHEET_STATE_HIDDEN;
    private int mStartDragY;
    private int mPrevDragY;
    private int mDragVelocity;
    private boolean isDraggingNow = false;
    private ValueAnimator mBottomSheetAnimator = null;
    private CompositeSubscription mSubscription = new CompositeSubscription();

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

        FragmentManager fm = getFragmentManager();
        Observable<List<Track>> obsTracks = tracksRepo.getAllTracks();
        fm.beginTransaction()
                .replace(R.id.content_main_container, TracksFragment.create(obsTracks))
                .commit();
        navigationView.setCheckedItem(R.id.nav_tracks);

        if (musiccoPlayer.getState() == MusiccoPlayer.STATE_STOPPED) {
            mBottomSheetState = BOTTOM_SHEET_STATE_HIDDEN;
        } else {
            mBottomSheetState = BOTTOM_SHEET_STATE_SHOWN;
        }

        bottomSheetView.setOnTouchListener(this);
        bottomSheetView.addOnLayoutChangeListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        mSubscription.add(musiccoPlayer.getTrackObservable().subscribe(this::onTrackChanged));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
        bottomSheetView.removeOnLayoutChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (musiccoPlayer.getTracks() != null) {
            navigationView.getMenu().findItem(R.id.nav_current_track_list).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_current_track_list).setVisible(false);
        }

        requestStoragePermissions();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
        } else if (id == R.id.nav_current_track_list) {
            Fragment f = TracksFragment.create(Observable.just(musiccoPlayer.getTracks()));
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_main_container, f).commit();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onTrackChanged(Track track) {
        if (track != null) {
            showPlayer();
        } else {
            hidePlayer();
        }

        if (musiccoPlayer.getTracks() != null) {
            navigationView.getMenu().findItem(R.id.nav_current_track_list).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_current_track_list).setVisible(false);
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
            mStartBottomSheetHeight = bottomSheetLayout.getBottomSheetPeekHeight();
            mStartDragY = yFromDraggerToRootLayout((int) event.getY());
            mPrevDragY = mStartDragY;
            isDraggingNow = true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int currY = yFromDraggerToRootLayout((int) event.getY());
            int deltaY = currY - mStartDragY;
            int newBottomSheetHeight = mStartBottomSheetHeight - deltaY;
            mDragVelocity = currY - mPrevDragY;
            mPrevDragY = currY;
            newBottomSheetHeight = Math.min(newBottomSheetHeight, mPlayerHeight);
            newBottomSheetHeight = Math.max(newBottomSheetHeight, mDraggerHeight);
            setPlayerPeekHeight(newBottomSheetHeight);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isDraggingNow = false;
            if (Math.abs(mDragVelocity) < MIN_DRAG_VELOCITY) {
                if (mBottomSheetState == BOTTOM_SHEET_STATE_SHOWN) {
                    if (getPlayerPeekHeight() < mPlayerHeight / 2) {
                        animatePlayerToHeight(mDraggerHeight);
                    } else {
                        animatePlayerToHeight(mPlayerHeight);
                    }
                } else if (mBottomSheetState == BOTTOM_SHEET_STATE_HIDDEN) {
                    animatePlayerToHeight(0);
                }
            } else {
                if (mBottomSheetState == BOTTOM_SHEET_STATE_SHOWN) {
                    if (mDragVelocity > 0) {
                        animatePlayerToHeight(mDraggerHeight);
                    } else {
                        animatePlayerToHeight(mPlayerHeight);
                    }
                } else if (mBottomSheetState == BOTTOM_SHEET_STATE_HIDDEN) {
                    animatePlayerToHeight(0);
                }
            }
        }

        return true;
    }

    private int getPlayerPeekHeight() {
        return bottomSheetLayout.getBottomSheetPeekHeight();
    }

    private void setPlayerPeekHeight(int height) {
        bottomSheetLayout.setBottomSheetPeekHeight(height);
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

    @Override
    public void onLayoutChange(View v, int l, int t, int r, int b, int ol, int ot, int or, int ob) {
        if (v == bottomSheetView) {
            mDraggerHeight = dragger.getMeasuredHeight();
            int newPlayerHeight = b - t;
            if (mPlayerHeight != newPlayerHeight) {
                mPlayerHeight = newPlayerHeight;
                if (mBottomSheetState == BOTTOM_SHEET_STATE_SHOWN) {
                    setPlayerPeekHeight(mPlayerHeight);
                } else if (mBottomSheetState == BOTTOM_SHEET_STATE_HIDDEN) {
                    setPlayerPeekHeight(0);
                }
            }
        }
    }

    private void requestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int perm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (perm != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FragmentManager fm = getFragmentManager();
                Observable<List<Track>> obsTracks = tracksRepo.getAllTracks();
                fm.beginTransaction()
                        .replace(R.id.content_main_container, TracksFragment.create(obsTracks))
                        .commit();
            }
        }
    }
}
