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
import com.baontq.mob201.model.Playlist;
import com.baontq.mob201.ui.home.adapter.PlaylistAdapter;
import com.baontq.mob201.until.PlaylistSongLoader;
import com.baontq.mob201.until.TaskRunner;
import com.kongzue.dialogx.dialogs.PopTip;

import java.util.List;
import java.util.concurrent.Callable;

public class HomePageFragment extends Fragment {

    private HomePageViewModel mViewModel;
    private FragmentHomePageBinding binding;
    private PlaylistAdapter playlistAdapter;
    private TaskRunner taskRunner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomePageBinding.inflate(inflater, container, false);
        taskRunner = new TaskRunner();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        taskRunner.execute(new Callable<List<Playlist>>() {
            @Override
            public List<Playlist> call() throws Exception {
                return PlaylistSongLoader.getAllPlaylist(getActivity());
            }
        }, new TaskRunner.Callback<List<Playlist>>() {
            @Override
            public void onComplete(List<Playlist> result) {
                playlistAdapter = new PlaylistAdapter(result);
                binding.rvFeaturePlaylist.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                binding.rvFeaturePlaylist.setAdapter(playlistAdapter);
                if (result.size() != 0) binding.tvListStatus.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}

