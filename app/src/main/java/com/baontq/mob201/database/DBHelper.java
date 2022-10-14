package com.baontq.mob201.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mob201_db";
    public static final String RECENT_SONG_TABLE_NAME = "tbl_recent_song";
    public static final int DATABASE_VERSION = 1;
    private static final String RECENT_SONG_TABLE_CREATE = "CREATE TABLE " + RECENT_SONG_TABLE_NAME + " (" +
            "song_id INTEGER PRIMARY KEY," +
            "song_title TEXT UNIQUE NOT NULL," +
            "song_track_number INTEGER," +
            "song_year INTEGER," +
            "song_duration INTEGER NOT NULL," +
            "song_data TEXT NOT NULL," +
            "song_date_modified INTEGER," +
            "song_album_id INTEGER," +
            "song_album_name TEXT," +
            "song_artist_id INTEGER," +
            "song_artist_name TEXT," +
            "song_play_time INTEGER NOT NULL);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RECENT_SONG_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE tbl_recent_song");
    }
}
