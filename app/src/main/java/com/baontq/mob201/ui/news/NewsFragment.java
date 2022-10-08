package com.baontq.mob201.ui.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.mob201.R;
import com.baontq.mob201.databinding.FragmentNewsBinding;
import com.baontq.mob201.model.News;
import com.baontq.mob201.model.RssChannel;
import com.baontq.mob201.ui.news.adapter.ChannelAdapter;
import com.baontq.mob201.ui.news.adapter.NewsAdapter;
import com.baontq.mob201.ui.news.interfaces.ChannelItemListener;
import com.baontq.mob201.ui.news.interfaces.NewsItemListener;
import com.baontq.mob201.until.ProgressBarDialog;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment implements NewsItemListener, ChannelItemListener {
    private FragmentNewsBinding binding;
    private List<News> list;
    private List<RssChannel> rssChannels;
    private ProgressBarDialog progressBarDialog;
    private NewsAdapter newsAdapter;
    private ChannelAdapter channelAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBarDialog = new ProgressBarDialog(getContext());
        rssChannels = new ArrayList<>();
        rssChannels.add(new RssChannel("Thế giới", "https://vnexpress.net/rss/the-gioi.rss"));
        rssChannels.add(new RssChannel("Kinh doanh", "https://vnexpress.net/rss/kinh-doanh.rss"));
        rssChannels.add(new RssChannel("Giáo dục", "https://vnexpress.net/rss/giao-duc.rss"));
        rssChannels.add(new RssChannel("Sức khoẻ", "https://vnexpress.net/rss/suc-khoe.rss"));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        initUI();
        NewsViewModel newsViewModel =
                new ViewModelProvider(this).get(NewsViewModel.class);
        newsViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoad -> {
            if (isLoad) {
                progressBarDialog.show();
            } else {
                progressBarDialog.dismiss();
            }
        });
        channelAdapter = new ChannelAdapter(rssChannels, this);
        binding.rvRssChannelList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.rvRssChannelList.setAdapter(channelAdapter);
        newsViewModel.getNewsList().observe(getViewLifecycleOwner(), listNews -> {
            list = listNews;
            newsAdapter = new NewsAdapter(getContext(), list, this);
            binding.rvNewsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            binding.rvNewsList.setAdapter(newsAdapter);
        });
        return binding.getRoot();
    }

    private void initUI() {
        progressBarDialog.setMessage("Loading");
        binding.tvShow.setOnClickListener(v -> {
            if (binding.rvRssChannelList.getVisibility() == View.VISIBLE) {
                binding.rvRssChannelList.setVisibility(View.GONE);
                binding.tvShow.setText("Mở rộng");
            } else {
                binding.rvRssChannelList.setVisibility(View.VISIBLE);
                binding.tvShow.setText("Thu gọn");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void setOnItemClickListener(int position, News news) {
        Bundle bundle = new Bundle();
        bundle.putString("link", news.getLink());
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_navigation_news_to_navigation_web_view, bundle);
    }

    @Override
    public void setOnItemClickListener(int position) {

    }
}