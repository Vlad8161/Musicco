package mmss.musicco.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mmss.musicco.R;
import mmss.musicco.dataobjects.Track;

/**
 * Created by User on 14.01.2017.
 */

public class TracksAdapter extends BaseAdapter {
    private List<Track> tracks;
    private Context context;

    public TracksAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        this.notifyDataSetChanged();
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Track track = tracks.get(position);

        holder.tvArtist.setText(track.artist != null ? track.artist : context.getString(R.string.tracks_repo_artist_unknown));
        holder.tvName.setText(track.name != null ? track.name : context.getString(R.string.tracks_repo_track_unknown));

        return convertView;
    }

    private class ViewHolder {
        TextView tvArtist;
        TextView tvName;
    }
}
