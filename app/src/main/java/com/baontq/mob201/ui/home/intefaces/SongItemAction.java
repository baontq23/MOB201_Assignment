package com.baontq.mob201.ui.home.intefaces;

import com.baontq.mob201.model.Song;

public interface SongItemAction {
    void showMoreAction(int position, Song song);
    void setOnItemClickListener(Song song);
}
