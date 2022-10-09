package com.baontq.mob201.ui.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.baontq.mob201.R;
import com.baontq.mob201.databinding.FragmentListNewsByChannelBinding;
import com.baontq.mob201.model.News;
import com.baontq.mob201.ui.news.adapter.NewsAdapter;
import com.baontq.mob201.ui.news.interfaces.NewsItemListener;
import com.baontq.mob201.until.ParseNews;
import com.baontq.mob201.until.ProgressBarDialog;
import com.baontq.mob201.until.TaskRunner;
import com.kongzue.dialogx.dialogs.PopTip;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

public class NewsByChannelFragment extends Fragment implements NewsItemListener {
    private FragmentListNewsByChannelBinding binding;
    private ProgressBarDialog progressBarDialog;
    private NewsAdapter newsAdapter;
    private TaskRunner taskRunner;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskRunner = new TaskRunner();
        taskRunner.execute(new Callable<List<News>>() {
            @Override
            public List<News> call() throws Exception {
                URL url = new URL(getArguments().getString("rss_channel"));
                InputStream inputStream = url.openConnection().getInputStream();
                ParseNews newsLoader = new ParseNews();
                return newsLoader.parseNews(inputStream);
            }
        }, new TaskRunner.Callback<List<News>>() {
            @Override
            public void onComplete(List<News> result) {
                newsAdapter = new NewsAdapter(requireContext(), result, NewsByChannelFragment.this);
                binding.rvNewsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                binding.rvNewsList.setAdapter(newsAdapter);
                progressBarDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                progressBarDialog.dismiss();
                PopTip.show("Có lỗi khi đọc dữ liệu");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListNewsByChannelBinding.inflate(inflater, container, false);
        progressBarDialog = new ProgressBarDialog(getContext());
        progressBarDialog.setMessage("Loading").show();
        binding.ivBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
        return binding.getRoot();
    }

    @Override
    public void setOnItemClickListener(int position, News news) {
        Bundle bundle = new Bundle();
        bundle.putString("link", news.getLink());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_newsByChannelFragment_to_navigation_web_view, bundle);
    }
}
