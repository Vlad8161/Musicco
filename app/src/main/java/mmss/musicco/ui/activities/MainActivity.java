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
import android.support.annotation.Nullable;
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

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.core.MusiccoPlayer;
import mmss.musicco.dataobjects.Track;
import mmss.musicco.core.TracksRepo;
import mmss.musicco.ui.customviews.NotOverlappingBottomSheetLayout;
import mmss.musicco.ui.fragments.AlbumsFragment;
import mmss.musicco.ui.fragments.ArtistsFragment;
import mmss.musicco.ui.fragments.TracksFragment;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnShowTracksListener, View.OnLayoutChangeListener {
    private static final String TAG = "MainActivity";
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

    private CompositeSubscription mSubscription = new CompositeSubscription();
    private BehaviorSubject<Integer> mPlayerHeightSubject = BehaviorSubject.create(0);
    private BehaviorSubject<Integer> mDraggerHeightSubject = BehaviorSubject.create(0);
    private BottomSheetHelper mBottomSheetHelper;

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
        Observable<List<Track>> obsTracks = tracksRepo.getAllTracks().toList();
        fm.beginTransaction()
                .replace(R.id.content_main_container, TracksFragment.create(obsTracks))
                .commit();
        navigationView.setCheckedItem(R.id.nav_tracks);

        mBottomSheetHelper = (BottomSheetHelper) fm.findFragmentByTag("bottomSheetHelper");
        if (mBottomSheetHelper == null) {
            mBottomSheetHelper = new BottomSheetHelper();
            fm.beginTransaction().add(mBottomSheetHelper, "bottomSheetHelper").commit();
        }

        navigationView.setNavigationItemSelectedListener(this);
        mSubscription.add(musiccoPlayer.getTrackObservable().subscribe(this::onTrackChanged));
        bottomSheetView.addOnLayoutChangeListener(this);
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
        mBottomSheetHelper.onActivityStarted(this, musiccoPlayer, mDraggerHeightSubject, mPlayerHeightSubject);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBottomSheetHelper.onActivityStopped();
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
            Fragment f = TracksFragment.create(tracksRepo.getAllTracks().toList());
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
    public void onLayoutChange(View v, int l, int t, int r, int b, int ol, int ot, int or, int ob) {
        if (v == bottomSheetView) {
            int newDraggerHeight = dragger.getMeasuredHeight();
            if (newDraggerHeight != mDraggerHeightSubject.getValue()) {
                mDraggerHeightSubject.onNext(newDraggerHeight);
            }

            int newPlayerHeight = b - t;
            if (newPlayerHeight != mPlayerHeightSubject.getValue()) {
                mPlayerHeightSubject.onNext(newPlayerHeight);
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
                Observable<List<Track>> obsTracks = tracksRepo.getAllTracks().toList();
                fm.beginTransaction()
                        .replace(R.id.content_main_container, TracksFragment.create(obsTracks))
                        .commit();
            }
        }
    }

    public static class BottomSheetHelper extends Fragment implements View.OnTouchListener, ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
        private static final int MIN_DRAG_VELOCITY = 4;
        private static int BOTTOM_SHEET_STATE_SHOWN = 0;
        private static int BOTTOM_SHEET_STATE_COLLAPSED = 1;
        /* views */
        private View mBottomSheetView;
        private NotOverlappingBottomSheetLayout mBottomSheetLayout;
        private View mRootLayout;

        /* rx related */
        private Observable<Integer> mDraggerHeightObservable;
        private Observable<Integer> mPlayerHeightObservable;
        private CompositeSubscription mCompositeSubscription;

        /* view state */
        private int mBottomSheetState = BOTTOM_SHEET_STATE_SHOWN;
        private int mDraggerHeight;
        private int mPlayerHeight;
        private MusiccoPlayer mPlayer;

        /* touch */
        private int mDragVelocity;
        private int mStartBottomSheetHeight;
        private int mStartDragY;
        private int mPrevDragY;
        private boolean isDraggingNow = false;
        private ValueAnimator mBottomSheetAnimator = null;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void onActivityStarted(
                MainActivity activity,
                MusiccoPlayer player,
                Observable<Integer> draggerHeightObservable,
                Observable<Integer> playerHeightObservable
        ) {
            this.mBottomSheetView = activity.findViewById(R.id.content_main_bottom_sheet_view);
            this.mBottomSheetLayout = (NotOverlappingBottomSheetLayout) activity.findViewById(R.id.content_main_bottom_sheet_layout);
            this.mRootLayout = activity.findViewById(R.id.content_main_root);
            this.mPlayer = player;
            this.mDraggerHeightObservable = draggerHeightObservable;
            this.mPlayerHeightObservable = playerHeightObservable;
            this.mCompositeSubscription = new CompositeSubscription();

            mBottomSheetView.setOnTouchListener(this);
            mCompositeSubscription.add(mDraggerHeightObservable.subscribe(this::onDraggerHeightChanged));
            mCompositeSubscription.add(mPlayerHeightObservable.subscribe(this::onPlayerHeightChanged));
            mCompositeSubscription.add(mPlayer.getStateObservable().subscribe(this::onPlayerStateChanged));
        }

        public void onActivityStopped() {
            mCompositeSubscription.unsubscribe();
            mBottomSheetView.setOnTouchListener(null);
            isDraggingNow = false;
            cancelAnimation();

            this.mBottomSheetView = null;
            this.mBottomSheetLayout = null;
            this.mPlayer = null;
            this.mRootLayout = null;
            this.mDraggerHeightObservable = null;
            this.mPlayerHeightObservable = null;
            this.mCompositeSubscription = null;
        }

        private void onPlayerStateChanged(int state) {
            animatePlayerToNeededHeight();
        }

        private void onDraggerHeightChanged(int draggerHeight) {
            mDraggerHeight = draggerHeight;
            animatePlayerToNeededHeight();
        }

        private void onPlayerHeightChanged(int playerHeight) {
            mPlayerHeight = playerHeight;
            animatePlayerToNeededHeight();
        }

        private void animatePlayerToNeededHeight() {
            cancelAnimation();
            if (mPlayer.getState() != MusiccoPlayer.STATE_STOPPED) {
                if (mBottomSheetState == BOTTOM_SHEET_STATE_SHOWN) {
                    animatePlayerToHeight(mPlayerHeight);
                } else if (mBottomSheetState == BOTTOM_SHEET_STATE_COLLAPSED) {
                    animatePlayerToHeight(mDraggerHeight);
                }
            } else {
                isDraggingNow = false;
                animatePlayerToHeight(0);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mBottomSheetAnimator != null) {
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mStartBottomSheetHeight = mBottomSheetLayout.getBottomSheetPeekHeight();
                mStartDragY = yFromDraggerToRootLayout((int) event.getY());
                mPrevDragY = mStartDragY;
                isDraggingNow = true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (!isDraggingNow) {
                    return true;
                }

                int currY = yFromDraggerToRootLayout((int) event.getY());
                int deltaY = currY - mStartDragY;
                int newBottomSheetHeight = mStartBottomSheetHeight - deltaY;
                mDragVelocity = currY - mPrevDragY;
                mPrevDragY = currY;
                newBottomSheetHeight = Math.min(newBottomSheetHeight, mPlayerHeight);
                newBottomSheetHeight = Math.max(newBottomSheetHeight, mDraggerHeight);
                setPlayerPeekHeight(newBottomSheetHeight);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!isDraggingNow) {
                    return true;
                }

                isDraggingNow = false;
                if (Math.abs(mDragVelocity) < MIN_DRAG_VELOCITY) {
                    if (getPlayerPeekHeight() < mPlayerHeight / 2) {
                        mBottomSheetState = BOTTOM_SHEET_STATE_COLLAPSED;
                    } else {
                        mBottomSheetState = BOTTOM_SHEET_STATE_SHOWN;
                    }
                } else {
                    if (mDragVelocity > 0) {
                        mBottomSheetState = BOTTOM_SHEET_STATE_COLLAPSED;
                    } else {
                        mBottomSheetState = BOTTOM_SHEET_STATE_SHOWN;
                    }
                }

                animatePlayerToNeededHeight();
            }
            return true;
        }

        private int getPlayerPeekHeight() {
            return mBottomSheetLayout.getBottomSheetPeekHeight();
        }

        private void setPlayerPeekHeight(int height) {
            mBottomSheetLayout.setBottomSheetPeekHeight(height);
        }

        private int yFromDraggerToRootLayout(int y) {
            int[] draggerLoc = new int[2];
            int[] rootLoc = new int[2];
            mBottomSheetView.getLocationOnScreen(draggerLoc);
            mRootLayout.getLocationOnScreen(rootLoc);
            return draggerLoc[1] - rootLoc[1] + y;
        }

        private void cancelAnimation() {
            if (mBottomSheetAnimator != null) {
                mBottomSheetAnimator.cancel();
                mBottomSheetAnimator = null;
            }
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
            if (mBottomSheetAnimator == animation) {
                mBottomSheetAnimator = null;
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            if (mBottomSheetAnimator == animation) {
                mBottomSheetAnimator = null;
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
