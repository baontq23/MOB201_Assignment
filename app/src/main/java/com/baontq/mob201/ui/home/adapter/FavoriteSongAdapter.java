package com.baontq.mob201.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.mob201.R;
import com.baontq.mob201.model.Song;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class FavoriteSongAdapter extends RecyclerView.Adapter<FavoriteSongAdapter.FavoriteSongVH> {
    private ArrayList<Song> list;
    private int index = 1;

    public FavoriteSongAdapter(ArrayList<Song> list) {
        this.list = list;
    }

    public void setList(ArrayList<Song> list) {
        this.list = list;
        index =1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteSongVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteSongVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_bigger, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteSongVH holder, int position) {
        Song song = list.get(position);
        holder.title.setText(song.getTitle());
        holder.description.setText(song.getArtistName());
        holder.index.setText("" + index);
        index++;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class FavoriteSongVH extends RecyclerView.ViewHolder {
        TextView index, title, description;
        RoundedImageView image;
        ImageView menuButton;

        public FavoriteSongVH(@NonNull View itemView) {
            super(itemView);


            index = itemView.findViewById(R.id.index);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            menuButton = itemView.findViewById(R.id.menu_button);

        }
    }
}
