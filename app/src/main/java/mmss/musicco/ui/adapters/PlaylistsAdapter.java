package mmss.musicco.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mmss.musicco.R;
import mmss.musicco.dao.Playlist;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by vlad on 6/5/17.
 */

public class PlaylistsAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private List<Playlist> mData;
    private Context mContext;
    private PublishSubject<Integer> mItemClickSubject = PublishSubject.create();

    public PlaylistsAdapter(Context context) {
        this.mContext = context;
    }

    public List<Playlist> getData() {
        return mData;
    }

    public void setData(List<Playlist> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_playlists_lv_item, null);
        v.setOnClickListener(this);
        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new PlaylistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PlaylistViewHolder) {
            PlaylistViewHolder playlistViewHolder = (PlaylistViewHolder) holder;
            Playlist item = mData.get(position);
            playlistViewHolder.tvPlaylistName.setText(item.getName());
            playlistViewHolder.tvCount.setText(Integer.toString(item.tracksCount));
            playlistViewHolder.root.setTag(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (mData != null) {
            return mData.get(position).getId();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    public Observable<Integer> onItemClickObservable() {
        return mItemClickSubject;
    }

    @Override
    public void onClick(View v) {
        mItemClickSubject.onNext((Integer) v.getTag());
    }

    private static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaylistName;
        TextView tvCount;
        View root;

        PlaylistViewHolder(View itemView) {
            super(itemView);
            this.tvPlaylistName = (TextView) itemView.findViewById(R.id.fragment_playlists_lv_item_tv_playlist);
            this.tvCount = (TextView) itemView.findViewById(R.id.fragment_playlists_lv_item_tv_count);
            this.root = itemView;
        }
    }
}
