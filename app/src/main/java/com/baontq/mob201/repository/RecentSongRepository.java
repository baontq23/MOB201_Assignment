package com.baontq.mob201.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.baontq.mob201.model.Song;

import java.util.ArrayList;

public class RecentSongRepository {
    private static final RecentSongRepository INSTANCE = new RecentSongRepository();
    private final MediatorLiveData<ArrayList<Song>> mData = new MediatorLiveData<>();
    public static RecentSongRepository getInstance() {
        return INSTANCE;
    }

    public LiveData<ArrayList<Song>> getData() {
        return mData;
    }

    public void addDataSource(LiveData<ArrayList<Song>> data) {
        mData.addSource(data, mData::setValue);
    }

    public void removeDataSource(LiveData<ArrayList<Song>> data) {
        mData.removeSource(data);
    }
}
