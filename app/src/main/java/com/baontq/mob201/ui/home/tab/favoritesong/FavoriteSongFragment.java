package com.baontq.mob201.ui.home.tab.favoritesong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import com.baontq.mob201.databinding.FragmentFavoriteSongBinding;
import com.baontq.mob201.model.Song;
import com.baontq.mob201.service.SongService;
import com.baontq.mob201.ui.home.adapter.FavoriteSongAdapter;
import com.baontq.mob201.ui.home.intefaces.SongItemAction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;

public class FavoriteSongFragment extends Fragment implements SongItemAction {

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
        //SongService.getFavoriteSong(getActivity(), user.getEmail());

    }

    @Override
    public void showMoreAction(int position, Song song) {
        BottomMenu.show(new String[]{"Phát", "Xoá khỏi yêu thích"})
                .setOnIconChangeCallBack(new OnIconChangeCallBack<BottomMenu>() {
                    @Override
                    public int getIcon(BottomMenu dialog, int index, String menuText) {
                        if (index == 0) return R.drawable.ic_play_white;
                        if (index == 1) return R.drawable.ic_outline_playlist_remove_24;
                        return 0;
                    }
                }).setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
                    @Override
                    public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                        if (index == 0) {

                        }
                        if (index == 1) {
                            SongService.removeFavoriteSong(getActivity(), user.getEmail(), song);
                        }
                        return false;
                    }
                });
    }

    @Override
    public void setOnItemClickListener(int position, Song song) {

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
//        if (favoriteSongAdapter == null) {
        favoriteSongAdapter = new FavoriteSongAdapter(favoriteSongs, this);
        binding.rvSongFavorite.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rvSongFavorite.setAdapter(favoriteSongAdapter);
//        } else {
//            favoriteSongAdapter.setList(favoriteSongs);
//            favoriteSongAdapter.notifyDataSetChanged();
//        }

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(SongService.ACTION_GET_LIST_FAVORITE_SONG);
//        getActivity().registerReceiver(receiver, intentFilter);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        getActivity().unregisterReceiver(receiver);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}