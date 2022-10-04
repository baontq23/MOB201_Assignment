package com.baontq.mob201.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Song implements Parcelable {
    public static Song EMPTY_SONG = new Song(-1, "", -1, -1, -1, "", -1, -1, "", -1, "");

    private int id;
    private String title;
    private int trackNumber;
    private int year;
    private long duration;
    private String data;
    private long dateModified;
    private int albumId;
    private String albumName;
    private int artistId;
    private String artistName;

    public Song(int id, String title, int trackNumber, int year, long duration, String data, long dateModified, int albumId, String albumName, int artistId, String artistName) {
        this.id = id;
        this.title = title;
        this.trackNumber = trackNumber;
        this.year = year;
        this.duration = duration;
        this.data = data;
        this.dateModified = dateModified;
        this.albumId = albumId;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
    }

    protected Song(Parcel in) {
        id = in.readInt();
        title = in.readString();
        trackNumber = in.readInt();
        year = in.readInt();
        duration = in.readLong();
        data = in.readString();
        dateModified = in.readLong();
        albumId = in.readInt();
        albumName = in.readString();
        artistId = in.readInt();
        artistName = in.readString();
    }

    public Song() {
    }
    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public int getYear() {
        return year;
    }

    public long getDuration() {
        return duration;
    }

    public String getData() {
        return data;
    }

    public long getDateModified() {
        return dateModified;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public int getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeInt(trackNumber);
        dest.writeInt(year);
        dest.writeLong(duration);
        dest.writeString(data);
        dest.writeLong(dateModified);
        dest.writeInt(albumId);
        dest.writeString(albumName);
        dest.writeInt(artistId);
        dest.writeString(artistName);
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("trackNumber", trackNumber);
        map.put("year", year);
        map.put("duration", duration);
        map.put("data", data);
        map.put("dateModified", dateModified);
        map.put("albumId", albumId);
        map.put("albumName", albumName);
        map.put("artistId", artistId);
        map.put("artistName", artistName);
        return map;
    }
}
