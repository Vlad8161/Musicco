package mmss.musicco.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.dataobjects.Album;
import mmss.musicco.models.TracksRepo;
import mmss.musicco.ui.activities.OnShowTracksListener;
import mmss.musicco.ui.adapters.AlbumsAdapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by User on 12.10.2016.
 */

public class AlbumsFragment extends Fragment implements AdapterView.OnItemClickListener {

    @Inject
    public TracksRepo tracksRepo;

    @BindView(R.id.fragment_albums_list_view)
    public ListView listView;

    @BindView(R.id.fragment_albums_pb_progress)
    public View pbProgress;

    @BindView(R.id.fragment_albums_tv_message)
    public TextView tvMessage;

    private AlbumsAdapter adapter;
    private Subscription subscription;
    private OnShowTracksListener showTracksListener;

    public static AlbumsFragment create(OnShowTracksListener onShowTracksListener) {
        AlbumsFragment f = new AlbumsFragment();
        f.showTracksListener = onShowTracksListener;
        return f;
    }

    public AlbumsFragment() {
        App.getApp().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_albums, null);
        ButterKnife.bind(this, root);
        adapter = new AlbumsAdapter(getActivity(), null);
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

        Album album = (Album) adapter.getItem(i);
        if (album != null) {
            showTracksListener.onShowTracks(tracksRepo.getAlbumTracks(album.artist, album.name).toList());
        }
    }

    private void startLoading() {
        cancelLoading();

        listView.setVisibility(View.GONE);
        pbProgress.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.GONE);

        subscription = tracksRepo.getAllAlbums()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((albums) -> {
                    if (albums != null && !albums.isEmpty()) {
                        adapter.setAlbums(albums);
                        listView.setVisibility(View.VISIBLE);
                        pbProgress.setVisibility(View.GONE);
                        tvMessage.setVisibility(View.GONE);
                    } else {
                        listView.setVisibility(View.GONE);
                        pbProgress.setVisibility(View.GONE);
                        tvMessage.setText(R.string.tracks_repo_no_albums);
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
