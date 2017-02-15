package com.kidguard.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kidguard.interfaces.Constant;

@SuppressWarnings("all")
public class NotificationReceiver extends BroadcastReceiver implements Constant {

    @Override
    public void onReceive(Context context, Intent intent) {
        String pack = intent.getStringExtra("package");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
    }
}

