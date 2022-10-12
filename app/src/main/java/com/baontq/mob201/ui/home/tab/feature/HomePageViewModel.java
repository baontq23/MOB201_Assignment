package com.baontq.mob201.ui.home.tab.feature;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.baontq.mob201.model.Playlist;
import com.baontq.mob201.model.Song;
import com.baontq.mob201.repository.RecentSongRepository;
import com.baontq.mob201.until.TaskRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class HomePageViewModel extends ViewModel {
    public LiveData<ArrayList<Song>> getListRecentSong() {
        return RecentSongRepository.getInstance().getData();
    }

    public HomePageViewModel() {
    }
}