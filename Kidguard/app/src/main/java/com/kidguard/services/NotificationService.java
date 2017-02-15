package com.kidguard.services;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

@SuppressWarnings("all")
public class NotificationService extends NotificationListenerService {
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = String.valueOf(extras.getCharSequence("android.text"));
        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        //Log.e("Msg", "Notification"+pack+" "+text+" "+title);
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e("Msg", "Notification Removed");
    }
}

