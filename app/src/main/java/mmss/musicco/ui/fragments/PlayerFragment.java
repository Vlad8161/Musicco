package mmss.musicco.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.core.MusiccoPlayer;
import mmss.musicco.dataobjects.Track;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by User on 12.10.2016.
 */

public class PlayerFragment extends Fragment {
    @Inject
    MusiccoPlayer musiccoPlayer;

    @BindView(R.id.fragment_player_btn_next)
    View btnNext;

    @BindView(R.id.fragment_player_btn_prev)
    View btnPrev;

    @BindView(R.id.fragment_player_btn_play)
    View btnPlay;

    @BindView(R.id.fragment_player_btn_pause)
    View btnPause;

    @BindView(R.id.fragment_player_seek_bar)
    SeekBar seekBar;

    @BindView(R.id.fragment_player_tv_track_name)
    TextView tvTrackName;

    @BindView(R.id.fragment_player_tv_track_artist)
    TextView tvTrackArtist;

    @BindView(R.id.fragment_player_tv_curr_pos)
    TextView tvCurrPos;

    @BindView(R.id.fragment_player_tv_max_pos)
    TextView tvMaxPos;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            userMovingSeekBar = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            musiccoPlayer.seek(seekBar.getProgress());
            userMovingSeekBar = false;
        }
    };

    private boolean userMovingSeekBar = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_player, null);
        ButterKnife.bind(this, root);
        App.getApp().inject(this);

        mSubscription.add(musiccoPlayer.getTrackObservable().subscribe(this::onTrackChanged));
        mSubscription.add(musiccoPlayer.getPositionObservable().subscribe(this::onPosChanged));
        mSubscription.add(musiccoPlayer.getDurationObservable().subscribe(this::onDurChanged));
        mSubscription.add(musiccoPlayer.getStateObservable().subscribe(this::onStateChanged));
        seekBar.setOnSeekBarChangeListener(seekBarListener);

        if (musiccoPlayer.getState() == MusiccoPlayer.STATE_PLAYING) {
            seekBar.setEnabled(true);
            btnPlay.setVisibility(View.INVISIBLE);
            btnPause.setVisibility(View.VISIBLE);
        } else if (musiccoPlayer.getState() == MusiccoPlayer.STATE_PAUSED) {
            seekBar.setEnabled(true);
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.INVISIBLE);
        } else if (musiccoPlayer.getState() == MusiccoPlayer.STATE_STOPPED) {
            seekBar.setEnabled(false);
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.INVISIBLE);
        }

        String trackName;
        String artistName;
        Track currTrack = musiccoPlayer.getCurrentTrack();
        if (currTrack != null && currTrack.name != null) {
            trackName = currTrack.name;
        } else {
            trackName = getResources().getString(R.string.tracks_repo_track_unknown);
        }
        if (currTrack != null && currTrack.artist != null) {
            artistName = currTrack.artist;
        } else {
            artistName = getResources().getString(R.string.tracks_repo_track_unknown);
        }
        tvTrackName.setText(trackName);
        tvTrackArtist.setText(artistName);

        int dur = musiccoPlayer.getDuration();
        int pos = musiccoPlayer.getCurrentPosition();
        seekBar.setMax(dur);
        seekBar.setProgress(pos);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSubscription.unsubscribe();
    }

    public void onTrackChanged(Track track) {
        String trackName;
        String artistName;
        if (track != null && track.name != null) {
            trackName = track.name;
        } else {
            trackName = getResources().getString(R.string.tracks_repo_track_unknown);
        }
        if (track != null && track.artist != null) {
            artistName = track.artist;
        } else {
            artistName = getResources().getString(R.string.tracks_repo_artist_unknown);
        }
        tvTrackName.setText(trackName);
        tvTrackArtist.setText(artistName);

        Integer trackIndex = musiccoPlayer.getCurrentTrackIndex();
        int tracksCount = musiccoPlayer.getTracksCount();
        if (trackIndex != null) {
            btnNext.setEnabled(trackIndex < tracksCount - 1);
            btnPrev.setEnabled(trackIndex > 0);
        }
    }

    public void onPosChanged(int pos) {
        if (!userMovingSeekBar) {
            seekBar.setProgress(pos);
        }

        int currMins = pos / 60000;
        int currSecs = (int) ((pos / 1000f) % 60);
        tvCurrPos.setText(String.format("%02d:%02d", currMins, currSecs));
    }

    public void onDurChanged(int dur) {
        if (!userMovingSeekBar) {
            seekBar.setMax(dur);
        }

        int maxMins = dur / 60000;
        int maxSecs = (int) ((dur / 1000f) % 60);
        tvMaxPos.setText(String.format("%02d:%02d", maxMins, maxSecs));
    }

    public void onStateChanged(int state) {
        if (state == MusiccoPlayer.STATE_PLAYING) {
            seekBar.setEnabled(true);
            btnPlay.setVisibility(View.INVISIBLE);
            btnPause.setVisibility(View.VISIBLE);
        } else if (state == MusiccoPlayer.STATE_PAUSED) {
            seekBar.setEnabled(true);
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.INVISIBLE);
        } else if (state == MusiccoPlayer.STATE_STOPPED) {
            seekBar.setEnabled(false);
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.fragment_player_btn_play)
    public void onClickPlay(View v) {
        musiccoPlayer.play();
    }

    @OnClick(R.id.fragment_player_btn_pause)
    public void onClickPause() {
        musiccoPlayer.pause();
    }

    @OnClick(R.id.fragment_player_btn_next)
    public void onClickNext() {
        Integer trackIndex = musiccoPlayer.getCurrentTrackIndex();
        int tracksCount = musiccoPlayer.getTracksCount();
        if (trackIndex != null && trackIndex >= 0 && trackIndex < tracksCount - 1) {
            musiccoPlayer.nextTrack();
        }
    }

    @OnClick(R.id.fragment_player_btn_prev)
    public void onClickPrev() {
        Integer trackIndex = musiccoPlayer.getCurrentTrackIndex();
        int tracksCount = musiccoPlayer.getTracksCount();
        if (trackIndex != null && trackIndex > 0 && trackIndex < tracksCount) {
            musiccoPlayer.prevTrack();
        }
    }
}
