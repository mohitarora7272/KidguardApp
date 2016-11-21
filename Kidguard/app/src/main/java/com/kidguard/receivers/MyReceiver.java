package com.kidguard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kidguard.services.MyService;


public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            {
                Log.e("extras","extras??"+extras);
                Intent myIntent = new Intent(context, MyService.class);
                context.startService(myIntent);
            }
        }

    }
}
