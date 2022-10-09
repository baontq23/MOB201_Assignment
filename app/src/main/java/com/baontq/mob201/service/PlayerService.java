package com.baontq.mob201.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.baontq.mob201.App;
import com.baontq.mob201.MainActivity;
import com.baontq.mob201.R;
import com.baontq.mob201.model.Song;
import com.baontq.mob201.receiver.NotificationMusicReceiver;
import com.kongzue.dialogx.dialogs.PopTip;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerService extends Service implements MediaPlayer.OnErrorListener {
    public static final String PACKAGE_NAME_MUSIC = "com.baontq.mob201.player";

    public static final String ACTION_TOGGLE_PAUSE = PACKAGE_NAME_MUSIC + ".toggle_pause";
    public static final String ACTION_PLAY = PACKAGE_NAME_MUSIC + ".play";
    public static final String ACTION_RESUME = PACKAGE_NAME_MUSIC + ".resume";
    public static final String ACTION_PAUSE = PACKAGE_NAME_MUSIC + ".pause";
    public static final String ACTION_STOP = PACKAGE_NAME_MUSIC + ".stop";
    public static final String ACTION_NEXT = PACKAGE_NAME_MUSIC + ".next";
    public static final String ACTION_PREV = PACKAGE_NAME_MUSIC + ".prev";
    public static final String ACTION_KILL = PACKAGE_NAME_MUSIC + ".kill_service";

    public static final String PARAM_SONG = PACKAGE_NAME_MUSIC + ".param_song";
    public static final String PARAM_SONG_INDEX = PACKAGE_NAME_MUSIC + ".param_song_index";
    private IBinder mBinder = new ServiceBinder();
    private MediaPlayer mediaPlayer;
    private Song mSong;
    private int length = 0;
    private ArrayList<Song> listSongs;
    private int playIndex = 0;

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

    public static void playSong(Context context, Song song, int index) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(PARAM_SONG, song);
        intent.putExtra(PARAM_SONG_INDEX, index);
        context.startService(intent);
    }

    public PlayerService() {
    }

    private void sendNotification(Song song) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.tv_notification_title, song.getTitle());
        remoteViews.setTextViewText(R.id.tv_notification_description, song.getAlbumName());
        remoteViews.setImageViewResource(R.id.notification_riv_image, R.drawable.music_round_gradient);
        if (mediaPlayer.isPlaying()) {
            remoteViews.setOnClickPendingIntent(R.id.notification_action_play_pause, getPendingIntent(this, ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.notification_action_play_pause, R.drawable.ic_pause_white);
        } else {
            remoteViews.setOnClickPendingIntent(R.id.notification_action_play_pause, getPendingIntent(this, ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.notification_action_play_pause, R.drawable.ic_play_white);
        }

        remoteViews.setOnClickPendingIntent(R.id.notification_action_prev, getPendingIntent(this, ACTION_PREV));
        remoteViews.setOnClickPendingIntent(R.id.notification_action_next, getPendingIntent(this, ACTION_NEXT));
        Notification notification = new NotificationCompat.Builder(this, App.PLAYER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_outline_music_24dp)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();
        startForeground(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(this, NotificationMusicReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case ACTION_PLAY:
                try {
                    mSong = intent.getParcelableExtra(PARAM_SONG);
                    playIndex = intent.getIntExtra(PARAM_SONG_INDEX, 0);
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(this, Uri.parse(mSong.getData()));
                        mediaPlayer.setOnCompletionListener(mp -> {
                            next();
                            sendNotification(listSongs.get(playIndex));
                            mSong = listSongs.get(playIndex);
                        });
                        mediaPlayer.setOnErrorListener(this);
                        mediaPlayer.setLooping(false);
                    } else {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(mSong.getData());
                        mediaPlayer.prepare();
                    }
                    mediaPlayer.start();
                    sendNotification(mSong);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ACTION_PAUSE:
                pause();
                sendNotification(mSong);
                break;
            case ACTION_STOP:
                stop();
                break;
            case ACTION_RESUME:
                resume();
                sendNotification(mSong);
                break;
            case ACTION_NEXT:
                next();
                sendNotification(mSong);
                break;
            case ACTION_PREV:
                prev();
                sendNotification(mSong);
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

    public void next() {
        try {
            if (playIndex == listSongs.size() - 1) {
                playIndex = 0;
            } else {
                playIndex++;
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(listSongs.get(playIndex).getData());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mSong = listSongs.get(playIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prev() {
        try {
            if (playIndex == 0) {
                playIndex = listSongs.size() - 1;
            } else {
                playIndex--;
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(listSongs.get(playIndex).getData());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mSong = listSongs.get(playIndex);
        } catch (IOException e) {
            e.printStackTrace();
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

    public void setListSongs(ArrayList<Song> listSongs) {
        this.listSongs = listSongs;
    }

    public Song getSong() {
        return mSong;
    }

    public Song getSongPlaying() {
        return mSong;
    }
}