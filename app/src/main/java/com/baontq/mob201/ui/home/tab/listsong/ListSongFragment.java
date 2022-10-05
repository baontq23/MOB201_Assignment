package com.baontq.mob201.ui.home.tab.listsong;

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
import com.baontq.mob201.databinding.FragmentListSongBinding;
import com.baontq.mob201.model.Song;
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
import java.util.List;

public class ListSongFragment extends Fragment implements SongItemAction {
    private FirebaseUser user;
    private ListSongViewModel mViewModel;
    private FragmentListSongBinding binding;
    private SongAdapter songAdapter;
    private ArrayList<Song> songs;
    private FavoriteSongViewModel favoriteSongViewModel;
    private ArrayList<Song> favoriteSongs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
        songAdapter = new SongAdapter(songs, this);
        favoriteSongViewModel.getData().observe(getViewLifecycleOwner(), listFavorite -> {
           favoriteSongs.clear();
           favoriteSongs.addAll(listFavorite);
        });
        binding.rvListSong.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rvListSong.setAdapter(songAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void showMoreAction(int position, Song song) {
        BottomMenu.show(new String[]{"Phát", "Thêm vào danh sách yêu thích"})
                .setOnIconChangeCallBack(new OnIconChangeCallBack<BottomMenu>() {
                    @Override
                    public int getIcon(BottomMenu dialog, int index, String menuText) {
                        if (index == 0) return R.drawable.ic_play_white;
                        if (index == 1) return R.drawable.love;
                        return 0;
                    }
                }).setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
                    @Override
                    public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                        if (index == 0) {

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

    @Override
    public void setOnItemClickListener(Song song) {

    }
}