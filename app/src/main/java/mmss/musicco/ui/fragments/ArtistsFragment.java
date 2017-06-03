package mmss.musicco.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.dataobjects.Artist;
import mmss.musicco.models.TracksRepo;
import mmss.musicco.ui.activities.OnShowTracksListener;
import mmss.musicco.ui.adapters.ArtistsAdapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by User on 12.10.2016.
 */

public class ArtistsFragment extends Fragment implements AdapterView.OnItemClickListener {

    @Inject
    public TracksRepo tracksRepo;

    @BindView(R.id.fragment_artists_list_view)
    public ListView listView;

    @BindView(R.id.fragment_artists_progress)
    public ProgressBar progressBar;

    @BindView(R.id.fragment_artists_tv_message)
    public TextView tvMessage;

    private ArtistsAdapter adapter;
    private Subscription subscription;
    private OnShowTracksListener showTracksListener;

    public static ArtistsFragment create(OnShowTracksListener listener) {
        ArtistsFragment f = new ArtistsFragment();
        f.showTracksListener = listener;
        return f;
    }

    public ArtistsFragment() {
        App.getApp().inject(this);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_artists, null);
        ButterKnife.bind(this, root);
        adapter = new ArtistsAdapter(getActivity(), null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        startLoading();
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelLoading();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (showTracksListener == null) {
            return;
        }

        Artist artist = (Artist) adapter.getItem(i);
        if (artist != null) {
            showTracksListener.onShowTracks(tracksRepo.getArtistTracks(artist.name).toList());
        }
    }

    private void startLoading() {
        cancelLoading();

        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.GONE);

        subscription = tracksRepo.getAllArtists().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((artists) -> {
                    if (artists != null && !artists.isEmpty()) {
                        adapter.setArtists(artists);
                        listView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        tvMessage.setVisibility(View.GONE);
                    } else {
                        listView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        tvMessage.setText(R.string.tracks_repo_no_artists);
                        tvMessage.setVisibility(View.VISIBLE);
                    }

                });
    }

    private void cancelLoading() {
        if (subscription != null) {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            subscription = null;
        }
    }

}

