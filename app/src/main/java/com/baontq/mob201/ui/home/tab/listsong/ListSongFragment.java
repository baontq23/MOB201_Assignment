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
import com.baontq.mob201.until.MusicUntil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kongzue.dialogx.dialogs.PopTip;

import java.util.ArrayList;
import java.util.List;

public class ListSongFragment extends Fragment implements SongItemAction {
    private FirebaseUser user;
    private ListSongViewModel mViewModel;
    private FragmentListSongBinding binding;
    private SongAdapter songAdapter;
    private ArrayList<Song> songs;
    private FirebaseFirestore db;

    public static ListSongFragment newInstance() {
        return new ListSongFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        songs = MusicUntil.getListSong(getActivity());
        binding = FragmentListSongBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListSongViewModel.class);
        songAdapter = new SongAdapter(songs, this);
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
        if (user != null) {
            SongService.addFavoriteSong(getActivity(), user.getEmail(), song);
        } else {
            PopTip.show(R.string.require_login);
        }

    }

    @Override
    public void setOnItemClickListener(Song song) {

    }
}