package com.baontq.mob201.ui.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.mob201.R;
import com.baontq.mob201.model.News;
import com.baontq.mob201.ui.news.interfaces.NewsItemListener;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsVH> {
    private NewsItemListener newsItemListener;
    private List<News> list;
    private Context context;

    public NewsAdapter(Context context, List<News> list, NewsItemListener newsItemListener) {
        this.newsItemListener = newsItemListener;
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsVH holder, int position) {
        News news = list.get(position);
        Glide.with(context).load(news.getPreviewImage()).placeholder(R.drawable.vibrant_bg).into(holder.rivPreviewImage);
        holder.tvTitle.setText(news.getTitle());
        holder.tvDescription.setText(news.getDescription());
        holder.itemView.setOnClickListener(v -> newsItemListener.setOnItemClickListener(position, news));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class NewsVH extends RecyclerView.ViewHolder {
        RoundedImageView rivPreviewImage;
        TextView tvTitle, tvDescription;

        public NewsVH(@NonNull View itemView) {
            super(itemView);
            rivPreviewImage = itemView.findViewById(R.id.riv_preview_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);

        }
    }
}
