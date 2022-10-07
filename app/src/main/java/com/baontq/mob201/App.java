package com.baontq.mob201;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.style.MIUIStyle;

public class App extends Application {
    public static final String PLAYER_CHANNEL_ID = "player_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        initialChannelNotification();
        DialogX.init(this);
        DialogX.DEBUGMODE = false;
        DialogX.autoRunOnUIThread = false;
        DialogX.globalTheme = DialogX.THEME.DARK;
//        DialogX.globalStyle = MIUIStyle.style();
    }

    private void initialChannelNotification() {
        NotificationChannel channel = new NotificationChannel(PLAYER_CHANNEL_ID, "Player Service", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null, null);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(channel);
    }
}
