package com.baontq.mob201.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.baontq.mob201.database.DBHelper;
import com.baontq.mob201.model.Song;

import java.util.ArrayList;

public class SongDAO {
    private SQLiteDatabase db;

    public SongDAO(Context context) {
        if (db == null)
            db = new DBHelper(context).getWritableDatabase();
    }

    public ArrayList<Song> getListRecentSong() {
        ArrayList<Song> list = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.RECENT_SONG_TABLE_NAME, null, null, null, null, null, "song_play_time DESC", "5");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Song(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                        cursor.getInt(3), cursor.getLong(4), cursor.getString(5), cursor.getLong(6), cursor.getInt(7),
                        cursor.getString(8), cursor.getInt(9), cursor.getString(10)));
            }
        }
        if (cursor != null) cursor.close();
        return list;
    }

    public long storeRecentSong(Song song) {
        ContentValues values = new ContentValues();
        values.put("song_id", song.getId());
        values.put("song_title", song.getTitle());
        values.put("song_track_number", song.getTrackNumber());
        values.put("song_year", song.getYear());
        values.put("song_duration", song.getDuration());
        values.put("song_data", song.getData());
        values.put("song_date_modified", song.getDateModified());
        values.put("song_album_id", song.getAlbumId());
        values.put("song_album_name", song.getAlbumName());
        values.put("song_artist_id", song.getArtistId());
        values.put("song_artist_name", song.getArtistName());
        values.put("song_play_time", System.currentTimeMillis());
        long insertResult = db.insertWithOnConflict(DBHelper.RECENT_SONG_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (insertResult == 0) {
            Log.d("Dao", "storeRecentSong: Replace");
            return db.replace(DBHelper.RECENT_SONG_TABLE_NAME, null, values);
        }
        return insertResult;
    }

    public int deleteRecentSongById(int id) {
        return db.delete(DBHelper.RECENT_SONG_TABLE_NAME, "song_id = ?", new String[]{String.valueOf(id)});
    }
}
