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
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.core.MusiccoPlayer;
import mmss.musicco.dataobjects.Track;

/**
 * Created by User on 12.10.2016.
 */

public class PlayerFragment extends Fragment implements MusiccoPlayer.OnTrackChangedListener, MusiccoPlayer.OnPosChangedListener, MusiccoPlayer.OnStateChangedListener {

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

    @BindView(R.id.fragment_player_btn_stop)
    View btnStop;

    @BindView(R.id.fragment_player_seek_bar)
    SeekBar seekBar;

    @BindView(R.id.fragment_player_tv_track_name)
    TextView tvTrackName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_player, null);
        ButterKnife.bind(this, root);
        App.getApp().inject(this);
        musiccoPlayer.addOnTrackChangedListener(this);
        musiccoPlayer.addOnPosChangedListener(this);
        musiccoPlayer.addOnStateChangedListener(this);

        if (musiccoPlayer.getState() == MusiccoPlayer.STATE_PLAYING) {
            btnPlay.setVisibility(View.INVISIBLE);
            btnPause.setVisibility(View.VISIBLE);
            btnStop.setEnabled(true);
        } else if (musiccoPlayer.getState() == MusiccoPlayer.STATE_PAUSED) {
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.INVISIBLE);
            btnStop.setEnabled(true);
        } else if (musiccoPlayer.getState() == MusiccoPlayer.STATE_STOPPED) {
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.INVISIBLE);
            btnStop.setEnabled(false);
        }

        String trackName;
        String artistName;
        Track currTrack = musiccoPlayer.getCurrentTrack();
        if (currTrack != null && currTrack.name != null) {
            trackName = currTrack.name;
        } else {
            trackName = getResources().getString(R.string.fragment_player_unknown_track_text);
        }
        if (currTrack != null && currTrack.artist != null) {
            artistName = currTrack.artist;
        } else {
            artistName = getResources().getString(R.string.fragment_player_unknown_track_text);
        }
        tvTrackName.setText(artistName + " - " + trackName);

        int dur = musiccoPlayer.getDuration();
        int pos = musiccoPlayer.getCurrentPosition();
        seekBar.setMax(dur);
        seekBar.setProgress(pos);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        musiccoPlayer.removeOnTrackChangedListener(this);
        musiccoPlayer.removeOnPosChangedListener(this);
        musiccoPlayer.removeOnStateChangedListener(this);
    }

    @Override
    public void onTrackChangedListener(Track track) {
        String trackName;
        String artistName;
        if (track != null && track.name != null) {
            trackName = track.name;
        } else {
            trackName = getResources().getString(R.string.fragment_player_unknown_track_text);
        }
        if (track != null && track.artist != null) {
            artistName = track.artist;
        } else {
            artistName = getResources().getString(R.string.fragment_player_unknown_track_text);
        }
        tvTrackName.setText(artistName + " - " + trackName);
    }

    @Override
    public void onPosChangedListener(int pos, int dur) {
        seekBar.setMax(dur);
        seekBar.setProgress(pos);
    }

    @Override
    public void onStateChangedListener(int state) {
        if (state == MusiccoPlayer.STATE_PLAYING) {
            btnPlay.setVisibility(View.INVISIBLE);
            btnPause.setVisibility(View.VISIBLE);
            btnStop.setEnabled(true);
        } else if (state == MusiccoPlayer.STATE_PAUSED) {
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.INVISIBLE);
            btnStop.setEnabled(true);
        } else if (state == MusiccoPlayer.STATE_STOPPED) {
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.INVISIBLE);
            btnStop.setEnabled(false);
        }
    }
}
