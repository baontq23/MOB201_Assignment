package com.baontq.mob201.ui.home.tab.favoritesong;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.baontq.mob201.databinding.FragmentFavoriteSongBinding;

public class FavoriteSongFragment extends Fragment {

    private FavoriteSongViewModel mViewModel;
    private FragmentFavoriteSongBinding binding;

    public static FavoriteSongFragment newInstance() {
        return new FavoriteSongFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoriteSongBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FavoriteSongViewModel.class);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}