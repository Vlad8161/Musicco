package mmss.musicco.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmss.musicco.R;
import mmss.musicco.dataobjects.Track;
import mmss.musicco.ui.adapters.TracksAdapter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by User on 12.10.2016.
 */

public class TracksFragment extends Fragment {
    private Observable<List<Track>> observableTracks;
    private Subscription subscription;
    private List<Track> tracks;
    private TracksAdapter adapter;

    @BindView(R.id.fragment_tracks_view_list)
    public ListView lvTracks;

    @BindView(R.id.fragment_tracks_view_progress)
    public View viewProgress;

    @BindView(R.id.fragment_tracks_view_message)
    public TextView viewMessage;

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
        adapter = new TracksAdapter(getActivity().getApplicationContext());
        lvTracks.setAdapter(adapter);
        return v;
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
            if (tracks == null || tracks.isEmpty()) {
                viewMessage.setText(R.string.fragment_tracks_empty_tracks);
                lvTracks.setVisibility(View.GONE);
                viewMessage.setVisibility(View.VISIBLE);
                viewProgress.setVisibility(View.GONE);
            } else {
                adapter.setTracks(tracks);
                lvTracks.setVisibility(View.VISIBLE);
                viewMessage.setVisibility(View.GONE);
                viewProgress.setVisibility(View.GONE);
            }
        });
    }
}
