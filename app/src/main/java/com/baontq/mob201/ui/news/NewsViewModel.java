package com.baontq.mob201.ui.news;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.baontq.mob201.model.News;
import com.baontq.mob201.until.ParseNews;
import com.baontq.mob201.until.TaskRunner;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

public class NewsViewModel extends ViewModel {
    private TaskRunner taskRunner;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<List<News>> mNewsList;

    public NewsViewModel() {
        taskRunner = new TaskRunner();
        mNewsList = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(true);
                taskRunner.execute(new Callable<List<News>>() {
            @Override
            public List<News> call() throws Exception {
                URL url = new URL("https://vnexpress.net/rss/tin-moi-nhat.rss");
                InputStream inputStream = url.openConnection().getInputStream();
                ParseNews newsLoader = new ParseNews();
                return newsLoader.parseNews(inputStream);
            }
        }, new TaskRunner.Callback<List<News>>() {
            @Override
            public void onComplete(List<News> result) {
                mNewsList.setValue(result);
                isLoading.setValue(false);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }

    public MutableLiveData<List<News>> getNewsList() {
        return mNewsList;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}