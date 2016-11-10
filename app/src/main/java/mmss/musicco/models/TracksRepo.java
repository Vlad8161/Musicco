package mmss.musicco.models;

import android.content.Context;

import java.util.List;

import mmss.musicco.DatabaseHelper;
import mmss.musicco.dataobjects.Album;
import mmss.musicco.dataobjects.Artist;
import mmss.musicco.dataobjects.Track;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by User on 12.10.2016.
 */

public class TracksRepo {
    private Context mContext;

    public TracksRepo(Context context) {
        this.mContext = context;
    }

    public Observable<List<Track>> getAllTracks() {
        return Observable.fromCallable(() -> DatabaseHelper.getAllTracks(mContext))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Album>> getAllAlbums(String artist) {
        return Observable.fromCallable(() -> DatabaseHelper.getAllAlbums(mContext, artist))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Artist>> getAllActors() {
        return Observable.fromCallable(() -> DatabaseHelper.getAllArtist(mContext))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Track>> getArtistTracks(String artist) {
        return Observable.fromCallable(() -> DatabaseHelper.getArtistTracks(mContext, artist))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Track>> getAlbumTracks(String artist, String album) {
        return Observable.fromCallable(() -> DatabaseHelper.getAlbumTracks(mContext, artist, album))
                .subscribeOn(Schedulers.io());
    }
}
