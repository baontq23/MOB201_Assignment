package com.baontq.mob201.ui.home.tab.feature;

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

import com.baontq.mob201.databinding.FragmentHomePageBinding;
import com.baontq.mob201.ui.home.adapter.PlaylistAdapter;
import com.baontq.mob201.until.PlaylistSongLoader;

public class HomePageFragment extends Fragment {

    private HomePageViewModel mViewModel;
    private FragmentHomePageBinding binding;
    private PlaylistAdapter playlistAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomePageBinding.inflate(inflater, container, false);
        playlistAdapter = new PlaylistAdapter(PlaylistSongLoader.getAllPlaylist(getActivity()));
        binding.rvFeaturePlaylist.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.rvFeaturePlaylist.setAdapter(playlistAdapter);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
    }

}