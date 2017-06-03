package mmss.musicco.models;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public Observable<List<Album>> getAllAlbums() {
        return Observable.fromCallable(() -> {
            List<Album> retVal = new ArrayList<>();

            if (!isExternalStorageReadable()) {
                return retVal;
            }

            File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            if (musicDir == null) {
                return retVal;
            }

            if (musicDir.exists()) {
                if (!musicDir.isDirectory()) {
                    return retVal;
                }
            } else {
                if (!musicDir.mkdir()) {
                    return retVal;
                }
            }

            File[] filesList = musicDir.listFiles();
            if (filesList == null) {
                return retVal;
            }

            for (File f : musicDir.listFiles()) {
                if (f.isDirectory()) {
                    continue;
                }

                if (!f.getName().endsWith(".mp3")) {
                    continue;
                }

                Track track = extractTrack(f);
                if (track == null) {
                    continue;
                }

                boolean found = false;
                for (Album album : retVal) {

                    if ((album.artist != null && track.artist == null) ||
                            (album.artist == null && track.artist != null)) {
                        continue;
                    }

                    if (album.artist != null && track.artist != null && !album.artist.equals(track.artist)) {
                        continue;
                    }

                    if ((album.name != null && track.album == null) ||
                            (album.name == null && track.album != null)) {
                        continue;
                    }

                    if (album.name != null && track.album != null && !album.name.equals(track.album)) {
                        continue;
                    }

                    album.tracksCount++;
                    found = true;
                }

                if (!found) {
                    Album album = new Album(track.album, track.artist, 1);
                    retVal.add(album);
                }
            }

            return retVal;
        })
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Artist>> getAllArtists() {
        return Observable.fromCallable(() -> {
            List<Artist> retVal = new ArrayList<>();

            if (!isExternalStorageReadable()) {
                return retVal;
            }

            File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            if (musicDir == null) {
                return retVal;
            }

            if (musicDir.exists()) {
                if (!musicDir.isDirectory()) {
                    return retVal;
                }
            } else {
                if (!musicDir.mkdir()) {
                    return retVal;
                }
            }

            File[] filesList = musicDir.listFiles();
            if (filesList == null) {
                return retVal;
            }

            for (File f : musicDir.listFiles()) {
                if (f.isDirectory()) {
                    continue;
                }

                if (!f.getName().endsWith(".mp3")) {
                    continue;
                }

                Track track = extractTrack(f);
                if (track == null) {
                    continue;
                }

                boolean found = false;
                for (Artist artist : retVal) {
                    if (track.artist == null && artist.name == null) {
                        artist.tracksCount++;
                        found = true;
                    } else if (track.artist != null && artist.name != null && artist.name.equals(track.artist)) {
                        artist.tracksCount++;
                        found = true;
                    }
                }

                if (!found) {
                    Artist artist = new Artist(track.artist, 1);
                    retVal.add(artist);
                }
            }

            return retVal;
        })
                .subscribeOn(Schedulers.io());
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
}
