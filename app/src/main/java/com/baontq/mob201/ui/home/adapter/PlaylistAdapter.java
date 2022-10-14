package com.baontq.mob201.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.mob201.R;
import com.baontq.mob201.model.Playlist;
import com.baontq.mob201.ui.home.intefaces.PlaylistItemAction;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistVH> {
    private List<Playlist> list;
    private PlaylistItemAction playlistItemAction;

    public PlaylistAdapter(List<Playlist> list, PlaylistItemAction playlistItemAction) {
        this.list = list;
        this.playlistItemAction = playlistItemAction;
    }

    @NonNull
    @Override
    public PlaylistVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature_playlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistVH holder, int position) {
        holder.playlistTitle.setText(list.get(position).getName());
        holder.itemView.setOnClickListener(v -> {
            playlistItemAction.onItemClickListener(position, list.get(position));
        });

        holder.playlistImage.setImageResource(R.drawable.music_round_gradient);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class PlaylistVH extends RecyclerView.ViewHolder {
        TextView playlistTitle;
        RoundedImageView playlistImage;

        public PlaylistVH(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.playlist_image);
            playlistTitle = itemView.findViewById(R.id.playlist_title);
        }
    }
}
