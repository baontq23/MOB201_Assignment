package com.baontq.mob201.ui.home.tab.feature;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.mob201.R;
import com.baontq.mob201.databinding.FragmentHomePageBinding;
import com.baontq.mob201.model.Playlist;
import com.baontq.mob201.model.Song;
import com.baontq.mob201.receiver.RecentSongReceiver;
import com.baontq.mob201.repository.RecentSongRepository;
import com.baontq.mob201.service.SongService;
import com.baontq.mob201.ui.home.adapter.PlaylistAdapter;
import com.baontq.mob201.ui.home.adapter.RecentSongAdapter;
import com.baontq.mob201.ui.home.adapter.SongAdapter;
import com.baontq.mob201.ui.home.intefaces.SongItemAction;
import com.baontq.mob201.until.PlaylistSongLoader;
import com.baontq.mob201.until.TaskRunner;
import com.kongzue.dialogx.dialogs.PopTip;

import java.util.List;
import java.util.concurrent.Callable;

public class HomePageFragment extends Fragment implements SongItemAction {

    private HomePageViewModel mViewModel;
    private FragmentHomePageBinding binding;
    private PlaylistAdapter playlistAdapter;
    private TaskRunner taskRunner;
    private RecentSongReceiver recentSongReceiver;
    private RecentSongAdapter songAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recentSongReceiver = new RecentSongReceiver();
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SongService.ACTION_GET_LIST_RECENT_SONG);
        intentFilter.addAction(SongService.ACTION_ADD_RECENT_SONG);
        RecentSongRepository.getInstance().addDataSource(recentSongReceiver.getData());
        requireActivity().registerReceiver(recentSongReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        RecentSongRepository.getInstance().removeDataSource(recentSongReceiver.getData());
        requireActivity().unregisterReceiver(recentSongReceiver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomePageBinding.inflate(inflater, container, false);
        taskRunner = new TaskRunner();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        taskRunner.execute(new Callable<List<Playlist>>() {
            @Override
            public List<Playlist> call() throws Exception {
                return PlaylistSongLoader.getAllPlaylist(getActivity());
            }
        }, new TaskRunner.Callback<List<Playlist>>() {
            @Override
            public void onComplete(List<Playlist> result) {
                playlistAdapter = new PlaylistAdapter(result);
                binding.rvFeaturePlaylist.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                binding.rvFeaturePlaylist.setAdapter(playlistAdapter);
                if (result.size() != 0) binding.tvListStatus.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

        });
        SongService.getListRecentSongSong(requireActivity());
        mViewModel.getListRecentSong().observe(getViewLifecycleOwner(), recentSongs -> {
            if (recentSongs != null && recentSongs.size() != 0) {
                songAdapter = new RecentSongAdapter(recentSongs, this);
                binding.rvListRecentSong.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                binding.rvListRecentSong.setAdapter(songAdapter);
                binding.tvListRecentStatus.setVisibility(View.GONE);
            } else {
                binding.tvListRecentStatus.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showMoreAction(int position, Song song) {

    }

    @Override
    public void setOnItemClickListener(int position, Song song) {
        songAdapter.notifyItemMoved(position, 0);
        songAdapter.notifyItemRangeChanged(0,4);
    }
}

