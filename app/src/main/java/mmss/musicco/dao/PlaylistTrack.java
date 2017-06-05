package mmss.musicco.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

/**
 * Created by vlad on 6/5/17.
 */

@Entity
public class PlaylistTrack {
    @Id
    private Long id;
    private String url;
    private long playlistId;
    @ToOne(joinProperty = "playlistId")
    private Playlist playlist;

    @Generated(hash = 230636950)
    public PlaylistTrack(Long id, String url, long playlistId) {
        this.id = id;
        this.url = url;
        this.playlistId = playlistId;
    }

    @Generated(hash = 496806649)
    public PlaylistTrack() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
}
