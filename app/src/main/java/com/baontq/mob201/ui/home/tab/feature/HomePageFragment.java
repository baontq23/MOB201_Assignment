package com.baontq.mob201.ui.home.tab.feature;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.baontq.mob201.ui.home.intefaces.PlaylistItemAction;
import com.baontq.mob201.ui.home.intefaces.SongItemAction;
import com.baontq.mob201.until.PlaylistSongLoader;
import com.baontq.mob201.until.TaskRunner;
import com.kongzue.dialogx.dialogs.PopTip;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class HomePageFragment extends Fragment implements SongItemAction, PlaylistItemAction {

    private HomePageViewModel mViewModel;
    private FragmentHomePageBinding binding;
    private PlaylistAdapter playlistAdapter;
    private TaskRunner taskRunner;
    private RecentSongReceiver recentSongReceiver;
    private RecentSongAdapter songAdapter;
    private ArrayList<Song> recentSongList;

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
                playlistAdapter = new PlaylistAdapter(result, HomePageFragment.this);
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
                if (recentSongList == null && songAdapter == null) {
                    songAdapter = new RecentSongAdapter(recentSongs, this);
                }else {
                    songAdapter.setList(recentSongs);
                }
                recentSongList = recentSongs;
                binding.rvListRecentSong.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                binding.rvListRecentSong.setAdapter(songAdapter);
                binding.tvListRecentStatus.setVisibility(View.GONE);
            } else {
                binding.tvListRecentStatus.setVisibility(View.VISIBLE);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                SongService.removeRecentSong(requireActivity(), recentSongList.get(viewHolder.getAdapterPosition()));
                recentSongList.remove(viewHolder.getAdapterPosition());
                songAdapter.setList(recentSongList);
                songAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                songAdapter.notifyItemRangeChanged(viewHolder.getAdapterPosition(), recentSongList.size());
                if (songAdapter.getItemCount() == 0) {
                    binding.tvListRecentStatus.setVisibility(View.VISIBLE);
                }else {
                    binding.tvListRecentStatus.setVisibility(View.GONE);
                }
            }
        }).attachToRecyclerView(binding.rvListRecentSong);
    }

    @Override
    public void showMoreAction(int position, Song song) {

    }

    @Override
    public void setOnItemClickListener(int position, Song song) {
        Bundle bundle = new Bundle();
        bundle.putString("playlist_name", "recent_song");
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_navigation_home_to_listSongByPlaylistFragment, bundle);
    }

    @Override
    public void onItemClickListener(int position, Playlist playlist) {
        Bundle bundle = new Bundle();
        bundle.putString("playlist_name", playlist.toString());
        bundle.putLong("playlist_id", playlist.getId());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_navigation_home_to_listSongByPlaylistFragment, bundle);
    }
}

