package com.baontq.mob201.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.baontq.mob201.model.Song;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kongzue.dialogx.dialogs.PopTip;

import java.util.ArrayList;


public class SongService extends IntentService {

    public static final String ACTION_GET_LIST_FAVORITE_SONG = "com.baontq.mob201.service.action.GET_LIST_FAVORITE_SONG";
    public static final String ACTION_ADD_FAVORITE_SONG = "com.baontq.mob201.service.action.ADD_FAVORITE_SONG";

    public static final String PARAM_EMAIL = "user_email";
    public static final String PARAM_SONG = "song_request";
    public static final String RESULT_LIST_FAVORITE_SONG = "list_favorite_song_data";
    private static final String TAG = "SongService";
    private FirebaseFirestore db;

    public SongService() {
        super("SongService");
    }


    public static void getFavoriteSong(Context context, String email) {
        Intent intent = new Intent(context, SongService.class);
        intent.setAction(ACTION_GET_LIST_FAVORITE_SONG);
        intent.putExtra(PARAM_EMAIL, email);
        context.startService(intent);
    }

    public static void addFavoriteSong(Context context, String email, Song song) {
        Intent intent = new Intent(context, SongService.class);
        intent.setAction(ACTION_ADD_FAVORITE_SONG);
        intent.putExtra(PARAM_EMAIL, email);
        intent.putExtra(PARAM_SONG, song);
        context.startService(intent);
        getFavoriteSong(context, email);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        db = FirebaseFirestore.getInstance();
        if (intent != null) {
            if (intent.getAction().equalsIgnoreCase(ACTION_GET_LIST_FAVORITE_SONG)) {
                handleGetFavoriteSongAction(intent);
            } else if (intent.getAction().equalsIgnoreCase(ACTION_ADD_FAVORITE_SONG)) {
                handleAddFavoriteSongAction(intent);
            }
        }
    }

    private void handleGetFavoriteSongAction(Intent intent) {
        Intent broadcastIntent = new Intent();

        broadcastIntent.setAction(ACTION_GET_LIST_FAVORITE_SONG);
        db.collection("user").document(intent.getStringExtra(PARAM_EMAIL))
                .collection("favorite_song").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        ArrayList<Song> favoriteSongs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            favoriteSongs.add(document.toObject(Song.class));
                        }
                        broadcastIntent.putParcelableArrayListExtra(RESULT_LIST_FAVORITE_SONG, favoriteSongs);
                        sendBroadcast(broadcastIntent);

                    }
                });
    }

    private void handleAddFavoriteSongAction(Intent intent) {
        Song song = intent.getParcelableExtra(PARAM_SONG);
        db.collection("user").document(intent.getStringExtra(PARAM_EMAIL))
                .collection("favorite_song").document(String.valueOf(song.getId()))
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        PopTip.show("Bài hát đã tồn tại trong danh sách yêu thích!");
                    } else {
                        db.collection("user").document(intent.getStringExtra(PARAM_EMAIL))
                                .collection("favorite_song").document(String.valueOf(song.getId()))
                                .set(song.getMap())
                                .addOnSuccessListener(unused -> {
                                    PopTip.show("Đã lưu vào danh sách yêu thích!");
                                })
                                .addOnFailureListener(Throwable::printStackTrace);
                    }
                });


    }

}