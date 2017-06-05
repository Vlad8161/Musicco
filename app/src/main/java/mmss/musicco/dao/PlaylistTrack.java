package mmss.musicco.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

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
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 2122428369)
    private transient PlaylistTrackDao myDao;
    @Generated(hash = 1010486704)
    private transient Long playlist__resolvedKey;

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

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1050268664)
    public Playlist getPlaylist() {
        long __key = this.playlistId;
        if (playlist__resolvedKey == null || !playlist__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PlaylistDao targetDao = daoSession.getPlaylistDao();
            Playlist playlistNew = targetDao.load(__key);
            synchronized (this) {
                playlist = playlistNew;
                playlist__resolvedKey = __key;
            }
        }
        return playlist;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1428398988)
    public void setPlaylist(@NotNull Playlist playlist) {
        if (playlist == null) {
            throw new DaoException(
                    "To-one property 'playlistId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.playlist = playlist;
            playlistId = playlist.getId();
            playlist__resolvedKey = playlistId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1077591959)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPlaylistTrackDao() : null;
    }
}
