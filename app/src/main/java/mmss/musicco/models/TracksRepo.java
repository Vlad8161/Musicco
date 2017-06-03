package mmss.musicco.models;

import android.os.Environment;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import mmss.musicco.dataobjects.Album;
import mmss.musicco.dataobjects.Artist;
import mmss.musicco.dataobjects.Track;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by User on 12.10.2016.
 */

public class TracksRepo {
    public Observable<Track> getAllTracks() {
        if (!isExternalStorageReadable()) {
            return Observable.empty();
        }

        File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        if (musicDir == null) {
            return Observable.empty();
        }

        if (musicDir.exists()) {
            if (!musicDir.isDirectory()) {
                return Observable.empty();
            }
        } else {
            if (!musicDir.mkdir()) {
                return Observable.empty();
            }
        }

        File[] filesList = musicDir.listFiles();
        if (filesList == null) {
            return Observable.empty();
        }

        return Observable.from(Arrays.asList(musicDir.listFiles()))
                .filter((f) -> !f.isDirectory())
                .filter((f) -> f.getName().endsWith(".mp3"))
                .map(this::extractTrack)
                .filter((t) -> t != null)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Track> getArtistTracks(String artist) {
        return getAllTracks()
                .filter((t) -> t.artist == null && artist == null || t.artist != null && artist != null && t.artist.equals(artist));
    }

    public Observable<Track> getAlbumTracks(String artist, String album) {
        return getArtistTracks(artist)
                .filter((t) -> t.album == null && album == null || t.album != null && album != null && t.artist.equals(album));
    }

    public Observable<Artist> getAllArtists() {
        return getAllTracks()
                .groupBy((t) -> t.artist)
                .flatMap((obs) -> {
                    Artist accumulator = new Artist();
                    accumulator.name = obs.getKey();
                    accumulator.tracksCount = 0;
                    return obs.reduce(accumulator, (a, t) -> {
                        a.tracksCount++;
                        return a;
                    });
                });
    }

    public Observable<Album> getAllAlbums() {
        return getAllTracks()
                .groupBy((t) -> new AlbumKey(t.artist, t.album))
                .flatMap((obs) -> {
                    Album accumulator = new Album();
                    AlbumKey key = obs.getKey();
                    accumulator.artist = key.artist;
                    accumulator.name = key.album;
                    accumulator.tracksCount = 0;
                    return obs.reduce(accumulator, (a, t) -> {
                        a.tracksCount++;
                        return a;
                    });
                });
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private Track extractTrack(File f) {
        if (!f.exists()) {
            return null;
        }

        AudioFile af = null;
        try {
            af = AudioFileIO.read(f);
        } catch (CannotReadException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
            return null;
        }

        Tag tag = af.getTag();
        if (tag == null) {
            return null;
        }

        AudioHeader header = af.getAudioHeader();

        Track t = new Track();
        t.artist = tag.getFirst(FieldKey.ARTIST).equals("") ? null : tag.getFirst(FieldKey.ARTIST);
        t.album = tag.getFirst(FieldKey.ALBUM).equals("") ? null : tag.getFirst(FieldKey.ALBUM);
        t.name = tag.getFirst(FieldKey.TITLE).equals("") ? null : tag.getFirst(FieldKey.TITLE);
        try {
            t.url = "file://" + f.getCanonicalPath();
        } catch (IOException e) {
            t.url = "file://" + f.getAbsolutePath();
        }
        t.length = header.getTrackLength();
        t.size = f.length();

        return t;
    }

    private static class AlbumKey {
        String artist;
        String album;

        AlbumKey(String artist, String album) {
            this.artist = artist;
            this.album = album;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AlbumKey albumKey = (AlbumKey) o;

            if (artist != null ? !artist.equals(albumKey.artist) : albumKey.artist != null)
                return false;
            return album != null ? album.equals(albumKey.album) : albumKey.album == null;

        }

        @Override
        public int hashCode() {
            int result = artist != null ? artist.hashCode() : 0;
            result = 31 * result + (album != null ? album.hashCode() : 0);
            return result;
        }
    }
}
