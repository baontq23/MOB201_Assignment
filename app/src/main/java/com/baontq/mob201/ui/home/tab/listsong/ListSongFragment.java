package com.baontq.mob201.ui.home.tab.listsong;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.baontq.mob201.until.TaskRunner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.concurrent.Callable;

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
    private TaskRunner taskRunner;
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
    private MusicReceiver receiver = new MusicReceiver();

    @Override
    public void onStart() {
        super.onStart();
        getActivity().bindService(new Intent(getActivity(), PlayerService.class), connection, Context.BIND_AUTO_CREATE);
        isBound = true;
        Log.d(TAG, "onStart: Bind Service");
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_NEXT);
        intentFilter.addAction(PlayerService.ACTION_PREV);
        requireActivity().registerReceiver(receiver, intentFilter);
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
        taskRunner = new TaskRunner();
        binding = FragmentListSongBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListSongViewModel.class);
        taskRunner.execute(new Callable<ArrayList<Song>>() {
            @Override
            public ArrayList<Song> call() throws Exception {
                return MusicUntil.getListSong(getActivity());
            }
        }, new TaskRunner.Callback<ArrayList<Song>>() {
            @Override
            public void onComplete(ArrayList<Song> result) {
                songs = result;
                songAdapter = new SongAdapter(getActivity(), result, ListSongFragment.this);
                binding.rvListSong.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                binding.rvListSong.setAdapter(songAdapter);
                songPlayingPosition = requireActivity().getSharedPreferences("current_song_playing", Context.MODE_PRIVATE).getInt("song_position", -1);
                if (songPlayingPosition != -1) {
                    songAdapter.setHighlightItemPosition(songPlayingPosition);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
        favoriteSongViewModel = new ViewModelProvider(requireActivity()).get(FavoriteSongViewModel.class);
        if (user != null) {
            favoriteSongViewModel.getData().observe(getViewLifecycleOwner(), listFavorite -> {
                favoriteSongs.clear();
                if (listFavorite != null)
                    favoriteSongs.addAll(listFavorite);
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().unregisterReceiver(receiver);
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
                            PlayerService.playSong(getActivity(), song, position);
                        }
                    } else {
                        if (playerService.getSongPlaying() != null) {
                            if (playerService.getSongPlaying().getId() == song.getId()) {
                                playerService.resume();
                            } else {
                                PlayerService.playSong(getActivity(), song, position);
                                songAdapter.setHighlightItemPosition(position);
                            }
                        } else {
                            PlayerService.playSong(getActivity(), song, position);
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
        PlayerService.playSong(getActivity(), song, position);
        updateCurrentSongPosition(position);
        playerService.setListSongs(songs);
    }

    class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(PlayerService.ACTION_NEXT) || intent.getAction().equalsIgnoreCase(PlayerService.ACTION_PREV)) {
                songAdapter.setHighlightItemPosition(playerService.getPlayIndex());
                SharedPreferences.Editor editor = requireActivity().getSharedPreferences("current_song_playing", Context.MODE_PRIVATE).edit();
                editor.putInt("song_position", playerService.getPlayIndex());
                editor.apply();
            }
        }
    }
}