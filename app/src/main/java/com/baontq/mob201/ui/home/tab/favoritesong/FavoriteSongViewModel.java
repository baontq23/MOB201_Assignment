package com.baontq.mob201.ui.home.tab.favoritesong;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.baontq.mob201.model.Song;
import com.baontq.mob201.repository.FavoriteSongRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class FavoriteSongViewModel extends ViewModel {
    private FirebaseUser user;
    private MutableLiveData<ArrayList<Song>> mList;

    public FavoriteSongViewModel() {
        super();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            mList = FavoriteSongRepository.getInstance().getList(user.getEmail());
    }

    public LiveData<ArrayList<Song>> getData() {
        return mList;
    }
}