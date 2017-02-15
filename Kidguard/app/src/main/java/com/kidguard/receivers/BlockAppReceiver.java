package com.kidguard.receivers;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kidguard.interfaces.Constant;
import com.kidguard.services.BlockAppService;

@SuppressWarnings("all")
public class BlockAppReceiver extends BroadcastReceiver implements Constant {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, BlockAppService.class);
        myIntent.putExtra(KEY_TAG, TAG_BLOCK_APP);
        myIntent.putExtra(KEY_PACKAGE_NAME, PACKAGE_NAME);
        context.startService(myIntent);

        try {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

                Log.d("BOOT CHECK??", "BlockAppReceiver");

                Intent alarm = new Intent(context, BlockAppReceiver.class);
                boolean alarmRunning = (PendingIntent.getBroadcast(context, 0, alarm,
                        PendingIntent.FLAG_NO_CREATE) != null);
                if (!alarmRunning) {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService
                            (Context.ALARM_SERVICE);
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            TIME_DELAY_FOR_CHECK, TIME_INTERVAL_FOR_CHECK, pendingIntent);
                }
            }
        } catch (Exception e) {
            Log.e("Exception", "BlockAppReceiver??" + e.getMessage());
        }
    }
}
