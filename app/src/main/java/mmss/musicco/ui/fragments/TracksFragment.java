package mmss.musicco.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.core.MusiccoPlayer;
import mmss.musicco.dataobjects.Track;
import mmss.musicco.ui.adapters.TracksAdapter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by User on 12.10.2016.
 */

public class TracksFragment extends Fragment implements AdapterView.OnItemClickListener, MusiccoPlayer.OnTrackChangedListener, MusiccoPlayer.OnStateChangedListener {
    @BindView(R.id.fragment_tracks_view_list)
    public ListView lvTracks;
    @BindView(R.id.fragment_tracks_view_progress)
    public View viewProgress;
    @BindView(R.id.fragment_tracks_view_message)
    public TextView viewMessage;
    @Inject
    public MusiccoPlayer musiccoPlayer;
    private Observable<List<Track>> observableTracks;
    private Subscription subscription;
    private TracksAdapter adapter;
    private List<Track> mTracks = null;

    public static TracksFragment create(Observable<List<Track>> observableTracks) {
        if (observableTracks == null) {
            throw new NullPointerException("Tracks observable can't be null");
        }

        TracksFragment f = new TracksFragment();
        f.observableTracks = observableTracks;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tracks, null);
        ButterKnife.bind(this, v);
        App.getApp().inject(this);
        adapter = new TracksAdapter(getActivity().getApplicationContext(), musiccoPlayer);
        lvTracks.setAdapter(adapter);
        lvTracks.setOnItemClickListener(this);
        musiccoPlayer.addOnTrackChangedListener(this);
        musiccoPlayer.addOnStateChangedListener(this);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        musiccoPlayer.removeOnTrackChangedListener(this);
        musiccoPlayer.removeOnStateChangedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        startLoading();
        Log.d("TAG", "TracksFragment started");
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelLoading();
        Log.d("TAG", "TracksFragment stopped");
    }

    private void cancelLoading() {
        if (subscription != null) {
            if (subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            subscription = null;
        }
    }

    private void startLoading() {
        cancelLoading();

        lvTracks.setVisibility(View.GONE);
        viewMessage.setVisibility(View.GONE);
        viewProgress.setVisibility(View.VISIBLE);

        subscription = observableTracks
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((tracks) -> {
                    mTracks = tracks;
                    if (tracks == null || tracks.isEmpty()) {
                        viewMessage.setText(R.string.tracks_repo_empty_tracks);
                        lvTracks.setVisibility(View.GONE);
                        viewMessage.setVisibility(View.VISIBLE);
                        viewProgress.setVisibility(View.GONE);
                    } else {
                        adapter.setTracks(tracks);
                        adapter.onTrackChanged(musiccoPlayer.getCurrentTrack());
                        lvTracks.setVisibility(View.VISIBLE);
                        viewMessage.setVisibility(View.GONE);
                        viewProgress.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Track track = (Track) adapter.getItem(position);
        Track currentTrack = musiccoPlayer.getCurrentTrack();
        if (currentTrack == null || !currentTrack.equals(track)) {
            musiccoPlayer.setTracks(mTracks);
            musiccoPlayer.playTrack(position);
        } else if (musiccoPlayer.getState() == MusiccoPlayer.STATE_PLAYING) {
            musiccoPlayer.pause();
        } else {
            musiccoPlayer.play();
        }
    }

    @Override
    public void onTrackChangedListener(Track track) {
        adapter.onTrackChanged(track);
    }

    @Override
    public void onStateChangedListener(int state) {
        adapter.notifyDataSetChanged();
    }
}
