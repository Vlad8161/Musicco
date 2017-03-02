package mmss.musicco.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mmss.musicco.R;
import mmss.musicco.dataobjects.Artist;

/**
 * Created by vlad on 3/2/17.
 */

public class ArtistsAdapter extends BaseAdapter {
    private List<Artist> mArtists;
    private Context mContext;

    public ArtistsAdapter(Context context, List<Artist> artists) {
        this.mArtists = artists;
        this.mContext = context;
    }

    public void setArtists(List<Artist> artists) {
        this.mArtists = artists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mArtists != null) {
            return mArtists.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        if (mArtists != null && i >= 0 && i < mArtists.size()) {
            return mArtists.get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return 1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (mArtists == null || i < 0 || i >= mArtists.size()) {
            return null;
        }
        Artist artist = mArtists.get(i);

        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.fragment_artists_lv_item, null);
            holder = new ViewHolder();
            holder.tvArtistName = (TextView) view.findViewById(R.id.fragment_artists_lv_item_tv_artist_name);
            holder.tvTracksCount = (TextView) view.findViewById(R.id.fragment_artists_lv_item_tv_tracks_count);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvArtistName.setText(artist.name != null ? artist.name : mContext.getString(R.string.tracks_repo_artist_unknown));
        holder.tvTracksCount.setText(Integer.toString(artist.tracksCount));

        return view;
    }

    private static class ViewHolder {
        TextView tvArtistName;
        TextView tvTracksCount;
    }
}
