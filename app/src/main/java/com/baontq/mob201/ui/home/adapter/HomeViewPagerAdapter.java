package com.baontq.mob201.ui.home.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.baontq.mob201.ui.home.tab.favoritesong.FavoriteSongFragment;
import com.baontq.mob201.ui.home.tab.feature.HomePageFragment;
import com.baontq.mob201.ui.home.tab.listsong.ListSongFragment;

public class HomeViewPagerAdapter extends FragmentStateAdapter {
    public HomeViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ListSongFragment();
            case 2:
                return new FavoriteSongFragment();
            default:
                return new HomePageFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
