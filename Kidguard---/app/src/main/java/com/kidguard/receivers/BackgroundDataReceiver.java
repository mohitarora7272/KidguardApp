package com.kidguard.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kidguard.interfaces.Constant;
import com.kidguard.services.GoogleAccountService;

public class BackgroundDataReceiver extends BroadcastReceiver implements Constant {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, GoogleAccountService.class);
        context.startService(myIntent);

//        try {
//            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//
//                Log.d("BOOT CHECK??", "BackgroundDataReceiver");
//
//                Intent alarm = new Intent(context, BackgroundDataReceiver.class);
//                boolean alarmRunning = (PendingIntent.getBroadcast(context, 0, alarm,
//                        PendingIntent.FLAG_NO_CREATE) != null);
//                if (alarmRunning == false) {
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm, 0);
//                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
//                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                            SystemClock.elapsedRealtime(),
//                            TIME_INTERVAL_FOR_DATA, pendingIntent);
//                }
//            }
//        } catch (Exception e) {
//            Log.e("Exception", "BackgroundDataReceiver??" + e.getMessage());
//        }
    }
}
