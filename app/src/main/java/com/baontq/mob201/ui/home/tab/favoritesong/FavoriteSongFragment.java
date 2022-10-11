package com.baontq.mob201.ui.home.tab.favoritesong;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
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
import com.baontq.mob201.databinding.FragmentFavoriteSongBinding;
import com.baontq.mob201.model.Song;
import com.baontq.mob201.service.PlayerService;
import com.baontq.mob201.service.SongService;
import com.baontq.mob201.ui.home.adapter.FavoriteSongAdapter;
import com.baontq.mob201.ui.home.intefaces.SongItemAction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack;

import java.util.ArrayList;

public class FavoriteSongFragment extends Fragment implements SongItemAction {

    private static final String TAG = "FavoriteSongFragment";
    private FavoriteSongViewModel mViewModel;
    private FragmentFavoriteSongBinding binding;
    private FirebaseUser user;
    private PlayerService playerService;
    private boolean isBound = false;
    private ArrayList<Song> favoriteSongs;
    private FavoriteSongAdapter favoriteSongAdapter;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            playerService = ((PlayerService.ServiceBinder) binder).getInstance();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playerService = null;
            isBound = false;
        }
    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        favoriteSongs = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        binding = FragmentFavoriteSongBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FavoriteSongViewModel.class);
        if (user == null) {
            binding.tvListStatus.setVisibility(View.VISIBLE);
            return;
        }
        mViewModel.getData().observe(getViewLifecycleOwner(), songs -> {
            int index = 0;
            if (favoriteSongs.size() == 0) {
                if (songs != null)
                    favoriteSongs.addAll(songs);
                initData();
            } else {
                if (songs.size() > favoriteSongs.size()) {
                    for (int i = 0; i < favoriteSongs.size(); i++) {
                        if (favoriteSongs.get(i).getId() != songs.get(i).getId()) {
                            break;
                        }
                        index++;
                    }
                    favoriteSongAdapter.setList(songs);
                    favoriteSongAdapter.notifyItemInserted(index);
                    favoriteSongAdapter.notifyItemRangeChanged(index, songs.size());
                } else if (songs.size() < favoriteSongs.size()) {
                    for (Song s : songs) {
                        if (favoriteSongs.get(index).getId() != s.getId()) {
                            break;
                        }
                        index++;
                    }
                    favoriteSongAdapter.setList(songs);
                    favoriteSongAdapter.notifyItemRemoved(index);
                }
                favoriteSongs.clear();
                favoriteSongs.addAll(songs);
            }

        });

    }

    @Override
    public void showMoreAction(int position, Song song) {
        String playOrPauseAction = getString(R.string.play);
        Integer playOrPauseIcon = R.drawable.ic_play_white;
        if (playerService.getSongPlaying() != null) {
            if (playerService.getSongPlaying().getId() == song.getId()) {
                if (playerService.isPlaying()) {
                    playOrPauseAction = getString(R.string.pause);
                    playOrPauseIcon = R.drawable.ic_pause_white;
                } else {
                    playOrPauseAction = getString(R.string.play);
                    playOrPauseIcon = R.drawable.ic_play_white;
                }
            } else {
                playOrPauseAction = getString(R.string.play);
                playOrPauseIcon = R.drawable.ic_play_white;
            }
        }
        int finalPlayOrPauseIcon = playOrPauseIcon;
        BottomMenu.show(new String[]{playOrPauseAction, getString(R.string.remove_favorite_song)})
                .setOnIconChangeCallBack(new OnIconChangeCallBack<BottomMenu>() {
                    @Override
                    public int getIcon(BottomMenu dialog, int index, String menuText) {
                        if (index == 0) return finalPlayOrPauseIcon;
                        if (index == 1) return R.drawable.ic_outline_playlist_remove_24;
                        return 0;
                    }
                }).setOnMenuItemClickListener((dialog, text, index) -> {
                    if (index == 0) {
                        playerService.setListSongs(favoriteSongs);
                        if (playerService.isPlaying()) {
                            if (playerService.getSongPlaying().getId() == song.getId()) {
                                playerService.pause();
                            } else {
                                PlayerService.playSong(getActivity(), song, position);
                            }
                        } else {
                            if (playerService.getSongPlaying() != null) {
                                if (playerService.getSongPlaying().getId() == song.getId()) {
                                    playerService.resume();
                                } else {
                                    PlayerService.playSong(getActivity(), song, position);
                                }
                            } else {
                                PlayerService.playSong(getActivity(), song, position);
                            }
                        }
                    }
                    if (index == 1) {
                        SongService.removeFavoriteSong(getActivity(), user.getEmail(), song);
                    }
                    return false;
                });
    }

    @Override
    public void setOnItemClickListener(int position, Song song) {
        PlayerService.playSong(getActivity(), song, position);
        playerService.setListSongs(favoriteSongs);
    }

    private void initData() {
        favoriteSongAdapter = new FavoriteSongAdapter(favoriteSongs, this);
        binding.rvSongFavorite.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rvSongFavorite.setAdapter(favoriteSongAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().bindService(new Intent(getActivity(), PlayerService.class), connection, Context.BIND_AUTO_CREATE);
        isBound = true;
        Log.d(TAG, "onStart: Bind Service");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            requireActivity().unbindService(connection);
            isBound = false;
            Log.d(TAG, "onStop: Unbind Service");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}