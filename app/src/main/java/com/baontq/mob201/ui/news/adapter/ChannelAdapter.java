package com.baontq.mob201.ui.news.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.mob201.R;
import com.baontq.mob201.model.RssChannel;
import com.baontq.mob201.ui.news.interfaces.ChannelItemListener;

import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelVH> {
    private List<RssChannel> list;
    private ChannelItemListener channelItemListener;

    public ChannelAdapter(List<RssChannel> list, ChannelItemListener channelItemListener) {
        this.list = list;
        this.channelItemListener = channelItemListener;
    }

    @NonNull
    @Override
    public ChannelVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChannelVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rss_channel_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelVH holder, int position) {
        holder.tvChannel.setText(list.get(position).getName());
        holder.itemView.setOnClickListener(v -> channelItemListener.setOnItemClickListener(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ChannelVH extends RecyclerView.ViewHolder {
        TextView tvChannel;

        public ChannelVH(@NonNull View itemView) {
            super(itemView);
            tvChannel = itemView.findViewById(R.id.tv_channel);

        }
    }
}
