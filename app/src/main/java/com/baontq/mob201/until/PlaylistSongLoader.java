package com.baontq.mob201.until;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.baontq.mob201.model.Playlist;

import java.util.ArrayList;
import java.util.List;


public class PlaylistSongLoader {
    private static final String TAG = "PlaylistSongUntil";

    public static Cursor makePlaylistCursor(final Context context) {
        try {
            return context.getContentResolver().query(MediaStore.Audio.Playlists.getContentUri("external"), new String[]{MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME

            }, null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
        } catch (Exception e) {
            return null;
        } finally {
            Log.d(TAG, "getPlaylistSongCursor");
        }
    }

    private static Playlist getPlayListFromCursorImpl(Cursor cursor) {
        final int id = cursor.getInt(0);
        final String name = cursor.getString(1);
        return new Playlist(id, name);
    }

    public static List<Playlist> getAllPlaylist(final Context context) {
        Cursor cursor = makePlaylistCursor(context);
        List<Playlist> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(getPlayListFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return list;
    }


}
