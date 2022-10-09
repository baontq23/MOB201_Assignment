package com.baontq.mob201.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baontq.mob201.service.PlayerService;

public class NotificationMusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, PlayerService.class);
        intent1.setAction(intent.getAction());
        context.startService(intent1);
    }
}
