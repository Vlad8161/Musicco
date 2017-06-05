package mmss.musicco.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mmss.musicco.R;
import mmss.musicco.dao.Playlist;

/**
 * Created by vlad on 6/5/17.
 */

public class PlaylistsAdapter extends BaseAdapter {
    private List<Playlist> mData;
    private Context mContext;

    public PlaylistsAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Playlist> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData != null) {
            mData.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mData != null) {
            return mData.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (mData != null) {
            mData.get(position).getId();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_playlists_lv_item, null);
            holder = new ViewHolder();
            holder.tvPlaylistName = (TextView) convertView.findViewById(R.id.fragment_playlists_lv_item_tv_playlist);
            holder.tvCount = (TextView) convertView.findViewById(R.id.fragment_playlists_lv_item_tv_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Playlist item = mData.get(position);
        holder.tvPlaylistName.setText(item.getName());
        holder.tvCount.setText(Integer.toString(item.tracksCount));
        return convertView;
    }

    private static class ViewHolder {
        TextView tvPlaylistName;
        TextView tvCount;
    }
}
