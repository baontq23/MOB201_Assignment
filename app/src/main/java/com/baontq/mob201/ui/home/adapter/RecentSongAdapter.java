package com.baontq.mob201.ui.home.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.mob201.R;
import com.baontq.mob201.model.Song;
import com.baontq.mob201.ui.home.intefaces.SongItemAction;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class RecentSongAdapter extends RecyclerView.Adapter<RecentSongAdapter.SongVH> {
    private List<Song> list;
    private SongItemAction songItemAction;

    public RecentSongAdapter( List<Song> list, SongItemAction songItemAction) {
        this.list = list;
        this.songItemAction = songItemAction;
    }

    @NonNull
    @Override
    public SongVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_recent, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongVH holder, int position) {
        Song song = list.get(position);
        holder.itemView.setOnClickListener(v -> {
            songItemAction.setOnItemClickListener(position, song);
        });
        holder.rivImage.setImageResource(R.drawable.ic_music_style);
        holder.tvTitle.setText(song.getTitle());
        holder.tvDescription.setText(song.getAlbumName());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class SongVH extends RecyclerView.ViewHolder {
        TextView  tvTitle, tvDescription;
        RoundedImageView rivImage;

        public SongVH(@NonNull View itemView) {
            super(itemView);

            rivImage = itemView.findViewById(R.id.riv_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
}
