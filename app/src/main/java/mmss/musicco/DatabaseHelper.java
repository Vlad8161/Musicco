package mmss.musicco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import mmss.musicco.dataobjects.Album;
import mmss.musicco.dataobjects.Artist;
import mmss.musicco.dataobjects.Track;

/**
 * Created by User on 09.11.2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "musicco.db";

    public static final String TABLE_TRACKS = "track";
    public static final String TABLE_TRACKS_COL_ID = "id";
    public static final String TABLE_TRACKS_COL_NAME = "name";
    public static final String TABLE_TRACKS_COL_ARTIST = "artist";
    public static final String TABLE_TRACKS_COL_ALBUM = "album";
    public static final String TABLE_TRACKS_COL_SIZE = "size";
    public static final String TABLE_TRACKS_COL_LENGTH = "length";
    public static final String TABLE_TRACKS_COL_URL = "url";
    public static final String TABLE_TRACKS_COL_LOCAL_URL = "local_url";

    public static final String QUERY_CREATE_TABLE_TRACKS = "CREATE TABLE IF NOT EXISTS " + TABLE_TRACKS + " (" +
            TABLE_TRACKS_COL_ID + " INTEGER PRIMARY KEY, " +
            TABLE_TRACKS_COL_NAME + " TEXT, " +
            TABLE_TRACKS_COL_ALBUM + " TEXT, " +
            TABLE_TRACKS_COL_ARTIST + " TEXT, " +
            TABLE_TRACKS_COL_SIZE + " INTEGER, " +
            TABLE_TRACKS_COL_LENGTH + " INTEGER, " +
            TABLE_TRACKS_COL_URL + " TEXT, " +
            TABLE_TRACKS_COL_LOCAL_URL + " TEXT)";
    public static final String QUERY_DROP_TABLE_TRACKS = "DROP TABLE IF EXIST " + TABLE_TRACKS;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public static List<Track> getAllTracks(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Track> tracks = new ArrayList<>();

        if (db == null) {
            return tracks;
        }

        try {
            Cursor c = db.query(TABLE_TRACKS,
                    new String[]{
                            TABLE_TRACKS_COL_ID,
                            TABLE_TRACKS_COL_NAME,
                            TABLE_TRACKS_COL_ALBUM,
                            TABLE_TRACKS_COL_ARTIST,
                            TABLE_TRACKS_COL_LENGTH,
                            TABLE_TRACKS_COL_SIZE,
                            TABLE_TRACKS_COL_URL,
                            TABLE_TRACKS_COL_LOCAL_URL,
                    }, null, null, null, null, TABLE_TRACKS_COL_NAME);

            if (c == null) {
                return tracks;
            }

            try {
                while (c.moveToNext()) {
                    Track track = new Track();
                    track.id = c.getInt(0);
                    track.name = c.getString(1);
                    track.album = c.getString(2);
                    track.artist = c.getString(3);
                    track.length = c.getInt(4);
                    track.size = c.getInt(5);
                    track.url = c.getString(6);
                    track.localUrl = c.getString(7);
                    track.isLocal = track.localUrl != null;
                    tracks.add(track);
                }
                return tracks;
            } finally {
                c.close();
            }
        } finally {
            db.close();
        }
    }

    public static List<Track> getArtistTracks(Context context, String artist) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Track> tracks = new ArrayList<>();

        if (db == null) {
            return tracks;
        }

        try {
            Cursor c = db.query(TABLE_TRACKS,
                    new String[]{
                            TABLE_TRACKS_COL_ID,
                            TABLE_TRACKS_COL_NAME,
                            TABLE_TRACKS_COL_ALBUM,
                            TABLE_TRACKS_COL_ARTIST,
                            TABLE_TRACKS_COL_LENGTH,
                            TABLE_TRACKS_COL_SIZE,
                            TABLE_TRACKS_COL_URL,
                            TABLE_TRACKS_COL_LOCAL_URL,
                    }, TABLE_TRACKS_COL_ARTIST + " = '" + artist + "'",
                    null, null, null, TABLE_TRACKS_COL_NAME);

            if (c == null) {
                return tracks;
            }

            try {
                while (c.moveToNext()) {
                    Track track = new Track();
                    track.id = c.getInt(0);
                    track.name = c.getString(1);
                    track.album = c.getString(2);
                    track.artist = c.getString(3);
                    track.length = c.getInt(4);
                    track.size = c.getInt(5);
                    track.url = c.getString(6);
                    track.localUrl = c.getString(7);
                    track.isLocal = track.localUrl != null;
                    tracks.add(track);
                }
                return tracks;
            } finally {
                c.close();
            }
        } finally {
            db.close();
        }
    }

    public static List<Track> getAlbumTracks(Context context, String artist, String album) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Track> tracks = new ArrayList<>();

        if (db == null) {
            return tracks;
        }

        try {
            Cursor c = db.query(TABLE_TRACKS,
                    new String[]{
                            TABLE_TRACKS_COL_ID,
                            TABLE_TRACKS_COL_NAME,
                            TABLE_TRACKS_COL_ALBUM,
                            TABLE_TRACKS_COL_ARTIST,
                            TABLE_TRACKS_COL_LENGTH,
                            TABLE_TRACKS_COL_SIZE,
                            TABLE_TRACKS_COL_URL,
                            TABLE_TRACKS_COL_LOCAL_URL,
                    },
                    TABLE_TRACKS_COL_ARTIST + " = '" + artist + "' AND " +
                            TABLE_TRACKS_COL_ALBUM + " = '" + album + "'",
                    null, null, null, TABLE_TRACKS_COL_NAME);
            if (c == null) {
                return tracks;
            }

            try {
                while (c.moveToNext()) {
                    Track track = new Track();
                    track.id = c.getInt(0);
                    track.name = c.getString(1);
                    track.album = c.getString(2);
                    track.artist = c.getString(3);
                    track.length = c.getInt(4);
                    track.size = c.getInt(5);
                    track.url = c.getString(6);
                    track.localUrl = c.getString(7);
                    track.isLocal = track.localUrl != null;
                    tracks.add(track);
                }
                return tracks;
            } finally {
                c.close();
            }
        } finally {
            db.close();
        }
    }

    public static List<Album> getAllAlbums(Context context, String artist) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Album> albums = new ArrayList<>();

        if (db == null) {
            return albums;
        }

        try {
            Cursor c = db.query(TABLE_TRACKS,
                    new String[]{
                            TABLE_TRACKS_COL_ALBUM,
                            TABLE_TRACKS_COL_ARTIST,
                            "COUNT(" + TABLE_TRACKS_COL_NAME + ")"
                    }, TABLE_TRACKS_COL_ARTIST + " = '" + artist + "'",
                    null, TABLE_TRACKS_COL_ARTIST + ", " + TABLE_TRACKS_COL_ALBUM,
                    null, TABLE_TRACKS_COL_ALBUM);

            if (c == null) {
                return albums;
            }

            try {
                while (c.moveToNext()) {
                    Album album = new Album();
                    album.name = c.getString(0);
                    album.artist = c.getString(1);
                    album.tracksCount = c.getInt(2);
                    albums.add(album);
                }
                return albums;
            } finally {
                c.close();
            }
        } finally {
            db.close();
        }
    }

    public static List<Artist> getAllArtist(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Artist> artists = new ArrayList<>();

        if (db == null) {
            return artists;
        }

        try {
            Cursor c = db.query(TABLE_TRACKS,
                    new String[]{
                            TABLE_TRACKS_COL_ARTIST,
                            "COUNT(" + TABLE_TRACKS_COL_NAME + ")"
                    }, null, null, TABLE_TRACKS_COL_ARTIST,
                    null, TABLE_TRACKS_COL_ARTIST);

            if (c == null) {
                return artists;
            }

            try {
                while (c.moveToNext()) {
                    Artist artist = new Artist();
                    artist.name = c.getString(0);
                    artist.tracksCount = c.getInt(1);
                    artists.add(artist);
                }
                return artists;
            } finally {
                c.close();
            }
        } finally {
            db.close();
        }
    }

    public static void insertTrack(Context context, Track track) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        if (db == null) {
            return;
        }

        try {
            ContentValues cv = new ContentValues();
            cv.put(TABLE_TRACKS_COL_ARTIST, track.artist);
            cv.put(TABLE_TRACKS_COL_ALBUM, track.album);
            cv.put(TABLE_TRACKS_COL_NAME, track.name);
            cv.put(TABLE_TRACKS_COL_SIZE, track.size);
            cv.put(TABLE_TRACKS_COL_LENGTH, track.length);
            cv.put(TABLE_TRACKS_COL_URL, track.url);
            cv.put(TABLE_TRACKS_COL_LOCAL_URL, track.localUrl);
            db.insert(TABLE_TRACKS, null, cv);
        } finally {
            db.close();
        }
    }

    public static void removeTrack(Context context, int id) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        if (db == null) {
            return;
        }

        try {
            db.delete(TABLE_TRACKS, TABLE_TRACKS_COL_ID + " = " + id, null);
        } finally {
            db.close();
        }
    }

    public static void updateTrack(Context context, Track track) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        if (db == null) {
            return;
        }

        try {
            ContentValues cv = new ContentValues();
            cv.put(TABLE_TRACKS_COL_ARTIST, track.artist);
            cv.put(TABLE_TRACKS_COL_ALBUM, track.album);
            cv.put(TABLE_TRACKS_COL_NAME, track.name);
            cv.put(TABLE_TRACKS_COL_SIZE, track.size);
            cv.put(TABLE_TRACKS_COL_LENGTH, track.length);
            cv.put(TABLE_TRACKS_COL_URL, track.url);
            cv.put(TABLE_TRACKS_COL_LOCAL_URL, track.localUrl);
            db.update(TABLE_TRACKS, cv, TABLE_TRACKS_COL_ID + " = " + track.id, null);
        } finally {
            db.close();
        }
    }

    private static void insertTrack(SQLiteDatabase db, Track track) {
        ContentValues cv = new ContentValues();
        cv.put(TABLE_TRACKS_COL_ARTIST, track.artist);
        cv.put(TABLE_TRACKS_COL_ALBUM, track.album);
        cv.put(TABLE_TRACKS_COL_NAME, track.name);
        cv.put(TABLE_TRACKS_COL_SIZE, track.size);
        cv.put(TABLE_TRACKS_COL_LENGTH, track.length);
        cv.put(TABLE_TRACKS_COL_URL, track.url);
        cv.put(TABLE_TRACKS_COL_LOCAL_URL, track.localUrl);
        db.insert(TABLE_TRACKS, null, cv);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(QUERY_CREATE_TABLE_TRACKS);
        makeTestData(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(QUERY_DROP_TABLE_TRACKS);
        sqLiteDatabase.execSQL(QUERY_CREATE_TABLE_TRACKS);
    }

    private void makeTestData(SQLiteDatabase db) {
        Track track;

        track = new Track();
        track.id = 0;
        track.name = "name1";
        track.artist = "artist1";
        track.album = "album1";
        track.size = 10;
        track.length = 20;
        track.url = "url";
        track.localUrl = "localUrl";
        insertTrack(db, track);

        track = new Track();
        track.id = 1;
        track.name = "name2";
        track.artist = "artist1";
        track.album = "album1";
        track.size = 10;
        track.length = 20;
        track.url = "url";
        track.localUrl = "localUrl";
        insertTrack(db, track);

        track = new Track();
        track.id = 2;
        track.name = "name2";
        track.artist = "artist1";
        track.album = "album2";
        track.size = 10;
        track.length = 20;
        track.url = "url";
        track.localUrl = "localUrl";
        insertTrack(db, track);

        track = new Track();
        track.id = 3;
        track.name = "name1";
        track.artist = "artist2";
        track.album = "album1";
        track.size = 10;
        track.length = 20;
        track.url = "url";
        track.localUrl = "localUrl";
        insertTrack(db, track);

        track = new Track();
        track.id = 4;
        track.name = "name2";
        track.artist = "artist2";
        track.album = "album2";
        track.size = 10;
        track.length = 20;
        track.url = "url";
        track.localUrl = "localUrl";
        insertTrack(db, track);

        track = new Track();
        track.id = 5;
        track.name = "name3";
        track.artist = "artist2";
        track.album = "album2";
        track.size = 10;
        track.length = 20;
        track.url = "url";
        track.localUrl = "localUrl";
        insertTrack(db, track);
    }
}
