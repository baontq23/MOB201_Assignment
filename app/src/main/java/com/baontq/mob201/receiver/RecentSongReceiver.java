package com.baontq.mob201.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.baontq.mob201.model.Song;
import com.baontq.mob201.service.SongService;

import java.util.ArrayList;

public class RecentSongReceiver extends BroadcastReceiver {
    private final MutableLiveData<ArrayList<Song>> mData = new MutableLiveData<>();

    public LiveData<ArrayList<Song>> getData() {
        return mData;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<Song> list = intent.getParcelableArrayListExtra(SongService.RESULT_LIST_RECENT_SONG);
        mData.setValue(list);
    }
}