package com.baontq.mob201.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.baontq.mob201.databinding.FragmentHomeBinding;
import com.baontq.mob201.ui.home.adapter.HomeViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewPagerAdapter viewPagerAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.vpHome.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(binding.tlHome, binding.vpHome, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Trang chủ");
                    break;
                case 1:
                    tab.setText("Bài hát");
                    break;
                case 2:
                    tab.setText("Yêu thích");
                    break;
            }
            binding.tlHome.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        binding.vpHome.setUserInputEnabled(false);
                    } else {
                        binding.vpHome.setUserInputEnabled(true);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }).attach();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewPagerAdapter = new HomeViewPagerAdapter(this);
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}