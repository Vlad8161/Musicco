package mmss.musicco.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mmss.musicco.R;
import mmss.musicco.core.MusiccoPlayer;
import mmss.musicco.dataobjects.Track;

/**
 * Created by User on 14.01.2017.
 */

public class TracksAdapter extends BaseAdapter implements View.OnClickListener {
    private List<Track> tracks;
    private Context context;
    private MusiccoPlayer musiccoPlayer;
    private String currentPlayingTrackUrl;
    private OnAddToPlaylistListener onAddToPlaylistListener;

    public TracksAdapter(Context context, MusiccoPlayer musiccoPlayer) {
        this.context = context;
        this.musiccoPlayer = musiccoPlayer;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        this.notifyDataSetChanged();
    }

    public void setOnAddToPlaylistListener(OnAddToPlaylistListener listener) {
        this.onAddToPlaylistListener = listener;
    }

    public void onTrackChanged(Track track) {
        if (tracks == null) {
            return;
        }

        if (track != null && track.url != null) {
            currentPlayingTrackUrl = track.url;
        } else {
            currentPlayingTrackUrl = null;
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (tracks != null) {
            return tracks.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (tracks != null) {
            return tracks.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (tracks == null || position >= tracks.size()) {
            return null;
        }

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_tracks_lv_item, null);
            holder = new ViewHolder();
            holder.tvArtist = (TextView) convertView.findViewById(R.id.fragment_tracks_lv_item_tv_artist);
            holder.tvName = (TextView) convertView.findViewById(R.id.fragment_tracks_lv_item_tv_name);
            holder.ivPlayState = (ImageView) convertView.findViewById(R.id.fragment_tracks_lv_item_image_view);
            holder.btnAddToPlaylist = (ImageView) convertView.findViewById(R.id.fragment_tracks_lv_item_btn_add_to_playlist);
            holder.btnAddToPlaylist.setOnClickListener(this);
            convertView.setTag(holder); } else { holder = (ViewHolder) convertView.getTag();
        }

        Track track = tracks.get(position);

        holder.track = track;
        holder.tvArtist.setText(track.artist != null ? track.artist : context.getString(R.string.tracks_repo_artist_unknown));
        holder.tvName.setText(track.name != null ? track.name : context.getString(R.string.tracks_repo_track_unknown));
        if (currentPlayingTrackUrl != null && track.url != null && currentPlayingTrackUrl.equals(track.url)) {
            if (musiccoPlayer.getState() == MusiccoPlayer.STATE_PLAYING) {
                holder.ivPlayState.setImageResource(R.drawable.ic_pause);
            } else {
                holder.ivPlayState.setImageResource(R.drawable.ic_play);
            }
        } else {
            holder.ivPlayState.setImageResource(R.drawable.ic_play);
        }

        holder.btnAddToPlaylist.setTag(holder);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (onAddToPlaylistListener == null) {
            return;
        }

        ViewHolder holder = (ViewHolder) v.getTag();
        onAddToPlaylistListener.onAddToPlaylist(holder.track.url);
    }

    private class ViewHolder {
        TextView tvArtist;
        TextView tvName;
        ImageView ivPlayState;
        ImageView btnAddToPlaylist;
        Track track;
    }

    public interface OnAddToPlaylistListener {
        void onAddToPlaylist(String trackUrl);
    }
}
