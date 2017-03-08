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
import mmss.musicco.dataobjects.Album;

/**
 * Created by Lenovo on 03.03.2017.
 */

public class AlbumsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Album> mAlbums;

    public AlbumsAdapter(Context context, List<Album> albums) {
        this.mContext = context;
        this.mAlbums = albums;
    }

    public void setAlbums(List<Album> albums) {
        this.mAlbums = albums;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mAlbums != null) {
            return mAlbums.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        if (mAlbums != null && i >= 0 && i < mAlbums.size()) {
            return mAlbums.get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (mAlbums == null || i < 0 || i >= mAlbums.size()) {
            return null;
        }

        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.fragment_albums_lv_item, null);
            holder = new ViewHolder();
            holder.tvAlbum = (TextView) view.findViewById(R.id.fragment_albums_lv_item_tv_album);
            holder.tvTracksCount = (TextView) view.findViewById(R.id.fragment_albums_lv_item_tv_count);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Album album = mAlbums.get(i);
        String albumText = album.artist != null
                ? album.artist
                : mContext.getString(R.string.tracks_repo_artist_unknown);
        albumText += " - ";
        albumText += album.name != null
                ? album.name
                : mContext.getString(R.string.tracks_repo_album_unknown);
        holder.tvAlbum.setText(albumText);
        holder.tvTracksCount.setText(Integer.toString(album.tracksCount));

        return view;
    }

    private static class ViewHolder {
        TextView tvAlbum;
        TextView tvTracksCount;
    }
}
