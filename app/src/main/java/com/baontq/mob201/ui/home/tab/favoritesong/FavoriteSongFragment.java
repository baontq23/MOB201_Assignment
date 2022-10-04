package com.baontq.mob201.ui.home.tab.favoritesong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

import com.baontq.mob201.databinding.FragmentFavoriteSongBinding;
import com.baontq.mob201.model.Song;
import com.baontq.mob201.service.SongService;
import com.baontq.mob201.ui.home.adapter.FavoriteSongAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FavoriteSongFragment extends Fragment {

    private static final String TAG = "FavoriteSongFragment";
    private FavoriteSongViewModel mViewModel;
    private FragmentFavoriteSongBinding binding;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private ArrayList<Song> favoriteSongs;
    private FavoriteSongAdapter favoriteSongAdapter;
    private ResponseReceiver receiver = new ResponseReceiver();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        favoriteSongs = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
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
        SongService.getFavoriteSong(getActivity(), user.getEmail());

    }

    class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equalsIgnoreCase(SongService.ACTION_GET_LIST_FAVORITE_SONG)) {
                    favoriteSongs = intent.getParcelableArrayListExtra(SongService.RESULT_LIST_FAVORITE_SONG);
                    initData();
                }
            }
        }
    }

    private void initData() {
        if (favoriteSongAdapter == null) {
            favoriteSongAdapter = new FavoriteSongAdapter(favoriteSongs);
            binding.rvSongFavorite.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            binding.rvSongFavorite.setAdapter(favoriteSongAdapter);
        }else {
            favoriteSongAdapter.setList(favoriteSongs);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SongService.ACTION_GET_LIST_FAVORITE_SONG);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}