package com.baontq.mob201.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.baontq.mob201.model.Song;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FavoriteSongRepository {
    private static final String TAG = "FavoriteSongRepository";
    private static final FavoriteSongRepository ourInstance = new FavoriteSongRepository();
    private FirebaseFirestore db;
    private MutableLiveData<ArrayList<Song>> list = new MutableLiveData<>();

    public static FavoriteSongRepository getInstance() {
        return ourInstance;
    }

    private FavoriteSongRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<ArrayList<Song>> getList(String email) {
        db.collection("user").document(email)
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
                        list.setValue(favoriteSongs);
                        Log.d(TAG, "onEvent: Fetch favorite song data");
                    }
                });
        return list;
    }
}