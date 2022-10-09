package com.baontq.mob201.ui.home.tab.feature;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.baontq.mob201.model.Playlist;
import com.baontq.mob201.until.TaskRunner;

import java.util.List;
import java.util.concurrent.Callable;

public class HomePageViewModel extends ViewModel {
    private MutableLiveData<List<Playlist>> mPlayLists;
    private TaskRunner taskRunner;

    public HomePageViewModel() {
        taskRunner = new TaskRunner();
        this.mPlayLists = new MutableLiveData<>();
        taskRunner.execute(new Callable<List<Playlist>>() {
            @Override
            public List<Playlist> call() throws Exception {
                return null;
            }
        }, new TaskRunner.Callback<List<Playlist>>() {
            @Override
            public void onComplete(List<Playlist> result) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public LiveData<List<Playlist>> getPlayLists() {
        return mPlayLists;
    }
}