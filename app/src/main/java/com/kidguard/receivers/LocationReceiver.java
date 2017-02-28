package com.kidguard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kidguard.interfaces.Constant;
import com.kidguard.services.LocationService;

@SuppressWarnings("all")
public class LocationReceiver extends BroadcastReceiver implements Constant {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, LocationService.class);
        context.startService(myIntent);

    }
}
