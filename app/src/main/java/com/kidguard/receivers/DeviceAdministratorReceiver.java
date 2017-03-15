package com.kidguard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kidguard.UninstallActivity;
import com.kidguard.preference.Preference;

@SuppressWarnings("all")
public class DeviceAdministratorReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Preference.getIsAdminActive(context)) {
            Intent myIntent = new Intent(context, UninstallActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }
    }
}
