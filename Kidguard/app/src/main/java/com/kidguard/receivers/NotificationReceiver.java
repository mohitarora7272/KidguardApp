package com.kidguard.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String pack = intent.getStringExtra("package");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        Log.d("package", "package??" + pack);
        Log.d("title", "title??" + title);
        Log.d("text", "text??" + text);

    }
}

