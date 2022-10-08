package com.baontq.mob201.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.baontq.mob201.model.Song;

public class PlayerService extends Service implements MediaPlayer.OnErrorListener {
    public static final String PACKAGE_NAME_MUSIC = "com.baontq.mob201.player";

    public static final String ACTION_TOGGLE_PAUSE = PACKAGE_NAME_MUSIC + ".toggle_pause";
    public static final String ACTION_PLAY = PACKAGE_NAME_MUSIC + ".play";
    public static final String ACTION_RESUME = PACKAGE_NAME_MUSIC + ".resume";
    public static final String ACTION_PAUSE = PACKAGE_NAME_MUSIC + ".pause";
    public static final String ACTION_STOP = PACKAGE_NAME_MUSIC + ".stop";
    public static final String ACTION_KILL = PACKAGE_NAME_MUSIC + ".kill_service";

    public static final String PARAM_SONG = PACKAGE_NAME_MUSIC + ".param_song";
    private IBinder mBinder = new ServiceBinder();
    private MediaPlayer mediaPlayer;
    private Song mSong;
    private int length = 0;

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } finally {
                mediaPlayer = null;
            }
        }
        return false;
    }


    public class ServiceBinder extends Binder {
        public PlayerService getInstance() {
            return PlayerService.this;
        }

    }

    public static void playSong(Context context, Song song) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(PARAM_SONG, song);
        context.startService(intent);
    }

    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case ACTION_PLAY:
                try {
                    mSong = intent.getParcelableExtra(PARAM_SONG);
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(this, Uri.parse(mSong.getData()));
                        mediaPlayer.setOnErrorListener(this);
                        mediaPlayer.setLooping(false);
                        mediaPlayer.start();
                    } else {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(mSong.getData());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ACTION_PAUSE:
                pause();
                break;
            case ACTION_STOP:
                stop();
                break;
            default:
                break;
        }
        return START_NOT_STICKY;
    }

    public void play() {

    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            length = mediaPlayer.getCurrentPosition();
        }
    }

    public void resume() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public Song getSong() {
        return mSong;
    }
    public Song getSongPlaying() {
        return mSong;
    }
}