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

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongVH> {
    private List<Song> list;
    private SongItemAction songItemAction;
    private int highlightItemPosition = -1;
    private Context context;

    public SongAdapter(Context context, List<Song> list, SongItemAction songItemAction) {
        this.list = list;
        this.songItemAction = songItemAction;
        this.context = context;
    }

    public int getHighlightItemPosition() {
        return highlightItemPosition;
    }

    public void setHighlightItemPosition(int highlightItemPosition) {
        int preItemPosition = this.highlightItemPosition;
        this.highlightItemPosition = highlightItemPosition;
        notifyItemChanged(highlightItemPosition);
        notifyItemChanged(preItemPosition);
    }

    @NonNull
    @Override
    public SongVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_normal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongVH holder, int position) {
        Song song = list.get(position);
        holder.itemView.setOnClickListener(v -> {
            songItemAction.setOnItemClickListener(position, song);
        });
        holder.rivImage.setImageResource(R.drawable.ic_music_style);
        holder.tvIndex.setText(String.valueOf(position + 1));
        holder.tvTitle.setText(song.getTitle());
        holder.tvDescription.setText(song.getAlbumName());
        holder.ivSongAction.setOnClickListener(v -> songItemAction.showMoreAction(position, song));
        if (position == highlightItemPosition) {
            holder.tvIndex.setTextColor(context.getResources().getColor(R.color.primaryColor, context.getTheme()));
            holder.rivImage.setBorderColor(context.getResources().getColor(R.color.primaryColor, context.getTheme()));
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.primaryColor, context.getTheme()));
            holder.tvDescription.setTextColor(context.getResources().getColor(R.color.primaryColor, context.getTheme()));
        }else {
            holder.tvIndex.setTextColor(Color.parseColor("#AAEEEEEE"));
            holder.rivImage.setBorderColor(Color.parseColor("#44F5F5F5"));
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.FlatWhite, context.getTheme()));
            holder.tvDescription.setTextColor(Color.parseColor("#AAF5F5F5"));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class SongVH extends RecyclerView.ViewHolder {
        TextView tvIndex, tvTitle, tvDescription;
        RoundedImageView rivImage;
        ImageView ivSongAction;

        public SongVH(@NonNull View itemView) {
            super(itemView);

            tvIndex = itemView.findViewById(R.id.tv_index);
            rivImage = itemView.findViewById(R.id.riv_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ivSongAction = itemView.findViewById(R.id.iv_song_action);

        }
    }
}
