package com.baontq.mob201.until;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import com.baontq.mob201.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MusicUntil {
    private static final String TAG = "MusicUntil";
    private static final String[] BASE_PROJECTION = new String[]{
            BaseColumns._ID,// 0
            MediaStore.Audio.AudioColumns.TITLE,// 1
            MediaStore.Audio.AudioColumns.TRACK,// 2
            MediaStore.Audio.AudioColumns.YEAR,// 3
            MediaStore.Audio.AudioColumns.DURATION,// 4
            MediaStore.Audio.AudioColumns.DATA,// 5
            MediaStore.Audio.AudioColumns.DATE_MODIFIED,// 6
            MediaStore.Audio.AudioColumns.ALBUM_ID,// 7
            MediaStore.Audio.AudioColumns.ALBUM,// 8
            MediaStore.Audio.AudioColumns.ARTIST_ID,// 9
            MediaStore.Audio.AudioColumns.ARTIST,// 10
    };

    private static Song getSongFromCursorImpl(Cursor cursor) {
        final int id = cursor.getInt(0);
        final String title = cursor.getString(1);
        final int trackNumber = cursor.getInt(2);
        final int year = cursor.getInt(3);
        final long duration = cursor.getLong(4);
        final String data = cursor.getString(5);
        final long dateModified = cursor.getLong(6);
        final int albumId = cursor.getInt(7);
        final String albumName = cursor.getString(8);
        final int artistId = cursor.getInt(9);
        final String artistName = cursor.getString(10);

        return new Song(id, title, trackNumber, year, duration, data, dateModified, albumId, albumName, artistId, artistName);
    }

    public static ArrayList<Song> getListSong(final Context context) {
        ArrayList<Song> songs = new ArrayList<>();
        Cursor cursor = makeSongCursor(context);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();


        return songs;
    }

    public static List<Song> getListSongByPlayList(final Context context, final long playlistId) {
        List<Song> songs = new ArrayList<>();
        Cursor cursor = makeSongCursor(context, null, null, playlistId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return songs;
    }

    public static Cursor makeSongCursor(final Context context) {
        try {
            return context.getContentResolver().
                    query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, BASE_PROJECTION, MediaStore.Audio.AudioColumns.DURATION + " > 10000", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        } catch (Exception e) {
            Log.e(TAG, "makeAllSongCursor", e);
            return null;
        } finally {
            Log.d(TAG, "makeAllSongCursor");
        }
    }

    public static Cursor makeSongCursor(final Context context, String selection, String[] selectionValues, final long playlistId) {
        try {
            return context.getContentResolver().
                    query(MediaStore.Audio.Playlists.Members.
                            getContentUri("external", playlistId), BASE_PROJECTION, selection, selectionValues, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
        } catch (Exception e) {
            Log.e(TAG, "makeAllSongCursor", e);
            return null;
        } finally {
            Log.d(TAG, "makeAllSongCursor");
        }
    }

    public static String convertDuration(long dur) {
        int m = (int) TimeUnit.MILLISECONDS.toMinutes(dur);
        int s = (int) (TimeUnit.MILLISECONDS.toSeconds(dur) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dur)));

        return m + ":" + s;
    }

}
