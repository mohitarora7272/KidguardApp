package com.kidguard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kidguard.services.LocationService;


public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            {
                Log.e("extras", "extras??" + extras.get("send_data"));
                Intent myIntent = new Intent(context, LocationService.class);
                context.startService(myIntent);
            }
        }

    }
}
