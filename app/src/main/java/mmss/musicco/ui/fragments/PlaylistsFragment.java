package mmss.musicco.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
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
import butterknife.OnClick;
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.core.TracksRepo;
import mmss.musicco.ui.activities.AddPlaylistActivity;
import mmss.musicco.ui.activities.OnShowTracksListener;
import mmss.musicco.ui.adapters.PlaylistsAdapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by vlad on 6/5/17.
 */

public class PlaylistsFragment extends Fragment implements AdapterView.OnItemClickListener {
    @Inject
    public TracksRepo mRepo;

    @BindView(R.id.fragment_playlists_list_view)
    public ListView mLvPlaylists;

    @BindView(R.id.fragment_playlists_progress)
    public View mPbProgress;

    @BindView(R.id.fragment_playlists_tv_message)
    public TextView mTvErrorMessage;

    private PlaylistsAdapter mAdapter;
    private Subscription mSubscription;
    private OnShowTracksListener mShowTracksListener;

    public static PlaylistsFragment create(OnShowTracksListener listener) {
        PlaylistsFragment f = new PlaylistsFragment();
        f.mShowTracksListener = listener;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlists, null);
        ButterKnife.bind(this, root);
        App.getApp().inject(this);
        mAdapter = new PlaylistsAdapter(getActivity());
        mLvPlaylists.setAdapter(mAdapter);
        mLvPlaylists.setOnItemClickListener(this);
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

    @OnClick(R.id.fragment_playlists_btn_add_playlist)
    public void onBtnAddPlaylistClick(View v) {
        Intent intent = new Intent(getActivity(), AddPlaylistActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mShowTracksListener == null) {
            return;
        }

        mShowTracksListener.onShowTracks(mRepo.getPlaylistTracks(id).toList());
    }

    private void startLoading() {
        cancelLoading();
        mLvPlaylists.setVisibility(View.GONE);
        mPbProgress.setVisibility(View.VISIBLE);
        mTvErrorMessage.setVisibility(View.GONE);

        mSubscription = mRepo.getAllPlaylists()
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((playlists) -> {
                            if (playlists != null && playlists.size() > 0) {
                                mAdapter.setData(playlists);
                                mLvPlaylists.setVisibility(View.VISIBLE);
                                mPbProgress.setVisibility(View.GONE);
                                mTvErrorMessage.setVisibility(View.GONE);
                            } else {
                                mTvErrorMessage.setText(R.string.framgent_playlists_empty_text);
                                mLvPlaylists.setVisibility(View.GONE);
                                mPbProgress.setVisibility(View.GONE);
                                mTvErrorMessage.setVisibility(View.VISIBLE);
                            }
                        }
                        , (throwable -> {
                            mTvErrorMessage.setText(R.string.fragment_playlists_error_text);
                            mLvPlaylists.setVisibility(View.GONE);
                            mPbProgress.setVisibility(View.GONE);
                            mTvErrorMessage.setVisibility(View.VISIBLE);
                        }));
    }

    private void cancelLoading() {
        if (mSubscription != null) {
            if (!mSubscription.isUnsubscribed()) {
                mSubscription.unsubscribe();
            }

            mSubscription = null;
        }

        mTvErrorMessage.setText(R.string.fragment_playlists_error_text);
        mLvPlaylists.setVisibility(View.GONE);
        mPbProgress.setVisibility(View.GONE);
        mTvErrorMessage.setVisibility(View.VISIBLE);
    }
}
