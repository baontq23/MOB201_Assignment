package com.baontq.mob201.ui.home.tab.listsong;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
import com.baontq.mob201.databinding.FragmentListSongBinding;
import com.baontq.mob201.model.Song;
import com.baontq.mob201.service.PlayerService;
import com.baontq.mob201.service.SongService;
import com.baontq.mob201.ui.home.adapter.SongAdapter;
import com.baontq.mob201.ui.home.intefaces.SongItemAction;
import com.baontq.mob201.ui.home.tab.favoritesong.FavoriteSongViewModel;
import com.baontq.mob201.until.MusicUntil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;

public class ListSongFragment extends Fragment implements SongItemAction {
    private static final String TAG = "ListSongFragment";

    private FirebaseUser user;
    private ListSongViewModel mViewModel;
    private FragmentListSongBinding binding;
    private SongAdapter songAdapter;
    private ArrayList<Song> songs;
    private FavoriteSongViewModel favoriteSongViewModel;
    private ArrayList<Song> favoriteSongs;
    private PlayerService playerService;
    private boolean isBound = false;
    private Integer songPlayingPosition = -1;
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
    public void onStart() {
        super.onStart();
        getActivity().bindService(new Intent(getActivity(), PlayerService.class), connection, Context.BIND_AUTO_CREATE);
        isBound = true;
        Log.d(TAG, "onStart: Bind Service");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            getActivity().unbindService(connection);
            isBound = false;
            Log.d(TAG, "onStop: Unbind Service");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        favoriteSongs = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        songs = MusicUntil.getListSong(getActivity());
        binding = FragmentListSongBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListSongViewModel.class);
        favoriteSongViewModel = new ViewModelProvider(getActivity()).get(FavoriteSongViewModel.class);
        songAdapter = new SongAdapter(getActivity(), songs, ListSongFragment.this);
        favoriteSongViewModel.getData().observe(getViewLifecycleOwner(), listFavorite -> {
            favoriteSongs.clear();
            if (listFavorite != null)
                favoriteSongs.addAll(listFavorite);
        });
        binding.rvListSong.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rvListSong.setAdapter(songAdapter);
        songPlayingPosition = getActivity().getSharedPreferences("current_song_playing", Context.MODE_PRIVATE).getInt("song_position", -1);
        if (songPlayingPosition != -1) {
            songAdapter.setHighlightItemPosition(songPlayingPosition);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
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
        BottomMenu.show(new String[]{playOrPauseAction, getString(R.string.add_favorite_song)}).setOnIconChangeCallBack(new OnIconChangeCallBack<BottomMenu>() {
            @Override
            public int getIcon(BottomMenu dialog, int index, String menuText) {
                if (index == 0) {
                    return finalPlayOrPauseIcon;
                }
                if (index == 1) return R.drawable.love;
                return 0;
            }
        }).setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
            @Override
            public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                if (index == 0) {
                    updateCurrentSongPosition(position);
                    if (playerService.isPlaying()) {
                        if (playerService.getSongPlaying().getId() == song.getId()) {
                            playerService.pause();
                        } else {
                            songAdapter.setHighlightItemPosition(position);
                            PlayerService.playSong(getActivity(), song);
                        }
                    } else {
                        if (playerService.getSongPlaying() != null) {
                            if (playerService.getSong().getId() == song.getId()) {
                                playerService.resume();
                            } else {
                                PlayerService.playSong(getActivity(), song);
                                songAdapter.setHighlightItemPosition(position);
                            }
                        } else {
                            PlayerService.playSong(getActivity(), song);
                            songAdapter.setHighlightItemPosition(position);
                        }
                    }

                }
                if (index == 1) {
                    if (user != null) {
                        for (Song s : favoriteSongs) {
                            if (s.getId() == song.getId()) {
                                PopTip.show(getString(R.string.song_favorite_exists));
                                return true;
                            }
                        }
                        SongService.addFavoriteSong(getActivity(), user.getEmail(), song);
                    } else {
                        PopTip.show(R.string.require_login);
                    }
                }
                return false;
            }
        });
    }

    private void updateCurrentSongPosition(int position) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("current_song_playing", Context.MODE_PRIVATE).edit();
        editor.putInt("song_position", position);
        editor.apply();
    }

    @Override
    public void setOnItemClickListener(int position, Song song) {
        songAdapter.setHighlightItemPosition(position);
        PlayerService.playSong(getActivity(), song);
        updateCurrentSongPosition(position);
    }
}